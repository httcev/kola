angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $filter, $state, $rootScope, dbService) {
    //$scope.tasks = pouchCollection("all/by_type", { keys:["task", "homework"] }, "all/filterByTypes", { types:["task", "homework"] });
    $scope.tasks = [];

    function reloadTasks() {
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
        return task.due ? task.due : (task.lastUpdated ? 'zzzzzzz' : '0'); 
    };
    $rootScope.$on("syncFinished", function () {
      reloadTasks();
    });

    reloadTasks();
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
                reflectionAnswers[reflectionQuestion.id] = dbService.createReflectionAnswer(task.id, reflectionQuestion.id);
              }
            });

            $scope.reflectionAnswers = reflectionAnswers;
            $scope.task = task;
          });
        });
      });
    }
  }

//  $scope.$on('$ionicView.enter', function(e) {

  $scope.attachPhoto = function(note) {
    mediaAttachment.attachPhoto(note);
  }

  $scope.attachVideo = function(note) {
    mediaAttachment.attachVideo(note);
  }

  $scope.attachAudio = function(note) {
    mediaAttachment.attachAudio(note);
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
    dbService.updateLogin()
    .then(function() {
      return dbService.sync();
    })
    .then(function() {
      return $state.go("tab.tasks");
    });
  };
})

.controller('TaskDetailCtrl', function($scope, $stateParams, dbService) {
  $scope.task = {};
  
  dbService.get($stateParams.taskId, "task").then(function(task) {
    $scope.task = task;
  }, function(err) {
    // TODO: 404 error message and open default/main page
    console.log(err);
  });
})

.controller('TaskStepCtrl', function($scope, $stateParams, dbService) {
  $scope.step = {};

  dbService.get($stateParams.stepId, "taskStep").then(function(taskStep) {
    $scope.taskId = $stateParams.taskId;
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
    $state.go("tab.task-create", params);
  }
})

.controller('TaskCreateCtrl', function($scope, $state, $stateParams, $ionicLoading, $rootScope, dbService, rfc4122) {
  if ($stateParams.templateId) {
    dbService.get($stateParams.templateId, "task").then(function(template) {
      var task = angular.copy(template);
      task.id = rfc4122.v4();
      task.isTemplate = false;
      task.template = template.id;
      angular.forEach(task.steps, function(step) {
        step.id = rfc4122.v4();
      });
      setTaskAndAssignableUsers(task)
    }, function() {
      // TODO: 404 error message and open default/main page
    });
  }
  else {
    setTaskAndAssignableUsers(dbService.createTask());
  }

  function setTaskAndAssignableUsers(task) {
    dbService.all("user").then(function(docs) {
      var filtered = [];
      var currentUser = {};
      angular.forEach(docs, function(doc) {
        if (localStorage["user"] == doc.username) {
          currentUser = doc;
          if (!task.assignee) {
            task.assignee = currentUser.id;
          }
          filtered.push(currentUser);
        }
      });
      angular.forEach(docs, function(doc) {
        if (!doc.deleted && currentUser.id != doc.id && currentUser.company && currentUser.company == doc.company) {
          filtered.push(doc);
        }
      });
      $scope.assignableUsers = filtered;
      $scope.task = task;
    });
  }


  $scope.save = function() {
    if ($scope.task.name) {
      var objectsToSave = [];
      angular.forEach($scope.task.steps, function(step) {
        objectsToSave.push(step);
      });
      objectsToSave.push($scope.task);
      dbService.save(objectsToSave).then(function() {
        // TODO: this is a hack to reload tasks
        $rootScope.$broadcast("syncFinished");
        $ionicLoading.show({template: "Neuer Auftrag gespeichert.", duration:2000});
        $state.go("tab.tasks");
      }, function(err) {
        console.log(err);
        $ionicLoading.show({template: "Speichern fehlgeschlagen!", duration:2000});
      });
    }
  }
});
