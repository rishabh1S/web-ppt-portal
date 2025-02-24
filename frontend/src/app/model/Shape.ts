export interface ShapeStyle {
  bottomInset: number;
  fillColor: string;
  flipHorizontal: boolean;
  flipVertical: boolean;
  horizontalCentered: boolean;
  lineCap: 'flat' | 'round' | 'square';
  lineColor: string;
  lineCompound: string;
  lineDash: 'solid' | 'dashed';
  lineHeadDecoration: string;
  lineHeadLength: string;
  lineHeadWidth: string;
  lineTailDecoration: string;
  lineTailLength: string;
  lineTailWidth: string;
  lineWidth: number;
  placeholder: boolean;
  rightInset: number;
  rotation: number;
  shapeId: number;
  shapeName: string;
  shapeType: string;
  text: string;
  textAutofit: string;
  textDirection: string;
  textHeight: number;
  textPlaceholder: string;
  topInset: number;
  verticalAlignment: string;
  wordWrap: boolean;
}

export interface ShapeData {
  id: string;
  type: string;
  content: string;
  x: number;
  y: number;
  width: number;
  height: number;
  style: ShapeStyle;
}
