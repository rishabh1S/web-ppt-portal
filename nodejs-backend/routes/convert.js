// routes/convert.js
import express from 'express';
import multer from 'multer';
import { convertPresentationToHtml } from '../services/presentationService.js';
import pool from '../Connection/db.js';

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
    const client = await pool.connect();
    try {
      // Insert presentation
      const presentationRes = await client.query(
        'INSERT INTO presentations (name) VALUES ($1) RETURNING id',
        [req.file.originalname]
      );
      const presentationId = presentationRes.rows[0].id;

      // Insert slides
      for (let i = 0; i < slides.length; i++) {
        await client.query(
          'INSERT INTO slides (presentation_id, slide_number, html_content) VALUES ($1, $2, $3)',
          [presentationId, i + 1, slides[i]]
        );
      }

      res.status(200).json({ presentationId });
    } finally {
      client.release();
    }
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error processing PPT: ' + error.message);
  }
});


// Fetch PPT data
router.get('/:id', async (req, res) => {
  const presentationId = req.params.id;

  try {
    const client = await pool.connect();
    try {
      // Fetch presentation
      const presentationRes = await client.query(
        'SELECT * FROM presentations WHERE id = $1',
        [presentationId]
      );
      if (presentationRes.rows.length === 0) {
        return res.status(404).send('Presentation not found.');
      }

      // Fetch slides
      const slidesRes = await client.query(
        'SELECT * FROM slides WHERE presentation_id = $1 ORDER BY slide_number',
        [presentationId]
      );

      res.status(200).json({
        presentation: presentationRes.rows[0],
        slides: slidesRes.rows,
      });
    } finally {
      client.release();
    }
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error fetching PPT data: ' + error.message);
  }
});

// Save edited PPT data
router.put('/:id', async (req, res) => {
  const presentationId = req.params.id;
  const { slides } = req.body; // Array of updated slides

  try {
    const client = await pool.connect();
    try {
      // Update each slide
      for (const slide of slides) {
        await client.query(
          'UPDATE slides SET html_content = $1 WHERE id = $2',
          [slide.htmlContent, slide.id]
        );
      }

      res.status(200).send('Presentation updated successfully.');
    } finally {
      client.release();
    }
  } catch (error) {
    console.error('Error:', error);
    res.status(500).send('Error updating PPT: ' + error.message);
  }
});

export default router;