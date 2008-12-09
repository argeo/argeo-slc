/**
 * Generic selection model associated to an IView content opened in a given ViewPane.
 * It contains in an array any row/data/node, and triggers changeSelection data events.
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.components.ViewSelection",
{
  extend : qx.core.Object,
  
  /**
   * @param viewId {String} The ViewPane unique id
   */
  construct : function(viewId){
  	this.base(arguments);
  	this.nodes = [];
  	this.setViewId(viewId);
  },

  properties : {
  	/**
  	 * The viewPane unique id 
  	 */
  	viewId : {
  	check : "String",
  	nullable: false
  	}
  },
  
  events : {
  	/**
  	 * Triggered each time the selection changes.
  	 */
  	"changeSelection" : "qx.event.type.Data"
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	/**
  	 * Empty the selection
  	 */
  	clear : function(){
		this.nodes = [];
		this.triggerEvent();
  	},
  	
  	/**
  	 * Add a row or xml node or whatever
  	 * @param node {mixed} Data to add to the selection
  	 */
    addNode : function(node) {
    	this.nodes.push(node);
    	this.triggerEvent();
    },
    
    /**
     * The number of rows/nodes selected
     * @return {Integer}
     */
    getCount : function() {
    	return this.nodes.length;
    },
    
    /**
     * Returns the content of the selection 
     * @return {Array}
     */
    getNodes : function(){
    	return this.nodes;
    },
    
    /**
     * Creates and fire a data event changeSelection
     */
    triggerEvent : function(){
    	this.fireDataEvent("changeSelection", this);
    }
    
  }
});