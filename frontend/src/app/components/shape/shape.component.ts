import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-shape',
  imports: [CommonModule],
  templateUrl: './shape.component.html',
  styleUrl: './shape.component.css',
})
export class ShapeComponent {
  @Input() shapeData: any; // Shape data from API
}
