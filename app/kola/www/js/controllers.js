angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $state, $rootScope, dbService) {
	function reload() {
		dbService.all("task").then(function(docs) {
			var filtered = [];
			angular.forEach(docs, function(doc) {
				if (!doc["isTemplate"] && !doc.deleted) {
					filtered.push(doc);
				}
			});
			$scope.tasks = filtered;
		}, function() {
			// this error handling is needed on the first start of the app. dbService initialization is rejected in this case.
			$scope.tasks = [];
		});
	}

	$scope.toggleDone = function(task) {
		task.done = !task.done;
		dbService.save(task);
	}

	$scope.create = function() {
		$state.go("task-choose-template");
	};

	$scope.sortByDue = function(task) {
		return task.due ? task.due : (task.lastUpdated ? 'zzzzzzz' : 0);
	};

	$scope.doRefresh = function() {
		dbService.sync().finally(function() {
			// Stop the ion-refresher from spinning
			$scope.$broadcast('scroll.refreshComplete');
		});
	};

	$scope.$on("$destroy", $rootScope.$on("syncFinished", function() {
		reload();
	}));
	reload();
})

.controller('TaskDetailCtrl', function($scope, $stateParams, $rootScope, dbService) {
	$scope.isStep = $stateParams.stepId != null;
	var targetId = $scope.isStep ? $stateParams.stepId : $stateParams.taskId;
	// store last opened task/step id in rootscope to be able to set default reference when creating new documentation
	//  var taskDocumentationProp = $scope.isStep ? "step" : "task";
	var table = $scope.isStep ? "taskStep" : "task";

	reload();

	function reload() {
		return dbService.get(targetId, table).then(function(target) {
			$scope.taskOrStep = target;
		}, function(err) {
			// TODO: 404 error message and open default/main page
			console.log(err);
		});
	}

	$scope.$on("$destroy", $scope.$on('$ionicView.enter', function() {
		$rootScope.lastOpenedTaskOrStepId = targetId;
	}));
})

.controller('ModalEditCtrl', function($scope, $stateParams, $q, $ionicPopup, $ionicModal, $ionicLoading, $rootScope, dbService, mediaAttachment, authenticationService) {
	$scope.docName = $scope.docName || "Dokument";
	$scope.attachPhoto = function() {
		mediaAttachment.attachPhoto($scope.editedDocument);
	}

	$scope.attachVideo = function() {
		mediaAttachment.attachVideo($scope.editedDocument);
	}

	$scope.attachAudio = function() {
		mediaAttachment.attachAudio($scope.editedDocument);
	}

	$scope.attachChosenMedia = function() {
		mediaAttachment.attachChosenMedia($scope.editedDocument);
	}

	$scope.editDocument = function(doc) {
		if (!doc) {
			doc = $scope.createNewDocument();
		}
		var autoFocus = $scope.firstProp && !doc[$scope.firstProp] || doc[$scope.firstProp].length <= 0;
		$ionicModal.fromTemplateUrl($scope.templateName, {
			scope: $scope,
			animation: "slide-in-up",
			focusFirstInput: autoFocus
		}).then(function(modal) {
			$scope.editDocumentModal = modal;
			$scope.editedDocument = doc;
			$scope.editedDocumentDirty = false;
			$scope.unbindWatch = $scope.$watchCollection("editedDocument.attachments", function(newValue, oldValue) {
				if (newValue !== oldValue) {
					$scope.setEditedDocumentDirty();
				}
			});
			$scope.openEditDocumentModal();
		});
	};

	$scope.deleteDocument = function() {
		$ionicPopup.confirm({
			title: '<b>' + $scope.docName + ' löschen</b>',
			template: 'Soll diese ' + $scope.docName + ' wirklich gelöscht werden?'
		}).then(function(result) {
			if (result) {
				$ionicLoading.show({
					template: "<ion-spinner class='spinner-large'></ion-spinner><p>" + $scope.docName + " wird gelöscht</p>"
				});
				$scope.editedDocument.deleted = true;
				dbService.save($scope.editedDocument).then(function() {
					$scope.reload();
					$ionicLoading.show({
						template: $scope.docName + " wurde gelöscht",
						duration: 1500
					});
					$scope.closeEditDocumentModal();
				}, function(err) {
					$ionicLoading.show({
						template: "Löschen fehlgeschlagen",
						duration: 2000
					});
					console.log(err);
				});
			}
		});
	};

	$scope.openEditDocumentModal = function() {
		$scope.editDocumentModal.show();
	};

	$scope.checkCloseEditDocumentModal = function() {
		if ($scope.editedDocumentDirty) {
			$ionicPopup.confirm({
				title: "<b>Änderungen verwerfen</b>",
				template: "Sollen alle Änderungen an der " + $scope.docName + " verworfen werden?"
			}).then(function(result) {
				if (result) {
					$scope.reload();
					$scope.closeEditDocumentModal();
				}
			});
		} else {
			$scope.closeEditDocumentModal();
		}
	};

	$scope.closeEditDocumentModal = function() {
		$scope.unbindWatch();
		$scope.editDocumentModal.hide();
		$scope.editDocumentModal.remove();
	};

	$scope.setEditedDocumentDirty = function() {
		$scope.editedDocumentDirty = true;
	};

	$scope.saveDocument = function() {
		if (typeof $scope.prepareSave === "function") {
			$scope.prepareSave($scope.editedDocument);
		}

		var objectsToSave = [];
		$ionicLoading.show({
			template: "<div class='saving'><ion-spinner></ion-spinner> " + $scope.docName + " wird gespeichert...</div>"
		});
		angular.forEach($scope.editedDocument.attachments, function(attachment) {
			objectsToSave.push(attachment);
		});
		objectsToSave.push($scope.editedDocument);
		dbService.save(objectsToSave).then(function() {
			$scope.reload().then(function() {
				$ionicLoading.show({
					template: "<div class='saving'><i class='icon ion-checkmark balanced icon-large'></i> " + $scope.docName + " erfolgreich gespeichert.</div>",
					duration: 1500
				});
				$scope.closeEditDocumentModal();
			});
		}, function(err) {
			console.log(err);
			$ionicLoading.show({
				template: "<div class='saving'><i class='icon ion-alert-circled assertive icon-large'></i> Speichern fehlgeschlagen!</div>",
				duration: 2000
			});
		});
	}

	$scope.canEdit = function(doc) {
		return authenticationService.canEdit(doc);
	}

	$scope._getPossibleTaskReferences = function(task) {
		var referenceIds = "('" + task.id + "'";
		angular.forEach(task.steps, function(step) {
			referenceIds += ",'" + step.id + "'";
		});
		referenceIds += ")";
		return referenceIds;
	}
})

