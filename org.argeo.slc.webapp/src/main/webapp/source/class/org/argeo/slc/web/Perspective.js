/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.Perspective",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
  },
  
  members : {
  	  	
  	initViewPanes : function(viewsManager){
  		
      var splitPane = new qx.ui.splitpane.Pane("horizontal");
	  var topLeft = new org.argeo.ria.components.ViewPane("list", "Collection", {
	  	orientation : "horizontal",
	  	min : 36
	  });
	  topLeft.set({width:300});
	  viewsManager.registerViewPane(topLeft);
	    
	  splitPane.add(topLeft, 0);
  	  var rightPane = new org.argeo.ria.components.ViewPane("applet", "Test Detail");  	  
  	  viewsManager.registerViewPane(rightPane);
	  splitPane.add(rightPane, 1);
      
      viewsManager.getViewPanesContainer().add(splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var view = viewsManager.initIViewClass(org.argeo.slc.web.TestList, "list");
	  view.load();
	  var command = org.argeo.ria.event.CommandsManager.getInstance().executeCommand("loadtestlist");
  	}
  	
  }

});