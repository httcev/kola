angular.module('kola.storage', ['uuid'])
.service('schemaService', function() {
	return {
		"user": {
			"idType": "integer",
			"sync": true,
			"extract": {
				"deleted": "BOOL"
			}
		},
		"task": {
			"references": {
				"creator": "user",
				"assignee": "user",
				"steps": "taskStep",
				"attachments": "asset",
				"resources": "asset",
				"reflectionQuestions": "reflectionQuestion"
			},
			"sync": true,
			"extract": {
				"deleted": "BOOL",
				"isTemplate": "BOOL",
			}
		},
		"taskStep": {
			"references": {
				"creator": "user",
				"attachments": "asset",
				"resources": "asset"
			},
			"sync": true,
			"extract": {
				"deleted": "BOOL"
			}
		},
		"taskDocumentation": {
			"references": {
				"creator": "user",
				"attachments": "asset",
				"_comments": "comment",
			},
			"joins": [{
				"field": "_comments",
				"targetTable": "comment",
				"targetField": "reference"
			}, ],
			"sync": true,
			"extract": {
				"deleted": "BOOL",
				"reference": "TEXT"
			}
		},
		"reflectionQuestion": {
			"idType": "integer",
			"sync": true,
			"extract": {
				"deleted": "BOOL"
			}
		},
		"reflectionAnswer": {
			"references": {
				"creator": "user",
			},
			"sync": true,
			"extract": {
				"deleted": "BOOL",
				"task": "TEXT"
			}
		},
		"question": {
			"references": {
				"creator": "user",
				"attachments": "asset",
				"_answers": "answer",
				"_comments": "comment",
			},
			"joins": [{
				"field": "_answers",
				"targetTable": "answer",
				"targetField": "question"
			}, {
				"field": "_comments",
				"targetTable": "comment",
				"targetField": "reference"
			}, ],
			"sync": true,
			"extract": {
				"deleted": "BOOL",
				"reference": "TEXT"
			}
		},
		"answer": {
			"references": {
				"creator": "user",
				"attachments": "asset",
				"_comments": "comment",
			},
			"joins": [{
				"field": "_comments",
				"targetTable": "comment",
				"targetField": "reference"
			}, ],
			"sync": true,
			"extract": {
				"deleted": "BOOL",
				"question": "TEXT"
			}
		},
		"comment": {
			"references": {
				"creator": "user",
			},
			"sync": true,
			"extract": {
				"reference": "TEXT",
				"deleted": "BOOL"
			}
		},
		"asset": {
			"references": {
				"creator": "user",
			},
			"sync": true,
			"extract": {
				"deleted": "BOOL"
			}
		}
	}
})

