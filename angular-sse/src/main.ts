import { bootstrapApplication, BrowserModule } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import {
  HttpClientXsrfModule,
  HttpErrorResponse,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest,
  HttpXsrfTokenExtractor,
  provideHttpClient,
  withInterceptors,
} from "@angular/common/http";
import { importProvidersFrom, inject } from "@angular/core";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { CommonModule } from "@angular/common";
import {
  PreloadAllModules,
  provideRouter,
  Router,
  Routes,
  withPreloading,
  withRouterConfig,
} from "@angular/router";
import { catchError, throwError } from "rxjs";
import { SseService } from "./app/sse.service";
import {SessionComponent} from "./app/session.component";

const routes: Routes = [
  { path: "", component: AppComponent, pathMatch: "full" },
  { path: "expired", component: SessionComponent, pathMatch: "full" },
  { path: "invalid", component: SessionComponent, pathMatch: "full" },
  { path: "**", redirectTo: "" },
];

const authInterceptor: HttpInterceptorFn = (
  httpRequest: HttpRequest<unknown>,
  httpHandlerFn: HttpHandlerFn
) => {
  const router: Router = inject(Router);
  return httpHandlerFn(httpRequest).pipe(
    catchError((httpErrorResponse: HttpErrorResponse) => {
      if (httpErrorResponse.status === 401) {
        router.navigate(["/login"]).catch(console.error);
      }
      return throwError(() => httpErrorResponse);
    })
  );
};

const xhrInterceptor: HttpInterceptorFn = (
  httpRequest: HttpRequest<unknown>,
  httpHandlerFn: HttpHandlerFn
) => {
  const xhrHttpRequest: HttpRequest<unknown> = httpRequest.clone({
    headers: httpRequest.headers.set("X-Requested-With", "XMLHttpRequest"),
  });
  return httpHandlerFn(xhrHttpRequest);
};

const xsrfInterceptor: HttpInterceptorFn = (
  httpRequest: HttpRequest<unknown>,
  httpHandlerFn: HttpHandlerFn
) => {
  const httpXsrfTokenExtractor: HttpXsrfTokenExtractor = inject(
    HttpXsrfTokenExtractor
  );
  const xsrfToken: string = httpXsrfTokenExtractor.getToken() as string;
  if (xsrfToken !== null && !httpRequest.headers.has("X-XSRF-TOKEN")) {
    httpRequest = httpRequest.clone({
      withCredentials: true,
    });
  }
  return httpHandlerFn(httpRequest);
};

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      BrowserAnimationsModule,
      BrowserModule,
      CommonModule,
      HttpClientXsrfModule.withOptions({
        cookieName: "XSRF-TOKEN",
        headerName: "X-XSRF-TOKEN",
      })
    ),
    provideRouter(
      routes,
      withPreloading(PreloadAllModules),
      withRouterConfig({
        onSameUrlNavigation: "reload",
      })
    ),
    provideHttpClient(
      withInterceptors([authInterceptor, xhrInterceptor, xsrfInterceptor])
    ),
    SseService,
  ],
}).catch(console.error);
