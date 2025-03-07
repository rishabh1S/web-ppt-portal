// routes/convert.js
import express from 'express';
import multer from 'multer';
import { convertPresentationToHtml } from '../services/presentationService.js';
import pool from '../Connection/db.js';
import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

const router = express.Router();
const upload = multer({ dest: 'uploads/' });

router.post('/', upload.single('file'), async (req, res) => {
  if (!req.file) {
    return res.status(400).send('No file uploaded.');
  }

  try {
    // Convert PPT to HTML and process each slide individually
    const slides = convertPresentationToHtml(req.file.path);

    // Save presentation and slides to the database
    const presentation = await prisma.presentation.create({
      data: {
        name: req.file.originalname,
        slides: {
          create: slides.map((htmlContent, index) => ({
            slideNumber: index + 1,
            htmlContent,
          })),
        },
      },
      include: {
        slides: true,
      },
    });

    res.status(200).json(presentation);
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error processing PPT: ' + error.message);
  }
});


// Fetch PPT data
router.get('/:id', async (req, res) => {
  const presentationId = req.params.id;

  // Validate the presentationId
  if (!presentationId) {
    return res.status(400).send('Invalid presentation ID');
  }

  try {
    const presentation = await prisma.presentation.findUnique({
      where: { id: presentationId },
      include: { slides: true },
    });

    if (!presentation) {
      return res.status(404).send('Presentation not found.');
    }

    res.status(200).json(presentation);
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error fetching PPT data: ' + error.message);
  }
});

// Save edited PPT data
router.put('/:id', async (req, res) => {
  const presentationId = req.params.id;
  const { slides } = req.body;

  // Validate the presentationId
  if (!presentationId) {
    return res.status(400).send('Invalid presentation ID');
  }

  try {
    // Update each slide
    for (const slide of slides) {
      await prisma.slide.update({
        where: { id: slide.id },
        data: { htmlContent: slide.htmlContent },
      });
    }

    res.status(200).send('Presentation updated successfully.');
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error updating PPT: ' + error.message);
  }
});
export default router;