export interface SlideElement {
  id: string;
  type: ElementType;
  content: string;
  x: number;
  y: number;
  width: number;
  height: number;
  style: {
    fontSize: number;
    color: string;
  };
}
export enum ElementType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  SHAPE = 'SHAPE',
}
