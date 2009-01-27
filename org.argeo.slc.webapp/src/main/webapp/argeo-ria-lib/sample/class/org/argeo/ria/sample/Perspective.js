/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
qx.Class.define("org.argeo.ria.sample.Perspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
  },
  
  members : {
  	  	
  	initViewPanes : function(viewsManager){
  		
      this.splitPane = new qx.ui.splitpane.Pane("horizontal");
	  var topLeft = new org.argeo.ria.components.ViewPane("list", "Sample List", {
	  	orientation : "horizontal",
	  	min : 36
	  });
	  topLeft.set({width:300});
	  viewsManager.registerViewPane(topLeft);
	    
	  this.splitPane.add(topLeft, 0);
  	  var rightPane = new org.argeo.ria.components.ViewPane("applet", "Sample Applet");  	  
  	  viewsManager.registerViewPane(rightPane);
	  this.splitPane.add(rightPane, 1);
      
      viewsManager.getViewPanesContainer().add(this.splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var view = viewsManager.initIViewClass(org.argeo.ria.sample.List, "list");
	  view.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPaneById("list").empty();
  		viewsManager.getViewPaneById("applet").empty();
  		viewsManager.getViewPanesContainer().remove(this.splitPane);
  	}
  	
  }

});