import { Component, inject, Input, WritableSignal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.css',
  imports: [RouterLink],
})
export class NavComponent {
  authService: AuthService = inject(AuthService);
  username: string = this.authService.getUsername();
  @Input() searchBoxValue!: WritableSignal<string>;

  handleClick() {
    this.authService.logout();
  }
}
