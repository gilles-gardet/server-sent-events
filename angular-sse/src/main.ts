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
  CanActivateFn,
  PreloadAllModules,
  provideRouter,
  Router,
  Routes,
  withPreloading,
  withRouterConfig,
} from "@angular/router";
import { catchError, throwError } from "rxjs";
import { SseService } from "./app/sse/sse.service";
import { AuthenticationService } from "./app/authentication/authentication.service";
import { AuthenticationComponent } from "./app/authentication/authentication.component";
import { SseComponent } from "./app/sse/sse.component";

const authenticationGuard: CanActivateFn = (): boolean => {
  const authService: AuthenticationService = inject(AuthenticationService);
  const router: Router = inject(Router);
  if (!!authService.currentUserSubject.getValue()) {
    return true;
  }
  router.navigate(["/authentication"]).catch(console.error);
  return false;
};

const authInterceptor: HttpInterceptorFn = (
  httpRequest: HttpRequest<unknown>,
  httpHandlerFn: HttpHandlerFn
) => {
  const router: Router = inject(Router);
  const authService: AuthenticationService = inject(AuthenticationService);
  return httpHandlerFn(httpRequest).pipe(
    catchError((httpErrorResponse: HttpErrorResponse) => {
      if (httpErrorResponse.status === 401) {
        authService.logout();
        router.navigate(["/authentication"]).catch(console.error);
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
    withCredentials: true,
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

const routes: Routes = [
  {
    path: "",
    component: SseComponent,
    pathMatch: "full",
    canActivate: [authenticationGuard],
  },
  {
    path: "authentication",
    component: AuthenticationComponent,
  },
  { path: "**", redirectTo: "" },
];

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
    AuthenticationService,
    SseService,
  ],
}).catch(console.error);