.controller('TaskDocumentationCtrl', function($scope, $rootScope, $stateParams, $q, $ionicModal, $ionicLoading, $controller, dbService) {
	$scope.docName = "Dokumentation";
	$scope.firstProp = "text";
	$scope.templateName = "templates/modal-documentation-editor.html";
	$controller('ModalEditCtrl', {
		$scope: $scope
	});

	$scope.reload = function() {
		// load task
		return dbService.get($stateParams.taskId, "task").then(function(task) {
			$scope.task = task;

			// load reflection answers for current task
			return dbService.all("reflectionAnswer", true, "task='" + $stateParams.taskId + "'").then(function(docs) {
				var reflectionAnswers = {};
				angular.forEach(docs, function(doc) {
					reflectionAnswers[doc.question] = doc;
				});
				// create empty reflection answers when not existing
				angular.forEach(task.reflectionQuestions, function(reflectionQuestion) {
					if (!reflectionAnswers[reflectionQuestion.id]) {
						reflectionAnswers[reflectionQuestion.id] = dbService.createReflectionAnswer($stateParams.taskId, reflectionQuestion.id);
					}
				});

				// load documentations for current task and its steps
				return dbService.all("taskDocumentation", true, "reference in " + $scope._getPossibleTaskReferences(task)).then(function(documentations) {
					$scope.documentations = documentations;
					$scope.reflectionAnswers = reflectionAnswers;
					// update badge count on tab icon
					$scope.badge = {
						count:documentations.length
					}
				});
			})
		});
	}

	$scope.createNewDocument = function() {
		var documentation = dbService.createTaskDocumentation();
		documentation.reference = $rootScope.lastOpenedTaskOrStepId;
		return documentation;
	}

	$scope.prepareSave = function(document) {
		if (!document.reference) {
			document.reference = $scope.task.id;
		}
	}

	$scope.saveReflectionAnswer = function(reflectionAnswer, form) {
		if (reflectionAnswer.text.length == 0) {
			reflectionAnswer.deleted = true;
		}
		dbService.save(reflectionAnswer).then(function() {
			form.$dirty = false;
			$scope.reload();
			$ionicLoading.show({
				template: "Antwort " + (reflectionAnswer.deleted ? "gelöscht" : "gespeichert"),
				duration: 2000
			});
		});
	};

	$scope.reload();
})

