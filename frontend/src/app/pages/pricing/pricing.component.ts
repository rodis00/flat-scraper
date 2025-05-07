import { Component } from "@angular/core";
import { NavComponent } from "../../components/nav/nav.component";
import { DomSanitizer, SafeHtml } from "@angular/platform-browser";
import { ChatService } from "../../services/chat.service";
import { FormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";
import { ChatMessage } from "../../interfaces/chat-message";

@Component({
    selector: 'app-pricing',
    templateUrl: './pricing.component.html',
    styleUrl: './pricing.component.css',
    imports: [NavComponent, FormsModule, CommonModule]
})

export class PricingComponent {
    userMessage: string = '';
    responseHtml: SafeHtml | null = null;
    chatHistory: ChatMessage[] = [];
  
    constructor(
      private chatService: ChatService,
      private sanitizer: DomSanitizer
    ) {}
  
    sendMessage() {
      const trimmed = this.userMessage.trim();
      if (!trimmed) return;
  
      this.chatHistory.push({ from: 'user', content: trimmed });
  
      this.chatService.predictPrice({ message: trimmed }).subscribe({
        next: (response) => {
          this.chatHistory.push({
            from: 'bot',
            content: response.response
          });
        },
        error: (err) => {
          this.chatHistory.push({
            from: 'bot',
            content: '<p class="text-danger">Wystąpił błąd przy komunikacji z asystentem.</p>'
          });
          console.error(err);
        }
      });
  
      this.userMessage = '';
    }
  
    sanitizeHtml(content: string): SafeHtml {
      return this.sanitizer.bypassSecurityTrustHtml(content);
    }
}