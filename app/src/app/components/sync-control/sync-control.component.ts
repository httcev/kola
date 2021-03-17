import { Component } from '@angular/core';
import { DatabaseService } from '../../services/database.service';
import { NetworkService } from '../../services/network.service';

@Component({
  selector: 'sync-control',
  templateUrl: './sync-control.component.html',
  styleUrls: ['./sync-control.component.scss']
})
export class SyncControlComponent {
  constructor(
    private databaseService: DatabaseService,
    private networkService: NetworkService
  ) { }
}
