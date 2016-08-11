angular.module('kola.controllers', [])

.controller('TasksCtrl', function($scope, $state, $rootScope, $ionicPopup, $ionicPopover, dbService) {
	function reload() {
		dbService.all("task", false, "isTemplate<>'true'").then(function(tasks) {
			$scope.tasks = tasks;
		}, function(err) {
			// this error handling is needed on the first start of the app. dbService initialization is rejected in this case.
			$scope.tasks = [];
			if (err !== "no_user") {
				console.log(err);
				$ionicPopup.alert({
					title: "<b>Fehler</b>",
					template: "<p class='assertive bold'>Es ist ein Fehler aufgetreten:</p>" + err
				});
			}
		});
	}
	var template = "<ion-popover-view><ion-content><div class='margin-horizontal'><p>Hier haben Sie die Möglichkeit, einen neuen Arbeitsauftrag anzulegen, der als Grundlage für die Dokumentation dient. Sie haben die Möglichkeit, bestehende Vorlagen anzupassen oder einen komplett neuen Arbeitsauftrag anzulegen.</p><p>Seien Sie kreativ und probieren Sie sich aus! Mithilfe der Plattform können Sie selbst angelegte Aufträge auch im Nachhinein ändern.</p></div></ion-content></ion-popover-view>";
	$scope.popover = $ionicPopover.fromTemplate(template, { scope: $scope });
	$scope.openPopover = function($event) {
		$scope.popover.show($event);
	};

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

	var cb = $rootScope.$on("syncFinished", function() {
		reload();
	});
	$scope.$on("$destroy", function() {
		$scope.popover.remove();
		cb();
	});
	reload();
})

