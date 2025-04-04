import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  imports: [RouterLink],
})
export class NavComponent {
  authService: AuthService = inject(AuthService);
  username: string = this.authService.getUsername();

  handleClick() {
    this.authService.logout();
  }
}
