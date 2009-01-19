/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
qx.Class.define("org.argeo.slc.web.LauncherPerspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
	  	org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP = {slc:"http://argeo.org/projects/slc/schemas"};
  },

  statics : {
  	LABEL : "Slc Execution",
  	ICON : "resource/slc/utilities-terminal.png"
  },
    
  members : {
  	
  	_rightPane : null,
  	  	
  	initViewPanes : function(viewsManager){

      this._splitPane = new qx.ui.splitpane.Pane("horizontal");
	  var topLeft = new org.argeo.ria.components.ViewPane("form", "Execution Launcher", {
	  	orientation : "horizontal",
	  	min : 36
	  });
	  topLeft.set({width:290});
	  viewsManager.registerViewPane(topLeft);
	    
	  this._splitPane.add(topLeft, 0);
  	  var rightPane = new org.argeo.ria.components.ViewPane("main", "Executions Log");  	  
  	  viewsManager.registerViewPane(rightPane);
	  this._splitPane.add(rightPane, 1);
      
      viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var formApplet = viewsManager.initIViewClass(org.argeo.slc.ria.LauncherApplet, "form");
	  formApplet.load();
	  
	  var logger = viewsManager.initIViewClass(org.argeo.slc.ria.SlcExecLoggerApplet, "main");
	  logger.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPaneById("main").empty();
  		viewsManager.getViewPaneById("form").empty();
		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}  	
  	
  }

});