/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
 
/* ************************************************************************

#asset(org.argeo.slc.ria/*)

************************************************************************ */ 
qx.Class.define("org.argeo.slc.web.Perspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
	  	org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP = {slc:"http://argeo.org/projects/slc/schemas"};
  },
  
  statics : {
  	LABEL : "Test Results",
  	ICON : "org.argeo.slc.ria/office-chart.png"
  },
  
  members : {
  	_splitPane : null,  	
  	
  	initViewPanes : function(viewsManager){
  		
      this._splitPane = new qx.ui.splitpane.Pane("horizontal");
	  var topLeft = new org.argeo.ria.components.ViewPane("list", "Collection", {
	  	orientation : "horizontal",
	  	min : 36
	  });
	  topLeft.set({width:300});
	  viewsManager.registerViewPane(topLeft);
	    
	  this._splitPane.add(topLeft, 0);
  	  var rightPane = new org.argeo.ria.components.TabbedViewPane("applet", "Test Detail");  	  
  	  viewsManager.registerViewPane(rightPane);
	  this._splitPane.add(rightPane, 1);
      
      viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var view = viewsManager.initIViewClass(org.argeo.slc.web.TestList, "list");
	  view.load();
	  view.loadCollections();
	  view.loadList();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPaneById("list").empty();
  		viewsManager.getViewPaneById("applet").empty();
  		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}
  	
  }

});