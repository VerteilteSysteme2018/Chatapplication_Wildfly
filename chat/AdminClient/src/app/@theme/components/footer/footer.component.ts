import { Component } from '@angular/core';

@Component({
  selector: 'ngx-footer',
  styleUrls: ['./footer.component.scss'],
  template: `
    <span class="created-by">Die Wanderer 2018</span>
    <div class="socials">
      <!--<a href="https://github.com/Johannes-Knippel" target="_blank" class="ion ion-social-github"></a>
      <a href="https://facebook.com" target="_blank" class="ion ion-social-facebook"></a>
      <a href="https://twitter.com" target="_blank" class="ion ion-social-twitter"></a>
      <a href="https://linkedin.com" target="_blank" class="ion ion-social-linkedin"></a> -->
    </div>
  `,
})
export class FooterComponent {
}
