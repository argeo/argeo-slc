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
  	LABEL : "SLC Administration",
  	ICON : "org.argeo.slc.ria/utilities-terminal.png"
  	//ROLES_RESTRICTION : ["ADMIN_ROLE"]
  },
  
  members : {
  	_splitPane : null,  	
  	
  	initViewPanes : function(viewsManager){
  		
  		this._splitPane = new qx.ui.splitpane.Pane("vertical");
		var mainPane = new org.argeo.ria.components.ViewPane("distrib", "Modular Distributions");
		this._splitPane.add(mainPane, 10);
		var uploadPane = new org.argeo.ria.components.ViewPane("upload", "Upload a distribution");
		this._splitPane.add(uploadPane, 1);
		viewsManager.registerViewPane(mainPane);      
		viewsManager.registerViewPane(uploadPane);      
		viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var pView = viewsManager.initIViewClass(org.argeo.slc.ria.monitor.DistListView, "distrib");
	  var view = viewsManager.initIViewClass(org.argeo.slc.ria.monitor.UploadView, "upload");
	  pView.load();
	  view.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}
  	
  }

});