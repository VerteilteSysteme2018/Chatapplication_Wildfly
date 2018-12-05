import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {UserComponent} from "./user/user.component";
import {CountDBComponent} from "./count-db/count-db.component";
import {TraceDBComponent} from "./trace-db/trace-db.component";
import {AdminClHomeComponent} from "./admin-cl-home/admin-cl-home.component";


const routes: Routes = [
  {path: 'user', component: UserComponent},
  {path: 'count-db', component: CountDBComponent},
  {path: 'trace-db', component: TraceDBComponent},
  {path: '', component: AdminClHomeComponent},
  {path: '**', redirectTo: '/', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes,{enableTracing: true})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
