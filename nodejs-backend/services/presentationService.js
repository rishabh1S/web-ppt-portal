import fs from "fs";
import path from "path";
import asposeSlides from "aspose.slides.via.java";

export function convertPresentationToHtml(inputPath) {
  // Create a Presentation object by loading the file
  const pres = new asposeSlides.Presentation(inputPath);

  // Define a temporary output file path for the HTML file
  const outputFileName = `output_${Date.now()}.html`;
  const outputPath = path.join("uploads", outputFileName);

  // Save the presentation as HTML
  pres.save(outputPath, asposeSlides.SaveFormat.Html5);

  // Read the generated HTML content
  const htmlContent = fs.readFileSync(outputPath, "utf8");

  // Clean up: remove temporary files
  fs.unlinkSync(inputPath);
  fs.unlinkSync(outputPath);

  return htmlContent;
}
