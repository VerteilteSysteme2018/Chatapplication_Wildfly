import {Component, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Count, CountResponse, Config} from "../../types";
import {HttpClient} from "@angular/common/http";



@Component({
  selector: 'app-count-db',
  templateUrl: './count-db.component.html',
  styleUrls: ['./count-db.component.scss']
})

export class CountDBComponent implements OnInit, OnChanges {

  countList: Count[] = [];
  configuration: Config = new Config();
  url: string = 'http://' +  this.configuration.ip + ':' + this.configuration.port + '/server-1.0-SNAPSHOT/graphql';

  constructor(private http: HttpClient) {
  }

  ngOnInit() {
    this.loadCounts();
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadCounts();
  }

  deleteAllCounts() {
    console.info("Delete all Counts");
    const data = {query: "mutation{clearCount}"};
    this.http.post(this.url, JSON.stringify(data)).subscribe(res =>
      console.log("delete Counts" + res)
    );
    this.loadCounts();
  }

  loadCounts() {
    console.log("Loading CountDB");
    const query = ("query={allCount{id username counting}}");
    this.http.get<CountResponse>(this.url + "?" + encodeURI(query))
      .subscribe(res => {
        this.countList = res.data.allCount;
        console.log(this.countList);
      });
    console.log("END Loading CountDB");
  }

}
