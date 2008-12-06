qx.Class.define("org.argeo.slc.web.remote.RequestManager",
{
	type : "singleton",
	extend : qx.core.Object,
  
	events : {
		"reload" : "org.argeo.slc.web.event.ReloadEvent"
	},
	
	construct : function(){
	  	this.base(arguments);	  	
	},
	
	members : {
		
		setStopCommand : function(stopCommand){
			this.command = stopCommand;
		},
		
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
	
		fireReloadEvent : function(dataType, content){
			this.fireEvent("reload", org.argeo.slc.web.event.ReloadEvent, [dataType, content]);			
		},
		
		requestCreated : function(e){
			var request = e.getTarget();
			this.enableCommand(request);
		},
		
		requestCompleted : function(e){
			var request = e.getTarget();
			this.disableCommand(request);
		},
		
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
		
		updateGuiParts : function(iLoadStatusables, loadStatus){
			for(var i=0;i<iLoadStatusables.length;i++){
				if(qx.Class.implementsInterface(qx.Class.getByName(iLoadStatusables[i].classname), org.argeo.slc.web.components.ILoadStatusable)){
					iLoadStatusables[i].setOnLoad(loadStatus);
				}else{
					this.debug("Does not implement the ILoadStatusable interface! GUIPART type : "+ iLoadStatusables[i].classname);
				}
			}
		}
	}
});