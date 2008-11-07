/* ************************************************************************

  Copyright:

   License:

   Authors:

************************************************************************ */

/* ************************************************************************

#asset(slc/*)

************************************************************************ */

/**
 * This is the main application class of your custom application "slc"
 */
qx.Class.define("org.argeo.slc.web.Application",
{
  extend : qx.application.Standalone,


  properties : 
  {
  	model : {nullable:true},
  	commandManager : {
  		init: null
  	}
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /**
     * This method contains the initial application code and gets called 
     * during startup of the application
     */
    main : function()
    {
      // Call super class
      this.base(arguments);
      this.views = {};      

      // Enable logging in debug variant
      if (qx.core.Variant.isSet("qx.debug", "on"))
      {
        qx.log.appender.Native;
        qx.log.appender.Console;
      }


      // May layout
      var layout = new qx.ui.layout.VBox();
      var container = new qx.ui.container.Composite(layout);
	  
      var menuBar = new qx.ui.menubar.MenuBar();
      var toolbar = new qx.ui.toolbar.ToolBar();
      var commandManager = new org.argeo.slc.web.event.CommandsManager(this);
      this.setCommandManager(commandManager);
      commandManager.createCommands();
      commandManager.registerMenuBar(menuBar);
      commandManager.registerToolBar(toolbar);
      toolbar.setShow("icon");
      commandManager.addToolbarContextMenu(toolbar);

      var stopCommand = commandManager.getCommandById("stop");
      var serviceManager = org.argeo.slc.web.util.RequestManager.getInstance();
      serviceManager.setStopCommand(stopCommand);
      
      var splitPane = new qx.ui.splitpane.Pane("horizontal");
	  var splitLeft = new qx.ui.splitpane.Pane("vertical").set({
	    width: 300,
	    minWidth:36	    
	  });
	  splitLeft.setDecorator(null);
	  var topLeft = new org.argeo.slc.web.components.View(this, "list", "Test Cases", {
	  	orientation : "horizontal",
	  	min : 36,
	  	object : splitLeft
	  });
	  this.registerView(topLeft);
	  
	  var bottomLeft = new org.argeo.slc.web.components.View(this, "details", "Details", {
	  	orientation : "vertical",
	  	min : 30
	  });
	  bottomLeft.set({height: 200});
	  this.registerView(bottomLeft);

	  splitLeft.add(topLeft, 1);
	  splitLeft.add(bottomLeft, 0);
	  
	  splitPane.add(splitLeft, 0);
  	  this.rightPane = new org.argeo.slc.web.components.View(this, "applet", "Test");  	  
  	  this.registerView(this.rightPane);
	  splitPane.add(this.rightPane, 1);
      
      container.add(menuBar);
      container.add(toolbar);
      container.add(splitPane, {flex:1});
      
      // Document is the application root
      var doc = this.getRoot();
      doc.add(container, {
      	left:0,
      	right:0,
      	top:0,
      	bottom:0
      });
  
      this.initializeViews();
      
    },

	registerView : function(view){
		this.views[view.getViewId()] = view;
	  	view.getViewSelection().addListener("changeSelection", function(e){
	  		this.getCommandManager().refreshCommands(e.getData());
	  }, this);
		
	},
	
	getSelectionForView : function(viewId){
		if(this.views[viewId]){
			return this.views[viewId].getViewSelection();
		}
		this.error("Cannot find view '"+viewId+"'");
		return null;
	},
    	
    /**************************
      CLIENT METHODS
     ************************/
    initializeViews : function(){
	  var view = this.views["list"];
	  var model = new qx.ui.table.model.Simple();
	  model.setColumns(["Test Case", "Date"]);	  
	  this.setModel(model);
	  this.table = new qx.ui.table.Table(model, {
	  	tableColumnModel: function(obj){
			return new qx.ui.table.columnmodel.Resize(obj)
		}
	  });	  
	  this.table.set({
	  	statusBarVisible: false,
		showCellFocusIndicator:false,
		columnVisibilityButtonVisible:false,
		contextMenu : this.getCommandManager().createMenuFromIds(["opentest", "deletetest", "copytocollection"]),
		decorator : new qx.ui.decoration.Background("#fff")
	  });
	  this.table.addListener("dblclick", function(e){
	this.getCommandManager().executeCommand("opentest");
	  }, this);
	  var columnModel = this.table.getTableColumnModel(); 
	  columnModel.getBehavior().setWidth(0, "60%");
	  columnModel.setDataCellRenderer(0, new org.argeo.slc.web.components.XmlRenderer());
	  columnModel.setDataCellRenderer(1, new org.argeo.slc.web.components.XmlRenderer());
	  
	  this.table.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
	  	var viewSelection = view.getViewSelection();
	  	viewSelection.clear();
	  	var selectionModel = this.table.getSelectionManager().getSelectionModel();
	  	if(!selectionModel.getSelectedCount()){
	  		return;
	  	}
	  	var ranges = this.table.getSelectionManager().getSelectionModel().getSelectedRanges();
	  	var xmlNode = this.getModel().getRowData(ranges[0].minIndex);
	  	viewSelection.addNode(xmlNode);
	  	view.setViewSelection(viewSelection);
	  }, this);
	  
	  view.setContent(this.table, false);
	
    },
    
    loadTable : function(url){
    	    	
	  	var model = this.getModel();
	  	model.removeRows(0, model.getRowCount());
	  	var serviceManager = org.argeo.slc.web.util.RequestManager.getInstance();
	  	var request = serviceManager.getRequest(url, "GET", "application/xml");	  	
	  	request.addListener("completed", function(response){
  			xml = response.getContent();
	  		qx.log.Logger.info("Successfully loaded XML");
	  		var nodes = qx.xml.Element.selectNodes(xml, "//data");
	  		for(var i=0; i<nodes.length;i++){
	  			var rowData = nodes[i];
	  			model.addRows([rowData]);
	  		}
	  		serviceManager.requestCompleted(this);
	  	}, request);
	  	request.send();
    },
    
	createTestApplet : function(xmlNode){
		var applet = new org.argeo.slc.web.components.Applet(this.rightPane);
		applet.initData(xmlNode);
		var commands = applet.getCommands();
		this.rightPane.empty();
		if(commands){
			this.rightPane.setCommands(commands);
			this.getCommandManager().addCommands(commands);
		}
		this.rightPane.setContent(applet, false);		
	}	
  }
});