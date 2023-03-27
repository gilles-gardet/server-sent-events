import { bootstrapApplication, BrowserModule } from "@angular/platform-browser";
import { AppComponent } from "./app/app.component";
import { HttpClientModule } from "@angular/common/http";
import { importProvidersFrom } from "@angular/core";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { CommonModule } from "@angular/common";

bootstrapApplication(AppComponent, {
  providers: [
    importProvidersFrom(
      BrowserAnimationsModule,
      BrowserModule,
      CommonModule,
      HttpClientModule
    ),
  ],
}).catch((err) => console.error(err));
