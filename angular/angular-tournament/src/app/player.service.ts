import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Player } from './player';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  private baseURL = "http://localhost:8080/player";

  constructor(private httpClient: HttpClient) { }

  getPlayersList(): Observable<Player[]>{
    return this.httpClient.get<Player[]>(`${this.baseURL}`)
  }

  getPlayersByPseudo(pseudo: string): Observable<Player>{
    console.log("pseudo : " + pseudo)
    return this.httpClient.get<Player>(`${this.baseURL}/byPseudo/${pseudo}`);
  }

  updatePlayersByPseudo(pseudo: string, player: Player): Observable<Object>{
    return this.httpClient.put(`${this.baseURL}/byPseudo/${pseudo}`, player.score);
  }

}