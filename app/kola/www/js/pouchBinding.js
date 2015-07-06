angular.module('kola.pouchBinding', [])
.factory('pouchCollection', function($timeout, $q, dbService) {
	/**
	 * @class item in the collection
	 * @param item
	 * @param {int} index             position of the item in the collection
	 *
	 * @property {String} _id         unique identifier for this item within the collection
	 * @property {int} $index         position of the item in the collection
	 */
	function PouchDbItem(item, index) {
	  this.$index = index;
	  angular.extend(this, item);
	}

	/**
	 * create a pouchCollection
	 * @param  {String} collectionUrl The pouchdb url where the collection lives
	 * @return {Array}                An array that will hold the items in the collection
	 */
	return function(viewName, viewOptions, filterName, filterOptions) {
	  var collection = [];
	  var indexes = {};
	  var db = dbService.localDatabase;
	  if (viewName) {
	  	collection = db.query(viewName, angular.extend({}, viewOptions, { include_docs: true }));
	  	console.log(collection);
	  }

	  function getIndex(prevId) {
	    return prevId ? indexes[id] + 1 : 0;
	  }

	  function addChild(index, item) {
	    indexes[item._id] = index;
	    collection.splice(index, 0, item);
	    console.log('added: ', index, item);
	  }

	  function removeChild(id) {
	    var index = indexes[id];

	    // Remove the item from the collection
	    collection.splice(index, 1);
	    indexes[id] = undefined;

	    console.log('removed: ', id);
	  }

	  function updateChild(index, item) {
	    collection[index] = item;
	    console.log('changed: ', index, item);
	  }

	  function moveChild(from, to, item) {
	    collection.splice(from, 1);
	    collection.splice(to, 0, item);
	    updateIndexes(from, to);
	    console.log('moved: ', from, ' -> ', to, item);
	  }

	  function updateIndexes(from, to) {
	    var length = collection.length;
	    to = to || length;
	    if (to > length) {
	      to = length;
	    }
	    for (index = from; index < to; index++) {
	      var item = collection[index];
	      item.$index = indexes[item._id] = index;
	    }
	  }

	  function handleChange(change) {
	  	console.log("---here -> DEL=" + change.deleted);
		if (!change.deleted) {
			db.get(change.id).then(function(data) {
			  if (indexes[change.id] == undefined) { // CREATE / READ
			    addChild(collection.length, new PouchDbItem(data, collection.length)); // Add to end
			    updateIndexes(0);
			  } else { // UPDATE
			    var index = indexes[change.id];
			    var item = new PouchDbItem(data, index);
			    updateChild(index, item);
			  }
			});
		} else if (collection.length && indexes[change.id]) { //DELETE
			removeChild(change.id);
			updateIndexes(indexes[change.id]);
		}
	  }

	  return $q.when(collection).then(function(result) {
	  	console.log("--- then:");
	  	collection = [];

		  collection.$add = function(item) {
		    db.post(angular.copy(item)).then(
		      function(res) {
		        item._rev = res.rev;
		        item._id = res.id;
		      }
		    );
		  };

		  collection.$remove = function(itemOrId) {
		    var item = angular.isString(itemOrId) ? collection[itemOrId] : itemOrId;
		    db.remove(item);
		  };

		  collection.$update = function(itemOrId) {
		    var item = angular.isString(itemOrId) ? collection[itemOrId] : itemOrId;
		    var copy = {};
		    angular.forEach(item, function(value, key) {
		      if (key.indexOf('$') !== 0) {
		        copy[key] = value;
		      }
		    });
		    db.get(item._id).then(
		      function(res) {
		        db.put(copy, res._rev);
		      }
		    );
		  };

		  collection.$get = function(itemOrId) {
		  	console.log("--- id=" + itemOrId);
		    var item = angular.isString(itemOrId) ? collection[itemOrId] : itemOrId;
		    console.log(item);
		    return item;
		  }


	  	angular.forEach(result.rows, function(row) {
		    addChild(collection.length, new PouchDbItem(row.doc, collection.length)); // Add to end
		    updateIndexes(0);
	  	});
	  	console.log(collection);

		var changeOptions = { live:true, since:"now", onChange:handleChange };
		if (filterName) {
			changeOptions.filter = filterName;
			if (filterOptions) {
				changeOptions.query_params = filterOptions;
			}
			if (!viewName) {
				changeOptions.since = 0;
			}
		}
		else if (viewName) {
			changeOptions.filter = "_view";
			changeOptions.view = viewName;
		}
		console.log(changeOptions);

		db.changes(changeOptions);
		return collection;
	  }, function(error) {
	  	console.log(error);
	  });
	};
})
.factory('pouchBindingSimple', function($timeout, $parse, pouchDB) {
	var stopTheWatch;
	return function(reference, scope, expression) {
		console.log(reference);
	  var getObj = $parse(expression);
	  var setObj = getObj.assign;
	  if (!setObj) {
	    throw new Error('expression ' + expression + 'must be assignable');
	  }
	  var database = pouchDB(reference);
	  database.get(expression).then(
	    function(res) {
	      setObj(scope, res);
	    },
	    function() {
	      newVal = angular.copy(getObj(scope));
	      database.put(newVal, expression).then(
	        function(res) {
	          newVal._rev = res.rev;
	          newVal._id = res.id;
	          setObj(newVal);
	        },
	        function(err) {
	          console.log(err);
	        })
	    });

	  function equalsIgnoreRev(val1, val2) {
	    var cleanVal1 = angular.copy(val1);
	    var cleanVal2 = angular.copy(val2);
	    cleanVal1._rev = null;
	    cleanVal2._rev = null;
	    return angular.equals(cleanVal1, cleanVal2);
	  }

	  database.changes({
	    live: true,
	    onChange: function(change) {
	      if (!change.deleted) {
	        database.get(change.id).then(
	          function(res) {
	            setObj(scope, res);
	          },
	          function(err) {
	            console.log(err);
	          });
	      }
	    }
	  });

	  var listener = function(ngValue) {
	    database.get(expression).then(
	      function(dbVal) {
	        if (!equalsIgnoreRev(dbVal, ngValue) && ngValue._id) {
	          database.put(angular.copy(ngValue)).then(
	            function(res) {
	              ngValue._rev = res.rev;
	            },
	            function(err) {
	              console.log(err);
	            });
	        }
	      });
	  };
	  stopTheWatch = scope.$watch(getObj, listener, true);
	}
	}
);
