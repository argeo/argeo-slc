/**
 * A standard client for sending/receiving JMS message.
 * It is based on ActiveMQ Ajax implementation.
 */
qx.Class.define("org.argeo.ria.remote.JmsClient", {

	type : "singleton",
	extend : qx.core.Object,
	
	construct : function(){
		this.base(arguments);
	},
	members : {
  		// The URI of the MessageListenerServlet
		uri : '../amq',		

		// Polling. Set to true (default) if waiting poll for messages is needed
		poll : true,
		pollTimeout : 25,
		interrupt : false,

		// Poll delay. if set to positive integer, this is the time to wait in ms before
		// sending the next poll after the last completes.
		_pollDelay : 0,

		_first : true,
		/**
		 * Trigger at each poll event.
		 * @param first {Boolean} Whether it is the first event to be triggered. 
		 */
		_pollEvent : function(first) {},
		_handlers : new Array(),

		/**
		 * Parses the XML response to a message POST.
		 * @param response {qx.io.remote.Response} The query response
		 */
		_messageHandler : function(response) {
			var doc = response.getContent();			
			var messages = org.argeo.ria.util.Element.selectNodes(doc, "//response");
			for(var i=0;i<messages.length;i++){
				var id = messages[i].getAttribute("id");
				if(id && this._handlers[id]){
					this._handlers[id](messages[i]);
				}
			}
		},
		
		/**
		 * Parses the empty response of a poll GET query.
		 * @param response {qx.io.remote.Response} The query response
		 */
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

		/**
		 * Send a poll query : GET query and no paramter at all. 
		 * @param request {qx.io.remote.Request} A request object
		 */
		_sendPoll : function(request) {
			if(this.interrupt) return;
			var request = new qx.io.remote.Request(this.uri, "GET", "application/xml");
			request.setTimeout(this.pollTimeout*1000+5000);
			request.addListener("completed", this._pollHandler, this);
			request.send();
		},

		/**
		 * Add a function that gets called on every poll response, after all received
		 * messages have been handled.  The poll handler is past a boolean that indicates
		 * if this is the first poll for the page.
		 * 
		 * @param func {Function} The handler to be called. 
		 */
		addPollHandler : function(func) {
			var old = this._pollEvent;
			this._pollEvent = function(first) {
				old(first);
				func(first);
			}
		},

		/**
		 * Send a JMS message to a destination (eg topic://MY.TOPIC).  
		 * Message should be xml or encoded xml content.
		 * 
		 * @param destination {String} The topic destination
		 * @param message {String} XML encoded message
		 * @param properties {Map} A map of additional parameters to add to the query.
		 */
		sendMessage : function(destination, message, properties) {
			this._sendMessage(destination, message, 'send', properties);
		},

		/**
		 * Listen on a channel or topic.   handler must be a function taking a message arguement
		 * @param id {String} A unique identifier for this handler
		 * @param destination {String} The topic to listen to (topic://MY.TOPIC) 
		 * @param handler {Function} The handler to trigger when receiving a message 
		 * @param context {Object} An object to bind on the handler.
		 */
		addListener : function(id, destination, handler, context) {
			this._handlers[id] = qx.lang.Function.bind(handler, context);
			this._sendMessage(destination, id, 'listen');
		},

		/**
		 * Remove Listener from channel or topic.
		 * @param id {String} identifier of the handler to remove.
		 * @param destination {String} The topic to listen to (topic://MY.TOPIC) 
		 */ 
		removeListener : function(id, destination) {
			this._handlers[id] = null;
			this._sendMessage(destination, id, 'unlisten');
		},
		
		/**
		 * Send a message of a given type.
		 * @param destination {String} The topic to listen to (topic://MY.TOPIC) 
		 * @param message {String} XML encoded message
		 * @param type {String} The JMS-Type of message (listen, unlisten, send).
		 * @param properties {Map} A map of additional parameters to add to the query.
		 */
		_sendMessage : function(destination, message, type, properties) {
			var req = new qx.io.remote.Request(this.uri, "POST", "text/plain");
			req.setParameter("destination", destination);
			req.setParameter("message", message);
			req.setParameter("type", type);
			if(properties){
				for(var key in properties){
					req.setParameter(key, properties[key]);
				}
			}
			//req.addListener("completed", this.endBatch, this);
			req.send();
		},

		/**
		 * Starts a poll on the JMS server.
		 */
		startPolling : function() {
			if (this.poll){
				this.interrupt = false;
				var req = new qx.io.remote.Request(this.uri, "GET", "application/xml");
				req.setParameter("timeout", "10");
				req.addListener("completed", this._pollHandler, this);
				req.send();
			}
		},
		
		/**
		 * Stops polling the JMS server.
		 */
		stopPolling : function(){
			this.interrupt = true;
		}
	}
});