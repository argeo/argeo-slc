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
  	 * A boolean registering whether the SpecEditor must autoOpen or not when a spec is added to the Batch. 
  	 */
  	autoOpen : {
  		init : true,
  		check : "Boolean"
  	},
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
  					//alert("Execute Batch on Agent "+commandId);
  					this.executeBatchOnAgent(commandId);
  				},
  				command 	: null
  			},  			
  			"addtobatch" : {
  				label	 	: "Add to batch", 
  				icon 		: "resource/slc/list-add.png",
  				shortcut 	: null,
  				enabled  	: true,
  				menu	   	: null,
  				toolbar  	: null,
  				callback	: function(e){
  					this._addFlowToBatch();
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "form:tree") return;
  					if(!selection || selection.length != 1) return;
  					var item = selection[0];
  					this.setEnabled(false);
  					if(qx.Class.isSubClassOf(qx.Class.getByName(item.classname), qx.ui.tree.TreeFile)){
  						this.setEnabled(true);
  					}
  					
  				},  				
  				command 	: null
  			}, 
  			"toggleopenonadd" : {
  				label		: "Auto Open", 
  				icon 		: "resource/slc/document-open.png",
  				shortcut 	: null,
  				enabled  	: true,
  				toggle		: true,
  				toggleInitialState	: true,
  				menu	   	: "Launcher",
  				toolbar  	: "launcher",
  				callback 	: function(event){
  					var state = event.getTarget().getUserData("slc.command.toggleState");
  					this.setAutoOpen(state);
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
  					 var specEditor = new org.argeo.slc.ria.execution.SpecEditor(item.getUserData("batchEntrySpec"));
  					specEditor.attachAndShow();
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "form:list") return;
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
  					var modal = new org.argeo.ria.components.Modal("Confirm", null);
  					modal.addConfirm("Are you sure you want to remove<br> the selected test" + (sel.length>1?"s":"") + " from the Batch?");
  					modal.addListener("ok", function(){
				  		for(var i=0;i<sel.length;i++){
				  			this.list.remove(sel[i]);
				  		}
  					}, this);
  					modal.attachAndShow();  					
  				},
  				selectionChange : function(viewId, selection){
  					if(viewId != "form:list") return;					
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
  				label	 	: "Reload", 
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+m",
  				enabled  	: false,
  				menu	   	: "Launcher",
  				toolbar  	: "launcher",
  				callback	: function(e){
  					var selected = this.tree.getSelectedItem();
  					if(selected.classname == "org.argeo.ria.components.DynamicTreeFolder"){
  						if(selected.getUserData("moduleData")){
  							// It's a "module" node, first trigger the reloadBundle.service
  							selected.setEnabled(false);
  							selected.setOpen(false);
  							var moduleData = selected.getUserData("moduleData");
  							var bundleService = org.argeo.slc.ria.SlcApi.getReloadBundleService(moduleData.name, moduleData.version);
  							bundleService.addListener("completed", function(response){
  								selected.setEnabled(true);
  								selected.setOpen(true);
		  						selected.reload();
  							}, this);
  							//bundleService.send();
  							//Do not send, not implemented yet, false timer instead.
  							qx.event.Timer.once(function(response){
  								selected.setEnabled(true);
  								selected.setOpen(true);
		  						selected.reload();
  							}, this, 2000);
  						}else{
		  					selected.reload();
  						}
  					}
  				},  
  				selectionChange : function(viewId, selection){
  					if(viewId != "form:tree") return;
  					if(!selection || selection.length != 1) return;
  					var item = selection[0];
  					if(!qx.Class.isSubClassOf(qx.Class.getByName(item.classname), qx.ui.tree.AbstractTreeItem)) return;
  					this.setEnabled(false);
  					if(qx.Class.isSubClassOf(qx.Class.getByName(item.classname), org.argeo.ria.components.DynamicTreeFolder)){
  						this.setEnabled(true);
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
  	/**
  	 * Loader for the "flow" level : takes a folder containing "moduleData" and create its children. 
  	 * @param folder {qx.ui.tree.TreeFolder} A Tree folder containing in the key "moduleData" of its user data a map containing the keys {name,version} 
  	 */
	flowLoader : function(folder){
		var moduleData = folder.getUserData("moduleData");
  		//var req = org.argeo.ria.remote.RequestManager.getInstance().getRequest("../argeo-ria-src/stub.xml", "GET", "application/xml");
		var req = org.argeo.slc.ria.SlcApi.getLoadExecutionDescriptorService(moduleData.name, moduleData.version);
  		req.addListener("completed", function(response){
  			var executionModule = new org.argeo.slc.ria.execution.Module();			  			
  			executionModule.setXmlNode(response.getContent());
  			var execFlows = executionModule.getExecutionFlows();
  			for(var key in execFlows){
  				if(execFlows[key].getPath()){
  					// Build a more complex tree with the path
  				}
  				var file = new qx.ui.tree.TreeFile(key);
  				file.setUserData("executionModule", executionModule);
  				file.setUserData("executionFlow", execFlows[key]);
  				folder.add(file);
  			}
  			folder.setLoaded(true);
  		});
  		req.send();		
	},
	
	/**
	 * Loader for the "modules" level : takes any tree folder, currently the root folder. 
	 * @param folder {qx.ui.tree.TreeFolder} The root folder
	 */
	modulesLoader : function(folder){
		var req = org.argeo.slc.ria.SlcApi.getListModulesService();
		req.addListener("completed", function(response){
			var descriptors = org.argeo.ria.util.Element.selectNodes(response.getContent(), "slc:object-list/slc:execution-module-descriptor");
			var mods = {};
			for(var i=0;i<descriptors.length; i++){
				var name = org.argeo.ria.util.Element.getSingleNodeText(descriptors[i], "slc:name");
				var version = org.argeo.ria.util.Element.getSingleNodeText(descriptors[i], "slc:version");
				if(!mods[name]) mods[name] = [];
				mods[name].push(version);
			}
			var flowLoader = org.argeo.slc.ria.NewLauncherApplet.flowLoader;
			for(var key in mods){
				for(var i=0;i<mods[key].length;i++){
					var versionFolder = new org.argeo.ria.components.DynamicTreeFolder(
						key + ' ('+mods[key][i]+')',
						flowLoader,
						"Loading Flows",
						folder.getDragData()
					);
					folder.add(versionFolder);
					versionFolder.setUserData("moduleData", {name:key, version:mods[key][i]});
				}
				folder.setLoaded(true);
			}
		});
		req.send();		
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
  		this._amqClient = org.argeo.ria.remote.JmsClient.getInstance();
  		this._amqClient.uri = "/org.argeo.slc.webapp/amq";
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
		this.tree.setContextMenu(org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["addtobatch", "reloadtree"]));
		
		this.tree.addListener("changeSelection", function(e){
			var viewSelection = this.getViewSelection();
			viewSelection.setViewId("form:tree");
			viewSelection.clear();
			var sel = this.tree.getSortedSelection();
			for(var i=0;i<sel.length;i++){
				viewSelection.addNode(sel[i]);
			}			
		}, this);
		
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
		
		var indicator = new qx.ui.core.Widget();
		indicator.setDecorator(new qx.ui.decoration.Single().set({top:[1,"solid","#33508D"]}));
		indicator.setHeight(0);
		indicator.setOpacity(0.5);
		indicator.setZIndex(100);
		indicator.setLayoutProperties({left:-1000,top:-1000});
		org.argeo.ria.Application.INSTANCE.getRoot().add(indicator);
		
		
		this.list = new qx.ui.form.List();
		this.list.setDecorator(null);
		this.list.setSelectionMode("multi");
		this.list.setDroppable(true);
		this.list.setDraggable(true);
		this.list.setContextMenu(org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["editexecutionspecs", "removefrombatch"]));
		
		
		this.list.addListener("dragstart", function(e){
			e.addType(["items"]);
			e.addAction(["move"]);
		},this);
		this.list.addListener("dragend", function(e){
			indicator.setDomPosition(-1000,-1000);
		});
		this.list.addListener("dragover", function(e){
			var orig = e.getOriginalTarget();
			var origCoords = orig.getContainerLocation();
			indicator.setWidth(orig.getBounds().width);
			indicator.setDomPosition(origCoords.left, origCoords.bottom);
		});
		this.list.addListener("drag", function(e){
			var orig = e.getOriginalTarget();
			var origCoords = orig.getContainerLocation();
			indicator.setWidth(orig.getBounds().width);
			indicator.setDomPosition(origCoords.left, origCoords.bottom);
		});
		
		this.list.addListener("drop", function(e){
			var target = e.getRelatedTarget();
			var afterItem = e.getOriginalTarget();
			indicator.setDomPosition(-1000,-1000);
			if(afterItem.classname != "qx.ui.form.ListItem") afterItem = null;
			if(!target){
				target = this.list.getSortedSelection()[0];
			}
			this._addFlowToBatch(target, afterItem);
		}, this);		
		this.listPane.add(this.list, {edge:"center"});		
		
		this.list.addListener("changeSelection", function(e){
			var viewSelection = this.getViewSelection();
			viewSelection.setViewId("form:list");
			viewSelection.clear();
			var listSel = this.list.getSortedSelection();
			for(var i=0;i<listSel.length;i++){
				viewSelection.addNode(listSel[i]);
			}
		}, this);
		
		splitPane.add(this.tree, 0);
		splitPane.add(this.listPane, 1);		
	},
	
	/**
	 * Adds a given ExecutionFlow to the batch
	 * @param target {mixed} The dropped target, can be a TreeFile (add) or a ListItem (reorder).
	 * @param after {qx.ui.form.ListItem} Optional list item : if set, the flow will be added as a new list item positionned after this one. 
	 */
	_addFlowToBatch : function(target, after){
		//this.debug(target);
		if(!target){
			 target = this.tree.getSelectedItem();
			 if(!target) return;
		}else if(target.classname == "qx.ui.form.ListItem"){
			if(!after) return;
			if(after == "first") this.list.addAt(target, 0);
			else this.list.addAfter(target, after);
			return;
		}
		var executionModule = target.getUserData("executionModule");
		var executionFlow = target.getUserData("executionFlow");
		var batchEntry = new org.argeo.slc.ria.execution.BatchEntrySpec(executionModule, executionFlow);
		var label = batchEntry.getLabel();
	  	var icon = target.getIcon();
		var item = new qx.ui.form.ListItem(label, icon);
		item.addListener("dblclick", function(e){
			this.getCommands()["editexecutionspecs"].command.execute();
		}, this);
		item.setUserData("batchEntrySpec", batchEntry);
		item.setPaddingTop(1);
		item.setPaddingBottom(2);
		if(after){
			if(after == "first") this.list.addAt(item, 0);
			else this.list.addAfter(item, after);
		}else{
			this.list.add(item);
		}
		this.list.select(item);
		if(this.getAutoOpen()){
			this.getCommands()["editexecutionspecs"].command.execute();
		}
	},
			
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
		// FAKE!!
		if(!menu.length){
			menu.push({"label":"Fake Agent", "icon":"resource/slc/mime-xsl.png", "commandId":"fake_agent_uuid"});
		}
		command.clearMenus();
		command.setMenu(menu);
		if(menu.length) command.setEnabled(true);
	},
		
	
	/**
	 * Called at execution
	 * @param agentUuid {String} The id of the target agent
	 */
	executeBatchOnAgent : function(agentUuid){
		var selection = this.list.getChildren();
		if(!selection.length) return;
		var slcExecMessage = new org.argeo.slc.ria.execution.Message();
		for(var i=0;i<selection.length;i++){
			var batchEntrySpec = selection[i].getUserData("batchEntrySpec");
			slcExecMessage.addBatchEntrySpec(batchEntrySpec);
		}		
		this._amqClient.sendMessage(
			"topic://agent.newExecution", 
			slcExecMessage.toXml(), 
			{"slc-agentId":agentUuid}
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