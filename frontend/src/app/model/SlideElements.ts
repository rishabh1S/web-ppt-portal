export interface SlideElement {
  id: string;
  type: ElementType;
  content: {
    text: string;
    url: string;
    svgPath: string;
    tableData: string[][];
    tableHeader: string[][];
  };
  x: number;
  y: number;
  width: number;
  height: number;
  style: any;
}
export enum ElementType {
  TEXT = 'TEXT',
  IMAGE = 'IMAGE',
  SHAPE = 'SHAPE',
  TABLE = 'TABLE',
}
