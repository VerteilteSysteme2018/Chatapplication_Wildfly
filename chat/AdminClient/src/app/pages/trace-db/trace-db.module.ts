import { NgModule } from '@angular/core';


import { ThemeModule } from '../../@theme/theme.module';
import { TraceDBComponent } from "./trace-db.component";

@NgModule({
  imports: [
    ThemeModule,
  ],
  declarations: [
    TraceDBComponent,
  ],
})
export class TraceDBModule { }
