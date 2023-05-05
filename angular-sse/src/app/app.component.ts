import { ChangeDetectionStrategy, Component, inject } from "@angular/core";
import { RouterOutlet } from "@angular/router";
import { AuthenticationService } from "./authentication/authentication.service";
import { MenubarModule } from "primeng/menubar";
import { AsyncPipe } from "@angular/common";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterOutlet, MenubarModule, AsyncPipe],
  selector: "sse-root",
  standalone: true,
  styles: [
    `
      :host {
        display: flex;
        flex-direction: column;
        height: 100%;
        width: 100%;
      }
    `,
  ],
  template: `
    <p-menubar
      [model]="$any(authenticationService.menuItems | async)"
    ></p-menubar>
    <router-outlet></router-outlet>
  `,
})
export class AppComponent {
  authenticationService: AuthenticationService = inject(AuthenticationService);

  constructor() {
    this.authenticationService.me$().subscribe();
  }
}
