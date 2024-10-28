import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PlayerService } from '../player.service';
import { Player } from '../player';


@Component({
  selector: 'app-player-details',
  standalone: true,
  imports: [],
  templateUrl: './player-details.component.html',
  styleUrl: './player-details.component.css'
})
export class PlayerDetailsComponent {
  pseudo!: string
  player!: Player
  constructor(private route: ActivatedRoute, private playerService: PlayerService) { }

  ngOnInit(): void {
    this.pseudo = this.route.snapshot.params['pseudo'];

    this.player = new Player();
    this.playerService.getPlayersByPseudo(this.pseudo).subscribe( data => {
      this.player = data;
    });
  }
}
