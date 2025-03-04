import { Annotation } from './Annotation';
import { SlideElement } from './SlideElements';

export interface Slide {
  id: string;
  slideNumber:any,
  elements: SlideElement[];
   annotations: Annotation[];
}
