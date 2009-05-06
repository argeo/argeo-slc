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
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+r",
  				enabled  	: true,
  				menu	   	: null,
  				toolbar  	: null,
  				callback	: function(e){
			  		this._reloadLogger();
  				},
  				command 	: null
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
		this.logPane.setDecorator(null);
		this._initLogger();
		this.add(this.logPane, {edge:'center'});
	},
	
	/**
	 * Initialize the log table.
	 */
	_initLogger : function(){
		this.logPane.set({	  	
		  	statusBarVisible: false,
			showCellFocusIndicator:false
		});
		var columnModel = this.logPane.getTableColumnModel();
		columnModel.getBehavior().setWidth(0, "30%");
		columnModel.getBehavior().setWidth(1, "15%");
		columnModel.getBehavior().setWidth(3, "12%");		
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