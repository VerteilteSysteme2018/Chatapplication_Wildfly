import { Component, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import {HttpClient} from "@angular/common/http";


@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit {

  constructor(private http: HttpClient) { }

  ngOnInit() {
  }

}
