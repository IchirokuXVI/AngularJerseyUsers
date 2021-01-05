import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'div[app-left]',
  templateUrl: './left.component.html',
  styleUrls: ['./left.component.scss']
})
export class LeftComponent implements OnInit {

  constructor(private _authServ: AuthService, private router: Router) { }

  ngOnInit(): void {
    $(() => {
      const sidebar = $('#sidebar');
      const sidebarWidth = $(sidebar).width();
      $(sidebar).width(sidebarWidth);
      const sidebarLargestSpan = Math.max(...$(sidebar).find('li a span').map(function() { return $(this).outerWidth(true)}));
      $(sidebar).on('mouseenter', (e) => {
        $(e.currentTarget).width(sidebarWidth + sidebarLargestSpan + 5);
      });

      $(sidebar).on('mouseleave', (e) => {
        $(e.currentTarget).width(sidebarWidth);
      });
    });
  }

  logout() {
    this._authServ.logout();
  }

}
