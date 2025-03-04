import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Presentation } from '../model/Presentation';
import { SlideElement } from '../model/SlideElements';
import { tap } from 'rxjs/operators';
import { Slide } from '../model/Slide';



@Injectable({
  providedIn: 'root',
})
export class PresentationService {
  private apiUrl = 'http://localhost:8080/api';


  constructor(private http: HttpClient) {}

  uploadPresentation(file: File): Observable<Presentation> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<Presentation>(`${this.apiUrl}/presentations`, formData)
      .pipe(
        catchError((error) => {
          console.error('Upload error:', error);
          return throwError(() => new Error('Failed to upload presentation'));
        })
      );
  }

  getPresentation(id: string): Observable<Presentation> {
    return this.http.get<Presentation>(`${this.apiUrl}/presentations/${id}`);
  }

  updateElement(id: string, content: string): Observable<SlideElement> {
    return this.http.patch<SlideElement>(`${this.apiUrl}/elements/${id}`, {
      content,
    });
  }

  downloadPresentation(id: string): Observable<Blob> {
    return this.http
      .get(`${this.apiUrl}/presentations/${id}/download`, {
        responseType: 'blob',
        headers: new HttpHeaders({
          'Content-Type': 'application/octet-stream',
        }),
      })
      .pipe(
        catchError((error) => {
          console.error('Download error:', error);
          return throwError(() => new Error('Failed to download presentation'));
        })
      );
  }

  updateSlide(updatedSlide: Slide): Observable<Slide> {
    return this.http.patch<Slide>(
      `${this.apiUrl}/elements/${updatedSlide.id}`, 
      updatedSlide
    ).pipe(
      tap(() => console.log(`Slide ${updatedSlide.id} updated successfully`)),
      catchError((error) => {
        console.error('Error updating slide:', error);
        return throwError(() => new Error('Failed to update slide'));
      })
    );
  }
  
  getImageUrl(url: string): string {
    if (!url) return '';

    if (/^(https?:\/\/)/i.test(url)) return url;

    const fileName = url.split(/[/\\]/).pop() || '';
    return `${this.apiUrl}/uploads/${fileName}`;
  }
}

