package com.example.aspose.service;

import java.awt.Color;
import org.springframework.stereotype.Service;

import com.aspose.slides.AutoShape;
import com.aspose.slides.BulletType;
import com.aspose.slides.FillType;
import com.aspose.slides.IAutoShape;
import com.aspose.slides.IParagraph;
import com.aspose.slides.IPortion;
import com.aspose.slides.ISlide;
import com.aspose.slides.ITable;
import com.aspose.slides.ITextFrame;
import com.aspose.slides.NullableBool;
import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;
import com.aspose.slides.ShapeType;
import com.aspose.slides.SlideLayoutType;
import com.aspose.slides.TextAutofitType;
import com.aspose.slides.Paragraph;
import com.aspose.slides.Portion;
import com.aspose.slides.TextAlignment;

@Service
public class OverviewSlideService {

    public void createOverviewSlide(Presentation pres) {
        // Remove the default empty slide (if it exists)
        if (pres.getSlides().size() > 0) {
            pres.getSlides().removeAt(0); // Remove the first slide
        }
        // Create a blank slide
        ISlide slide = pres.getSlides().addEmptySlide(pres.getLayoutSlides().getByType(SlideLayoutType.TitleOnly));

        IAutoShape titleShape = (IAutoShape) slide.getShapes().get_Item(0); // First shape is the title placeholder
        titleShape.getTextFrame().setText("Overview Template");

        // Format the text
        IPortion titlePortion = titleShape.getTextFrame().getParagraphs().get_Item(0).getPortions().get_Item(0);
        titlePortion.getPortionFormat().setFontHeight(32);
        titlePortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        titlePortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(new Color(0, 102, 204)); // Blue
                                                                                                              // color

        // Add Citigroup logo/text in top right
        addCitiLogo(slide);

        // Add "Presenter(s)" text
        addPresenterText(slide);

        // Add decision/review/noting table header
        addTableHeader(slide);

        // --------------------------------------------------------------------
        // Create a table for questions and answer placeholders
        // --------------------------------------------------------------------
        float tableX = 50f;
        float tableY = 240f; // Position below the header
        // Define two columns: adjust widths as needed
        double[] colWidths = { 350, 550 };
        // Four rows with equal height (adjust height as needed)
        double[] rowHeights = { 65, 65, 65, 65 };

        ITable table = slide.getShapes().addTable(tableX, tableY, colWidths, rowHeights);

        // Questions array
        String[] questions = {
                "What information is being provided?",
                "Why is this being reported / escalated to the Committee?",
                "What are the key points for the Committee to consider?",
                "Is a Governance Committee decision needed and, if so, what is recommended?"
        };

        for (int i = 0; i < questions.length; i++) {
            // Left cell: Question text
            ITextFrame leftCell = table.get_Item(0, i).getTextFrame();
            leftCell.setText(questions[i]);
            IParagraph leftParagraph = leftCell.getParagraphs().get_Item(0);
            leftParagraph.getParagraphFormat().getBullet().setType(BulletType.Symbol);
            leftParagraph.getParagraphFormat().getBullet().setChar('\u2023');

            IPortion leftPortion = leftCell.getParagraphs().get_Item(0).getPortions().get_Item(0);
            leftPortion.getPortionFormat().setFontHeight(14);
            leftPortion.getPortionFormat().setFontBold(NullableBool.True);
            leftPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
            leftPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLACK);

            // Right cell: Answer placeholder
            ITextFrame rightCell = table.get_Item(1, i).getTextFrame();
            rightCell.setText("[Enter Text]");
            IPortion rightPortion = rightCell.getParagraphs().get_Item(0).getPortions().get_Item(0);
            rightPortion.getPortionFormat().setFontHeight(14);
            rightPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
            rightPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLUE);
        }

        // Add Details section
        addSectionHeader(slide, "Details", 650f);
        addPlaceholderText(slide, 675f);

        // Add footer with Citi logo and confidential text
        addFooter(slide);

        pres.save("OverviewSlide.pptx", SaveFormat.Pptx);
    }

    private void addCitiLogo(ISlide slide) {
        // Add Citigroup text/box in top right corner
        AutoShape citiGroupShape = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 1080f, 150f, 95f, 30f);
        citiGroupShape.getFillFormat().setFillType(FillType.Solid);
        citiGroupShape.getFillFormat().getSolidFillColor().setColor(new Color(13, 71, 161)); // Deep blue
        citiGroupShape.getLineFormat().setWidth(0); // No border

        ITextFrame citiGroupText = citiGroupShape.getTextFrame();
        citiGroupText.setText("Citigroup");
        IPortion citiGroupPortion = citiGroupText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        citiGroupPortion.getPortionFormat().setFontHeight(10);
        citiGroupPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        citiGroupPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.WHITE);
        citiGroupText.getParagraphs().get_Item(0).getParagraphFormat().setAlignment(TextAlignment.Center);

        // Add Citibank, N.A. text/box
        AutoShape citiBankShape = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 1175f, 150f, 110f, 30f);
        citiBankShape.getFillFormat().setFillType(FillType.Solid);
        citiBankShape.getFillFormat().getSolidFillColor().setColor(new Color(211, 47, 47)); // Red
        citiBankShape.getLineFormat().setWidth(0); // No border

        ITextFrame citiBankText = citiBankShape.getTextFrame();
        citiBankText.setText("Citibank, N.A.");
        IPortion citiBankPortion = citiBankText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        citiBankPortion.getPortionFormat().setFontHeight(10);
        citiBankPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        citiBankPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.WHITE);
        citiBankText.getParagraphs().get_Item(0).getParagraphFormat().setAlignment(TextAlignment.Center);
    }

    private void addPresenterText(ISlide slide) {
        // Add Presenter(s) text
        AutoShape presenterShape = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 1080f, 190f, 205f, 30f);
        presenterShape.getLineFormat().setWidth(0); // No border

        ITextFrame presenterText = presenterShape.getTextFrame();
        presenterText.setText("Presenter(s)");
        IPortion presenterPortion = presenterText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        presenterPortion.getPortionFormat().setFontHeight(12);
        presenterPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        presenterPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLACK);
    }

    private void addTableHeader(ISlide slide) {
        // Add black header bar with options
        AutoShape headerBar = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 50f, 210f, 900f, 30f);
        headerBar.getFillFormat().setFillType(FillType.Solid);
        headerBar.getFillFormat().getSolidFillColor().setColor(Color.BLACK);
        headerBar.getLineFormat().setWidth(0); // No border

        ITextFrame headerText = headerBar.getTextFrame();
        headerText.setText("For Decision / For Review and Challenge / For Noting");
        IPortion headerPortion = headerText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        headerPortion.getPortionFormat().setFontHeight(14);
        headerPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        headerPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.WHITE);
        headerText.getParagraphs().get_Item(0).getParagraphFormat().setAlignment(TextAlignment.Center);
    }

    private void addSectionHeader(ISlide slide, String text, float yPos) {
        AutoShape header = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 50f, yPos, 200f, 25f);
        header.getLineFormat().setWidth(0);
        ITextFrame tf = header.getTextFrame();
        tf.setText(text);

        IPortion portion = tf.getParagraphs().get_Item(0).getPortions().get_Item(0);
        portion.getPortionFormat().setFontBold(NullableBool.True);
        portion.getPortionFormat().setFontHeight(16);
        portion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        portion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLACK);
    }

    private IPortion addPlaceholderText(ISlide slide, float yPos) {
        AutoShape placeholder = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 50f, yPos, 900f, 40f);
        placeholder.getLineFormat().setWidth(0);
        ITextFrame tf = placeholder.getTextFrame();
        tf.getTextFrameFormat().setAutofitType(TextAutofitType.Shape);

        Paragraph para = new Paragraph();
        para.getParagraphFormat().getBullet().setType(BulletType.None);

        IPortion portion = new Portion();
        portion.setText("[Enter Text]");
        portion.getPortionFormat().setFontHeight(14);
        portion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        portion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLUE); // Blue color

        para.getPortions().add(portion);
        tf.getParagraphs().add(para);
        return portion;
    }

    private void addFooter(ISlide slide) {
        // Add Citi logo in footer (simplified as text with styling)
        AutoShape citiLogo = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 50f, 780f, 40f, 25f);
        citiLogo.getLineFormat().setWidth(0); // No border

        ITextFrame citiLogoText = citiLogo.getTextFrame();
        citiLogoText.setText("citi");
        IPortion citiLogoPortion = citiLogoText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        citiLogoPortion.getPortionFormat().setFontHeight(16);
        citiLogoPortion.getPortionFormat().setFontBold(NullableBool.True);
        citiLogoPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        citiLogoPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLUE);

        // Add "Footer" text
        AutoShape footerShape = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 100f, 780f, 150f, 25f);
        footerShape.getLineFormat().setWidth(0);

        ITextFrame footerText = footerShape.getTextFrame();
        footerText.setText("Footer");
        IPortion footerPortion = footerText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        footerPortion.getPortionFormat().setFontHeight(12);
        footerPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        footerPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.GRAY);

        // Add confidential footer text at bottom
        AutoShape confidentialShape = (AutoShape) slide.getShapes().addAutoShape(
                ShapeType.Rectangle, 50f, 830f, 900f, 20f);
        confidentialShape.getLineFormat().setWidth(0);

        ITextFrame confidentialText = confidentialShape.getTextFrame();
        confidentialText.setText("Confidential Supervisory Information - Confidential Treatment Requested");
        IPortion confidentialPortion = confidentialText.getParagraphs().get_Item(0)
                .getPortions().get_Item(0);
        confidentialPortion.getPortionFormat().setFontHeight(10);
        confidentialPortion.getPortionFormat().getFillFormat().setFillType(FillType.Solid);
        confidentialPortion.getPortionFormat().getFillFormat().getSolidFillColor().setColor(Color.BLUE);
        confidentialText.getParagraphs().get_Item(0).getParagraphFormat().setAlignment(TextAlignment.Center);
    }
}
