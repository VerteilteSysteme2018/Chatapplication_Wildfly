import { Component, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Config, ChatUserResponse} from "../../types";



@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent implements OnInit, OnChanges {

  chatUserList: any;
  path: string;
  urlFull: string;
  configuration: Config = new Config();
  url: string = 'http://' +  this.configuration.ip + ':' + this.configuration.port + '/server-1.0-SNAPSHOT/rest/users/';

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.loadCounts();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadCounts();
  }

  deleteAllChatUsers() {
    console.info("Logout all Users");
    for (let i of this.chatUserList) {
      this.path = ('logout/' + i);
      this.urlFull = this.url + this.path;
      console.log(this.urlFull);

      this.http.delete(this.urlFull);
    }
    this.loadCounts();
  }

  loadCounts() {
    console.log("Loading Users");
    this.path = 'currentusers';
    this.http.get<ChatUserResponse>((this.url+this.path))
      .subscribe(data => {
       this.chatUserList = data;
    });
    console.log("END Loading Users"+this.chatUserList);
  }


}
