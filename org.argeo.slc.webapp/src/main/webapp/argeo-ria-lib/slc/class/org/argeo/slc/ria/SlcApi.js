/**
 * SLC API Client implementation :
 * This class encapsulate the various SLC services available. It just creates the Request object
 * and return them, it does not execute them. 
 * Available services are : 
 * + loadResult / removeResult / addResult
 * + listCollection / listResults
 * When using it, be sure the static constant DEFAULT_CONTEXT is pointing to the right URL.
 */
qx.Class.define("org.argeo.slc.ria.SlcApi",
{
  extend : qx.core.Object,
  
  statics : {
  	DEFAULT_CONTEXT : "/org.argeo.slc.webapp",
  	
  	REMOVE_RESULT_FROM_COLL_SERVICE : "removeResultFromCollection.service",
  	ADD_RESULT_TO_COLL_SERVICE : "addResultToCollection.service",
  	LIST_COLLECTIONS_SERVICE : "listCollectionRefs.service",
  	LIST_RESULTS_SERVICE : "listResultAttributes.service",
  	GET_RESULT_SERVICE : "getResult.service",
  	LIST_SLCEXEC_SERVICE : "listSlcExecutions.service",
  	
  	LIST_AGENTS_SERVICE : "listAgents.service",  	
  	AMQ_SERVICE : "amq",
  	
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
  			org.argeo.slc.ria.SlcApi.DEFAULT_CONTEXT+"/"+serviceName,
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
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.REMOVE_RESULT_FROM_COLL_SERVICE, 
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
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.ADD_RESULT_TO_COLL_SERVICE, 
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
  		return org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.LIST_COLLECTIONS_SERVICE, 
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
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.LIST_RESULTS_SERVICE, 
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
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.GET_RESULT_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("uuid", resultId);
  		return request;
  	},

  	/**
  	 * List currently registered SlcExecutions.
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getListSlcExecutionsService:function(fireReloadEventType, iLoadStatusables){
  		return org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.LIST_SLCEXEC_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  	},
  	
  	
  	/**
  	 * List currently available agents queues.
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getListAgentsService:function(fireReloadEventType, iLoadStatusables){
  		return org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.LIST_AGENTS_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  	},
  	
  	/**
  	 * Send a JMS message to the AMQ_CONTEXT
  	 * @param destination {String} The destination queue, in the form "topic://destination" 
  	 * @param message {org.argeo.slc.ria.SlcExecutionMessage} The message object
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 */
  	getSendAmqMessageRequest : function(destination, message, iLoadStatusables){
  		var serviceManager = org.argeo.ria.remote.RequestManager.getInstance();
  		var request = serviceManager.getRequest(
  			org.argeo.slc.ria.SlcApi.DEFAULT_CONTEXT+"/"+org.argeo.slc.ria.SlcApi.AMQ_SERVICE,
  			"POST",
  			"text/plain",
  			null,
  			iLoadStatusables
  		);
  		request.setParameter("destination", destination);
  		request.setParameter("message", message.toXml());
  		request.setParameter("type", "send");
  		return request;
  	}
  	
  }
});