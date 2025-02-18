import { Injectable } from '@angular/core';
import Quill from 'quill';

@Injectable({
  providedIn: 'root',
})
export class EditorService {
  private activeEditor: Quill | null = null;

  setActiveEditor(editor: Quill): void {
    this.activeEditor = editor;
  }

  getActiveEditor(): Quill | null {
    return this.activeEditor;
  }
}
