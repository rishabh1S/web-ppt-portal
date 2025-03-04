package com.example.webppt.model;

import org.apache.poi.sl.usermodel.ShapeType;

public enum ShapeSVGPath {
    LINE("M 0 50 L 100 50"),
    RECT("M 0 0 H 100 V 100 H 0 Z"),
    ELLIPSE("M 50,0 A 50,50 0 1 1 50,100 A 50,50 0 1 1 50,0"),
    TRIANGLE("M 50 0 L 100 100 L 0 100 Z"),
    RT_TRIANGLE("M 0 0 L 100 100 L 0 100 Z"),
    RIGHT_ARROW("M 0 50 L 70 50 L 70 30 L 100 50 L 70 70 L 70 50 Z"),
    LEFT_ARROW("M 100 50 L 30 50 L 30 30 L 0 50 L 30 70 L 30 50 Z"),
    UP_ARROW("M 50 0 L 70 30 L 50 30 L 50 100 L 50 30 L 30 30 Z"),
    DOWN_ARROW("M 50 100 L 70 70 L 50 70 L 50 0 L 50 70 L 30 70 Z"),
    DIAMOND("M 50 0 L 100 50 L 50 100 L 0 50 Z"),
    HEXAGON("M 50 0 L 100 25 L 100 75 L 50 100 L 0 75 L 0 25 Z"),
    PENTAGON("M 50 0 L 100 38 L 82 100 L 18 100 L 0 38 Z"),
    CHEVRON("M 0 0 L 100 0 L 80 50 L 100 100 L 0 100 L 20 50 Z"),
    STAR_5("M 50 0 L 61 35 L 98 35 L 68 57 L 79 91 L 50 70 L 21 91 L 32 57 L 2 35 L 39 35 Z"),
    HEART("M 50 100 C 25 75 0 50 0 30 C 0 0 25 0 50 25 C 75 0 100 0 100 30 C 100 50 75 75 50 100 Z"),
    CLOUD("M 25 50 C 25 35 40 25 55 25 C 70 25 80 35 80 45 C 95 45 100 60 95 70 C 95 80 85 90 75 90 C 65 90 55 90 35 90 C 20 90 10 80 15 65 C 15 55 20 50 25 50 Z"),
    PLUS("M 40 0 V 40 H 0 V 60 H 40 V 100 H 60 V 60 H 100 V 40 H 60 V 0 Z"),
    PARALLELOGRAM("M 20 0 L 100 0 L 80 100 L 0 100 Z"),
    TRAPEZOID("M 20 0 L 80 0 L 100 100 L 0 100 Z"),
    OCTAGON("M 30 0 L 70 0 L 100 30 L 100 70 L 70 100 L 30 100 L 0 70 L 0 30 Z"),
    ROUND_RECT("M 10,0 L 90,0 Q 100,0 100,10 L 100,90 Q 100,100 90,100 L 10,100 Q 0,100 0,90 L 0,10 Q 0,0 10,0 Z"),
    TEARDROP("M 50,0 C 75,25 75,75 50,100 C 25,75 25,25 50,0 Z"),
    PIE("M 50,50 L 100,50 A 50,50 0 0,0 50,0 Z"),
    DEFAULT("M 0 0 H 100 V 100 H 0 Z");

    private final String svgPath;

    ShapeSVGPath(String svgPath) {
        this.svgPath = svgPath;
    }

    public String getSvgPath() {
        return svgPath;
    }

    public static String getSVGPathForShapeType(ShapeType shapeType) {
        if (shapeType == null) {
            return DEFAULT.getSvgPath();
        }
        try {
            return ShapeSVGPath.valueOf(shapeType.name()).getSvgPath();
        } catch (IllegalArgumentException e) {
            return DEFAULT.getSvgPath();
        }
    }
}
