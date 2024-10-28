import { Component } from '@angular/core';
import { Player } from '../player';
import {PlayerService} from '../player.service'
import { Router } from '@angular/router';

@Component({
  selector: 'app-player-list',
  standalone: true,
  imports: [],
  templateUrl: './player-list.component.html',
  styleUrl: './player-list.component.css'
})
export class PlayerListComponent {
  players!: Player[];

  constructor(private playerService : PlayerService, private router: Router){}

  ngOnInit(): void {
    this.getPlayers()
  }

  private getPlayers(){
    this.playerService.getPlayersList().subscribe(data => this.players = data);
  }

  playerDetails(pseudo: string){
    console.log("detail pseudo : " + pseudo)
    this.router.navigate(['player-details', pseudo]);
  }
}
