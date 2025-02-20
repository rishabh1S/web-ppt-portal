export interface SlideElement {
  id: string;
  type: 'TEXT' | 'IMAGE' | 'SHAPE';
  content: string;
  x: number;
  y: number;
  width: number;
  height: number;
}
