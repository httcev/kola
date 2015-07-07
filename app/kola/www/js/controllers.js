angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, pouchCollection) {
    //$scope.tasks = pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] });
    $scope.tasks = [];
    pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] }).then(function(result) {
      $scope.tasks = result;
      console.log($scope.tasks);  
    });

    $scope.remove = function(task) {
      $scope.tasks.$remove(task);
    };
})

.controller('NotesCtrl', function($scope, $stateParams, $ionicPopup, dbService, pouchCollection, rfc4122, modalDialog, mediaAttachment) {
//  $scope.$on('$ionicView.enter', function(e) {
    pouchCollection("notes/by_task", { key:$stateParams.taskId   }, "notes/filterByTask", { taskId:$stateParams.taskId }).then(function(result) {
      $scope.notes = result;
    });
/*
    $scope.notes.$db.query("notes/by_task", {
      key: $stateParams.taskId,
      include_docs: true
    }).then(function(res) {
      console.log("---resu:");
      console.log(res);
    }).catch(function (err) {
      console.log(err);
    });
    */
//  });

  modalDialog.createModalDialog($scope, "templates/modal-note-edit.html");

  $scope.attachPhoto = function(note) {
    mediaAttachment.attachPhoto(note);
  }
  $scope.attachVideo = function() {
    mediaAttachment.attachVideo(note);
  }

  $scope.remove = function(note) {
    if (note === $scope.newNote && !$scope.newNote.text && !$scope.newNote._attachments) {
      $scope.newNote = false;
    }
    else {
      $ionicPopup.confirm({
        title: 'Notiz löschen',
        template: 'Soll diese Notiz wirklich gelöscht werden?'
      }).then(function(result) {
        if(result) {
          if (note === $scope.newNote) {
            $scope.newNote = false;
          }
          else {
            $scope.notes.$remove(note);
          }
        }
     });
    }
  };

  $scope.save = function(note) {
    dbService.localDatabase.put(note).then(function() {
      console.log("-----------------------");
      console.log($scope.notes);
      $scope.newNote = false;
    });
  };

 $scope.addNote = function() {
    //$scope.newNote = {};
    //$scope.openModal();
    /*
    dbService.localDatabase.put({"_id":rfc4122.v4(), "type":"note", "taskId":$stateParams.taskId}).then(function() {
      console.log("-----------------------");
      console.log($scope.notes);
    });
*/
    dbService.getProfile().then(function(profile) {
      $scope.newNote = { "_id":rfc4122.v4(), "type":"note", "taskId":$stateParams.taskId, "created_at":new Date(), "creator":profile.name };
    });
  };
})

.controller('ChatsCtrl', function($scope, Chats) {
  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //
  //$scope.$on('$ionicView.enter', function(e) {
  //});
  
  $scope.chats = Chats.all();
  $scope.remove = function(chat) {
    Chats.remove(chat);
  }
})

.controller('ChatDetailCtrl', function($scope, $stateParams, Chats) {
  $scope.chat = Chats.get($stateParams.chatId);
})

.controller('AccountCtrl', function($scope, dbService) {
  $scope.profile = {};

  dbService.getProfile().then(function (profile) {
    console.log("--- init profile to:");
    console.log(profile);
    $scope.profile.name = profile.name;
    $scope.profile.password = profile.password;
  });

  $scope.updateProfile = function() {
    console.log("--- update profile: name="+$scope.profile.name+", pass="+$scope.profile.password);
    dbService.setProfile($scope.profile);
  };
})

.controller('TaskDetailCtrl', function($scope, $stateParams, dbService) {
  dbService.localDatabase.get($stateParams.taskId).then(function(result) {
    $scope.task = result;
  });
})

.controller('TaskStepCtrl', function($scope, $stateParams, dbService) {
  dbService.localDatabase.get($stateParams.taskId).then(function(result) {
    $scope.step = result.steps[$stateParams.stepIndex];
  });
});