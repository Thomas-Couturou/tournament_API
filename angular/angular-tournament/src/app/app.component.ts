import { Component } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { PlayerListComponent } from './player-list/player-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterModule, PlayerListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Tournament';
}
