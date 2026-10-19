import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AskRequest { question: string; customerTier: string; }
export interface AskResponse {
  answer: string;
  confidence: number;
  citations: { documentId: string; chunkId: string; title: string }[];
  latencyMs: number;
}

@Injectable({ providedIn: 'root' })
export class QaService {
  private readonly api = 'http://localhost:8081/api';
  constructor(private http: HttpClient) {}
  ask(body: AskRequest): Observable<AskResponse> {
    return this.http.post<AskResponse>(`${this.api}/qa/ask`, body);
  }
}
