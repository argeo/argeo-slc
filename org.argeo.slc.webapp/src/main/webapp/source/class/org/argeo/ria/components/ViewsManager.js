/**
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.components.ViewsManager",
{
	type : "singleton",
  extend : qx.core.Object,

  properties : {
  	applicationRoot : {init : null},
  	viewPanesContainer : {init: null}
  },
  construct : function(){
  	this.views = {};
  },
  members : {
  	
  	initIViewClass: function(classObj, viewPaneId){
  		//var iView = eval("new "+iViewClass+"()");
  		//var classObj = qx.Class.getByName(iViewClass);
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
  	
  	registerViewPane : function(viewPane){
		this.views[viewPane.getViewId()] = viewPane;
	  	viewPane.getViewSelection().addListener("changeSelection", function(e){
	  		org.argeo.ria.event.CommandsManager.getInstance().refreshCommands(e.getData());
		});  		
  	},
  	getViewPaneById : function(viewPaneId){
  		if(this.views[viewPaneId]) return this.views[viewPaneId];
		throw new Error("Cannot find view '"+viewPaneId+"'");  		
  	},
  	getViewPaneSelection : function(viewPaneId){
  		return this.getViewPaneById(viewPaneId).getViewSelection();
  	},
  	setViewPaneTitle : function(viewPaneId, viewTitle){
  		this.getViewPaneById(viewPaneId).setViewTitle(viewTitle);
  	}
  }
});