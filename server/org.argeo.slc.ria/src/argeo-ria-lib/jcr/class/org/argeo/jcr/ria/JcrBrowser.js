/**
 * IPerspective Implementation : Horizontal split pane defining two panes, "list" and "applet".
 */
 
/* ************************************************************************

************************************************************************ */ 
qx.Class.define("org.argeo.jcr.ria.JcrBrowser",
{
  extend : qx.core.Object,
  implement : [org.argeo.ria.components.IPerspective], 
  
  construct : function(){
	  	this.base(arguments);
	  	org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP = {slc:"http://argeo.org/projects/slc/schemas"};
  },
  
  statics : {
  	LABEL : "JCR Browser",
  	ICON : "org.argeo.slc.ria/utilities-terminal.png"
  	//ROLES_RESTRICTION : ["ROLE_ADMIN"]
  },
  
  members : {
  	_splitPane : null,  	
  	
  	initViewPanes : function(viewsManager){
  		
  		this._splitPane = new qx.ui.splitpane.Pane("horizontal");
		var mainPane = new org.argeo.ria.components.ViewPane("fulltree", "Full Tree");
		this._splitPane.add(mainPane, 1);
		viewsManager.registerViewPane(mainPane);      
  		/*
		var uploadPane = new org.argeo.ria.components.ViewPane("upload", "Upload a distribution");
		this._splitPane.add(uploadPane, 1);
		viewsManager.registerViewPane(uploadPane);   
		*/   
		viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
  		var nodeProvider = new org.argeo.jcr.ria.provider.XmlNodeProvider();
  		nodeProvider.initProvider({
  			xmlSrc : "/org.argeo.slc.webapp/getJcrItem.jcr?",
  			dynamic : true,
  			pathParameter:"path"
  			});  		
  		var rootNode = new org.argeo.jcr.ria.model.Node("Root", true);
  		rootNode.setPath('/slc');
  		rootNode.setNodeProvider(nodeProvider);
  		var dataModel = new org.argeo.jcr.ria.model.DataModel(rootNode);
		var testView = viewsManager.initIViewClass(org.argeo.jcr.ria.views.XmlNodeEditor, "fulltree", dataModel);
		testView.load();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}
  	
  }

});