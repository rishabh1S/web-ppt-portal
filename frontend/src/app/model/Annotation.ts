export interface Annotation {
  id: string;
  type: AnnotationType;
  data: Record<string, any>;
}

export enum AnnotationType {
  TEXT = 'TEXT',
  FREEHAND = 'FREEHAND',
  ARROW = 'ARROW',
}
