export interface SlideElement {
  id: string;
  type: ElementType;
  content: string;
  x: number;
  y: number;
  width: number;
  height: number;
  style: {
    lineDash: string;
    lineCap: any;
    lineWidth: any;
    lineColor: any;
    autoNumberingScheme: any;
    bullet: any;
    fontSize: number; // Font size in points
    fontColor: string; // Font color in hex format
    bold?: boolean; // Whether the text is bold
    italic?: boolean; // Whether the text is italic
    underline?: boolean; // Whether the text is underlined
    strikethrough?: boolean; // Whether the text is strikethrough
    textAlign?: string; // Text alignment (left, right, center, justify)
    indent?: number; // Text indentation in pixels
    leftMargin?: number; // Left margin in pixels
    rightMargin?: number; // Right margin in pixels
    spacingBefore?: number; // Spacing before the paragraph in pixels
    spacingAfter?: number; // Spacing after the paragraph in pixels
    verticalAlignment?: string; // Vertical alignment (baseline, subscript, superscript)
    highlightColor?: string; // Background highlight color in hex format

    // Shape-specific styles
    fillColor?: string; // Fill color for shapes in hex format
    strokeColor?: string; // Stroke color for shapes in hex format
    strokeWidth?: number; // Stroke width for shapes in pixels

    // Table-specific styles
    cellStyles?: any; // Styles for individual table cells (if applicable)
  };
}
export enum ElementType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  SHAPE = 'SHAPE',
  TABLE = 'TABLE',
}