.controller('TaskDetailCtrl', function($scope, $stateParams, $rootScope, $state, $ionicPopover, dbService) {
	$scope.isStep = $stateParams.stepId != null;
	var targetId = $scope.isStep ? $stateParams.stepId : $stateParams.taskId;
	var table = $scope.isStep ? "taskStep" : "task";

	function reload() {
		return dbService.get(targetId, table).then(function(target) {
			$scope.taskOrStep = target;
		}, function(err) {
			// TODO: 404 error message and open default/main page
			console.log(err);
		});
	}

	$scope.createDocumentation = function() {
		$state.go("task.documentation", { triggerCreateDocument: true, refId: $scope.taskOrStep.id });
	}

	$scope.createQuestion = function() {
		$state.go("task.questions", { triggerCreateDocument: true, refId: $scope.taskOrStep.id });
	}

	var popoverTemplateDocumentation = "<ion-popover-view><ion-content><div class='margin-horizontal'><p>Hier haben Sie die Möglichkeit, den Arbeitsauftrag zu dokumentieren. Dabei können Sie den gesamten Arbeitsauftrag oder einzelne Teilschritte per Text, Foto, Video oder Spracheingabe dokumentieren. Seien Sie kreativ und probieren Sie sich aus! Sie können alle Eingaben jederzeit rückgängig machen.</p></div></ion-content></ion-popover-view>";
	$scope.popoverDocumentation = $ionicPopover.fromTemplate(popoverTemplateDocumentation, { scope: $scope });
	$scope.openPopoverDocumentation = function($event) {
		$scope.popoverDocumentation.show($event);
	};
	var popoverTemplateQuestion = "<ion-popover-view><ion-content><div class='margin-horizontal'><p>Hier haben Sie die Möglichkeit, eigene Fragen einzustellen (z.B. wenn etwas unklar ist, Sie Fragen zum weiteren Vorgehen haben o.ä.) und Fragen anderer Azubis zu beantworten. Fragen können auch von Lehrern und Ausbildern beantwortet werden.</p></div></ion-content></ion-popover-view>";
	$scope.popoverQuestion = $ionicPopover.fromTemplate(popoverTemplateQuestion, { scope: $scope });
	$scope.openPopoverQuestion = function($event) {
		$scope.popoverQuestion.show($event);
	};
	$scope.$on("$destroy", function() {
		$scope.popoverDocumentation.remove();
		$scope.popoverQuestion.remove();
	});

	reload();
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
		var autoFocus = $scope.firstProp && (!doc[$scope.firstProp] || doc[$scope.firstProp].length <= 0);
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
			template: 'Soll diese ' + $scope.docName + ' wirklich gelöscht werden?',
			cancelText: 'Abbrechen'
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
				template: "Sollen alle Änderungen an der " + $scope.docName + " verworfen werden?",
				cancelText: "Abbrechen"
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

	$scope.saveDocument = function(doc) {
		doc = doc || $scope.editedDocument;
		if (typeof $scope.prepareSave === "function") {
			$scope.prepareSave($scope.editedDocument);
		}

		var objectsToSave = [];
		$ionicLoading.show({
			template: "<div class='saving'><ion-spinner></ion-spinner> " + $scope.docName + " wird gespeichert...</div>"
		});
		angular.forEach(doc.attachments, function(attachment) {
			objectsToSave.push(attachment);
		});
		objectsToSave.push(doc);
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

	$scope._resolveTaskReference = function(referencingModel) {
		if (referencingModel.reference) {
			return dbService.get(referencingModel.reference, "task", false).then(function(task) {
				referencingModel._task = task;
			}, function(error) {
				// 404 for the task, so try loading task step
				return dbService.get(referencingModel.reference, "taskStep", false).then(function(step) {
					referencingModel._step = step;
					return dbService.get(step.task, "task", false).then(function(task) {
						referencingModel._task = task;
					});
				});
			})
		}
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

.controller('TaskDocumentationCtrl', function($scope, $rootScope, $stateParams, $controller, $ionicPopover, dbService) {
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

			// load documentations for current task and its steps
			return dbService.all("taskDocumentation", true, "reference in " + $scope._getPossibleTaskReferences(task)).then(function(documentations) {
				$scope.documentations = documentations;
				// update badge count on tab icon
				$scope.badge = {
					count:documentations.length
				}
			});
		});
	}

	$scope.createNewDocument = function() {
		return dbService.createTaskDocumentation();
	}

	$scope.prepareSave = function(document) {
		if (!document.reference) {
			document.reference = $scope.task.id;
		}
	}

	$scope.$on("$ionicView.afterEnter", function(event, data) {
		if (data && data.stateParams && data.stateParams.triggerCreateDocument) {
			data.stateParams.triggerCreateDocument = false;
			var doc = $scope.createNewDocument();
			doc.reference = data.stateParams.refId;
			$scope.editDocument(doc);
		}
	});

	var popoverTemplateDocumentation = "<ion-popover-view><ion-content><div class='margin-horizontal'><p>Hier haben Sie die Möglichkeit, den Arbeitsauftrag zu dokumentieren. Dabei können Sie den gesamten Arbeitsauftrag oder einzelne Teilschritte per Text, Foto, Video oder Spracheingabe dokumentieren. Seien Sie kreativ und probieren Sie sich aus! Sie können alle Eingaben jederzeit rückgängig machen.</p></div></ion-content></ion-popover-view>";
	$scope.popoverDocumentation = $ionicPopover.fromTemplate(popoverTemplateDocumentation, { scope: $scope });
	$scope.openPopoverDocumentation = function($event) {
		$scope.popoverDocumentation.show($event);
	};
	$scope.$on("$destroy", function() {
		$scope.popoverDocumentation.remove();
	});

	$scope.reload();
})

.controller('ReflectionQuestionsCtrl', function($scope, $stateParams, $ionicLoading, $controller, dbService) {
	$scope.docName = "Einschätzung";
	//$scope.firstProp = null;
	$scope.templateName = "templates/modal-reflectionAnswer-editor.html";
	$controller('ModalEditCtrl', {
		$scope: $scope
	});

	$scope.reload = function() {
		// load task
		return dbService.get($stateParams.taskId, "task").then(function(task) {
			// load reflection answers for current task
			return dbService.all("reflectionAnswer", true, "task='" + $stateParams.taskId + "'").then(function(docs) {
				var reflectionAnswers = {};
				var reflectionAnswerCount = docs.length;
				angular.forEach(docs, function(doc) {
					if (!reflectionAnswers[doc.question]) {
						reflectionAnswers[doc.question] = [];
					}
					reflectionAnswers[doc.question].push(doc);
				});
				// create empty reflection answers when not existing
				angular.forEach(task.reflectionQuestions, function(reflectionQuestion) {
					if (!reflectionAnswers[reflectionQuestion.id]) {
						reflectionAnswers[reflectionQuestion.id] = [dbService.createReflectionAnswer($stateParams.taskId, reflectionQuestion.id)];
					}
				});
				$scope.task = task;
				$scope.reflectionAnswers = reflectionAnswers;
				$scope.badge = {
					count:reflectionAnswerCount
				}
			})
		});
	}

	$scope.checkReflectionQuestionsVisible = function() {
		if ($scope.task && $scope.task.reflectionQuestions.length == 0) {
			return "ng-hide";
		}
	}

	$scope.reload();
})

.controller('QuestionsCtrl', function($scope, $rootScope, $stateParams, $q, $ionicModal, $controller, $ionicPopover, dbService) {
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
				return dbService.all("question", true, "reference in " + $scope._getPossibleTaskReferences(task)).then(function(questions) {
					$scope.badge = {
						count:questions.length
					}
					var promises = [];
					angular.forEach(questions, function(question) {
						promises.push($scope._resolveTaskReference(question));
					})
					return $q.all(promises).finally(function() {
						$scope.questions = questions;
						// update badge count on tab icon
					});
				});
			});
		} else {
			return dbService.all("question", true).then(function(questions) {
				var promises = [];
				angular.forEach(questions, function(question) {
					promises.push($scope._resolveTaskReference(question));
				})
				return $q.all(promises).finally(function() {
					$scope.questions = questions;
				});
			});
		}
	}

	$scope.createNewDocument = function() {
		return dbService.createQuestion();
	}

	$scope.prepareSave = function(document) {
		if (!document.reference && $scope.task) {
			document.reference = $scope.task.id;
		}
	}

	$scope.$on("$ionicView.afterEnter", function(event, data) {
		if (data && data.stateParams && data.stateParams.triggerCreateDocument) {
			data.stateParams.triggerCreateDocument = false;
			var doc = $scope.createNewDocument();
			doc.reference = data.stateParams.refId;
			$scope.editDocument(doc);
		}
	});

	var popoverTemplateQuestion = "<ion-popover-view><ion-content><div class='margin-horizontal'><p>Hier haben Sie die Möglichkeit, eigene Fragen einzustellen (z.B. wenn etwas unklar ist, Sie Fragen zum weiteren Vorgehen haben o.ä.) und Fragen anderer Azubis zu beantworten. Fragen können auch von Lehrern und Ausbildern beantwortet werden.</p></div></ion-content></ion-popover-view>";
	$scope.popoverQuestion = $ionicPopover.fromTemplate(popoverTemplateQuestion, { scope: $scope });
	$scope.openPopoverQuestion = function($event) {
		$scope.popoverQuestion.show($event);
	};
	$scope.$on("$destroy", function() {
		$scope.popoverQuestion.remove();
	});

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
			return $scope._resolveTaskReference(question).finally(function() {
				$scope.question = question;
			});
		}, function(error) {
			console.log(error);
		});
	}

	$scope.createNewDocument = function() {
		return dbService.createAnswer($scope.question);
	}

	$scope.toggleAcceptedAnswer = function(answer) {
		if ($scope.canEdit($scope.question)) {
			if ($scope.question.acceptedAnswer === answer.id) {
				$scope.question.acceptedAnswer = null;
			}
			else {
				$scope.question.acceptedAnswer = answer.id;
			}
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
	dbService.all("task", false, "isTemplate='true'").then(function(templates) {
		$scope.templates = templates;
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
				if (currentUser.id != doc.id && currentUser.company && currentUser.company == doc.company) {
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
