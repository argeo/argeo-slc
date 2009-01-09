/**
 * The main "view" manager (in a standard MVC conception) of the application.
 * It register various containers org.argeo.ria.components.viewPane and feed them with org.argeo.ria.components.IView implementations.
 * It is a singleton and can thus be called by any part of the application.
 * 
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.components.ViewsManager",
{
	type : "singleton",
  extend : qx.core.Object,

  properties : {
  	/**
  	 * The application root (like Application.getRoot()), used to attach and show modal windows.
  	 */
  	applicationRoot : {init : null},
  	/**
  	 * The main container for the org.argeo.ria.components.ViewPane instances. 
  	 */
  	viewPanesContainer : {init: null}
  },
  construct : function(){
  	this.views = {};
  },
  members : {
  	/**
  	 * Initialize and load a given IView implementation into a viewPane.
  	 * The IView itself is returned.
  	 * 
  	 * @param classObj {Clazz} The class object to instantiate
  	 * @param viewPaneId {String} The unique ID of the view pane
  	 * @return {org.argeo.ria.components.IView}
  	 */
  	initIViewClass: function(classObj, viewPaneId){
  		var iView = new classObj;
  		var viewPane = this.getViewPaneById(viewPaneId);
		iView.init(viewPane);		
		var commands = iView.getCommands();
		viewPane.empty();
		if(commands){
			viewPane.setCommands(commands);
			org.argeo.ria.event.CommandsManager.getInstance().addCommands(commands, iView);
		}
		viewPane.setContent(iView); 
		return iView;
  	},
  	
  	/**
  	 * Registers a new viewPane
  	 * @param viewPane {org.argeo.ria.components.ViewPane} The new ViewPane instance
  	 */
  	registerViewPane : function(viewPane){
		this.views[viewPane.getViewId()] = viewPane;
	  	viewPane.getViewSelection().addListener("changeSelection", function(e){
	  		org.argeo.ria.event.CommandsManager.getInstance().refreshCommands(e.getData());
		});  		
  	},
  	/**
  	 * Returns a viewPane by its unique id.
  	 * @param viewPaneId {String} The unique id
  	 * @return {org.argeo.ria.components.ViewPane}
  	 */
  	getViewPaneById : function(viewPaneId){
  		if(this.views[viewPaneId]) return this.views[viewPaneId];
		throw new Error("Cannot find view '"+viewPaneId+"'");  		
  	},
  	/**
  	 * Returns a viewPane current viewSelection object
  	 * @param viewPaneId {String} The unique id. 
  	 * @return {org.argeo.ria.components.ViewSelection}
  	 */
  	getViewPaneSelection : function(viewPaneId){
  		return this.getViewPaneById(viewPaneId).getViewSelection();
  	},
  	/**
  	 * Changes a viewPane title dynamically.
  	 * @param viewPaneId {String} ViewPane unique Id. 
  	 * @param viewTitle {String} the new title for this viewPane.
  	 */
  	setViewPaneTitle : function(viewPaneId, viewTitle){
  		this.getViewPaneById(viewPaneId).setViewTitle(viewTitle);
  	}
  }
});