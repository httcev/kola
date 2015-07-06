angular.module('kola', ['ionic', 'kola.controllers', 'kola.services', 'kola.directives', 'ngCordova'])

.run(function($ionicPlatform, syncService) {
  // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
  // for form inputs)
  if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
    cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
  }
  if (window.StatusBar) {
    // org.apache.cordova.statusbar required
    StatusBar.styleLightContent();
  }
})

.config(function($stateProvider, $urlRouterProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider

  // setup an abstract state for the tabs directive
  .state('tab', {
    url: "/tab",
    abstract: true,
    templateUrl: "templates/tabs.html"
  })

  // Each tab has its own nav history stack:

  .state('tab.tasks', {
    url: '/tasks',
    views: {
      'tab-tasks': {
        templateUrl: 'templates/tab-tasks.html',
        controller: 'TasksCtrl'
      }
    }
  })
  .state('tab.task-detail', {
    url: '/tasks/:taskId',
    views: {
      'tab-tasks': {
        templateUrl: 'templates/task-detail.html',
        controller: 'TaskDetailCtrl'
      }
    }
  })
  .state('tab.task-notes', {
    url: '/tasks/:taskId/notes',
    views: {
      'tab-tasks': {
        templateUrl: 'templates/task-notes.html',
        controller: 'NotesCtrl'
      }
    }
  })
  .state('tab.task-step', {
    url: '/tasks/:taskId/step/:stepIndex',
    views: {
      'tab-tasks': {
        templateUrl: 'templates/task-step.html',
        controller: 'TaskStepCtrl'
      }
    }
  })
  .state('tab.chats', {
    url: '/chats',
    views: {
      'tab-chats': {
        templateUrl: 'templates/tab-chats.html',
        controller: 'ChatsCtrl'
      }
    }
  })
  .state('tab.chat-detail', {
    url: '/chats/:chatId',
    views: {
      'tab-chats': {
        templateUrl: 'templates/chat-detail.html',
        controller: 'ChatDetailCtrl'
      }
    }
  })
  .state('tab.account', {
    url: '/account',
    views: {
      'tab-account': {
        templateUrl: 'templates/tab-account.html',
        controller: 'AccountCtrl'
      }
    }
  });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/tab/tasks');

  

})

.constant("remoteDatabaseUrl", "http://koladb.httc.de/tasks")
.constant("localDatabaseName", "tasks");
