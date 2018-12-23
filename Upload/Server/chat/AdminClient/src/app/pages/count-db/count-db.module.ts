import { NgModule } from '@angular/core';


import { ThemeModule } from '../../@theme/theme.module';
import { CountDBComponent } from "./count-db.component";

@NgModule({
  imports: [
    ThemeModule,
  ],
  declarations: [
    CountDBComponent,
  ],
})
export class CountDBModule { }
