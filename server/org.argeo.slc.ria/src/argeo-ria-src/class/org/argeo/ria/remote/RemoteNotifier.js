/**
 * This can be triggered at the end of a IO Request. In that case, it contains
 * a data type as an identifier, and the request response itself.
 * Can be used this way to listen for data changes from various parts of the application.
 */
qx.Class.define("org.argeo.ria.remote.RemoteNotifier",
{
	extend : qx.core.Object,
  
	construct : function(uri, pollService, addService, removeService){
		this.base(arguments);
		this.setUri(uri);
		this.setPollService(pollService);
		this.setAddService(addService);
		this.setRemoveService(removeService);
	},
	properties :{
		uri : {
			check : "String"			
		},
		pollService : {check : "String"},
		addService : {check : "String"},
		removeService : {check : "String"},
		eventParamName : {check : "String", init:"eventType"},
		eventXPath : {check : "String", init:"//event"},
		eventTypeXPath : {check : "String", init:"@type"},
		eventDataXPath : {check : "String", init:"@data"},
		answerStatusXPath : {check : "String", init:"slc:execution-answer/slc:status"},
		timeout : {			
			init : 20000
		},
		errorTimeout : {
			init : 5000
		},
		interrupt : {
			check : "Boolean",
			init : false
		}
	},
	members : {
		addListener : function(eventType, eventParamName){
			var req = this._getRequest(this.getAddService());
			req.setParameter(this.getEventParamName(), eventType);
			req.send();
		},		
		removeListener : function(eventType, eventParamName){
			var req = this._getRequest(this.getRemoveService());
			req.setParameter(this.getEventParamName(), eventType);
			req.send();
		},
		startPolling : function(){
			this.setInterrupt(false);
			var req = this._getRequest(this.getPollService());
			req.setParameter("timeout", "10");
			req.addListener("completed", this._pollHandler, this);
			req.send();			
		},
		stopPolling : function(){
			this.setInterrupt(true);
		},
		_poll : function(){
			if(this.getInterrupt()) return;
			var req = this._getRequest(this.getPollService());
			req.setParameter("timeout", this.getTimeout());
			req.setTimeout(this.getTimeout() + 5000);
			req.addListener("completed", this._pollHandler, this);
			req.addListener("failed", this._errorHandler, this);
			req.addListener("timeout", this._errorHandler, this);
			req.addListener("aborted", this._errorHandler, this);
			req.send();
		},
		_pollHandler : function(response){
			// Parse response
			var status = org.argeo.ria.util.Element.getSingleNodeText(response.getContent(), this.getAnswerStatusXPath());
			if(status && status == "ERROR"){
				this._errorHandler();
				return;
			}
			var messages = org.argeo.ria.util.Element.selectNodes(response.getContent(), this.getEventXPath());
			if(messages){
				for(var i=0;i<messages.length;i++){
					try{
						var eventType = org.argeo.ria.util.Element.getSingleNodeText(messages[i], this.getEventTypeXPath());
						var eventData = org.argeo.ria.util.Element.getSingleNodeText(messages[i], this.getEventDataXPath());
						org.argeo.ria.event.UIBus.getInstance().dispatchEvent(eventType, eventData);
					}catch(e){
						this.error(e);
					}
				}
			}
			this._poll();
		},
		_errorHandler : function(){
			// Wait an try again later
			qx.event.Timer.once(this._poll, this, this.getErrorTimeout());
		},
		_getRequest : function(service, method, type){
			return new qx.io.remote.Request(
				this.getUri()+service, 
				method || "GET", 
				type||"application/xml"
			);
		}
	}
});