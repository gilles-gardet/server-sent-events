import { inject, Injectable, NgZone } from "@angular/core";
import { finalize, Observable, Subscriber } from "rxjs";
import { HttpClient } from "@angular/common/http";

interface CustomEventSource extends EventSource {
  oncomplete?: () => void | null | undefined;
}

@Injectable({
  providedIn: "root",
})
export class SseService {
  private readonly httpClient = inject(HttpClient);
  private readonly ngZone = inject(NgZone);
  private readonly serverUri = "/api/sse/servlet/notifications";

  public listenEvents$(channel: 1 | 2 = 1): Observable<string> {
    const eventSource: CustomEventSource = new EventSource(
      `${this.serverUri}?eventId=${channel}`,
      {
        withCredentials: true,
      }
    );
    return new Observable((observer: Subscriber<string>): void => {
      eventSource.onmessage = (event: MessageEvent<string>): void => {
        this.ngZone.run(() => observer.next(event.data));
      };
      eventSource.onerror = (error: Event): void => {
        this.ngZone.run(() => observer.error(error));
      };
      eventSource.oncomplete = (): void => eventSource.close();
    }).pipe(finalize(() => eventSource.close()));
  }

  public fireEvent$(message: string, channel: 1 | 2 = 1): Observable<unknown> {
    return this.httpClient.post(`${this.serverUri}?eventId=${channel}`, {
      message,
    });
  }
}
