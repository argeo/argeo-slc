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
	  	org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP = 
  		{
	  		slc:"http://argeo.org/projects/slc/schemas",
	  		fn:"http://www.w3.org/2005/xpath-functions", 
			fn_old:"http://www.w3.org/2004/10/xpath-functions", 
			xs:"http://www.w3.org/2001/XMLSchema" ,
			jcr:"http://www.jcp.org/jcr/1.0" ,
			mix:"http://www.jcp.org/jcr/mix/1.0", 
			sv:"http://www.jcp.org/jcr/sv/1.0" ,
			rep:"internal" ,
			nt:"http://www.jcp.org/jcr/nt/1.0"
  		};
  },
  
  statics : {
  	LABEL : "JCR Browser (Tech Preview)",
  	ICON : "org.argeo.slc.ria/utilities-terminal.png"/*,
  	ACCESS : "!ROLE_ANONYMOUS"*/
  },
  
  members : {
  	_splitPane : null,  	
  	
  	initViewPanes : function(viewsManager){
  		
  		this._tBar = new org.argeo.ria.components.ViewPane("toolbar", "");
  		this._tBar.header.setVisibility("excluded");
  		viewsManager.getViewPanesContainer().add(this._tBar);
  		
  		this._splitPane = new qx.ui.splitpane.Pane("horizontal");
		var leftPane = new org.argeo.ria.components.TabbedViewPane("treeview", "Tree View");
		this._splitPane.add(leftPane, 1);
		var mainPane = new org.argeo.ria.components.TabbedViewPane("editor", "Editor View", true);
		this._splitPane.add(mainPane, 2);
  		viewsManager.registerViewPane(this._tBar);
		viewsManager.registerViewPane(mainPane);      
		viewsManager.registerViewPane(leftPane);   
		viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
  		var nodeProvider = new org.argeo.jcr.ria.provider.XmlNodeProvider();
  		nodeProvider.initProvider({
  			xmlSrc : "/org.argeo.slc.webapp/getJcrItem.jcr?",
  			dynamic : true,
  			pathParameter:"path"
  			});  		
  		var rootNode = new org.argeo.jcr.ria.model.Node("Root", nodeProvider, true);
  		rootNode.setPath('/slc');
  		var dataModel = new org.argeo.jcr.ria.model.DataModel(rootNode);
  		
  		var inputView = viewsManager.initIViewClass(org.argeo.jcr.ria.views.ContextNodeInputView, "toolbar", dataModel);
  		inputView.load();
  		
		var listView = viewsManager.initIViewClass(org.argeo.jcr.ria.views.ListView, "editor", dataModel);
		listView.load();
		
		var treeView = viewsManager.initIViewClass(org.argeo.jcr.ria.views.TreeView, "treeview", dataModel);
		treeView.load();
		
		var queriesView = viewsManager.initIViewClass(org.argeo.jcr.ria.views.QueriesView, "treeview", dataModel);
		queriesView.load();
		
		dataModel.requireContextChange();
  	},
  	
  	remove : function(viewsManager){
  		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  		viewsManager.getViewPanesContainer().remove(this._tBar);
  	}
  	
  }

});