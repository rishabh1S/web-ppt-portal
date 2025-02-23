export interface SlideElement {
  id: string;
  type: ElementType;
  content: string;
  x: number;
  y: number;
  width: number;
  height: number;
  style: {
    cellStyles: any;
    fontSize: number;
    color: string;
    fillColor: string;
    strokeColor: string;
    strokeWidth: number;
  };
}
export enum ElementType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  SHAPE = 'SHAPE',
  TABLE = 'TABLE',
}
