import { inject, Injectable, NgZone } from "@angular/core";
import { Observable, Subscriber } from "rxjs";
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

  public listenEvents$(uri: string): Observable<string> {
    const eventSource: CustomEventSource = new EventSource(uri, {
      withCredentials: true,
    });
    return new Observable((observer: Subscriber<string>): void => {
      eventSource.onmessage = (event: MessageEvent<string>): void => {
        this.ngZone.run(() => observer.next(event.data));
      };
      eventSource.onerror = (error: Event): void => {
        this.ngZone.run(() => observer.error(error));
      };
      eventSource.addEventListener(
        "complete",
        (event: MessageEvent<string>): void => {
          this.ngZone.run(() => observer.next(event.data));
          observer.complete();
          eventSource.close();
        }
      );
    });
  }

  public fireEvent$(
    uri: string,
    message: string,
    channel: 1 | 2 = 1
  ): Observable<unknown> {
    return this.httpClient.post(`${uri}?eventId=${channel}`, {
      message,
    });
  }
}
