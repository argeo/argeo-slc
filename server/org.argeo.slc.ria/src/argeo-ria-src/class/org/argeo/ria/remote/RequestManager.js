/**
 * A management class for all request sent to the server
 * Basically, to access the server, always get a new Request object from this class.
 * It will then trigger various user-interface events during the Request lifecycle. 
 * 
 * For the moment, it's about the "Stop" button command, handling any passed ILoadStatusable states, 
 * and logging the Request status/errors.
 * 
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.remote.RequestManager",
{
	type : "singleton",
	extend : qx.core.Object,
  
	events : {
		/**
		 * Triggered on the user demand at the end of the Request 
		 */
		"reload" : "org.argeo.ria.event.ReloadEvent"
	},
	
	construct : function(){
	  	this.base(arguments);	  	
	},
	
	members : {
		/**
		 * Sets the unique "stop" command of the application.
		 * @param stopCommand {org.argeo.ria.event.Command} The command
		 */
		setStopCommand : function(stopCommand){
			this.command = stopCommand;
		},
		
		/**
		 * Creates a Request and handle various parts of its lifecycle.
		 * @see org.argeo.ria.event.ReloadEvent
		 * @see org.argeo.ria.components.ILoadStatusable
		 * 
		 * @param url {String} The server url
		 * @param method {String} Connexion method (POST, GET, etc.)
		 * @param responseType {String} Expected response mime type (application/xml, etc...).
		 * @param fireReloadEventType {String} On user-demand, if this parameter is not null, a org.argeo.ria.event.ReloadEvent will be triggered when the request is completed. 
		 * @param iLoadStatusables {Array} An array of ILoadStatusable implementations that need to be updated by the Request state (loading/ended).
		 * @return {qx.io.remote.Request}
		 */
		getRequest : function(url, method, responseType, fireReloadEventType, iLoadStatusables){
			var request = new qx.io.remote.Request(url, method, responseType);
			if(iLoadStatusables){
				request.setUserData("iLoadStatusables", iLoadStatusables);
			}
			if(fireReloadEventType){
				request.addListener("completed", function(response){
					this.fireReloadEvent(fireReloadEventType, response.getContent());
				}, this);
			}
			this.enableCommand(request);
			request.addListener("timeout", this.requestTerminated, this);
			request.addListener("failed", this.requestTerminated, this);
			request.addListener("aborted", this.requestTerminated, this);
			request.addListener("completed", this.requestCompleted, this);	
			return request;
		},		
	
		/**
		 * Creates a ReloadEvent and fire it.
		 * @param dataType {String} The data type 
		 * @param content {mixed} The content of the request response.
		 */
		fireReloadEvent : function(dataType, content){
			this.fireEvent("reload", org.argeo.ria.event.ReloadEvent, [dataType, content]);			
		},
		
		/**
		 * Triggered when request is created
		 * @param e {qx.event.type.Event} The event
		 */
		requestCreated : function(e){
			var request = e.getTarget();
			this.enableCommand(request);
		},
		
		/**
		 * Triggered when request is completed normally
		 * @param e {qx.event.type.Event} The event
		 */
		requestCompleted : function(e){
			var request = e.getTarget();
			this.disableCommand(request);
		},
		
		/**
		 * Triggered when request is completed abnormally
		 * @param e {qx.event.type.Event} The event
		 */
		requestTerminated : function(e){
			var request = e.getTarget();
			var errorType = e.getType();
			this.disableCommand(request);
			var message = "";
			if(errorType == "aborted"){
				message = "Request aborted by user";
			}else if(errorType == "failed"){
				message = "Request failed!";
			}else if(errorType == "timeout"){
				message = "Request timed out!";
			}
			this.error(message);
		},
		
		/**
		 * Triggered by a request creation. Update the GUI parts according to its status. 
		 * @param request {qx.io.remote.Request} The current Request 
		 */
		disableCommand : function(request){
			this.command.setEnabled(false);
			if(request.getUserData("iLoadStatusables")){
				this.updateGuiParts(request.getUserData("iLoadStatusables"), false);
			}
			var listener = request.getUserData("listener");
			if(listener){
				this.command.removeListener("execute", listener);
			}
		},
		
		/**
		 * Triggered by a request ending. Update the GUI parts according to its status. 
		 * @param request {qx.io.remote.Request} The current Request 
		 */
		enableCommand : function(request){
			this.command.setEnabled(true);
			if(request.getUserData("iLoadStatusables")){
				this.updateGuiParts(request.getUserData("iLoadStatusables"), true);
			}
			qx.ui.core.queue.Manager.flush();
			var listener = request.abort;
			request.setUserData("listener", listener);
			this.command.addListener("execute", listener, request);
		},
		
		/**
		 * Update the ILoadStatusable implementations
		 * @param iLoadStatusables {Array} An array of ILoadStatusable 
		 * @param loadStatus {Boolean} The current status of a request 
		 */
		updateGuiParts : function(iLoadStatusables, loadStatus){
			for(var i=0;i<iLoadStatusables.length;i++){
				if(qx.Class.implementsInterface(qx.Class.getByName(iLoadStatusables[i].classname), org.argeo.ria.components.ILoadStatusable)){
					iLoadStatusables[i].setOnLoad(loadStatus);
				}else{
					this.debug("Does not implement the ILoadStatusable interface! GUIPART type : "+ iLoadStatusables[i].classname);
				}
			}
		}
	}
});