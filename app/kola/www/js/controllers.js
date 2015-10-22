angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $state, $rootScope, dbService) {
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

    $scope.toggleDone = function(task) {
      task.done = !task.done;
      dbService.save(task);
    }
/*
    $scope.remove = function(task) {
      task.deleted = true;
      dbService.save(task);
    };
*/
    $scope.create = function() {
      $state.go("tab.task-choose-template");
    };

    $scope.sortByDue = function(task) {
        return task.due ? task.due : (task.lastUpdated ? 'zzzzzzz' : 0); 
    };
    $rootScope.$on("syncFinished", function () {
      reloadTasks();
    });
    reloadTasks();
})

.controller('TaskDetailCtrl', function($scope, $stateParams, $q, $ionicPopup, $ionicLoading, dbService, mediaAttachment) {
  $scope.isStep = $stateParams.stepId != null;
  var targetId = $scope.isStep ? $stateParams.stepId : $stateParams.taskId;
  var taskDocumentationProp = $scope.isStep ? "step" : "task";
  var table = $scope.isStep ? "taskStep" : "task";

  loadTargetAndDocumentations();

  function loadTargetAndDocumentations() {
    // load documentation
    dbService.all("taskDocumentation").then(function(docs) {
      var documentations = [];
      var promises = [];
      angular.forEach(docs, function(doc) {
        if (doc[taskDocumentationProp] == targetId && !doc.deleted) {
          promises.push(dbService.resolveIds(doc, "taskDocumentation"));
          documentations.push(doc);
        }
      });
      $q.all(promises).finally(function() {
        if (!$scope.isStep) {
          // load reflection answers and questions
          promises = [];
          dbService.all("reflectionAnswer").then(function(allReflectionAnswers) {
            var reflectionAnswers = {};
            angular.forEach(allReflectionAnswers, function(doc) {
              if (doc.task == $stateParams.taskId && !doc.deleted) {
                promises.push(dbService.resolveIds(doc, "reflectionAnswer").then(function() {
                  reflectionAnswers[doc.question] = doc;
                }));
              }
            });
            $q.all(promises).finally(function() {
              loadTarget(documentations, reflectionAnswers);
            });
          });
        }
        else {
          loadTarget(documentations);
        }
      });
    });
  }

  function loadTarget(documentations, reflectionAnswers) {
    dbService.get(targetId, table).then(function(target) {
      angular.forEach(target.reflectionQuestions, function(reflectionQuestion) {
        if (!reflectionAnswers[reflectionQuestion.id]) {
          reflectionAnswers[reflectionQuestion.id] = dbService.createReflectionAnswer(target.id, reflectionQuestion.id);
        }
      });
      $scope.newDocumentation = false;
      $scope.documentations = documentations;
      $scope.reflectionAnswers = reflectionAnswers;
      $scope.taskOrStep = target;
    }, function(err) {
      // TODO: 404 error message and open default/main page
      console.log(err);
    });
  }

//  $scope.$on('$ionicView.enter', function(e) {

  $scope.attachPhoto = function(documentation) {
    mediaAttachment.attachPhoto(documentation);
  }

  $scope.attachVideo = function(documentation) {
    mediaAttachment.attachVideo(documentation);
  }

  $scope.attachAudio = function(documentation) {
    mediaAttachment.attachAudio(documentation);
  }

  $scope.attachChosenMedia = function(documentation) {
    mediaAttachment.attachChosenMedia(documentation);
  }

  $scope.removeDocumentation = function(documentation) {
    $ionicPopup.confirm({
      title: 'Dokumentation löschen',
      template: 'Soll diese Dokumentation wirklich gelöscht werden?'
    }).then(function(result) {
      if(result) {
        documentation.deleted = true;
        dbService.save(documentation).then(function() {
          loadTargetAndDocumentations();
        }, function(err) {
          alert("Löschen fehlgeschlagen");
          console.log(err);
        });
      }
    });
  };

  $scope.addDocumentation = function() {
    $scope.newDocumentation = dbService.createTaskDocumentation(targetId, $scope.isStep);
  }

  $scope.saveDocumentation = function(documentation) {
    var objectsToSave = [];
    angular.forEach(documentation.attachments, function(attachment) {
      objectsToSave.push(attachment);
    });
    objectsToSave.push(documentation);
    documentation._saving = true;
    dbService.save(objectsToSave).then(function() {
      loadTargetAndDocumentations();
      $ionicLoading.show({template: "Dokumentation gespeichert", duration:2000});
    }, function(err) {
      console.log(err);
      $ionicLoading.show({template: "Speichern fehlgeschlagen!", duration:2000});
    }).finally(function() {
      documentation._saving = false;
    });
  };

  $scope.saveReflectionAnswer = function(reflectionAnswer, form) {
    dbService.save(reflectionAnswer).then(function() {
      form.$dirty = false;
      loadTargetAndDocumentations();
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
    dbService.updateLogin().then(function() {
      return dbService.sync();
    }).then(function() {
      return $state.go("tab.tasks");
    });
  };
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
      delete task.creator
      angular.forEach(task.steps, function(step) {
        step.id = rfc4122.v4();
      });
      setTaskAndAssignableUsers(task)
    }, function(err) {
      // TODO: 404 error message and open default/main page
      console.log(err);
    });
  }
  else {
    dbService.createTask().then(function(task) {
      setTaskAndAssignableUsers(task);
    });
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

  $scope.removeReflectionQuestion = function(index) {
    if (index > -1 && $scope.task && $scope.task.reflectionQuestions && $scope.task.reflectionQuestions.length > index) {
      $scope.task.reflectionQuestions.splice(index, 1);
    }
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
        $ionicLoading.show({template: "Neuer Arbeitsauftrag gespeichert.", duration:2000});
        $state.go("tab.tasks");
      }, function(err) {
        console.log(err);
        $ionicLoading.show({template: "Speichern fehlgeschlagen!", duration:2000});
      });
    }
  }
});
