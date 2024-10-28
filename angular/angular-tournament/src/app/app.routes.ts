import { Routes } from '@angular/router';
import { PlayerListComponent } from './player-list/player-list.component';
import { PlayerDetailsComponent } from './player-details/player-details.component';

export const routes: Routes = [
    {path: 'players', component: PlayerListComponent},
    {path: '', redirectTo: 'players', pathMatch: 'full'},
    {path: 'player-details/:pseudo', component: PlayerDetailsComponent},
];
