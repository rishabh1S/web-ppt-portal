import { Slide } from './Slide';

export interface Presentation {
  id: string;
  name: string;
  slides: Slide[];
  width: number;
  height: number;
}
