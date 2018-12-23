import { NgModule } from '@angular/core';


import { ThemeModule } from '../../@theme/theme.module';
import { UserComponent } from "./user.component";


@NgModule({
  imports: [
    ThemeModule,
  ],
  declarations: [
    UserComponent,
  ],
})
export class UserModule { }
