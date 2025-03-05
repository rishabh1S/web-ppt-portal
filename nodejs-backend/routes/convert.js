import express from "express";
import multer from "multer";
import { convertPresentationToHtml } from "../services/presentationService.js";

const router = express.Router();
const upload = multer({ dest: "uploads/" });

router.post("/", upload.single("file"), (req, res) => {
  if (!req.file) {
    return res.status(400).send("No file uploaded.");
  }

  try {
    const htmlContent = convertPresentationToHtml(req.file.path);
    res.type("html").send(htmlContent);
  } catch (error) {
    console.error("Conversion error:", error);
    res.status(500).send("Error converting PPT to HTML: " + error.message);
  }
});

export default router;
