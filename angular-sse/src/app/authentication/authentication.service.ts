import { BehaviorSubject, filter, Observable, tap } from "rxjs";
import { environment } from "../../environments/environment";
import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { MenuItem } from "primeng/api";
import { HttpClient, HttpHeaders } from "@angular/common/http";

export interface Principal {
  enabled: boolean;
  authorities?: AuthoritiesEntity[] | null;
  authDate: string;
}
export interface AuthoritiesEntity {
  authority: string;
}

const defaultMenuItems: MenuItem[] = [
  {
    label: "Server-Sent Events",
    icon: "pi pi-fw pi-sync",
    routerLink: ["/"],
    disabled: true,
    tooltip: "You need to login first",
  },
];

export class AuthenticationService {
  private http: HttpClient = inject(HttpClient);
  private router: Router = inject(Router);
  currentUserSubject = new BehaviorSubject<unknown>(null);
  menuItems = new BehaviorSubject<MenuItem[]>(defaultMenuItems);
  logoutMenuItem: MenuItem = {
    label: "Logout",
    icon: "pi pi-fw pi-sign-out",
    command: () => this.logout(),
    tooltip: "Logout",
  };

  login$(username: string, password: string): Promise<any> {
    return fetch(
      `${environment.apiUrl}/authentication?username=${username}&password=${password}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
  }

  me$(): Observable<unknown> {
    return this.http.get(`${environment.apiUrl}/me`).pipe(
      filter((user: unknown) => !!user),
      tap((user: unknown): void => {
        this.currentUserSubject.next(user);
        this.menuItems.next([this.logoutMenuItem]);
        this.router.navigate(["/"]).catch(console.error);
      })
    );
  }

  logout(): void {
    this.currentUserSubject.next(undefined);
    this.menuItems.next(defaultMenuItems);
    this.router.navigate(["/authentication"]).catch(console.error);
    this.http.get(`${environment.apiUrl}/logout`).subscribe();
  }
}
