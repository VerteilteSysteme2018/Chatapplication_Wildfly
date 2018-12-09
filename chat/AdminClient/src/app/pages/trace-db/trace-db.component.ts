import { Component, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { Trace, TraceResponse, Config } from "../../types";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-trace-db',
  templateUrl: './trace-db.component.html',
  styleUrls: ['./trace-db.component.scss']
})
export class TraceDBComponent implements OnInit, OnChanges {

  traceList: Trace[] = [];
  configuration: Config = new Config();
  url: string = 'http://' +  this.configuration.ip + ':' + this.configuration.port + '/server-1.0-SNAPSHOT/graphql';

  constructor(private http: HttpClient)  { }

  ngOnInit() {
    this.loadTraces();
  }

  deleteAllTraces() {
    console.info("Delete all Traces");
    const data = {query: "mutation{clearTrace}"};
    this.http.post(this.url, JSON.stringify(data)).subscribe(res =>
      console.log("delete Trace" + res)
    );
    this.loadTraces();
  }

  loadTraces() {
    console.log("Load all Traces");
    const query = "query={allTrace{id username clientthread message}}";
    this.http.get<TraceResponse>(this.url + "?" + encodeURI(query))
      .subscribe(res => {
      this.traceList = res.data.allTrace;
      console.log(this.traceList);
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.loadTraces();
  }

}




