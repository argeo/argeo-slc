/**
 * A Basic IView implementation displaying a fake list of result and opening 
 * an org.argeo.ria.sample.Applet
 */
qx.Class.define("org.argeo.ria.sample.List",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	this.base(arguments, new qx.ui.layout.VBox());  	
  	var model = new qx.ui.table.model.Simple();
  	model.setColumns(["Test Case", "Date"]);
  	this.table = new qx.ui.table.Table(model, {
	  	tableColumnModel: function(obj){
			return new qx.ui.table.columnmodel.Resize(obj)
		}
	});
  },

  properties : 
  {
  	/**
  	 * The viewPane containing this applet.
  	 */
  	view : {
  		init : null
  	},
  	/**
  	 * The applet commands.
  	 */
  	commands : {
  		init : {
  			"opentest" : {
  				label	 	: "Open", 
  				icon 		: "resource/slc/media-playback-start.png",
  				shortcut 	: "Control+o",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){
  					var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
  					var classObj = org.argeo.ria.sample.Applet;
					var iView = viewsManager.initIViewClass(classObj, "applet");
  					var rowData = viewsManager.getViewPaneSelection("list").getNodes();
					iView.load(rowData[0]);
  				},
  				selectionChange : function(viewId, rowData){
  					if(viewId != "list") return;
  					this.setEnabled(false);
  					if(rowData == null || !rowData.length) return;
					this.setEnabled(true);
  				},
  				command 	: null
  			}
  		}
	}
  },
  
  members : {
	init : function(viewPane){
	  this.setView(viewPane);
	},
	load : function(data){
	  //
	  // Customize table appearance
	  //
	  this.table.set({	  	
	  	statusBarVisible: false,
		showCellFocusIndicator:false,
		columnVisibilityButtonVisible:false,
		contextMenu : org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["opentest", "download", "copytocollection", "deletetest"]),
		decorator : new qx.ui.decoration.Background("#fff")
	  });
	  
	  //	  
	  // Link table selection changes to the standard viewSelection mechanism
	  //
	  var viewPane = this.getView();
	  var selectionModel = this.table.getSelectionManager().getSelectionModel();
	  selectionModel.addListener("changeSelection", function(e){
	  	var viewSelection = viewPane.getViewSelection();
	  	viewSelection.clear();
	  	if(!selectionModel.getSelectedCount()){
	  		return;
	  	}
	  	var ranges = selectionModel.getSelectedRanges();
	  	var rowData = this.table.getTableModel().getRowData(ranges[0].minIndex);
	  	viewSelection.addNode(rowData);
	  	viewPane.setViewSelection(viewSelection);
	  }, this);		
	  
	  //
	  // Add table to the GUI component
	  //	  
	  this.add(this.table, {flex:1});

	  //
	  // Now create fake rows
	  //	  
	  var model = this.table.getTableModel();
	  model.addRows([
	  	["Id 1","Sample 1"], 
	  	["Id 2","Sample 2"],
	  	["Id 3","Sample 3"]
	  ]);
	},
					
	addScroll : function(){
		return false;
	}  	
  }
});