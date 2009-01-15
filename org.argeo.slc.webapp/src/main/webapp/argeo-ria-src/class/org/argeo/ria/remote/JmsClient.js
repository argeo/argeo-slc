qx.Class.define("org.argeo.ria.remote.JmsClient", {

	type : "singleton",
	extend : qx.core.Object,
	
	construct : function(){
		this.base(arguments);
	},
	members : {
  		// The URI of the MessageListenerServlet
		uri : '/org.argeo.slc.webapp/amq',		

		// Polling. Set to true (default) if waiting poll for messages is needed
		poll : true,
		pollTimeout : 25,
		interrupt : false,

		// Poll delay. if set to positive integer, this is the time to wait in ms before
		// sending the next poll after the last completes.
		_pollDelay : 0,

		_first : true,
		_pollEvent : function(first) {},
		_handlers : new Array(),

		_messageHandler : function(response) {
			var doc = response.getContent();
			var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
			var messages = org.argeo.ria.util.Element.selectNodes(doc, "//response", NSMap);
			for(var i=0;i<messages.length;i++){
				var id = messages[i].getAttribute("id");
				if(id && this._handlers[id]){
					this._handlers[id](messages[i]);
				}
			}
		},
		
		_pollHandler : function(response) {
			try {
				this._messageHandler(response);
				this._pollEvent(this._first);
				this._first = false;
			} catch (e) {
				alert(e);
			}

			if (this._pollDelay > 0)
				qx.event.Timer.once(this._sendPoll, this, this._pollDelay);
			else
				this._sendPoll();
		},

		_sendPoll : function(request) {
			if(this.interrupt) return;
			var request = new qx.io.remote.Request(this.uri, "GET", "application/xml");
			request.setTimeout(this.pollTimeout*1000+5000);
			request.addListener("completed", this._pollHandler, this);
			request.send();
		},

		// Add a function that gets called on every poll response, after all received
		// messages have been handled.  The poll handler is past a boolean that indicates
		// if this is the first poll for the page.
		addPollHandler : function(func) {
			var old = this._pollEvent;
			this._pollEvent = function(first) {
				old(first);
				func(first);
			}
		},

		// Send a JMS message to a destination (eg topic://MY.TOPIC).  Message should be xml or encoded
		// xml content.
		sendMessage : function(destination, message) {
			this._sendMessage(destination, message, 'send');
		},

		// Listen on a channel or topic.   handler must be a function taking a message arguement
		addListener : function(id, destination, handler, context) {
			this._handlers[id] = qx.lang.Function.bind(handler, context);
			this._sendMessage(destination, id, 'listen');
		},

		// remove Listener from channel or topic.
		removeListener : function(id, destination) {
			this._handlers[id] = null;
			this._sendMessage(destination, id, 'unlisten');
		},

		_sendMessage : function(destination, message, type) {
			var req = new qx.io.remote.Request(this.uri, "POST", "text/plain");
			req.setParameter("destination", destination);
			req.setParameter("message", message);
			req.setParameter("type", type);
			//req.addListener("completed", this.endBatch, this);
			req.send();
		},

		startPolling : function() {
			if (this.poll){
				this.interrupt = false;
				var req = new qx.io.remote.Request(this.uri, "GET", "application/xml");
				req.setParameter("timeout", "10");
				req.addListener("completed", this._pollHandler, this);
				req.send();
			}
		},
		
		stopPolling : function(){
			this.interrupt = true;
		}
	}
});