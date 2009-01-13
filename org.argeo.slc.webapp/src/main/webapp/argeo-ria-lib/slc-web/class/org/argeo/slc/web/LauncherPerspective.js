/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
qx.Class.define("org.argeo.slc.web.LauncherPerspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
  },

  statics : {
  	LABEL : "Slc Execution",
  	ICON : "resource/slc/utilities-terminal.png"
  },
    
  members : {
  	
  	_rightPane : null,
  	  	
  	initViewPanes : function(viewsManager){
  		
  	  this._rightPane = new org.argeo.ria.components.ViewPane("main", "Slc Execution");
  	  this._rightPane.setBackgroundColor("white");
  	  viewsManager.registerViewPane(this._rightPane);      
      viewsManager.getViewPanesContainer().add(this._rightPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var view = viewsManager.initIViewClass(org.argeo.slc.ria.LauncherApplet, "main");
	  view.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPaneById("main").empty();
		viewsManager.getViewPanesContainer().remove(this._rightPane);  		
  	}  	
  	
  }

});