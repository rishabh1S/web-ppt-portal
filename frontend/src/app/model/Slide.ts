import { Annotation } from './Annotation';
import { SlideElement } from './SlideElements';

export interface Slide {
  id: string;
  elements: SlideElement[];
  annotations: Annotation[];
}
