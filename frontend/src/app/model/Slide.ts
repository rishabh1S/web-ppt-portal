import { Annotation } from './Annotation';

export interface Slide {
  id: string;
  htmlContent: string;
  annotations: Annotation[];
}
