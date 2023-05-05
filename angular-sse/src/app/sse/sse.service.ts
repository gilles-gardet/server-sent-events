import { inject, NgZone } from "@angular/core";
import { Observable, Subscriber } from "rxjs";
import { HttpClient } from "@angular/common/http";
import {
  EventSourceMessage,
  EventStreamContentType,
  fetchEventSource,
} from "@microsoft/fetch-event-source";

interface CustomEventSource extends EventSource {
  oncomplete?: () => void | null | undefined;
}

class RetriableError extends Error {}
class FatalError extends Error {}

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

  public fetchEvents(uri: string): Promise<void> {
    return fetchEventSource(uri, {
      async onopen(response: Response): Promise<void> {
        if (
          response.ok &&
          response.headers.get("content-type") === EventStreamContentType
        ) {
          return;
        } else if (
          response.status >= 400 &&
          response.status < 500 &&
          response.status !== 429
        ) {
          throw new FatalError();
        }
        throw new RetriableError();
      },
      onmessage(eventSourceMessage: EventSourceMessage): void {
        if (eventSourceMessage.event === "FatalError") {
          throw new FatalError(eventSourceMessage.data);
        }
        console.log(eventSourceMessage.data);
      },
      onclose(): void {
        throw new RetriableError();
      },
      onerror(error: unknown): void {
        if (error instanceof FatalError) {
          throw error;
        }
      },
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
