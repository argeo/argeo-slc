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
	
	this.COMMON_FORM_HEADER_LABEL = "Choose Agent";
	this.CHOOSE_AGENT_LABEL = "Agent Uuid";
	this.CHOOSE_FORM_PART_LABEL = "Test Type";
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
  					this.submitForm();
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
  			// Delay reload to be sure the jms was first integrated by the db, then ask the db.
  			qx.event.Timer.once(function(){
  				org.argeo.ria.event.CommandsManager.getInstance().getCommandById("reloadagents").execute();
  			}, this, 1000);	  		
  		}
  		this._amqClient.addListener("agentregister", "topic://agent.register", reloadHandler, this);
		this._amqClient.addListener("agentunregister", "topic://agent.unregister", reloadHandler, this);
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
		this.formPane.setPadding(10);
		this.add(this.scroll, {edge:'center'});
		
	},
		
	_createForm : function(){
  		this.fields = {};
  		this.hiddenFields = {};
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
		
		var commonForm = {pane:this.formPane};		
		this._addFormHeader(commonForm, this.COMMON_FORM_HEADER_LABEL);
		this._addFormEntry(commonForm, new qx.ui.basic.Label(this.CHOOSE_AGENT_LABEL), this.agentSelector);
		this._createFormVariableParts();
		if(!this.parts) return;
		if(qx.lang.Object.getLength(this.parts) > 1){
			// Add chooser
			this.partChooser = new qx.ui.form.SelectBox();
			for(var key in this.parts){
				this.partChooser.add(new qx.ui.form.ListItem(this.parts[key].label, null, key));
			}
			this._addFormEntry(commonForm, new qx.ui.basic.Label(this.CHOOSE_FORM_PART_LABEL), this.partChooser);
			this.partChooser.addListener("changeValue", function(ev){
				this._showSelectedPart(ev.getData());
			}, this);
		}
		this._showSelectedPart(qx.lang.Object.getKeys(this.parts)[0]);		
	},
	
	_showSelectedPart : function(partId){
		if(!this.parts) return;
		if(!this.partsContainer){
			this.partsContainer = new qx.ui.container.Composite(new qx.ui.layout.Canvas());
			this.formPane.add(this.partsContainer);			
		}
		for(var i in this.parts){
			var formObject = this.parts[i];
			if(!formObject.added){
				this.partsContainer.add(formObject.pane, {top:0, left:0});
				formObject.added = true;
			}
			formObject.pane.hide();
		}
		if(this.parts[partId]){
			this.parts[partId].pane.show();
		}
	},
	
	_initFormPart : function(formId, label){
		if(!this.parts) this.parts = {};		
		var formObject = {};
		formObject.hiddenFields = {};
		formObject.freeFields = [];
		formObject.fields = {};
		formObject.id = formId;
		formObject.label = label;
		this.parts[formId] = formObject;
		formObject.pane = new qx.ui.container.Composite(new qx.ui.layout.VBox(5));
		return formObject;
	},
	
	_createFormVariableParts : function(){
		var standard = this._initFormPart("standard", "Canonical");
		this._createStandardForm(standard);
		var simple = this._initFormPart("simple", "SLC Sample");
		this._createSimpleForm(simple);
	},
	
	_createSimpleForm : function(formObject){

		this._addFormInputText(formObject, "ant.file", "File", "Category1/SubCategory2/build.xml");
		var moreButton = new qx.ui.basic.Image("resource/slc/list-add.png");
		moreButton.setToolTip(new qx.ui.tooltip.ToolTip("Add a parameter"));
		moreButton.setCursor("pointer");
		moreButton.addListener("click", function(){
			this._addFormInputText();
		}, this);
		this._addFormHeader(formObject, "Add optionnal parameters", moreButton);
		this._addFormInputText(formObject);
		this._addFormInputText(formObject);		
		
	},
	
	_createStandardForm : function(formObject){
		
		this._addFormHeader(formObject, "Set Execution Parameters");
		this._addFormInputText(formObject, "status", "Status", "STARTED");
		this._addFormInputText(formObject, "host", "Host", "localhost");
		this._addFormInputText(formObject, "user", "User", "user");
		
		var moreButton = new qx.ui.basic.Image("resource/slc/list-add.png");
		moreButton.setToolTip(new qx.ui.tooltip.ToolTip("Add a parameter"));
		moreButton.setCursor("pointer");
		moreButton.addListener("click", function(){
			this._addFormInputText();
		}, this);
		this._addFormHeader(formObject, "Add optionnal parameters", moreButton);
		this._addFormInputText(formObject);
		this._addFormInputText(formObject);		
	},
	
	_addFormHiddenField : function(formObject, fieldName, fieldValue){
		formObject.hiddenFields[fieldName] = fieldValue;
	},
	
	_addFormInputText : function(formObject, fieldName, fieldLabel, defaultValue, choiceValues){
		var labelElement;
		if(choiceValues){
			var fieldElement = new qx.ui.form.SelectBox();
			for(var key in choiceValues){
				fieldElement.add(new qx.ui.form.ListItem(choiceValues[key], null, key));
			}
		}else{
			var fieldElement = new qx.ui.form.TextField();
		}
		if(defaultValue){
			fieldElement.setValue(defaultValue);
		}
		if(fieldName && fieldLabel){
			labelElement = new qx.ui.basic.Label(fieldLabel);
			formObject.fields[fieldName] = fieldElement;
		}else{
			labelElement = new qx.ui.form.TextField();
			formObject.freeFields.push({
				labelEl:labelElement, 
				valueEl:fieldElement
			});
		}
		this._addFormEntry(formObject, labelElement, fieldElement);
	},
	
	_addFormHeader : function(formObject, content, additionnalButton){
		var header = new qx.ui.basic.Label('<b>'+content+'</b>');
		header.setRich(true);		
		if(!additionnalButton){
			header.setPaddingTop(10);
			formObject.pane.add(header);
		}else{
			var pane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
			pane.setPaddingTop(10);
			pane.setPaddingRight(10);
			pane.add(header, {edge:'center'});
			pane.add(additionnalButton, {edge:'east'});
			formObject.pane.add(pane);
		}
	},
	
	_addFormEntry : function(formObject, labelElement, fieldElement){
		var entryPane = new qx.ui.container.Composite(new qx.ui.layout.HBox(5));
		labelElement.setWidth(100);
		labelElement.setTextAlign("right");		
		entryPane.add(labelElement);
		entryPane.add(new qx.ui.basic.Label(':'));
		fieldElement.setWidth(150);
		entryPane.add(fieldElement);
		formObject.pane.add(entryPane);
	},
	
	_refreshTopicsSubscriptions : function(changeTopicsEvent){
		var oldTopics = changeTopicsEvent.getOldData() || {};
		var newTopics = changeTopicsEvent.getData();
		var removed = [];
		var added = [];
		for(var key in oldTopics){
			if(!newTopics[key]) {
				//this._removeAmqListener(key);
			}
		}
		for(var key in newTopics){
			if(!oldTopics[key]) {
				//this._addAmqListener(key);
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
	
	_prepareSlcExecutionMessage : function(crtPartId, slcExec, fields, hiddenFields, freeFields){
		if(crtPartId == "standard"){
			slcExec.setStatus(fields.status.getValue());		
			slcExec.setHost(fields.host.getValue());
			slcExec.setUser(fields.user.getValue());
		}else{
			slcExec.addAttribute("ant.file", fields["ant.file"].getValue());
		}
		for(var i=0;i<freeFields.length;i++){
			var fF = freeFields[i];
			if(fF.labelEl.getValue() != "" && fF.valueEl.getValue() != ""){
				slcExec.addAttribute(fF.labelEl.getValue(), fF.valueEl.getValue());
			}
		}		
	},
	
	submitForm : function(){
		var currentUuid = this.agentSelector.getValue();
		if(!currentUuid) return;
		var slcExec = new org.argeo.slc.ria.SlcExecutionMessage();
		
		var fields = {};
		var hiddenFields = {};
		var freeFields = {};
		var crtPartId = "";
		if(this.parts){
			if(this.partChooser){
				crtPartId = this.partChooser.getValue();
			}else{
				crtPartId = qx.lang.Object.getKeys(this.parts)[0];
			}
			var crtPart = this.parts[crtPartId];
			fields = crtPart.fields;
			hiddenFields = crtPart.hiddenFields;
			freeFields = crtPart.freeFields;
		}
		
		this._prepareSlcExecutionMessage(crtPartId, slcExec, fields, hiddenFields, freeFields);
		
		var destination = "topic://agent."+currentUuid+".newExecution";
		this._amqClient.sendMessage(destination, slcExec.toXml());
		// Force logs refresh right now!
		qx.event.Timer.once(function(){
			var command = org.argeo.ria.event.CommandsManager.getInstance().getCommandById("reloadlogs");
			if(command){
				command.execute();
			}
		}, this, 2000);
	}
	  	
  }
});