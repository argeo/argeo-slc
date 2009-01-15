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
  	},
  	registeredTopics : {
  		init : {},
  		check : "Map", 
  		event : "changeRegisteredTopics"
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
  		this._amqClient = org.argeo.ria.remote.JmsClient.getInstance();
  		this._amqClient.startPolling();
  	},
  	
  	/**
  	 *  
  	 */
  	load : function(){
  		this.getView().setViewTitle("Execution Launcher");
  		this.addListener("changeRegisteredTopics", function(event){
  			this._refreshTopicsSubscriptions(event);
  			this._feedSelector(event);
  		}, this);
  		var reloadHandler = function(message){
	  		org.argeo.ria.event.CommandsManager.getInstance().getCommandById("reloadagents").execute();
  		}
  		this._amqClient.addListener("agentregister", "topic://agent.register", reloadHandler, this);
  		//qx.event.Timer.once(function(){  		
  			this._amqClient.addListener("agentunregister", "topic://agent.unregister", reloadHandler, this);
  		//}, this, 500);
  		reloadHandler();
  	},
  	 
	addScroll : function(){
		return false;
	},
	
	close : function(){
  		this._amqClient.removeListener("agentregister", "topic://agent.register");
  		this._amqClient.removeListener("agentunregister", "topic://agent.unregister");
  		
  		this.setRegisteredTopics({});
		this._amqClient.stopPolling();
	},
	  	
	_createLayout : function(){
		this.formPane = new qx.ui.container.Composite(new qx.ui.layout.VBox(5));		
		this.scroll = new qx.ui.container.Scroll(this.formPane);
		this.scroll.setWidth(290);
		this.formPane.setPadding(10);
		this.add(this.scroll, {edge:'west'});
		
		this.logModel = new qx.ui.table.model.Simple();
		this.logModel.setColumns(["Date", "Agent Uuid", "Status"]);
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
			var xmlDoc = reloadEvent.getContent();
			var nodes = org.argeo.ria.util.Element.selectNodes(xmlDoc, "//slc:slc-agent-descriptor");
			var newTopics = {};
			for(var i=0;i<nodes.length;i++){
				var uuid = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "@uuid");
				var host = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "slc:host");
				newTopics[uuid] = host+" ("+uuid+")";
			}
			this.setRegisteredTopics(newTopics);
		}, this);
		
		this._addFormHeader("Choose Agent Uuid");
		this._addFormEntry(new qx.ui.basic.Label("Agent Uuid"), this.agentSelector);
		this._addFormHeader("Set Execution Parameters");
		this._addFormInputText("status", "Status", "STARTED");
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
	
	_refreshTopicsSubscriptions : function(changeTopicsEvent){
		var oldTopics = changeTopicsEvent.getOldData() || {};
		var newTopics = changeTopicsEvent.getData();
		var removed = [];
		var added = [];
		for(var key in oldTopics){
			if(!newTopics[key]) {
				this._removeAmqListener(key);
			}
		}
		for(var key in newTopics){
			if(!oldTopics[key]) {
				this._addAmqListener(key);
			}			
		}
	},
		
	_feedSelector : function(changeTopicsEvent){
		var topics = changeTopicsEvent.getData();
		this.agentSelector.removeAll();
		var emptyItem = new qx.ui.form.ListItem("", null, "");
		this.agentSelector.add(emptyItem);
		this.agentSelector.setSelected(emptyItem);
		for(var key in topics){
			var listItem = new qx.ui.form.ListItem(topics[key], null, key);
			this.agentSelector.add(listItem);
		}
	},
	
	_addAmqListener: function(uuid){
		this._amqClient.addListener("slcExec", "topic://agent."+uuid+".newExecution", function(response){
			var message = org.argeo.ria.util.Element.selectSingleNode(response, "slc:slc-execution");				
			var slcExec = new org.argeo.slc.ria.SlcExecutionMessage(message.getAttribute("uuid"));
			slcExec.fromXml(message);
			this.logModel.addRows([
				[new Date().toString(), slcExec.getHost()+' ('+slcExec.getUuid()+')', slcExec.getStatus()]
			]);				
		}, this);
	},
	
	_removeAmqListener : function(uuid){
		this._amqClient.removeListener("slcExec", "topic://agent."+uuid+".newExecution");
	},
	
	submitForm : function(){
		var currentUuid = this.agentSelector.getValue();
		if(!currentUuid) return;
		var slcExec = new org.argeo.slc.ria.SlcExecutionMessage(currentUuid);
		slcExec.setStatus(this.fields.status.getValue());		
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