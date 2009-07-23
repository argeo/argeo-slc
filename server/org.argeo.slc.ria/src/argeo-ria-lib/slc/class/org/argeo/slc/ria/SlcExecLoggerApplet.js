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
  				icon 		: "org.argeo.slc.ria/view-refresh.png",
  				shortcut 	: "Control+r",
  				enabled  	: true,
  				menu	   	: null,
  				toolbar  	: null,
  				callback	: function(e){
			  		this._reloadLogger();
  				},
  				command 	: null
  			},
  			"openrealized" : {
  				label		: "Re-open",
  				icon		: "org.argeo.slc.ria/document-open.png",
  				shortcut	: "Control+o",
  				enabled		: false,
  				menu		: null,
  				toolbar		: "realized",
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
		var host = "charlie";//  logData[1];
		
		
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
			var realizedFlows = org.argeo.ria.util.Element.selectNodes(xmlDoc, "slc:slc-execution/realized-flows/slc:realized-flow");
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
		}, this);
		request.send();		
	}
	  	
  }
});