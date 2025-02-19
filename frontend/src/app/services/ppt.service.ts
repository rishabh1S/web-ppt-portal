import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root',
})
export class PptService {
  private uploadUrl = 'http://localhost:8080/api/ppt/upload';

  constructor() {}

  async uploadPpt(file: File): Promise<any> {
    const formData = new FormData();
    formData.append('file', file, file.name);

    try {
      const response = await axios.post(this.uploadUrl, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('Upload error:', error);
      throw error;
    }
  }
}
