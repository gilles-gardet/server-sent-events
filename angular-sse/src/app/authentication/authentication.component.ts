import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
} from "@angular/core";
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from "@angular/forms";
import { AuthenticationService } from "./authentication.service";
import { CommonModule } from "@angular/common";
import { InputTextModule } from "primeng/inputtext";
import { ButtonModule } from "primeng/button";
import { MessagesModule } from "primeng/messages";
import { Message } from "primeng/api";
import { AutoFocusModule } from "primeng/autofocus";
import { Router } from "@angular/router";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    MessagesModule,
    AutoFocusModule,
  ],
  selector: "sse-authentication",
  standalone: true,
  styles: [
    `
      :host {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .auth-form {
          display: grid;
          gap: 1rem;
          align-items: center;
          grid-template-columns: minmax(auto, 30rem);
          justify-content: center;
        }
      }
    `,
  ],
  template: `
    <h2>Authentication</h2>
    <form
      class="p-inputgroup auth-form"
      [formGroup]="formGroup"
      (ngSubmit)="onSubmit()"
    >
      <p-messages
        *ngIf="errorMessages.length"
        [(value)]="errorMessages"
        (close)="errorMessages = []"
      ></p-messages>
      <div class="p-inputgroup">
        <span class="p-inputgroup-addon">
          <i class="pi pi-user"></i>
        </span>
        <input
          pInputText
          pAutoFocus
          [autofocus]="true"
          type="text"
          formControlName="username"
          placeholder="Name"
        />
      </div>
      <div class="p-inputgroup">
        <span class="p-inputgroup-addon">
          <i class="pi pi-key"></i>
        </span>
        <input
          pInputText
          type="password"
          formControlName="password"
          placeholder="Password"
        />
      </div>
      <p-button label="Submit" type="submit"></p-button>
    </form>
  `,
})
export class AuthenticationComponent {
  authenticationService: AuthenticationService = inject(AuthenticationService);
  router: Router = inject(Router);
  changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  formGroup: FormGroup = new FormGroup({
    username: new FormControl(""),
    password: new FormControl(""),
  });
  errorMessages: Message[] = [];

  onSubmit(): void {
    this.authenticationService
      .login$(this.formGroup.value.username, this.formGroup.value.password)
      .then((response) => {
        if (response.ok) {
          return response.json();
        }
        throw new Error(`${response.status} - ${response.statusText}`);
      })
      .then((): void => {
        document.location.href = "/";
      })
      .catch((error): void => {
        this.errorMessages = [
          {
            severity: "error",
            summary: "Error",
            detail: error.message,
          },
        ];
        this.changeDetectorRef.detectChanges();
      });
  }
}
