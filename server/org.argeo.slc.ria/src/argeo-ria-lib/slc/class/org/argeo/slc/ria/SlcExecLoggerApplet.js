/**
 * A simple Hello World applet for documentation purpose. 
 * The only associated command is the "Close" command.
 */
qx.Class.define("org.argeo.slc.ria.SlcExecLoggerApplet",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	this.base(arguments);
	this.setLayout(new qx.ui.layout.Dock());	
  },

  properties : 
  {
  	/**
  	 * The viewPane inside which this applet is added. 
  	 */
  	view : {
  		init : null
  	},
  	viewSelection : {
  		nullable:false, 
  		check:"org.argeo.ria.components.ViewSelection"
  	},  
  	instanceId : {init:""},
  	instanceLabel : {init:""},  	
  	/**
  	 * Commands definition, see {@link org.argeo.ria.event.CommandsManager#definitions} 
  	 */
  	commands : {
  		init : {
  			"reloadlogs" : {
  				label	 	: "Reload Logs", 
  				icon 		: "org/argeo/slc/ria/view-refresh.png",
  				shortcut 	: "Control+r",
  				enabled  	: true,
  				menu	   	: null,
  				toolbar  	: null,
  				callback	: function(e){
			  		this._reloadLogger();
  				},
  				command 	: null
  			},
  			"opendetail" : {
  				label		: "Logs Detail",
  				icon		: "org/argeo/slc/ria/mime-xls.png",
  				shortcut	: null,
  				enabled		: false,
  				menu		: null,
  				toolbar		: "slc_logs",
  				callback	: function(e){
  					var selection = this.getViewSelection();
  					var rowData = selection.getNodes()[0];
  					this.openDetail(rowData);
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "logger") return;
  					this.setEnabled((selection!=null && selection.length==1));
  				},
  				command		: null  				
  			},
  			"reopenrealized" : {
  				label		: "Re-open",
  				icon		: "org/argeo/slc/ria/document-open.png",
  				shortcut	: "Control+o",
  				enabled		: false,
  				menu		: null,
  				toolbar		: "slc_logs",
  				callback	: function(e){
  					var selection = this.getViewSelection();
  					var rowData = selection.getNodes()[0];
  					this.openRealized(rowData);
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "logger") return;
  					this.setEnabled((selection!=null && selection.length==1));
  				},
  				command		: null
  			}
  		}
  	}
  },

  members :
  {
  	/**
  	 * Called at applet creation. Just registers viewPane.
  	 * @param viewPane {org.argeo.ria.components.ViewPane} The viewPane.
  	 */
  	init : function(viewPane){
  		this.setView(viewPane);
  		this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));  		
  		this._createLayout();
  		this.UIBus = org.argeo.ria.event.UIBus.getInstance();
  	},
  	
  	/**
  	 *  
  	 */
  	load : function(){
		this._reloadLogger();
  		this.UIBus.addListener("newSlcExecution", this._reloadLogger, this);
  		this.UIBus.addListener("updateSlcExecutionStatus", this._reloadLogger, this);
  	},
  	 
	addScroll : function(){
		return false;
	},
	
	close : function(){
  		this.UIBus.removeListener("newSlcExecution", this._reloadLogger, this);
  		this.UIBus.removeListener("updateSlcExecutionStatus", this._reloadLogger, this);
	},
	  	
	openRealized : function(logData){
		
		// DEBUG PURPOSE
		var CHECK_HOST = false;
		
		var uuid = logData[2];
		var host = logData[1];
		
		
		// 1. Check that both associated views are here
		var batchView;
		var flowsView;
		try{
			batchView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("batch").getContent();
			flowsView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("selector").getContent();
		}catch(e){
			this.debug("Cannot find either bath or flows IView!");
		}
		if(!batchView || !flowsView) return;		
		
		// 2. Check that at least a host with the same name exists.
		var agentsMap = flowsView.getAgentsMap();		
		var currentBatchId = batchView.getBatchAgentId();
		if(currentBatchId != null){
			var currentHost = agentsMap[currentBatchId];
			if(currentHost != host){
				this.error("Cannot re-open these flows on a different host. Please clear the batch first.");
				return;
			}
		}				
		if(!qx.lang.Object.contains(agentsMap, host)){
			this.error("Cannot find any agent running on '"+host+"'! Please start an agent on this host.");
			return;
		}
		//console.log(currentBatchId);
		if(currentBatchId == null){
			var defaultId = qx.lang.Object.getKeyFromValue(agentsMap, host);
			batchView.setBatchAgentId(defaultId);
		}
		
		// 3. Call service to load execution message
		var req = org.argeo.slc.ria.SlcApi.getSlcExecutionService(uuid);
		var handler = function(xmlDoc){						
			var realizedFlows = org.argeo.ria.util.Element.selectNodes(xmlDoc, "slc:slc-execution/slc:realized-flows/slc:realized-flow");
			for(var i=0;i<realizedFlows.length;i++){				
				var newEntrySpec = new org.argeo.slc.ria.execution.BatchEntrySpec(null, null, realizedFlows[i]);
				batchView.appendBatchEntrySpec(newEntrySpec);
			}			
		};
		req.addListener("completed", function(response){			
			handler(response.getContent());
		});
		// STUB CASE
		req.addListener("failed", function(){
			if(!window.xmlExecStub || !window.xmlExecStub[uuid]){				
				return;
			}
			var xmlDoc = window.xmlExecStub[uuid];
			handler(xmlDoc);
		});	
		req.send();
	},
	
	openDetail : function(logData){
				
		var uuid = logData[2];
		var window = new qx.ui.window.Window("Logs Detail", "org/argeo/slc/ria/mime-xls.png");
		window.setLayout(new qx.ui.layout.VBox(0));
		window.setContentPadding(0);		
		window.open();
		org.argeo.ria.components.ViewsManager.getInstance().getApplicationRoot().add(window, {
			top : '20%',
			left : '20%',
			width: '60%',
			height: '60%'
		});
		
		var tBar = new qx.ui.toolbar.ToolBar();
		window.add(tBar);
		var menuButton = new qx.ui.toolbar.Button("Close", "org/argeo/slc/ria/window-close.png");
		tBar.add(menuButton);
		menuButton.addListener("execute", function(e){
			window.close();
		});
		
		var tableModel = new qx.ui.table.model.Simple();
		var table = new org.argeo.ria.components.ui.Table(tableModel, {
			"date":{NAME : "Date", WIDTH:180}, 
			"type":{NAME : "Type", WIDTH:90, ALIGN:"CENTER"}, 
			"thread":{NAME : "Thread", WIDTH:90, ALIGN:"CENTER"}, 
			"log":"Log"
		});
		table.setStatusBarVisible(true);
		window.add(table, {flex:1});
		window.setAllowMinimize(false);
		window.setResizable(true, true, true, true);
		
		tableModel.addListener("dataChanged", function(event){
			if(!event.getData()) return;
			var dataMap = event.getData();
			table.scrollCellVisible(0, dataMap.lastRow);
		});
		
		var cpButton = new qx.ui.toolbar.Button("Download", "org/argeo/slc/ria/document-save-as.png");
		tBar.add(cpButton);
		cpButton.addListener("execute", function(e){
			var downloadUrl = org.argeo.slc.ria.SlcApi.DEFAULT_CONTEXT+"/"+org.argeo.slc.ria.SlcApi.DOWNLOAD_SLCEXEC_STEPS + "?ext=log&uuid=" + uuid;
			org.argeo.ria.Application.INSTANCE.javascriptDownloadLocation(downloadUrl);
		});
		
		// Call service to load execution message
		this._loadSlcExecutionSteps(uuid, tableModel, false);
		
		var poller = new qx.event.Timer(3000);
		poller.addListener("interval", function(e){
			this._loadSlcExecutionSteps(uuid, tableModel, true);
		}, this);
		
		window.addListener("close", function(){
			poller.stop();
		}, this);
		poller.start();
	},	

	_loadSlcExecutionSteps : function(uuid, tableModel, poller){
		var req;
		var lastUuid = tableModel.getUserData("lastStepUuid");
		if(!poller || !lastUuid){
			req = org.argeo.slc.ria.SlcApi.getTailSlcExecutionStepService(uuid, 200, null);
		}else{
			req = org.argeo.slc.ria.SlcApi.getTailSlcExecutionStepService(uuid, null, lastUuid);
		}
		req.addListener("completed", function(response){			
			var xmlDoc = response.getContent();
			var tableLines = (poller?tableModel.getData():[]);
			var parser = org.argeo.ria.util.Element;
			var slcSteps = parser.selectNodes(xmlDoc, "slc:object-list/slc:slc-execution-step");
			for(var i=0;i<slcSteps.length;i++){				
				var step = slcSteps[i];
				var stepUuid = parser.getSingleNodeText(step, "@uuid");
				var date = parser.getSingleNodeText(step, "slc:timestamp");
				var type = parser.getSingleNodeText(step, "slc:type");
				var thread = parser.getSingleNodeText(step, "slc:thread");
				var logLines = parser.selectNodes(step, "slc:log-lines/slc:log-line");
				if(logLines.length > 0){
					tableLines.push([date, type, thread, parser.getSingleNodeText(logLines[0], ".")]);
					if(logLines.length>1){
						for(var j=1;j<logLines.length;j++){
							tableLines.push(["", "", "", parser.getSingleNodeText(logLines[j], ".")]);	
						}
					}
				}else{
					tableLines.push([date, type, thread, ""]);
				}
			}
			if(slcSteps.length){
				tableModel.setUserData("lastStepUuid", stepUuid);
			}
			tableModel.setData(tableLines);
		});
		req.send();
		
	},
	
	/**
	 * Creates the applet layout
	 */
	_createLayout : function(){
		this.logModel = new qx.ui.table.model.Simple();
		this.logModel.setColumns(["Date", "Host", "Id", "Status"]);
		this.logPane = new qx.ui.table.Table(this.logModel,  {
		  	tableColumnModel: function(obj){
				return new qx.ui.table.columnmodel.Resize(obj)
			}
		});
		this.logPane.addListener("cellDblclick", function(e){
			this.getCommands()["opendetail"].command.execute();
		}, this);
		
		var selectionModel = this.logPane.getSelectionModel();
		selectionModel.addListener("changeSelection", function(e){			
			var viewSelection = this.getViewSelection();			
			viewSelection.setViewId("logger");			
			viewSelection.clear();			
			selectionModel.iterateSelection(function(index){				
				viewSelection.addNode(this.logModel.getRowData(index));
			}, this);
		}, this);
		
		this.logPane.set({	
			decorator : null,
		  	statusBarVisible: false,
			showCellFocusIndicator:false
		});
		var columnModel = this.logPane.getTableColumnModel();
		columnModel.getBehavior().setWidth(0, "30%");
		columnModel.getBehavior().setWidth(1, "15%");
		columnModel.getBehavior().setWidth(3, "12%");		
		
		this.add(this.logPane, {edge:'center'});
	},
	
	/**
	 * Refresh the data model.
	 */
	_reloadLogger : function(){
		var request = org.argeo.slc.ria.SlcApi.getListSlcExecutionsService();
		request.addListener("completed", function(response){			
			var messages = org.argeo.ria.util.Element.selectNodes(response.getContent(), "//slc:slc-execution");
			this.logModel.setData([]);
			for(var i=0;i<messages.length;i++){
				var message = messages[i];
				var slcExec = new org.argeo.slc.ria.SlcExecutionMessage(message.getAttribute("uuid"));
				slcExec.fromXml(message);
				this.logModel.addRows([
					[slcExec.getDate(), slcExec.getHost(), slcExec.getUuid(), slcExec.getStatus()]
				]);				
			}
			this.logModel.sortByColumn(0, false);
		}, this);
		request.send();		
	}
	  	
  }
});