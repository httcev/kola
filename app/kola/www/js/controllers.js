angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $filter, $state, dbService) {
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

    $scope.create = function() {
      $state.go("tab.task-choose-template");
    };

    $scope.sortByDue = function(task) {
        return task.due ? task.due : 'zzzzzzz'; 
    };

    $scope.reloadTasks();
})

.controller('NotesCtrl', function($scope, $stateParams, $q, $ionicPopup, $ionicLoading, dbService, rfc4122, mediaAttachment) {
  var isStep = $stateParams.stepId != null;
  var targetId = isStep ? $stateParams.stepId : $stateParams.taskId;
  var taskDocumentationProp = isStep ? "step" : "task";

  loadNotes();

  function loadNotes() {
    dbService.all("taskDocumentation").then(function(docs) {
      var filtered = [];
      var promises = [];
      angular.forEach(docs, function(doc) {
        if (doc[taskDocumentationProp] == targetId && !doc.deleted) {
          promises.push(dbService.resolveIds(doc, "taskDocumentation"));
          filtered.push(doc);
        }
      });
      $q.all(promises).finally(function() {
        $scope.notes = filtered;
      });
    });

    if (!isStep) {
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
      $scope.newNote = dbService.createTaskDocumentation(targetId, isStep);
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
      return $state.go("tab.tasks");
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

  dbService.get($stateParams.stepId, "taskStep").then(function(taskStep) {
    console.log(taskStep);
    $scope.step = taskStep;
  }, function() {
    // TODO: 404 error message and open default/main page
  });
})

.controller('TaskChooseTemplateCtrl', function($scope, $state, $q, dbService) {
  dbService.all("task").then(function(docs) {
    var filtered = [];
    var promises = [];
    angular.forEach(docs, function(doc) {
      if (doc["isTemplate"] == true && !doc.deleted) {
        //promises.push(dbService.resolveIds(doc, "task"));
        filtered.push(doc);
      }
    });
    $q.all(promises).finally(function() {
      $scope.templates = filtered;
    });
  });

  $scope.choose = function(template) {
    var params = {};
    if (template) {
      params.templateId = template.id;
    }
    console.log(params);
    $state.go("tab.task-create", params);
  }
})

.controller('TaskCreateCtrl', function($scope, $state, $stateParams, $ionicLoading, dbService) {
  console.log("--- GOT template id " + $stateParams.templateId);
  if ($stateParams.templateId) {
    dbService.get($stateParams.templateId, "task").then(function(template) {
      console.log("--- got template -> ", template);
      var task = angular.copy(template);
      delete task.id;
      task.isTemplate = false;
      task.template = template.id;
      task.isTemplate = false;
      $scope.task = task;
      console.log("--- created new task WITH template");
    }, function() {
      // TODO: 404 error message and open default/main page
    });
  }
  else {
    console.log("--- created new task without template");
    $scope.task = dbService.createTask();
  }

  dbService.all("user").then(function(docs) {
    var filtered = [];
    var currentUser = {};
    angular.forEach(docs, function(doc) {
      if (localStorage["user"] == doc.username) {
        currentUser = doc;
        filtered.push(currentUser);
      }
    });
    console.log("--- current user -> ", currentUser);
    angular.forEach(docs, function(doc) {
      if (!doc.deleted && currentUser.id != doc.id && currentUser.company && currentUser.company == doc.company) {
        filtered.push(doc);
      }
    });
    console.log("--- assignableUsers -> ", filtered);
    $scope.assignableUsers = filtered;
  });

  $scope.save = function() {
    // TODO: save new task steps too
    if ($scope.task.name) {
      console.log("--- assignee -> ", $scope.task.assignee);
      dbService.save($scope.task).then(function() {
        $ionicLoading.show({template: "Neuer Auftrag gespeichert.", duration:2000});
        $state.go("tab.tasks");
      }, function(err) {
        console.log(err);
        $ionicLoading.show({template: "Speichern fehlgeschlagen!", duration:2000});
      });
    }
  }
});
