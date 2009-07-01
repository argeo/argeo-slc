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
  	ICON : "org.argeo.slc.ria/utilities-terminal.png"
  },
    
  members : {
  	  	  	
  	initViewPanes : function(viewsManager){

      this._splitPane = new qx.ui.splitpane.Pane("horizontal");      
      this._secondSplit = new qx.ui.splitpane.Pane("vertical");
      this._secondSplit.setDecorator(null);
      this._thirdSplit = new qx.ui.splitpane.Pane("horizontal");
      this._thirdSplit.setDecorator(null);
            
	  var selectorPane = new org.argeo.ria.components.ViewPane("selector", "Available Scripts");
	  selectorPane.set({width:290});
	  viewsManager.registerViewPane(selectorPane);
	  
	  var batchPane = new org.argeo.ria.components.ViewPane("batch", "Batch");
  	  batchPane.set({width: 500, height:500});
	  viewsManager.registerViewPane(batchPane);
	  
  	  var logPane = new org.argeo.ria.components.ViewPane("main", "Executions Log");
  	  viewsManager.registerViewPane(logPane);
  	  
  	  var specEditorPane = new org.argeo.ria.components.ViewPane("editor", "Specs Editor");
  	  specEditorPane.set({width:200});
  	  viewsManager.registerViewPane(specEditorPane);
  	  
	  this._secondSplit.add(this._thirdSplit, 0);	  
	  this._secondSplit.add(logPane, 1);	  
	  
	  this._thirdSplit.add(batchPane, 0);
	  this._thirdSplit.add(specEditorPane, 1);
	  
	  this._splitPane.add(selectorPane, 0);	  
	  this._splitPane.add(this._secondSplit, 1);

	  
      viewsManager.getViewPanesContainer().add(this._splitPane, {flex:1});
  		
  	},
  	
  	initViews : function(viewsManager){
	  var formApplet = viewsManager.initIViewClass(org.argeo.slc.ria.FlowsSelectorView, "selector");
	  formApplet.load();
	  
	  var batchApplet = viewsManager.initIViewClass(org.argeo.slc.ria.BatchView, "batch");
	  batchApplet.load();
	  
	  var editor = viewsManager.initIViewClass(org.argeo.slc.ria.SpecsEditorView, "editor");
	  editor.load();
	  
	  var logger = viewsManager.initIViewClass(org.argeo.slc.ria.SlcExecLoggerApplet, "main");
	  logger.load();
  	},
  	
  	remove : function(viewsManager){
		viewsManager.getViewPanesContainer().remove(this._splitPane);  		
  	}  	
  	
  }

});