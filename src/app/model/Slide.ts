export interface Slide {
  id: number;
  template: 'title' | 'content' | 'blank';
  content: {
    title?: string;
    subtitle?: string;
    body?: string;
  };
}
