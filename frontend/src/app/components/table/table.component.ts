import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { QuillModule } from 'ngx-quill';
import { FormsModule } from '@angular/forms';
import { ApplyStylesDirective } from '../../directives/apply-styles.directive';
@Component({
  selector: 'app-table',
  imports: [CommonModule, QuillModule, FormsModule, ApplyStylesDirective],
  templateUrl: './table.component.html',
  styleUrl: './table.component.css',
})
export class TableComponent {
  @Input() tableHeader: string[][] = [];
  @Input() tableData: string[][] = [];
  @Input() cellStyles: any[][] = [];
  @Input() headerCellStyles: any[][] = [];
  @Input() quillModules: any;

  // Called when a cell's content is updated by Quill
  onCellTextChange(rowIndex: number, cellIndex: number, event: any): void {
    // Get the content from the event (adjust based on your ngx-quill version)
    let newValue = event.html || event.content || '';

    // If newValue is not a string (e.g., an empty Delta) or is the default empty HTML, set it to an empty string
    if (typeof newValue !== 'string' || newValue === '<p><br></p>') {
      newValue = '';
    }

    this.tableData[rowIndex][cellIndex] = newValue;
  }

  getCellStyle(rowIndex: number, cellIndex: number): any {
    return this.cellStyles?.[rowIndex]?.[cellIndex] || {};
  }

  getHeaderCellStyle(rowIndex: number, cellIndex: number): any {
    return this.headerCellStyles?.[rowIndex]?.[cellIndex] || {};
  }

  // trackBy functions to prevent unnecessary re-renders
  trackByRow(index: number, row: any): number {
    return index;
  }

  trackByCell(index: number, cell: any): number {
    return index;
  }
}
