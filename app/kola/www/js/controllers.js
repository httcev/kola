angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $filter, dbService) {
    //$scope.tasks = pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] });
    $scope.tasks = [];

    $scope.reloadTasks = function() {
      dbService.all("task").then(function(docs) {
        var filtered = [];
        angular.forEach(docs, function(doc) {
          if (!doc["isTemplate"] && !doc.deleted) {
            filtered.push(doc);
          }
        });
        $scope.tasks = filtered;
      });
    }

    $scope.markDone = function(task) {
      task.done = true;
      dbService.save(task);
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

.controller('NotesCtrl', function($scope, $stateParams, $q, $ionicPopup, $ionicLoading, dbService, rfc4122, mediaAttachment) {
  loadNotes();

  function loadNotes() {
    dbService.all("taskDocumentation").then(function(docs) {
      var filtered = [];
      var promises = [];
      angular.forEach(docs, function(doc) {
        if (doc.task == $stateParams.taskId && !doc.deleted) {
          promises.push(dbService.resolveIds(doc, "taskDocumentation"));
          filtered.push(doc);
        }
      });
      $q.all(promises).finally(function() {
        $scope.notes = filtered;
      });
    });
    /*
    dbService.all("reflectionQuestion").then(function(docs) {
      var filtered = [];
      angular.forEach(docs, function(doc) {
        if (doc.task == $stateParams.taskId && !doc.deleted) {
          filtered.push(doc);
        }
      });
      console.log("--- reflectionQuestions -> ", filtered);
      $scope.reflectionQuestions = filtered;
    });
    */
    dbService.all("reflectionAnswer").then(function(allReflectionAnswers) {
      var reflectionAnswers = {};
      var promises = [];
      angular.forEach(allReflectionAnswers, function(doc) {
        if (doc.task == $stateParams.taskId && !doc.deleted) {
          promises.push(dbService.resolveIds(doc, "reflectionAnswer").then(function() {
            reflectionAnswers[doc.question] = doc;
          }));
        }
      });
      $q.all(promises).finally(function() {
        dbService.get($stateParams.taskId, "task").then(function(task) {
          angular.forEach(task.reflectionQuestions, function(reflectionQuestion) {
            if (!reflectionAnswers[reflectionQuestion.id]) {
              console.log("--- creating stub answer");
              reflectionAnswers[reflectionQuestion.id] = dbService.createReflectionAnswer(task.id, reflectionQuestion.id);
            }
          });

          console.log("--- reflectionAnswers -> ", reflectionAnswers);
          $scope.reflectionAnswers = reflectionAnswers;
          $scope.task = task;
        });
      });
    });
  }

//  $scope.$on('$ionicView.enter', function(e) {
/*  
    pouchCollection("notes/by_task", { key:$stateParams.taskId   }, "notes/filterByTask", { taskId:$stateParams.taskId }).then(function(result) {
      $scope.notes = result;
    });
*/    

  $scope.attachPhoto = function(note) {
    mediaAttachment.attachPhoto(note);
  }

  $scope.attachVideo = function(note) {
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
    $scope.newNoteSaving = true;
    dbService.save(objectsToSave).then(function() {
      $scope.newNote = false;
      loadNotes();
    }, function(err) {
      $ionicLoading.show({template: "Speichern fehlgeschlagen!", duration:2000});
    }).finally(function() {
      $scope.newNoteSaving = false;
    });
  };

 $scope.addNote = function() {
    if (!$scope.newNote) {
      $scope.newNote = dbService.createTaskDocumentation($stateParams.taskId);
    }
  };

  $scope.saveReflectionAnswer = function(reflectionAnswer, form) {
    dbService.save(reflectionAnswer).then(function() {
      form.$dirty = false;
      $ionicLoading.show({template: "Antwort gespeichert", duration:2000});
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
