/**
 * A simple Hello World applet for documentation purpose. 
 * The only associated command is the "Close" command.
 */
qx.Class.define("org.argeo.slc.ria.LauncherApplet",
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
  	/**
  	 * Commands definition, see {@link org.argeo.ria.event.CommandsManager#definitions} 
  	 */
  	commands : {
  		init : {
  			"submitform" : {
  				label	 	: "Execute", 
  				icon 		: "resource/slc/media-playback-start.png",
  				shortcut 	: "Control+e",
  				enabled  	: true,
  				menu	   	: "Launcher",
  				toolbar  	: "launcher",
  				callback	: function(e){
  					this.getView().getContent().submitForm();
  				},
  				command 	: null
  			},  			
  			"reloadagents" : {
  				label	 	: "Reload Agents", 
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+r",
  				enabled  	: true,
  				menu	   	: "Launcher",
  				toolbar  	: "launcher",
  				callback	: function(e){
			  		var req = org.argeo.slc.ria.SlcApi.getListAgentsService("agents");
			  		req.send();
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
  		this._createLayout();
  		this._createForm();
  		this._amqClient = new org.argeo.ria.remote.AmqClient();
  		this._amqClient.startPolling(); 
  		qx.io.remote.RequestQueue.getInstance().setDefaultTimeout(30000);
  	},
  	
  	/**
  	 * Load a given row : the data passed must be a simple data array.
  	 * @param data {Element} The text xml description. 
  	 */
  	load : function(){
  		this.getView().setViewTitle("Slc Execution Launcher");
  		org.argeo.ria.event.CommandsManager.getInstance().getCommandById("reloadagents").execute();
  	},
  	 
	addScroll : function(){
		return false;
	},
	  	
	_createLayout : function(){
		this.formPane = new qx.ui.container.Composite(new qx.ui.layout.VBox(5));		
		this.scroll = new qx.ui.container.Scroll(this.formPane);
		this.scroll.setWidth(290);
		this.formPane.setPadding(10);
		this.add(this.scroll, {edge:'west'});
		
		this.logModel = new qx.ui.table.model.Simple();
		this.logModel.setColumns(["Date", "Agent Uuid", "Status", "Type"]);
		this.logPane = new qx.ui.table.Table(this.logModel,  {
		  	tableColumnModel: function(obj){
				return new qx.ui.table.columnmodel.Resize(obj)
			}
		});
		this._initLogger();
		this.add(this.logPane, {edge:'center'});
	},
	
	_initLogger : function(){
		this.logPane.set({	  	
		  	statusBarVisible: false,
			showCellFocusIndicator:false
		});
		var columnModel = this.logPane.getTableColumnModel(); 
		columnModel.getBehavior().setWidth(2, "12%");
		columnModel.getBehavior().setWidth(3, "12%");		
	},
	
	_createForm : function(){
  		this.fields = {};
  		this.freeFields = [];
  		
		var execButtonPane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
		var execButton = new qx.ui.form.Button(
			"Execute", 
			"resource/slc/media-playback-start-32.png"			
		)
		execButton.addListener("click", function(){
			this.submitForm();
		}, this);
		execButtonPane.setPadding(10, 80);
		execButtonPane.add(execButton, {edge:"center"});
		this.formPane.add(execButtonPane);
  		
		this.agentSelector = new qx.ui.form.SelectBox();
		var serviceManager = org.argeo.ria.remote.RequestManager.getInstance();
		serviceManager.addListener("reload", function(reloadEvent){
			if(reloadEvent.getDataType()!= "agents") return ;
			this._xmlListToSelector(reloadEvent.getContent(), this.agentSelector);
		}, this);
		
		this._addFormHeader("Choose Agent Uuid");
		this._addFormEntry(new qx.ui.basic.Label("Agent Uuid"), this.agentSelector);
		this._addFormHeader("Set SlcExecution Parameters");
		this._addFormInputText("status", "Status", "STARTED");
		this._addFormInputText("type", "Type", "slcAnt");
		this._addFormInputText("host", "Host", "localhost");
		this._addFormInputText("user", "User", "user");
		
		var moreButton = new qx.ui.basic.Image("resource/slc/list-add.png");
		moreButton.setToolTip(new qx.ui.tooltip.ToolTip("Add a parameter"));
		moreButton.setCursor("pointer");
		moreButton.addListener("click", function(){
			this._addFormInputText();
		}, this);
		this._addFormHeader("Add optionnal parameters", moreButton);
		this._addFormInputText();
		this._addFormInputText();
	},
	
	_addFormInputText : function(fieldName, fieldLabel, defaultValue){
		var labelElement;
		var fieldElement = new qx.ui.form.TextField();
		if(defaultValue){
			fieldElement.setValue(defaultValue);
		}
		if(fieldName && fieldLabel){
			labelElement = new qx.ui.basic.Label(fieldLabel);
			this.fields[fieldName] = fieldElement;
		}else{
			labelElement = new qx.ui.form.TextField();
			this.freeFields.push({
				labelEl:labelElement, 
				valueEl:fieldElement
			});
		}
		this._addFormEntry(labelElement, fieldElement);
	},
	
	_addFormHeader : function(content, additionnalButton){
		var header = new qx.ui.basic.Label('<b>'+content+'</b>');
		header.setRich(true);		
		if(!additionnalButton){
			header.setPaddingTop(10);
			this.formPane.add(header);
		}else{
			var pane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
			pane.setPaddingTop(10);
			pane.setPaddingRight(10);
			pane.add(header, {edge:'center'});
			pane.add(additionnalButton, {edge:'east'});
			this.formPane.add(pane);
		}
	},
	
	_addFormEntry : function(labelElement, fieldElement){
		var entryPane = new qx.ui.container.Composite(new qx.ui.layout.HBox(5));
		labelElement.setWidth(100);
		labelElement.setTextAlign("right");		
		entryPane.add(labelElement);
		entryPane.add(new qx.ui.basic.Label(':'));
		fieldElement.setWidth(150);
		entryPane.add(fieldElement);
		this.formPane.add(entryPane);
	},
	
	_xmlListToSelector : function(xmlDoc, selector){
		selector.removeAll();
		var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
		var nodes = org.argeo.ria.util.Element.selectNodes(xmlDoc, "//slc:slc-agent-descriptor", NSMap);
		for(var i=0;i<nodes.length;i++){
			var uuid = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "@uuid", NSMap);
			var host = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "slc:host", NSMap);
			var listItem = new qx.ui.form.ListItem(uuid+' ('+host+')', null, uuid);
			selector.add(listItem);
			this._addAmqListenerDeferred(uuid, i);
		}
	},
	
	_addAmqListenerDeferred: function(uuid, index){
		qx.event.Timer.once(function(){			
			this._amqClient.addListener("launcherId", "topic://agent."+uuid+".newExecution", function(message){
				this.info("Received message!");
				var slcExec = new org.argeo.slc.ria.SlcExecutionMessage(message.getAttribute("uuid"));
				slcExec.fromXml(message);
				this.logModel.addRows([
					[new Date().toString(), slcExec.getUuid(), slcExec.getStatus(), slcExec.getType()]
				]);				
			}, this);
		}, this, 500*index);		
	},
	
	submitForm : function(){
		var currentUuid = this.agentSelector.getValue();
		if(!currentUuid) return;
		var slcExec = new org.argeo.slc.ria.SlcExecutionMessage(currentUuid);
		slcExec.setStatus(this.fields.status.getValue());
		slcExec.setType(this.fields.type.getValue());
		slcExec.setHost(this.fields.host.getValue());
		slcExec.setUser(this.fields.user.getValue());
		for(var i=0;i<this.freeFields.length;i++){
			var fF = this.freeFields[i];
			if(fF.labelEl.getValue() != "" && fF.valueEl.getValue() != ""){
				slcExec.addAttribute(fF.labelEl.getValue(), fF.valueEl.getValue());
			}
		}
		var destination = "topic://agent."+currentUuid+".newExecution";
		this._amqClient.sendMessage(destination, slcExec.toXml());
	}
	  	
  }
});