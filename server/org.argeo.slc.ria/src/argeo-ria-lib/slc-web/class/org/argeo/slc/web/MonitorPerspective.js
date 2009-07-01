/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
 
/* ************************************************************************

#asset(argeo-ria-lib/slc/resource/org.argeo.slc.ria/*)

************************************************************************ */ 
qx.Class.define("org.argeo.slc.web.MonitorPerspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
	  	org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP = {slc:"http://argeo.org/projects/slc/schemas"};
  },
  
  statics : {
  	LABEL : "SLC Monitoring",
  	ICON : "ria/system-run.png"
  },
  
  members : {
  	_splitPane : null,  	
  	
  	initViewPanes : function(viewsManager){
  		
      this._splitPane = new qx.ui.splitpane.Pane("horizontal");
      
      this._secondSplitPane = new qx.ui.splitpane.Pane("vertical");
      this._splitPane.add(this._secondSplitPane, 0);
      
	  var topLeft = new org.argeo.ria.components.ViewPane("selector", "Fows and Modules");
	  topLeft.set({width:300, height:400});
	  viewsManager.registerViewPane(topLeft);
	  this._secondSplitPane.add(topLeft, 0);
	  this._secondSplitPane.setDecorator(null);
	  
	  var bottomLeft = new org.argeo.ria.components.ViewPane("properties", "Properties");	  
	  viewsManager.registerViewPane(bottomLeft);
	  this._secondSplitPane.add(bottomLeft, 1);
	    
  	  var rightPane = new org.argeo.ria.components.TabbedViewPane("applet", "Detail");  	  
  	  viewsManager.registerViewPane(rightPane);
  	  
  	  
  	  
  	  
	  this._splitPane.add(rightPane, 1);
      
      viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var view = viewsManager.initIViewClass(org.argeo.slc.ria.monitor.FlowsSelectorView, "selector");
	  view.load();
	  var pView = viewsManager.initIViewClass(org.argeo.slc.ria.monitor.PropertiesView, "properties");
	  pView.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}
  	
  }

});