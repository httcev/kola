angular.module('kola', ['ionic', 'ngCordova', 'monospaced.elastic', 'hc.marked', 'kola.controllers', 'kola.services', 'kola.directives', 'kola.notification'])

.run(function($ionicPlatform) {
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

  .state('home', {
    url: "/home",
    controller: 'TasksCtrl',
    templateUrl: "templates/home.html"
  })

  .state('task-choose-template', {
    url: '/new-task/chooseTemplate',
    templateUrl: 'templates/task-choose-template.html',
    controller: 'TaskChooseTemplateCtrl'
  })
  .state('task-create', {
    url: '/new-task/create/:templateId',
    templateUrl: 'templates/task-create.html',
    controller: 'TaskCreateCtrl',
    cache: false
  })

  .state('task', {
    url: "/task/:taskId",
    abstract: true,
    templateUrl: "templates/task.html"
  })
  .state('task.detail', {
    url: '/detail',
    views: {
      'task-detail': {
        templateUrl: 'templates/task-detail.html',
        controller: 'TaskDetailCtrl'
      }
    }
  })
  .state('task.documentation', {
    url: '/documentation',
    views: {
      'task-documentation': {
        templateUrl: 'templates/task-documentation.html',
        controller: 'TaskDetailCtrl'
      }
    }
  })
  .state('task.questions', {
    url: '/questions',
    views: {
      'task-questions': {
        templateUrl: 'templates/task-questions.html',
        controller: 'TaskDetailCtrl'
      }
    }
  })
  .state('task.step', {
    url: '/step/:stepId',
    views: {
      'task-step': {
        templateUrl: 'templates/task-detail.html',
        controller: 'TaskDetailCtrl'
      }
    }
  })
  .state('settings', {
    url: '/settings',
    templateUrl: 'templates/settings.html',
    controller: 'SettingsCtrl'
  });

  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise('/tab/tasks');
})

.config(function($compileProvider) {
  $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|file|blob|cdvfile|content):|data:image\//);
})

.config(['markedProvider', function(markedProvider) {
  // configure markdown links (e.g. in task descriptions) to open in external app
  markedProvider.setRenderer({
    link: function(href, title, text) {
      return "<a href='#'" + (title ? " title='" + title + "'" : '') + " onclick='ionic.Platform.isWebView() ? navigator.startApp.start([[\"action\", \"VIEW\"], [\"" + href + "\"]]) :  window.open(\"" + href + "\")'>" + text + "</a>";
    }
  });
}])

.config(function($ionicConfigProvider) {
  $ionicConfigProvider.backButton.text("Zurück");
})

.constant("appName", "/* @echo APP_NAME */")
.constant("appVersion", "/* @echo APP_VERSION */")
// @if ENV == 'dev'
.constant("gcmSenderID", "686594383179")
.constant("serverUrl", "http://130.83.139.161:8080/platform");
// @endif
// @if ENV == 'prod'
.constant("gcmSenderID", "502614463037")
.constant("serverUrl", "http://plattform.kola-projekt.de");
// @endif
// @if ENV == 'demo'
.constant("gcmSenderID", "406168835925")
.constant("serverUrl", "https://demo.kola-projekt.de");
// @endif
// @if ENV == 'staging'
.constant("gcmSenderID", "686594383179")
.constant("serverUrl", "https://staging.kola-projekt.de");
// @endif
