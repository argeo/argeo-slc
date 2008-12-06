/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.TestList",
{
  extend : qx.ui.table.Table,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	var model = new qx.ui.table.model.Simple();
  	model.setColumns(["Test Case", "Date"]);
  	this.base(arguments, model, {
	  	tableColumnModel: function(obj){
			return new qx.ui.table.columnmodel.Resize(obj)
		}
	});
	this.setModel(model);
  },

  properties : 
  {
  	view : {
  		init : null
  	},
  	model : {
  		init: null
  	},
  	commands : {
  		init : {
  			"loadtestlist" : {
  				label		: "Load Tests", 
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+l",
  				enabled  	: true,
  				menu	   	: "File",
  				toolbar  	: "test",
  				callback 	: function(e){
  					this.loadList();
  				}, 
  				command 	: null
  			},
  			"opentest" : {
  				label	 	: "Open", 
  				icon 		: "resource/slc/document-open.png",
  				shortcut 	: "Control+o",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "test",
  				callback	: function(e){
  					var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
  					var classObj = org.argeo.slc.web.Applet;
					var iView = viewsManager.initIViewClass(classObj, "applet");
  					var xmlNodes = viewsManager.getViewPaneSelection("list").getNodes();
					iView.load(xmlNodes[0]);
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId != "list") return;
  					this.setEnabled(false);
  					if(xmlNodes == null || !xmlNodes.length) return;
  					var applet = org.argeo.ria.util.Element.selectSingleNode(xmlNodes[0],'report[@type="applet"]'); 
  					if(applet != null && qx.dom.Node.getText(applet) != ""){
  						this.setEnabled(true);  						
  					}  					
  				},
  				command 	: null
  			},
  			"download" : {
  				label	 	: "Download as...", 
  				icon 		: "resource/slc/go-down.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "test",
  				callback	: function(e){ },
  				command 	: null,
  				submenu 	: {},
  				submenuCallback : function(commandId){
  					var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
  					var xmlNodes = viewsManager.getViewPaneSelection("list").getNodes();
  					// Single selection
  					var uuid = qx.xml.Element.getSingleNodeText(xmlNodes[0], 'param[@name="uuid"]');
  					var urls = {
  						xsl : "resultView.xslt",
  						xml : "resultViewXml.xslt",
  						xls : "resultView.xls",
  						pdf : "resultView.pdf"
  					};
  					var url = "../"+urls[commandId]+"?uuid="+uuid;
  					if(commandId == "xls" || commandId == "pdf"){
	  					document.location.href = url;
  					}else{
  						var win = window.open(url);
  					}
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId!="list")return;
  					this.clearMenus();
  					this.setEnabled(false);
  					if(xmlNodes == null) return;
  					
  					var reports = qx.xml.Element.selectNodes(xmlNodes[0],'report[@type="download"]');
  					if(reports == null || !reports.length)return;
  					
  					for(var i=0; i<reports.length;i++){
  						var report = reports[i];
  						var commandId = qx.dom.Node.getText(org.argeo.ria.util.Element.selectSingleNode(report, "@commandid")); 
  						this.addSubMenuButton(
  							qx.dom.Node.getText(report),
  							"resource/slc/mime-"+commandId+".png",
  							commandId
						);
  					}
  					this.setEnabled(true);
  					this.fireDataEvent("changeMenu", this.getMenu());
  				}
  			},
  			"deletetest" : {
  				label	 	: "Delete", 
  				icon 		: "resource/slc/edit-delete.png",
  				shortcut 	: "Control+d",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "test",
  				callback	: function(e){
  					// Call service to delete
  				},
  				command 	: null
  			},
  			"copytocollection" : {
  				label	 	: "Copy to...", 
  				icon 		: "resource/slc/edit-copy.png",
  				shortcut 	: "Control+c",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "test",
  				callback	: function(e){
  					// Call service to copy
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
	  this.set({	  	
	  	statusBarVisible: false,
		showCellFocusIndicator:false,
		columnVisibilityButtonVisible:false,
		contextMenu : org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["opentest", "deletetest", "copytocollection"]),
		decorator : new qx.ui.decoration.Background("#fff")
	  });
	  var viewPane = this.getView();
	  this.addListener("dblclick", function(e){
		org.argeo.ria.event.CommandsManager.getInstance().executeCommand("opentest");
	  }, this);
	  var columnModel = this.getTableColumnModel(); 
	  columnModel.getBehavior().setWidth(0, "60%");
	  columnModel.setDataCellRenderer(0, new org.argeo.ria.components.XmlRenderer());
	  columnModel.setDataCellRenderer(1, new org.argeo.ria.components.XmlRenderer());
	  
	  this.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
	  	var viewSelection = viewPane.getViewSelection();
	  	viewSelection.clear();
	  	var selectionModel = this.getSelectionManager().getSelectionModel();
	  	if(!selectionModel.getSelectedCount()){
	  		return;
	  	}
	  	var ranges = this.getSelectionManager().getSelectionModel().getSelectedRanges();
	  	var xmlNode = this.getModel().getRowData(ranges[0].minIndex);
	  	viewSelection.addNode(xmlNode);
	  	viewPane.setViewSelection(viewSelection);
	  }, this);		
	},
	
	loadList : function(){
		var url = "/org.argeo.slc.webapp/resultList.ui";
	  	var model = this.getModel();
	  	model.removeRows(0, model.getRowCount());
	  	var serviceManager = org.argeo.ria.remote.RequestManager.getInstance();
	  	var commandManager = org.argeo.ria.event.CommandsManager.getInstance();
	  	var request = serviceManager.getRequest(
	  		url, 
	  		"GET", 
	  		"application/xml",
	  		"test_cases",
	  		[commandManager.getCommandById("loadtestlist"), this.getView()]
	  	);	  	
	  	request.addListener("completed", function(response){
  			xml = response.getContent();
	  		qx.log.Logger.info("Successfully loaded XML");
	  		//var resManager = org.argeo.ria.event.ResourcesManager.getInstance().fireReloadEvent("test_cases", xml);
	  		var nodes = qx.xml.Element.selectNodes(xml, "//data");
	  		for(var i=0; i<nodes.length;i++){
	  			var rowData = nodes[i];
	  			model.addRows([rowData]);
	  		}
	  	}, request);
	  	request.send();		
	},
	
	addScroll : function(){
		return false;
	}  	
  }
});