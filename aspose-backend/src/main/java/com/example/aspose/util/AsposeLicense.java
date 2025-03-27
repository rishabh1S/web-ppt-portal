package com.example.aspose.util;

import com.aspose.slides.License;

public class AsposeLicense {
    public static void setLicense() {
        try {
            License license = new License();
            license.setLicense("src/main/resources/Aspose.SlidesforJava.lic");
            System.out.println("Aspose.Slides license applied successfully.");
        } catch (Exception e) {
            System.err.println("Failed to load Aspose.Slides license: " + e.getMessage());
        }
    }
}
