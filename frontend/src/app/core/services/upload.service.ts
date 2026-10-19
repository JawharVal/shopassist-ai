import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UploadDocumentRequest {
  id: string;
  title: string;
  sourceType: string;
  sourceUri: string;
  content: string;
}

@Injectable({ providedIn: 'root' })
export class UploadService {
  private readonly api = 'http://localhost:8081/api';
  constructor(private http: HttpClient) {}
  bootstrap(): Observable<any> { return this.http.post(`${this.api}/ingest/bootstrap`, {}); }
  upload(body: UploadDocumentRequest): Observable<any> { return this.http.post(`${this.api}/ingest/upload`, body); }
}
