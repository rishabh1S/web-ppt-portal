import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, Observable, throwError } from 'rxjs';
import { Presentation } from '../model/Presentation';

@Injectable({
  providedIn: 'root',
})
export class PresentationService {
  private apiUrl = 'http://localhost:8080/api';
  private currentPresentationId: string | null = null;

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

  downloadPresentation(id: string): Observable<Blob> {
    return this.http
      .get(`${this.apiUrl}/presentations/${id}/export`, {
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

  exportPresentation(id: string): Observable<Blob> {
    return this.http.get(`/api/presentations/${id}/export`, { responseType: 'blob' });
    
  }

  setCurrentPresentationId(id: string) {
    this.currentPresentationId = id;
  }

  getCurrentPresentationId(): string {
    return this.currentPresentationId || ''; // Return empty string if null
  }
}
