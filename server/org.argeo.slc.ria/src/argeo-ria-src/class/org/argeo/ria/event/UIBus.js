/**
 * A generic channel for posting reload events.
 */
qx.Class.define("org.argeo.ria.event.UIBus",
{	
	type : "singleton",
	extend : qx.core.Object,
  
	properties :{
		listeners : {
			check : "Map"
		},
		notifiers : {
			check : "Array"
		}
	},
	construct : function(){
		this.base(arguments);
		this.setListeners({});
		this.setNotifiers([]);
	},
	members : {
		addListener : function(eventType, listenerFunction, contextObject){
			var notifiers = this.getNotifiers();
			for(var i=0;i<notifiers.length;i++){
				notifiers[i].addListener(eventType);
			}
			var typeListeners = this.getListeners()[eventType];
			if(!typeListeners) {
				typeListeners = new Array();
				this.getListeners()[eventType] = typeListeners;
			}
			var func = listenerFunction;
			if(contextObject){
				func = qx.lang.Function.bind(listenerFunction, contextObject);
			}
			typeListeners.push(func);
		},
		removeListener : function(eventType, listenerFunction, contextObject){
			var notifiers = this.getNotifiers();
			for(var i=0;i<notifiers.length;i++){
				notifiers[i].removeListener(eventType);
			}
			var typeListeners = this.getListeners()[eventType];
			if(!typeListeners) return;
			var func = listenerFunction;
			if(contextObject){
				func = qx.lang.Function.bind(listenerFunction, contextObject);
			}
			for(var i=0;i<typeListeners.length;i++){
				if(typeListeners[i] == func){
					delete(typeListeners[i]);
					return;
				}
			}
		},
		dispatchEvent : function(eventType, eventData){
			var listeners = this.getListeners()[eventType];
			if(listeners && listeners.length){
				for(var i=0;i<listeners.length;i++){
					listeners[i](eventData);
				}
			}
		},
		registerNotifier : function(notifier){
			this.getNotifiers().push(notifier);
		}
	}
});