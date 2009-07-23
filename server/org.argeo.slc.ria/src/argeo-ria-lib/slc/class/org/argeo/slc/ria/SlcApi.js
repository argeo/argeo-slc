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
  	COPY_COLLECTION_TO_COLL_SERVICE : "copyCollectionToCollection.service",
  	LIST_RESULTS_SERVICE : "listResultAttributes.service",
  	GET_RESULT_SERVICE : "getResult.service",
  	GET_ATTACHEMENT_SERVICE : "getAttachment.service",
  	LIST_SLCEXEC_SERVICE : "listSlcExecutions.service",
  	NEW_SLCEXEC_SERVICE : "newSlcExecution.service",
  	
  	LIST_AGENTS_SERVICE : "listAgents.service",
	LIST_MODULES_SERVICE : "listModulesDescriptors.service",
	GET_EXECUTION_DESC_SERVICE : "getExecutionDescriptor.service",
	RELOAD_BUNDLE_SERVICE : "reloadBundle.service",
  	AMQ_SERVICE : "amq",
  	
  	LIST_MODULAR_DISTRIB_SERVICE : "listModularDistributions.service",
  	INSTALL_MODULE_SERVICE : "installModule.service",
  	UNINSTALL_MODULE_SERVICE : "uninstallModule.service",
  	
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
  	 * Remove a set of results from a collection. Either filtered by a given pattern, or the whole collection.
  	 * @param collectionId {String} The id of the collection
  	 * @param patternAttribute {String} An optional attribute name on which to filter
  	 * @param patternValue {String} The pattern to use for filtering a subset of result
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request} The Request object
  	 */
  	getRemoveFromCollectionService : function(collectionId, patternAttribute, patternValue, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.REMOVE_RESULT_FROM_COLL_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("collectionId", collectionId);
  		if(patternAttribute && patternValue){
  			request.setParameter("attrName", patternAttribute);
  			request.setParameter("attrPattern", patternValue);
  		}
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
  	 * Copy a whole collection or a subset of it to another collection. If a new id is provided for the target, it will be created.
  	 * @param sourceCollectionId {String} The current collection from which to copy
  	 * @param targetCollectionId {String} The target collection. If unknown, it will be created.
  	 * @param patternAttribute {String} An optional attribute on which a filter can be applied to create a subset.
  	 * @param patternValue {String} The associated pattern to filter on the atttribute's value.
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request} The request object
  	 */
  	getCopyCollectionService : function(sourceCollectionId, targetCollectionId, patternAttribute, patternValue, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.COPY_COLLECTION_TO_COLL_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables  			
  		);
  		request.setParameter("sourceCollectionId", sourceCollectionId);
  		request.setParameter("targetCollectionId", targetCollectionId);
  		if(patternAttribute && patternValue){
  			request.setParameter("attrName", patternAttribute);
  			request.setParameter("attrPattern", patternValue);
  		}
  		return request;
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

  	buildGetAttachmentUrl : function(attachmentId, contentType, name){
  		return org.argeo.slc.ria.SlcApi.DEFAULT_CONTEXT + "/" + org.argeo.slc.ria.SlcApi.GET_ATTACHEMENT_SERVICE
  			+ "?uuid=" + attachmentId + "&contentType=" + contentType + "&name=" + name;
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
  	 * List currently registered SlcExecutions.
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getListModularDistributionsService:function(fireReloadEventType, iLoadStatusables){
  		return org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.LIST_MODULAR_DISTRIB_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  	},
  	
  	getInstallModuleService : function(){
  		return org.argeo.slc.ria.SlcApi.DEFAULT_CONTEXT +"/"+ org.argeo.slc.ria.SlcApi.INSTALL_MODULE_SERVICE;
  	},

  	/**
  	 * Load a result test
  	 * @param resultId {String} Id of the test result to load
  	 * @param fireReloadEventType {String} Whether query should trigger a ReloadEvent
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update
  	 * @return {qx.io.remote.Request}
  	 */
  	getUninstallModuleService : function(moduleName, moduleVersion, fireReloadEventType, iLoadStatusables){
  		var request = org.argeo.slc.ria.SlcApi.getServiceRequest(
	  		org.argeo.slc.ria.SlcApi.UNINSTALL_MODULE_SERVICE, 
	  		fireReloadEventType, 
	  		iLoadStatusables
  		);
  		request.setParameter("name", moduleName);
  		request.setParameter("version", moduleVersion);
  		return request;
  	},

  	
  	
  	/**
  	 * New SlcExecution
  	 * @param agentId {String} Agent id target
  	 * @param xmlDescription {String} XML of the Slc Execution
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getNewSlcExecutionService:function(agentId, xmlDescription, fireReloadEventType, iLoadStatusables){
  		var req = org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.NEW_SLCEXEC_SERVICE + "?slc_agentId="+agentId,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  		req.setMethod("POST");
  		//req.setRequestHeader("Content-Type", "text/xml");
  		req.setData("body=" + encodeURIComponent(xmlDescription));
  		return req;
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
  	 * Load the module descriptors
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getListModulesService : function(agentId, fireReloadEventType, iLoadStatusables){
  		var req = org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.LIST_MODULES_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  		req.setParameter("agentId", agentId);
  		return req;
  	},
  	
  	/**
  	 * Get an execution module descriptor by its name and version
  	 * @param moduleName {String} The name of the module to get
  	 * @param moduleVersion {String} Its version, passed directly as a string
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getLoadExecutionDescriptorService : function(agentId,moduleName, moduleVersion, fireReloadEventType, iLoadStatusables){
  		var req = org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.GET_EXECUTION_DESC_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		); 
  		req.setParameter("agentId", agentId);
		req.setParameter("moduleName", moduleName);
		req.setParameter("version", moduleVersion);
		return req;
  	},

  	/**
  	 * Fire the reload of an OSGI bundle.
  	 * @param fireReloadEventType {String} Event type to trigger (optionnal)
  	 * @param iLoadStatusables {org.argeo.ria.components.ILoadStatusables[]} Gui parts to update 
  	 * @return {qx.io.remote.Request}
  	 */
  	getReloadBundleService : function(bundleName, bundleVersion, fireReloadEventType, iLoadStatusables){
  		var req = org.argeo.slc.ria.SlcApi.getServiceRequest(
  			org.argeo.slc.ria.SlcApi.RELOAD_BUNDLE_SERVICE,
  			fireReloadEventType,
  			iLoadStatusables
  		);
  		req.setParameter("bundleName", bundleName);
  		req.setParameter("bundleVersion", bundleVersion);
  		return req;
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