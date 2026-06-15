import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ChatRequest {
  message: string;
  userId: string;
}

export interface ChatResponse {
  response: string;
  apiData: string;
  timestamp: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) {}

  sendMessage(message: string, userId: string = 'user-1'): Observable<ChatResponse> {
    const request: ChatRequest = { message, userId };
    return this.http.post<ChatResponse>(`${this.apiUrl}/message`, request);
  }

  checkHealth(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
  }
}
