import fs from "fs";
import path from "path";
import asposeSlides from "aspose.slides.via.java";

export function convertPresentationToHtml(inputPath) {
  let license = new asposeSlides.License();
  license.setLicense("Aspose.SlidesforNode.jsviaJava.lic");

  const pres = new asposeSlides.Presentation(inputPath);
  const slides = [];

  // Loop through each slide
  for (let i = 0; i < pres.getSlides().size(); i++) {
    const slide = pres.getSlides().get_Item(i);

    // Create a temporary presentation with only the current slide
    const tempPres = new asposeSlides.Presentation();
    try {
      tempPres.getSlides().removeAt(0); // Remove default empty slide
      tempPres.getSlides().addClone(slide); // Add only the current slide

      // Save the temporary presentation as HTML to a temporary file
      const tempFilePath = path.join("uploads", `temp_slide_${i}.html`);
      tempPres.save(tempFilePath, asposeSlides.SaveFormat.Html5);

      // Read the HTML content from the temporary file
      const htmlContent = fs.readFileSync(tempFilePath, "utf8");
      slides.push(htmlContent); // Add the slide content to the array

      // Clean up the temporary file
      fs.unlinkSync(tempFilePath);
    } finally {
      tempPres.dispose(); // Clean up the temporary presentation
    }
  }

  return slides; // Return an array of slide contents
}
