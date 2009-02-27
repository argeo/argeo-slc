/**
 * A simple Hello World applet for documentation purpose. 
 * The only associated command is the "Close" command.
 */
qx.Class.define("org.argeo.slc.ria.NewLauncherApplet",
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
  			"submitform" : {
  				label	 	: "Execute Batch On...", 
  				icon 		: "resource/slc/media-playback-start.png",
  				shortcut 	: null,
  				enabled  	: true,
  				menu	   	: "Launcher",
  				toolbar  	: null,
  				callback	: function(e){},
  				submenu		: [],
  				submenuCallback : function(commandId){
  					alert("Execute Batch on Agent "+commandId);
  				},
  				command 	: null
  			},  			
  			"editexecutionspecs" : {
  				label	 	: "Edit Execution Specs", 
  				icon 		: "resource/slc/document-open.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Launcher",
  				toolbar  	: null,
  				callback	: function(e){
  					var sel = this.list.getSortedSelection();
  					var item = sel[0];
			  		alert('Edit Specs for : ' + item.getLabel());
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "form") return;
  					this.setEnabled(false);
  					if((selection && selection.length == 1)) this.setEnabled(true);
  				},
  				command 	: null
  			},
  			"removefrombatch" : {
  				label	 	: "Remove from batch", 
  				icon 		: "resource/slc/edit-delete.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Launcher",
  				toolbar  	: null,
  				callback	: function(e){
  					var sel = this.list.getSortedSelection();
			  		alert('Remove '+ sel.length+ ' elements from batch');
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "form") return;
  					this.setEnabled(false);
  					if((selection && selection.length > 0)) this.setEnabled(true);
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
  			},
  			"reloadtree" : {
  				label	 	: "Reload Node", 
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+m",
  				enabled  	: true,
  				menu	   	: "Launcher",
  				toolbar  	: "launcher",
  				callback	: function(e){
  					var selected = this.tree.getSelectedItem();
  					if(selected.classname == "org.argeo.ria.components.DynamicTreeFolder"){
	  					selected.reload();
  					}
  				},  				
  				command 	: null
  			}
  		}
  	},
  	/**
  	 * A map containing all currently registered agents.
  	 */
  	registeredTopics : {
  		init : {},
  		check : "Map", 
  		event : "changeRegisteredTopics"
  	}
  },

  statics : {
	flowLoader : function(folder){
		var moduleData = folder.getUserData("executionModule");
  		var req = org.argeo.ria.remote.RequestManager.getInstance().getRequest("stub.xml", "GET", "application/xml");
  		req.addListener("completed", function(response){
  			var executionModule = new org.argeo.slc.ria.execution.Module();			  			
  			executionModule.setXmlNode(response.getContent());
  			var execFlows = executionModule.getExecutionFlows();
  			for(var key in execFlows){
  				var file = new qx.ui.tree.TreeFile(key);
  				file.setUserData("executionFlow", execFlows);
  				folder.add(file);
  			}
  			folder.setLoaded(true);
  		});
  		req.send();		
	},
	
	modulesLoader : function(folder){
		// Call service, and parse
		var mods = {
			"Module 1":["ver1.1", "ver1.2", "ver1.3"], 
			"Module 2":["ver2.1", "ver2.2", "ver2.3", "ver2.4"], 
			"Module 3":["ver3.1", "ver3.2"]
		};
		var flowLoader = org.argeo.slc.ria.NewLauncherApplet.flowLoader;
		for(var key in mods){
			var moduleFolder = new qx.ui.tree.TreeFolder(key);
			folder.add(moduleFolder);
			for(var i=0;i<mods[key].length;i++){
				var versionFolder = new org.argeo.ria.components.DynamicTreeFolder(
					mods[key][i],
					flowLoader,
					"Loading Flows",
					folder.getDragData()
				);
				moduleFolder.add(versionFolder);
				//versionFolder.setUserData("executionModule", "object");
			}
			folder.setLoaded(true);
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
  		//this._createForm();
  		this._amqClient = org.argeo.ria.remote.JmsClient.getInstance();
  		this._amqClient.startPolling();
  	},
  	
  	/**
  	 *  
  	 */
  	load : function(){
  		this._createLayout();
  		this.getView().setViewTitle("Execution Launcher");
		org.argeo.ria.remote.RequestManager.getInstance().addListener("reload", function(reloadEvent){
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
  		this.addListener("changeRegisteredTopics", function(event){
  			//this._refreshTopicsSubscriptions(event);
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
  		
  		this._amqClient.addListener("modulesResponse", "modulesManager.response", function(message){
  			this.info(message);
  		}, this);  		
  	},
  	 
	addScroll : function(){
		return false;
	},
	
	close : function(){
  		this._amqClient.removeListener("agentregister", "topic://agent.register");
  		this._amqClient.removeListener("agentunregister", "topic://agent.unregister");
  		this._amqClient.removeListener("modulesResponse", "topic://modulesManager.response");
  		this.setRegisteredTopics({});
		this._amqClient.stopPolling();
	},
	  	
	/**
	 * Creates the main applet layout.
	 */
	_createLayout : function(){
		
		var splitPane = new qx.ui.splitpane.Pane("vertical");
		splitPane.setDecorator(null);
		this.add(splitPane);
		
		this.formPane = new qx.ui.container.Composite(new qx.ui.layout.VBox(5));		
		this.scroll = new qx.ui.container.Scroll(this.formPane);
		this.formPane.setPadding(10);
		
		this.tree = new qx.ui.tree.Tree();
		this.tree.setDecorator(null);
		var dragData = {
			"file" : {
				"type" : ["items"], 
				"action":["move"]
			}
		};
		
		var root = new org.argeo.ria.components.DynamicTreeFolder(
			"All Tests", 
			this.self(arguments).modulesLoader,
			"Loading Modules",
			dragData
		);
		this.tree.setRoot(root);
		root.setOpen(true);
		
		this.listPane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
		var listToolBar = new qx.ui.toolbar.ToolBar();		
		var toolGroup = new qx.ui.toolbar.Part();		
		listToolBar.add(toolGroup);
		
		var execButton = this.getCommands()["submitform"].command.getToolbarButton();
		toolGroup.add(execButton);

	    listToolBar.addSpacer();
	    listToolBar.setPaddingRight(4);
	    var delButton = this.getCommands()["removefrombatch"].command.getToolbarButton();
	    var formButton = this.getCommands()["editexecutionspecs"].command.getToolbarButton();
	    delButton.setShow("icon");
	    formButton.setShow("icon");
	    listToolBar.add(formButton);
	    listToolBar.add(delButton);
				
		this.listPane.add(listToolBar, {edge:"north"});
		
		this.list = new qx.ui.form.List();
		this.list.setDecorator(null);
		this.list.setSelectionMode("multi");
		this.list.setDroppable(true);
		this.list.addListener("drop", function(e){
		  var label = e.getRelatedTarget().getLabel();
		  var li = new qx.ui.container.Composite(new qx.ui.layout.HBox(5));
		  li.add(new qx.ui.basic.Label(label));
		  var item = new qx.ui.form.ListItem(label, e.getRelatedTarget().getIcon());
		  item.setPaddingTop(1);
		  item.setPaddingBottom(2);
		  this.list.add(item);
		  this.list.select(item);
		  this.getCommands()["editexecutionspecs"].command.execute();
		}, this);		
		this.listPane.add(this.list, {edge:"center"});		
		
		this.list.addListener("changeSelection", function(e){
			var viewSelection = this.getViewSelection();
			viewSelection.clear();
			var listSel = this.list.getSortedSelection();
			for(var i=0;i<listSel.length;i++){
				viewSelection.addNode(listSel[i]);
			}
		}, this);
		
		splitPane.add(this.tree, 0);
		splitPane.add(this.listPane, 1);
		//this.add(this.scroll, {edge:'center'});
		
	},
		
	/**
	 * Creates the form.
	 */
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
	
	/**
	 * Show a form part given its id.
	 * @param partId {String} The part id
	 */
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
	
	/**
	 * Init a form part : creates a pane, a set of fields, etc.
	 * @param formId {String} A unique ID
	 * @param label {String} A label
	 * @return {Map} The form part.
	 */
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
	
	/**
	 * To be overriden by this class children.
	 */
	_createFormVariableParts : function(){
		var standard = this._initFormPart("standard", "Canonical");
		this._createStandardForm(standard);
		var simple = this._initFormPart("simple", "SLC Sample");
		this._createSimpleForm(simple);
	},
	
	/**
	 * Creates a form for SLC demo
	 * @param formObject {Map} The form part
	 */
	_createSimpleForm : function(formObject){

		this._addFormInputText(formObject, "ant.file", "File", "Category1/SubCategory2/build.xml");
		var moreButton = new qx.ui.basic.Image("resource/slc/list-add.png");
		moreButton.setToolTip(new qx.ui.tooltip.ToolTip("Add a parameter"));
		moreButton.setCursor("pointer");
		moreButton.addListener("click", function(){
			this._addFormInputText(formObject);
		}, this);
		this._addFormHeader(formObject, "Add optionnal parameters", moreButton);
		this._addFormInputText(formObject);
		this._addFormInputText(formObject);		
		
	},
	
	/**
	 * Create a canonical form.
	 * @param formObject {Map} The form part
	 */
	_createStandardForm : function(formObject){
		
		this._addFormHeader(formObject, "Set Execution Parameters");
		this._addFormInputText(formObject, "status", "Status", "STARTED");
		this._addFormInputText(formObject, "host", "Host", "localhost");
		this._addFormInputText(formObject, "user", "User", "user");
		
		var moreButton = new qx.ui.basic.Image("resource/slc/list-add.png");
		moreButton.setToolTip(new qx.ui.tooltip.ToolTip("Add a parameter"));
		moreButton.setCursor("pointer");
		moreButton.addListener("click", function(){
			this._addFormInputText(formObject);
		}, this);
		this._addFormHeader(formObject, "Add optionnal parameters", moreButton);
		this._addFormInputText(formObject);
		this._addFormInputText(formObject);		
	},
	
	/**
	 * Add an hidden field to the form
	 * @param formObject {Map} The form part
	 * @param fieldName {String} Name
	 * @param fieldValue {String} Value
	 */
	_addFormHiddenField : function(formObject, fieldName, fieldValue){
		formObject.hiddenFields[fieldName] = fieldValue;
	},
	
	/**
	 * Creates a simple label/input form entry.
	 * @param formObject {Map} The form part
	 * @param fieldName {String} Name
	 * @param fieldLabel {String} Label of the field
	 * @param defaultValue {String} The default value
	 * @param choiceValues {Map} An map of values
	 */
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
	
	/**
	 * Add an header
	 * @param formObject {Map} The form part
	 * @param content {Mixed} Content to add.
	 * @param additionnalButton {Mixed} Any widget to add on the east.
	 */
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
	
	/**
	 * Adds a label/input like entry in the form.
	 * @param formObject {Map} The form part
	 * @param labelElement {Object} Either a label or an input 
	 * @param fieldElement {Object} Any form input.
	 */
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
	
	/*
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
	*/
		
	/**
	 * Refresh the selector when the topics are updated.
	 * @param changeTopicsEvent {qx.event.type.DataEvent} The reload event.
	 */
	_feedSelector : function(changeTopicsEvent){
		var topics = changeTopicsEvent.getData();
		var command = this.getCommands()["submitform"].command;
		command.setEnabled(false);
		var menu = [];
		for(var key in topics){
			var submenu = {"label":topics[key],"icon":"resource/slc/mime-xsl.png", "commandId":key};
			menu.push(submenu);
		}
		command.clearMenus();
		command.setMenu(menu);
		if(menu.length) command.setEnabled(true);
	},
	
	/*
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
	*/
	
	/**
	 * Make an SlcExecutionMessage from the currently displayed form.
	 * @param crtPartId {String} The form part currently displayed
	 * @param slcExec {org.argeo.slc.ria.SlcExecutionMessage} The message to fill.
	 * @param fields {Map} The fields of the form
	 * @param hiddenFields {Map} The hidden ones 
	 * @param freeFields {Array} The free fields.
	 */
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
	
	/**
	 * Called when the user clicks the "Execute" button.
	 */
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
				
		this._amqClient.sendMessage(
			"topic://agent.newExecution", 
			slcExec.toXml(), 
			{"slc-agentId":currentUuid}
		);
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