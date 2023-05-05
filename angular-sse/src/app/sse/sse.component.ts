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
import { environment } from "../../environments/environment";
import { AutoFocusModule } from "primeng/autofocus";

@Component({
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    ButtonModule,
    FormsModule,
    InputTextModule,
    MessagesModule,
    RadioButtonModule,
    ReactiveFormsModule,
    RippleModule,
    AutoFocusModule,
  ],
  selector: "sse-sse",
  standalone: true,
  styles: [
    `
      :host {
        display: flex;
        height: 100%;
        width: 100%;
        flex-direction: column;
        align-items: center;
      }

      .container {
        margin-top: 1rem;
        display: flex;
        flex-grow: 1;
        align-items: center;
        flex-direction: column;

        &__message {
          position: fixed;
          top: 0;
          left: 0;
          width: 40rem;
          padding: 1rem;
          z-index: 1;
        }

        &__listen-channel,
        &__fire-channel {
          display: flex;
          flex-direction: row;
          margin-bottom: 1rem;
          gap: 1rem;
          justify-content: center;

          &__selection {
            display: flex;
            gap: 0.5rem;
            align-items: center;
          }
        }

        &__event {
          display: flex;
          gap: 1rem;
          padding: 2rem;
        }
      }
    `,
  ],
  template: `
    <h2>Events handling</h2>
    <div class="container">
      <div class="container__message">
        <p-messages [(value)]="messages" [closable]="true"></p-messages>
      </div>
      <form [formGroup]="formGroup">
        <div class="container__listen-channel">
          <div class="container__listen-channel__selection">
            <p-radioButton
                formControlName="listenChannel"
                [value]="1"
                inputId="listen-channel-1"
            ></p-radioButton>
            <label for="listen-channel-1" class="ml-2"
            >Listen to channel 1</label
            >
          </div>
          <div class="container__listen-channel__selection">
            <p-radioButton
                formControlName="listenChannel"
                [value]="2"
                inputId="listen-channel-2"
            ></p-radioButton>
            <label for="listen-channel-2" class="ml-2"
            >Listen to channel 2</label
            >
          </div>
        </div>
        <div class="container__fire-channel">
          <div class="container__fire-channel__selection">
            <p-radioButton
                formControlName="fireChannel"
                [value]="1"
                inputId="fire-channel-1"
            ></p-radioButton>
            <label for="fire-channel-1" class="ml-2">Fire to channel 1</label>
          </div>

          <div class="container__fire-channel__selection">
            <p-radioButton
                formControlName="fireChannel"
                [value]="2"
                inputId="fire-channel-2"
            ></p-radioButton>
            <label for="fire-channel-2" class="ml-2">Fire to channel 2</label>
          </div>
        </div>
        <div class="container__event">
          <input
              type="text"
              pInputText
              pAutoFocus
              [autofocus]="true"
              formControlName="message"
          />
          <p-button
              label="Submit event"
              icon="pi pi-check"
              type="submit"
              (onClick)="onSubmit()"
          ></p-button>
        </div>
      </form>
    </div>
  `,
})
export class SseComponent implements AfterViewInit, OnInit, OnDestroy {
  protected eventService: SseService = inject(SseService);
  protected changeDetectorRef: ChangeDetectorRef = inject(ChangeDetectorRef);
  protected messages: Message[] = [];
  protected formGroup: FormGroup = this._initFormControls();
  private readonly destroy$ = new Subject<void>();

  ngOnInit(): void {
    this._listenChannelEvents$().subscribe(this._handleReceivedEvent());
    of({})
      .pipe(switchMap(() => this._listenSessionEvents$()))
      .subscribe({
        next: (event: string): void => console.log(event),
        error: (error): void => console.error(error),
        complete: (): void => console.log("complete"),
      });
    // this.eventService.fetchEvents(`${environment.apiUrl}/notifications?eventId=1`).then();
  }

  ngAfterViewInit(): void {
    this._listenSelectedChannelEvents();
  }

  get listenChannel(): AbstractControl<any, any> | null {
    return this.formGroup.get("listenChannel");
  }

  get fireChannel(): AbstractControl<any, any> | null {
    return this.formGroup.get("fireChannel");
  }

  get message(): AbstractControl<any, any> | null {
    return this.formGroup.get("message");
  }

  private _initFormControls(): FormGroup<{
    listenChannel: FormControl<1 | 2 | null>;
    message: FormControl<string | null>;
    fireChannel: FormControl<1 | 2 | null>;
  }> {
    return new FormGroup({
      listenChannel: new FormControl<1 | 2>(1),
      fireChannel: new FormControl<1 | 2>(1),
      message: new FormControl<string>("Test event message"),
    });
  }

  private _listenSelectedChannelEvents(): void {
    this.listenChannel?.valueChanges
      .pipe(
        tap(() => this._unsubscribeObservable()),
        switchMap((channel: 1 | 2) => this._listenChannelEvents$(channel))
      )
      .subscribe(this._handleReceivedEvent());
  }

  private _listenChannelEvents$(channel?: 1 | 2): Observable<string> {
    return this.eventService
      .listenEvents$(
        `${environment.apiUrl}/notifications?eventId=${channel ?? 1}`
      )
      .pipe(takeUntil(this.destroy$));
  }

  private _listenSessionEvents$(): Observable<any> {
    return this.eventService
      .listenEvents$(`${environment.apiUrl}/sse`)
      .pipe(takeUntil(this.destroy$));
  }

  private _handleReceivedEvent(): {
    next: (string) => void;
    error: (string) => void;
  } {
    return {
      next: this._handleReceivedEventOnNext(),
      error: this._handleReceivedEventOnError(),
    };
  }

  private _handleReceivedEventOnError(): (string) => void {
    return (error): void => console.error(error);
  }

  private _handleReceivedEventOnNext(): (string) => void {
    return (event: string): void => {
      const notification: Message = {
        severity: "success",
        summary: "Success",
        detail: JSON.parse(event).message,
      };
      const copy: Message[] = structuredClone(this.messages);
      copy.push(notification);
      this.messages = copy;
      this.changeDetectorRef.detectChanges();
    };
  }

  protected onSubmit(): void {
    const uri = `${environment.apiUrl}/notifications`;
    this.eventService
      .fireEvent$(uri, this.message?.value, this.fireChannel?.value)
      .subscribe();
  }

  private _unsubscribeObservable(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnDestroy(): void {
    this._unsubscribeObservable();
  }
}
