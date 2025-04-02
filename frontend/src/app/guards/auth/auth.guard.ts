import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { TokenService } from '../../services/token.service';

export const authGuard: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
) => {
  const router: Router = inject(Router);
  const tokenService: TokenService = inject(TokenService);

  const token: string | null = tokenService.getToken();

  if (token == null) {
    router.navigate(['/auth/login']);
    return false;
  }

  return true;
};
