qx.Class.define("org.argeo.slc.web.util.RequestManager",
{
	type : "singleton",
	extend : qx.core.Object,
  
	construct : function(){
	  	this.base(arguments);	  	
	},
	
	members : {
		
		setStopCommand : function(stopCommand){
			this.command = stopCommand;
		},
		
		getRequest : function(url, method, responseType){
			var request = new qx.io.remote.Request(url, method, responseType);
			this.enableCommand(request);
			request.addListener("timeout", function(e){
				this.requestTimeout(request);
			}, this);
			request.addListener("failed", function(e){
				this.requestFailed(request);
			}, this);
			request.addListener("aborted", function(e){
				this.requestFailed(request);
			}, this);
			return request;
		},		
	
		requestCreated : function(req){
			this.enableCommand(req);
		},
		
		requestAborted : function(req){
			this.disableCommand(req);
		},
		
		requestFailed : function(req){
			this.disableCommand(req);
		},
		
		requestTimeout : function(req){
			this.disableCommand(req);
		},

		requestCompleted : function(request){
			this.disableCommand(request);
		},
		
		disableCommand : function(request){
			this.command.setEnabled(false);
			var listener = request.getUserData("listener");
			if(listener){
				this.command.removeListener("execute", listener);
			}
		},
		
		enableCommand : function(request){
			this.command.setEnabled(true);
			qx.ui.core.queue.Manager.flush();
			var listener = request.abort;
			request.setUserData("listener", listener);
			this.command.addListener("execute", listener);
		}
	}
});