.service('dbService', function($rootScope, $q, $state, $ionicPlatform, $ionicLoading, $cordovaFile, $cordovaFileTransfer, $window, serverUrl, appVersion, schemaService, authenticationService, rfc4122) {
	var self = this;
	var firstRun = true;
	var debug = false;
	self.initDeferred = $q.defer();

	self.sync = dependOnInit(function() {
		var deferred = $q.defer();
		if (canSync()) {
			$rootScope.onlineState.isSyncing = true;
			DBSYNC.syncNow(onSyncProgress, function(result) {
				$rootScope.onlineState.isSyncing = false;
				if (result && result.status == 401) {
					openAccountTab("Login fehlgeschlagen. Bitte überprüfen Sie Nutzernamen und Passwort.");
					deferred.reject("sync failed");
				} else {
					if (result && result.syncOK === true) {
						// synchronized successfully
						deferred.resolve();
						$rootScope.$broadcast("syncSucceeded");
					} else {
						deferred.reject("sync failed");
					}
				}
				$rootScope.$broadcast("syncFinished");
			});
		} else {
			// in offline mode, let syncing pretend to be successful
			deferred.resolve("sync denied");
			$rootScope.$broadcast("syncFinished");
		}
		return deferred.promise;
	});

	self.all = dependOnInit(function(tableName, resolve, whereClause) {
		var d = $q.defer();
		var promises = [];
		var docs = [];

		self.db.transaction(function(t) {
			var sql = "select doc,modified from " + tableName + " where ";
			if (whereClause) {
				sql += whereClause + " and ";
			}
			sql += "deleted <> 'true'";
			if (debug) {
				console.log(sql);
			}
			t.executeSql(sql, [], function(tx, results) {
				for (var i = 0; i < results.rows.length; i++) {
					var doc = JSON.parse(results.rows.item(i).doc);
					doc._table = tableName;
					doc._modified = (results.rows.item(i).modified === "true");
					docs.push(doc);
					if (resolve) {
						promises.push(self._attachOneToMany(doc, tx).then(function(doc) {
							return resolveIds(doc);
						}));
					}
				}
				d.resolve();
			}, function(tx, err) {
				console.log(err);
				d.reject(err);
			});
		}, function(err) {
			console.log(err);
			d.reject(err);
		});
		return d.promise.then(function() {
			return $q.all(promises).then(function() {
				return docs;
			})
		});
	});

	self.save = dependOnInit(function(doc, isUpdateFromServer) {
		var d = $q.defer();
		self.db.transaction(function(tx) {
			var promises = [];
			if (angular.isArray(doc)) {
				angular.forEach(doc, function(o) {
					promises.push(_save(o, tx, isUpdateFromServer));
				});
			} else {
				promises.push(_save(doc, tx, isUpdateFromServer));
			}
			$q.all(promises).then(function() {
				d.resolve();
			}, function(err) {
				d.reject(err);
			})
		}, function(err) {
			throw err;
		});

		return d.promise.then(function() {
			// saving should succeed in offline mode. since sync() rejects when offline, check canSync() first.
			if (!isUpdateFromServer) {
				return self.sync();
			}
		});
	});

	self._get = dependOnInit(function(id, tableName) {
		var d = $q.defer();
		self.db.transaction(function(tx) {
			var sql = "select doc,modified from " + tableName + " where id=?";
			if (debug) {
				console.log(sql, [id]);
			}
			tx.executeSql(sql, [id], function(tx, results) {
				if (results.rows.length == 1) {
					var result = results.rows.item(0);
					var doc = JSON.parse(result.doc);
					doc._table = tableName;
					doc._modified = (result.modified === "true");
					// attach oneToMany relations
					self._attachOneToMany(doc, tx).then(function() {
//						console.log("--- loaded", doc);
						if (tableName == "asset" && doc.typeLabel == "attachment") {
							self._setLocalURL(doc).then(function() {
								d.resolve(doc);
							}, function(err) {
								console.log(err);
								d.reject();
							});
						} else {
							d.resolve(doc);
						}
					});
				} else {
					d.reject("no document with id '" + id + "' in table '" + tableName + "'");
				}
			}, function(tx, err) {
				console.log(err);
				d.reject(err);
			});
		}, function(err) {
			console.log(err);
			d.reject(err);
		});
		return d.promise;
	});

	self._attachOneToMany = function(doc, tx) {
		var joinPromises = [];
		angular.forEach(schemaService[doc._table].joins, function(join) {
			var joinPromise = $q.defer();
			joinPromises.push(joinPromise.promise);
			doc[join.field] = [];
			var sql = "select id from " + join.targetTable + " where " + join.targetField + "=? and deleted <> 'true'";
			if (debug) {
				console.log(sql, [doc.id]);
			}
			tx.executeSql(sql, [doc.id], function(tx, results) {
				for (var i = 0; i < results.rows.length; i++) {
					doc[join.field].push(results.rows.item(i).id);
				}
				joinPromise.resolve();
			});
		});
		return $q.all(joinPromises).then(function() {
			return doc;
		});
	}

	self._setLocalURL = dependOnInit(function(attachment) {
		var d = $q.defer();
		if (window.cordova) {
			$cordovaFile.checkFile(self._assetsDirName, attachment.id).then(function(fileEntry) {
				// file exists.
				// for images (and TODO: videos) use cdvfile:// url to let the cordova webview load the attachments.
				// for other file types (e.g. PDFs), use fileEntry's nativeURL, so that Android can open it natively.
				if (attachment.mimeType.indexOf("image/") == 0) {
					fileEntry.file(function(f) {
						attachment._localURL = f.localURL;
						d.resolve();
					});
				} else {
					attachment._localURL = fileEntry.nativeURL;
					d.resolve();
				}
			}, function() {
				// file not found
				d.reject("asset file NOT found " + attachment.id);
			});
		} else {
			d.resolve();
		}
		return d.promise;
	});


	function _createTables() {
		var promises = [];
		self.db.transaction(function(tx) {
			angular.forEach(schemaService, function(tableDef, tableName) {
				var idType = tableDef.idType ? tableDef.idType : "VARCHAR(255)"
				var sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (id " + idType + " PRIMARY KEY NOT NULL, ";
				angular.forEach(tableDef.extract, function(type, field) {
					sql += field + " " + type + ", ";
				});
				sql += "modified BOOL, doc TEXT NOT NULL)";
				if (debug) {
					console.log(sql);
				}
				var d = $q.defer();
				promises.push(d);
				tx.executeSql(sql, null, function() {
					d.resolve();
				}, function(tx, err) {
					d.reject(err);
				});
			});
		});
		return $q.all(promises);
	}

	function _init() {
		try {
			var user = authenticationService.getCredentials().user;
			if (user) {
				if (window.cordova) {
					//	  self._assetsDirName = cordova.file.dataDirectory + "assets/";
					self._cacheDirName = cordova.file.externalCacheDirectory;
					self._dataDirName = ionic.Platform.isAndroid() ? cordova.file.externalDataDirectory : cordova.file.dataDirectory;
					self._assetsDirName = self._dataDirName + "assets/";
					$cordovaFile.createDir(self._dataDirName, "assets", false).finally(function() {
						window.resolveLocalFileSystemURL(self._assetsDirName, function(dir) {
							self._assetsDir = dir;
						});
					});
					window.openDatabase = function(dbname, ignored1, ignored2, ignored3) {
						return window.sqlitePlugin.openDatabase({name: dbname, location: 'default', androidDatabaseImplementation: 2});
					};
				}
				self.db = window.openDatabase(user + '.db', '1', 'KOLA DB', 1024 * 1024 * 100);

				_createTables().then(function() {
					DBSYNC.initSync(self, schemaService, self.db, {
						version: appVersion
					}, serverUrl + "/api/changes", serverUrl + "/api/upload", self._assetsDirName, function() {
						self.initDeferred.resolve();
						if (firstRun) {
							$rootScope.$watch("onlineState.isOnline", onOnlineStateChanged);
							firstRun = false;
						}
					}, $cordovaFileTransfer, $cordovaFile, $q, authenticationService);
				});
			} else {
				self.initDeferred.reject("no_user");
			}
		}
		catch(e) {
			self.initDeferred.reject(e);
		}
	}

	function dependOnInit(fn) {
		return function() {
			var args = arguments;
			return self.initDeferred.promise.then(function() {
				return fn.apply(this, args);
			}, function(err) {
				if ("no_user" == err) {
					openAccountTab("Willkommen bei KOLA! Bitte geben Sie Ihren Nutzernamen und Passwort ein.");
				} else {
					console.log(err);
				}
				return $q.reject(err);
			});
		}
	}

	function onCredentialsChanged() {
		self.initDeferred = $q.defer();
		_init();
		return self.initDeferred.promise;
	}

	function onOnlineStateChanged() {
		//console.log("--- online state changed", $rootScope.onlineState);
		self.sync();
	}

	function onSyncProgress(msg) {
		//console.log(msg);
	}

	function canSync() {
		return $rootScope.onlineState.isOnline && !$rootScope.onlineState.isSyncing;
	}

	function _create(tableName, props) {
		return angular.extend({
			id: rfc4122.v4(),
			_table: tableName,
			_isNew: true,
			_modified: true
		}, props);
	}

	function createTask() {
		var task = _create("task", {
			isTemplate: false,
			reflectionQuestions: []
		});
		// auto link standard reflection questions
		return self.all("reflectionQuestion").then(function(reflectionQuestions) {
			angular.forEach(reflectionQuestions, function(reflectionQuestion) {
				if (reflectionQuestion.autoLink) {
					task.reflectionQuestions.push(reflectionQuestion);
				}
			});
			return task;
		});
	}

	function createAttachment(parent) {
		var attachment = _create("asset", {
			typeLabel: "attachment"
		});
		parent.attachments = parent.attachments || [];
		parent.attachments.push(attachment);
		return attachment;
	}

	function createTaskDocumentation() {
		/*
		  function createTaskDocumentation(targetId, isStep) {
			var options = {};
			if (isStep) {
			  options.step = targetId;
			}
			else {
			  options.task = targetId;
			}
			return _create("taskDocumentation", options);
		*/
		return _create("taskDocumentation");
	}

	function createReflectionAnswer(taskId, questionId) {
		return _create("reflectionAnswer", {
			task: taskId,
			question: questionId
		});
	}

	function createQuestion(referenceId) {
		return _create("question", {
			reference: referenceId
		});
	}

	function createAnswer(question) {
		var answer = _create("answer", {
			question: question.id
		});
		return answer;
	}

	function createComment(target) {
		var comment = _create("comment", {
			reference: target.id
		});
		return comment;
	}

	function get(id, tableName, resolve) {
		return self._get(id, tableName).then(function(doc) {
			if (resolve !== false) {
				return resolveIds(doc).then(function() {
					return doc;
				});
			} else {
				return doc;
			}
		});
	}

	function _save(doc, tx, isUpdateFromServer) {
		var d = $q.defer();
		if (!doc._table) {
			d.reject("Object has no _table property");
		} else {
			if (!doc.id) {
				doc.id = rfc4122.v4();
			}
			var copy = angular.copy(doc);
			var localURL = copy._localURL;

			// delete local properties (all properties beginning with an underscore)
			for (var property in copy) {
				if (property.indexOf("_") == 0) {
					delete copy[property];
				}
			}

			var tableSchema = schemaService[doc._table];
			_replaceIds(copy, tableSchema);

//			console.log("--- saving", copy);
			var sql = "INSERT OR REPLACE INTO " + doc._table + " (id, modified, doc";
			var sqlValues = "?, ?, ?";
			var values = [copy.id, isUpdateFromServer !== true, JSON.stringify(copy)];
			angular.forEach(tableSchema.extract, function(type, field) {
				sql += ", " + field;
				sqlValues += ", ?";
				values.push(copy[field] || false);
			});
			sql += ") values (" + sqlValues + ")";
			if (debug) {
				console.log(sql, values);
			}

			tx.executeSql(sql, values, function(tx, results) {
				if (doc._table == "asset" && copy.typeLabel == "attachment" && localURL) {
					window.resolveLocalFileSystemURL(localURL, function(fileEntry) {
						// move attachment data to assets dir if it is not already there
						if (fileEntry.nativeURL != (self._assetsDirName + copy.id)) {
							// check if the asset is in the app's cache folder. if yes, move the asset, if not copy it (to avoid removing files selected from the photo library).
							if (fileEntry.nativeURL.indexOf(self._cacheDirName) == 0) {
								fileEntry.moveTo(self._assetsDir, copy.id, function() {
									d.resolve();
								}, function(err) {
									// error
									d.reject(err);
								});
							} else {
								fileEntry.copyTo(self._assetsDir, copy.id, function() {
									d.resolve();
								}, function(err) {
									// error
									d.reject(err);
								});
							}
						} else {
							// file is already in assets dir
							d.resolve();
						}
					});
				} else {
					d.resolve();
				}
			}, function(err) {
				d.reject(err);
			});
		}
		return d.promise;
	}

	function resolveIds(target) {
		var promises = [];
		angular.forEach(schemaService[target._table].references, function(targetTable, field) {
			var ids = target[field];
			if (ids != null) {
				if (angular.isArray(ids)) {
					angular.forEach(ids, function(id, index) {
						promises.push(self._get(id, targetTable).then(function(doc) {
							target[field][index] = doc;
							return resolveIds(doc);
						}));
					});
				} else {
					promises.push(self._get(ids, targetTable).then(function(doc) {
						target[field] = doc;
						return resolveIds(doc);
					}));
				}
			}
		});
		return $q.all(promises).then(function() {
			return target;
		});
	}

	function _replaceIds(target, tableSchema) {
		angular.forEach(tableSchema.references, function(targetTable, field) {
			var values = target[field];
			if (values != null) {
				if (angular.isArray(values)) {
					angular.forEach(values, function(value, index) {
						if (value.id) {
							target[field][index] = value.id;
						}
					});
				} else {
					if (values.id) {
						target[field] = values.id;
					}
				}
			}
		});
	}

	function openAccountTab(message) {
		$ionicLoading.show({
			template: message,
			duration: 2000
		});
		$state.go("settings");
	}

	$rootScope.$on("credentialsChanged", onCredentialsChanged);
	$ionicPlatform.ready(_init);

	return {
		sync: self.sync,
		all: self.all,
		save: self.save,
		get: get,
		resolveIds: resolveIds,
		createTask: createTask,
		createAttachment: createAttachment,
		createTaskDocumentation: createTaskDocumentation,
		createReflectionAnswer: createReflectionAnswer,
		createQuestion: createQuestion,
		createAnswer: createAnswer,
		createComment: createComment
	}
})