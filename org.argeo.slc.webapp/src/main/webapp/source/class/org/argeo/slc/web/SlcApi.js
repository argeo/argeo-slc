qx.Class.define("org.argeo.slc.web.SlcApi",
{
  extend : qx.core.Object,
  
  statics : {
  	DEFAULT_CONTEXT : "/org.argeo.slc.webapp",
  	
  	REMOVE_RESULT_FROM_COLL_SERVICE : "removeResultFromCollection.service",
  	ADD_RESULT_TO_COLL_SERVICE : "addResultToCollection.service",
  	LIST_COLLECTIONS_SERVICE : "listCollectionRefs.service",
  	LIST_RESULTS_SERVICE : "listResultAttributes.service",
  	GET_RESULT_SERVICE : "getResult.service",
  	
  	/**
  	 * Standard Request getter
  	 * @param serviceName {String} The name of the service to call (without base context)
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getServiceRequest:function(serviceName, fireReloadEventType, iLoadStatusables){
  		var serviceManager = org.argeo.ria.remote.RequestManager.getInstance();
  		return serviceManager.getRequest(
  			org.argeo.slc.web.SlcApi.DEFAULT_CONTEXT+"/"+serviceName,
  			"GET",
  			"application/xml",
  			fireReloadEventType,
  			iLoadStatusables
  		);
  	},
  	
  	/**
  	 * Remove a result from a collection
  	 * @param collectionId {String} Id of the destination collection
  	 * @param resultId {String} Id of the test result to remove
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getRemoveResultService : function(collectionId, resultId, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.web.SlcApi.getServiceRequest(
	  		org.argeo.slc.web.SlcApi.REMOVE_RESULT_FROM_COLL_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("collectionId", collectionId);
  		request.setParameter("resultUuid", resultId);
  		return request;
  	},
  	
  	/**
  	 * Add a result to a given collection
  	 * @param collectionId {String} Id of the destination collection
  	 * @param resultId {String} Id of the test result to add
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getAddResultService : function(collectionId, resultId, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.web.SlcApi.getServiceRequest(
	  		org.argeo.slc.web.SlcApi.ADD_RESULT_TO_COLL_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("collectionId", collectionId);
  		request.setParameter("resultUuid", resultId);
  		return request;
  	},
  	
  	/**
  	 * List current collections
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getListCollectionsService : function(fireReloadEventType, iLoadStatusables){
  		return org.argeo.slc.web.SlcApi.getServiceRequest(
	  		org.argeo.slc.web.SlcApi.LIST_COLLECTIONS_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  	},
  	
  	/**
  	 * List all results or results of a given collection 
  	 * @param collectionId {String} Id of the collection to load
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getListResultsService : function(collectionId, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.web.SlcApi.getServiceRequest(
	  		org.argeo.slc.web.SlcApi.LIST_RESULTS_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		if(collectionId){
  			request.setParameter("id", collectionId);
  		}
  		return request;
  	},
  	
  	/**
  	 * Load a result test
  	 * @param resultId {String} Id of the test result to load
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getLoadResultService : function(resultId, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.web.SlcApi.getServiceRequest(
	  		org.argeo.slc.web.SlcApi.GET_RESULT_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("uuid", resultId);
  		return request;
  	}
  	
  }
});