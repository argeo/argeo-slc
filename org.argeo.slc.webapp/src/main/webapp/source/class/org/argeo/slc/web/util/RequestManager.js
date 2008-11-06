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
			/*
			request.addListener("sending", function(event){
				this.requestCreated(request);
			}, this);
			request.addListener("aborted", function(event){
				this.requestAborted(request);
			}, this);
			request.addListener("failed", function(event){
				this.requestFailed(request);
			}, this);
			request.addListener("timeout", function(event){
				this.requestTimeout(request);
			}, this);
			request.addListener("completed", function(event){
				this.requestCompleted(request);
			}, this);
			*/
			this.enableCommand(request);
			return request;
		},		
	
		requestCreated : function(req){
			this.enableCommand(req);
		},
		
		requestAborted : function(req){
			this.disableCommand();
		},
		
		requestFailed : function(req){
			this.disableCommand();
		},
		
		requestTimeout : function(req){
			this.disableCommand();
		},

		requestCompleted : function(req){
			this.disableCommand();
		},
		
		disableCommand : function(){
			this.command.setEnabled(false);			
			var manager = qx.event.Registration.getManager(this.command);
			manager.removeAllListeners(this.command);
		},
		
		enableCommand : function(request){
			this.command.setEnabled(true);
			qx.ui.core.queue.Manager.flush();
			this.command.addListener("execute", function(){
				request.abort();
			});
		}
	}
});