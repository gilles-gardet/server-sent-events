import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
} from "@angular/core";
import { SseService } from "./sse.service";
import { Message } from "primeng/api";
import { Observable, of, Subject, switchMap, takeUntil, tap } from "rxjs";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from "@angular/forms";
import { ButtonModule } from "primeng/button";
import { InputTextModule } from "primeng/inputtext";
import { MessagesModule } from "primeng/messages";
import { RadioButtonModule } from "primeng/radiobutton";
import { RippleModule } from "primeng/ripple";
import { environment } from "../environments/environment";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  selector: "sse-session",
  standalone: true,
  template: `
    <div>session out</div>
      
  `,
})
export class SessionComponent {}