.controller('QuestionsCtrl', function($scope, $rootScope, $stateParams, $q, $ionicModal, $controller, dbService) {
	$scope.docName = "Frage";
	$scope.firstProp = "title";
	$scope.templateName = "templates/modal-question-editor.html";
	$controller('ModalEditCtrl', {
		$scope: $scope
	});

	$scope.reload = function() {
		if ($stateParams.taskId) {
			return dbService.get($stateParams.taskId, "task").then(function(task) {
				$scope.task = task;
				var referenceIds = [task.id];
				angular.forEach(task.steps, function(step) {
					referenceIds.push(step.id);
				});
				return dbService.all("question", true, "reference in " + $scope._getPossibleTaskReferences(task)).then(function(questions) {
					$scope.questions = questions;
					// update badge count on tab icon
					$scope.badge = {
						count:questions.length
					}
				});
			});
		} else {
			// clear last selected task/step since we're not in task context here
			$rootScope.lastOpenedTaskOrStepId = null;
			return dbService.all("question", true).then(function(questions) {
				$scope.questions = questions;
			});
		}
	}

	$scope.createNewDocument = function() {
		var question = dbService.createQuestion();
		console.log("setting default selected task/step to " + $rootScope.lastOpenedTaskOrStepId);
		question.reference = $rootScope.lastOpenedTaskOrStepId;
		return question;
	}

	$scope.prepareSave = function(document) {
		if (!document.reference && $scope.task) {
			document.reference = $scope.task.id;
		}
	}

	$scope.reload();
})

.controller('QuestionDetailCtrl', function($scope, $rootScope, $stateParams, $q, $controller, $ionicPopup, $ionicModal, $ionicLoading, $filter, dbService, mediaAttachment) {
	$scope.docName = "Antwort";
	$scope.firstProp = "text";
	$scope.templateName = "templates/modal-answer-editor.html";
	$controller('ModalEditCtrl', {
		$scope: $scope
	})

	$scope.reload = function() {
		return dbService.get($stateParams.questionId, "question").then(function(question) {
			// order answers by hand to avoid resorting on accepting/rating answers
			question._answers = $filter('orderBy')(question._answers, function(answer) {
				// keep accepted answer on top of list
				if (question.acceptedAnswer === answer.id) {
					return 0;
				}
				return answer.dateCreated;
			})
			$scope.question = question;
		}, function(error) {
			console.log(error);
		});
	}

	$scope.createNewDocument = function() {
		return dbService.createAnswer($scope.question);
	}

	$scope.setAcceptedAnswer = function(answer) {
		if ($scope.canEdit($scope.question)) {
			$scope.question.acceptedAnswer = answer.id;
			dbService.save($scope.question);
		}
	}

	$scope.prepareSave = function(answer) {
		$scope.question._answers.push(answer)
	}

	$scope.$on("$destroy", $rootScope.$on("syncFinished", function() {
		$scope.reload();
	}));
	$scope.reload();
})

.controller('SettingsCtrl', function($scope, $state, appName, appVersion, dbService, authenticationService) {
	$scope.profile = authenticationService.getCredentials();
	$scope.scaleImages = ((localStorage["scaleImages"] || "true") === "true");
	$scope.appName = appName;
	$scope.appVersion = appVersion;

	$scope.update = function() {
		localStorage["scaleImages"] = $scope.scaleImages.toString();
		if ($scope.profile.user && $scope.profile.password) {
			authenticationService.updateCredentials($scope.profile.user, $scope.profile.password);
			dbService.sync();
			return $state.go("home");
		}
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
		$state.go("task-create", params);
	};
})

.controller('TaskCreateCtrl', function($scope, $state, $stateParams, $ionicLoading, $rootScope, $ionicHistory, dbService, rfc4122) {
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
	} else {
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
	};

	$scope.save = function() {
		if ($scope.task.name) {
			var objectsToSave = [];
			angular.forEach($scope.task.steps, function(step) {
				objectsToSave.push(step);
			});
			objectsToSave.push($scope.task);
			dbService.save(objectsToSave).then(function() {
				$ionicLoading.show({
					template: "Neuer Arbeitsauftrag gespeichert.",
					duration: 2000
				});
				// prevent going back to editor
				$ionicHistory.nextViewOptions({
					disableBack: true
				});
				$state.go("home");
			}, function(err) {
				console.log(err);
				$ionicLoading.show({
					template: "Speichern fehlgeschlagen!",
					duration: 2000
				});
			});
		}
	};
});
