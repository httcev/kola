angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, dbService) {
    //$scope.tasks = pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] });
    $scope.tasks = [];

    $scope.reloadTasks = function() {
      dbService.all("task").then(function(docs) {
        var filtered = [];
        angular.forEach(docs, function(doc) {
          if (!doc["isTemplate"]) {
            filtered.push(doc);
          }
        });
        $scope.tasks = filtered;
      });
    }
    /*
    pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] }).then(function(result) {
      $scope.tasks = result;
      console.log($scope.tasks);  
    });
*/

    $scope.remove = function(task) {
      task.deleted = true;
      dbService.save(task);
    };

    $scope.reloadTasks();
})

.controller('NotesCtrl', function($scope, $stateParams, $q, $ionicPopup, dbService, rfc4122, modalDialog, mediaAttachment) {
  loadNotes();

  function loadNotes() {
    dbService.all("taskDocumentation").then(function(docs) {
      var filtered = [];
      angular.forEach(docs, function(doc) {
        if (doc.task == $stateParams.taskId && !doc.deleted) {
          dbService.resolveIds(doc, "taskDocumentation");
          filtered.push(doc);
        }
      });
      $scope.notes = filtered;
    });
  }

//  $scope.$on('$ionicView.enter', function(e) {
/*  
    pouchCollection("notes/by_task", { key:$stateParams.taskId   }, "notes/filterByTask", { taskId:$stateParams.taskId }).then(function(result) {
      $scope.notes = result;
    });
*/    
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
    if (note === $scope.newNote && !$scope.newNote.text && !$scope.newNote.attachments) {
      $scope.newNote = false;
    }
    else {
      $ionicPopup.confirm({
        title: 'Dokumentation löschen',
        template: 'Soll diese Dokumentation wirklich gelöscht werden?'
      }).then(function(result) {
        if(result) {
          if (note === $scope.newNote) {
            $scope.newNote = false;
          }
          else {
            note.deleted = true;
            dbService.save(note).then(function() {
              loadNotes();
            }, function(err) {
              alert("Speichern fehlgeschlagen");
              console.log(err);
            });
          }
        }
     });
    }
  };

  $scope.save = function(note) {
    var objectsToSave = [];
    angular.forEach(note.attachments, function(attachment) {
      objectsToSave.push(attachment);
    });
    objectsToSave.push(note);

    dbService.save(objectsToSave).then(function() {
      $scope.newNote = false;
      loadNotes();
    }, function(err) {
      alert("Speichern fehlgeschlagen!");
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
    $scope.newNote = dbService.createTaskDocumentation($stateParams.taskId);
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

.controller('AccountCtrl', function($scope, $state, dbService) {
  $scope.profile = { name:localStorage["user"], password:localStorage["password"] };

  $scope.updateProfile = function() {
    localStorage["user"] = $scope.profile.name;
    localStorage["password"] = $scope.profile.password;
    dbService.initSync().then(function() {
      return $state.transitionTo("tab.tasks", {}, { reload: true, inherit: false, notify: true });  
    });
  };
})

.controller('TaskDetailCtrl', function($scope, $stateParams, dbService) {
  $scope.task = {};
  
  dbService.get($stateParams.taskId, "task").then(function(task) {
    $scope.task = task;
    console.log(task);
  }, function() {
    // TODO: 404 error message and open default/main page
  });
})

.controller('TaskStepCtrl', function($scope, $stateParams, dbService) {
  $scope.step = {};

  dbService.get($stateParams.taskId, "task").then(function(task) {
    console.log(task);
    $scope.step = task.steps[$stateParams.stepIndex];
  }, function() {
    // TODO: 404 error message and open default/main page
  });
});
