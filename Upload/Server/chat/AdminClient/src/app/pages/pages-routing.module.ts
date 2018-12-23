import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';

import { PagesComponent } from './pages.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CountDBComponent } from "./count-db/count-db.component";
import { TraceDBComponent } from "./trace-db/trace-db.component";
import { UserComponent } from "./user/user.component";

const routes: Routes = [{
  path: '',
  component: PagesComponent,
  children: [
    {
      path: 'admindashboard',
      component: DashboardComponent,
    },
    {
      path: 'countdb',
      component: CountDBComponent,
    },
    {
      path: 'tracedb',
      component: TraceDBComponent,
    },
    {
      path: 'user',
      component: UserComponent,
    },
    {
      path: '',
      redirectTo: 'admindashboard',
      pathMatch: 'full',
    },
  ],
}];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PagesRoutingModule {
}
