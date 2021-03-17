import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

import { SettingsService } from './services/settings.service';

const routes: Routes = [
  { path: '', canActivate:[SettingsService], children: [
    {
      path: '',
      redirectTo: 'home',
      pathMatch: 'full'
    },
    {
      path: 'home',
      loadChildren: () => import('./pages/home/home.module').then(m => m.HomePageModule)
    },
    {
      path: 'question',
      loadChildren: () => import('./pages/qaa/qaa.module').then( m => m.QaaModule)
    },
    {
      path: 'task',
      loadChildren: () => import('./pages/task/task.module').then( m => m.TaskModule)
    }
  ]},
  {
    path: 'settings',
    loadChildren: () => import('./pages/settings/settings.module').then( m => m.SettingsPageModule)
  },
  {
    path: 'tutorial',
    loadChildren: () => import('./pages/tutorial/tutorial.module').then(m => m.TutorialPageModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
