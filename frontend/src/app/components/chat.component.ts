import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, ChatResponse } from '../services/chat.service';

interface Message {
  id: string;
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  apiData?: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit {
  messages: Message[] = [];
  inputMessage: string = '';
  isLoading: boolean = false;
  connectionStatus: string = 'checking...';
  
  private messageIdCounter = 0;

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.checkConnection();
    this.messages.push({
      id: 'welcome',
      text: 'Hello! I\'m your chat assistant. Ask me about the weather or anything else!',
      sender: 'bot',
      timestamp: new Date()
    });
  }

  checkConnection(): void {
    this.chatService.checkHealth().subscribe({
      next: (response) => {
        this.connectionStatus = 'connected';
      },
      error: (err) => {
        this.connectionStatus = 'disconnected';
        console.error('Connection error:', err);
      }
    });
  }

  sendMessage(): void {
    if (!this.inputMessage.trim()) {
      return;
    }

    const userMessage: Message = {
      id: `msg-${this.messageIdCounter++}`,
      text: this.inputMessage,
      sender: 'user',
      timestamp: new Date()
    };

    this.messages.push(userMessage);
    this.isLoading = true;

    this.chatService.sendMessage(this.inputMessage).subscribe({
      next: (response: ChatResponse) => {
        const botMessage: Message = {
          id: `msg-${this.messageIdCounter++}`,
          text: response.response,
          sender: 'bot',
          timestamp: new Date(response.timestamp),
          apiData: response.apiData
        };
        this.messages.push(botMessage);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error sending message:', err);
        const errorMessage: Message = {
          id: `msg-${this.messageIdCounter++}`,
          text: 'Sorry, there was an error processing your request. Please make sure the backend is running.',
          sender: 'bot',
          timestamp: new Date()
        };
        this.messages.push(errorMessage);
        this.isLoading = false;
      }
    });

    this.inputMessage = '';
    this.scrollToBottom();
  }

  scrollToBottom(): void {
    setTimeout(() => {
      const chatContainer = document.querySelector('.messages-container');
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    }, 100);
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
