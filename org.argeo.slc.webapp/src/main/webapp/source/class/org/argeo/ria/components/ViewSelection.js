qx.Class.define("org.argeo.ria.components.ViewSelection",
{
  extend : qx.core.Object,
  
  construct : function(viewId){
  	this.base(arguments);
  	this.nodes = [];
  	this.setViewId(viewId);
  },

  properties : {
  	viewId : {
  	check : String,
  	nullable: false
  	}
  },
  
  events : {
  	"changeSelection" : "qx.event.type.Data"
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	clear : function(){
		this.nodes = [];
		this.triggerEvent();
  	},
  	
    addNode : function(node) {
    	this.nodes.push(node);
    	this.triggerEvent();
    },
    
    getCount : function() {
    	return this.nodes.length;
    },
    
    getNodes : function(){
    	return this.nodes;
    },
    
    triggerEvent : function(){
    	this.fireDataEvent("changeSelection", this);
    }
    
  }
});