qx.Class.define("org.argeo.ria.remote.AmqClient", {

	extend : qx.core.Object, 
	construct : function(){
		this.base(arguments);
	},
	members : {
  		// The URI of the MessageListenerServlet
		uri : '/amq',

		// Polling. Set to true (default) if waiting poll for messages is needed
		poll : true,

		// Poll delay. if set to positive integer, this is the time to wait in ms before
		// sending the next poll after the last completes.
		_pollDelay : 0,

		_first : true,
		_pollEvent : function(first) {
		},
		_handlers : new Array(),

		_messages : 0,
		_messageQueue : '',
		_queueMessages : 0,

		_messageHandler : function(request) {
			try {
				if (request.status == 200) {
					var response = request.responseXML
							.getElementsByTagName("ajax-response");
					if (response != null && response.length == 1) {
						for (var i = 0; i < response[0].childNodes.length; i++) {
							var responseElement = response[0].childNodes[i];

							// only process nodes of type element.....
							if (responseElement.nodeType != 1)
								continue;

							var id = responseElement.getAttribute('id');

							var handler = this._handlers[id];
							if (handler != null) {
								for (var j = 0; j < responseElement.childNodes.length; j++) {
									handler(responseElement.childNodes[j]);
								}
							}
						}
					}
				}
			} catch (e) {
				alert(e);
			}
		},

		startBatch : function() {
			this._queueMessages++;
		},

		endBatch : function() {
			this._queueMessages--;
			if (this._queueMessages == 0 && this._messages > 0) {
				var body = this._messageQueue;
				this._messageQueue = '';
				this._messages = 0;
				this._queueMessages++;
				var request = new qx.io.remote.Request(this.uri, "post", "text/plain");
				request.addListener("completed", this.endBatch, this);
				request.send();
			}
		},

		_pollHandler : function(request) {
			this.startBatch();
			try {
				this._messageHandler(request);
				this._pollEvent(this._first);
				this._first = false;
			} catch (e) {
				alert(e);
			}
			this.endBatch();

			if (this._pollDelay > 0)
				qx.event.Timer.once(this._sendPoll, this, this._pollDelay);
			else
				this._sendPoll();
		},

		_sendPoll : function(request) {
			var request = new qx.io.remote.Request(this.uri, "get", "application/xml");
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
		addListener : function(id, destination, handler) {
			this._handlers[id] = handler;
			this._sendMessage(destination, id, 'listen');
		},

		// remove Listener from channel or topic.
		removeListener : function(id, destination) {
			this._handlers[id] = null;
			this._sendMessage(destination, id, 'unlisten');
		},

		_sendMessage : function(destination, message, type) {
			if (this._queueMessages > 0) {
				if (this._messages == 0) {
					this._messageQueue = 'destination=' + destination
							+ '&message=' + message + '&type=' + type;
				} else {
					this._messageQueue += '&d' + this._messages + '='
							+ destination + '&m' + this._messages + '='
							+ message + '&t' + this._messages + '=' + type;
				}
				this._messages++;
			} else {
				this.startBatch();
				var req = new qx.io.remote.Request(this.uri, "post", "application/xml");
				req.setParameter("destination", destination);
				req.setParameter("message", message);
				req.setParameter("type", type);
				req.addListener("completed", this.endBatch, this);
				req.send();
			}
		},

		_startPolling : function() {
			if (this.poll){
				var req = new qx.io.remote.Request(this.uri, "get", "application/xml");
				req.setParameter("timeout", "10");
				req.addListener("completed", this._pollHandler, this);
				req.send();
			}
		}		
	}
});