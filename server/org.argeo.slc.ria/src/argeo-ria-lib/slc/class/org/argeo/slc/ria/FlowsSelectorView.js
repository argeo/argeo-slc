/**
 * The selector view
 * 
 */
qx.Class.define("org.argeo.slc.ria.FlowsSelectorView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView],
	include : [org.argeo.ria.session.MPrefHolder],

	construct : function() {
		this.base(arguments);
		this.setLayout(new qx.ui.layout.Dock());
	},

	properties : {
		/**
		 * The viewPane inside which this applet is added.
		 */
		view : {
			init : null
		},
		viewSelection : {
			nullable : false,
			check : "org.argeo.ria.components.ViewSelection"
		},
		instanceId : {
			init : ""
		},
		instanceLabel : {
			init : ""
		},
		executeAfterAdd : {
			init : false,
			check : "Boolean"
		},
		/**
		 * Commands definition, see
		 * {@link org.argeo.ria.event.CommandsManager#definitions}
		 */
		commands : {
			init : {
				"addtobatch" : {
					label : "Add to batch",
					icon : "org.argeo.slc.ria/list-add.png",
					shortcut : null,
					enabled : true,
					menu : null,
					toolbar : null,
					callback : function(e) {
						if (this.tree.isSelectionEmpty()) {
							return;
						}
						var batchView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("batch").getContent();
						if(!batchView) return;
						selection = this.tree.getSelection();
						if (selection.length > 1) {
							for (var i = 0; i < selection.length; i++) {
								try{
									batchView.addFlowToBatch(selection[i], null, true);
								}catch(e){
									return;
								}
							}
							return;
						} else {
							try{
								batchView.addFlowToBatch(selection[0], null);
							}catch(e){
								return;
							}
						}
						if(this.getExecuteAfterAdd() && batchView.getCommands()){
							batchView.setForceClearPreference(true);
							batchView.getCommands()["submitform"].command.execute();
						}
						this.setExecuteAfterAdd(false);
					},
					selectionChange : function(viewId, selection) {
						if (viewId != "form:tree")
							return;
						if (!selection || selection.length != 1)
							return;
						var item = selection[0];
						this.setEnabled(false);
						switch (item.classname) {
							case "qx.ui.tree.TreeFile" :
								this.setEnabled(true);
								break;
							case "org.argeo.ria.components.PersistentTreeFolder" :
								if (item.getTree() && item.getTree().getRoot() == item)
									break;
								this.setEnabled(true);
								break;
							case "org.argeo.ria.components.DynamicTreeFolder" :
								if (item.getTree() && item.getTree().getRoot() == item)
									break;	
								if (item.getState() == "loaded")
									this.setEnabled(true);
								break;
						}
					},
					command : null
				},
				"preferredHost" : {
					label : "Toggle 'Preferred Host'",
					icon : "ria/bookmark.png",
					shortcut : null,
					enabled : true,
					menu : null,
					toolbar : null,
					callback : function(e) {
						if (this.tree.isSelectionEmpty()) {
							return;
						}
						var selection = this.tree.getSelection();
						if(selection.length != 1) return;
						var agentNode = selection[0];
						if(!agentNode.getUserData("agentHost")) return;
						this.togglePreferredHost(agentNode);
					},
					selectionChange : function(viewId, selection) {
						if (viewId != "form:tree")
							return;
						if (!selection || selection.length != 1)
							return;
						var item = selection[0];
						this.setEnabled(false);
						if(item.getUserData("agentHost")){
							this.setEnabled(true);
						}
					},
					command : null
				},
				"reloadfull" : {
					label : "Reload Agents",
					icon : "org.argeo.slc.ria/view-refresh.png",
					shortcut : "control+h",
					enabled : true,
					menu : "Launcher",
					toolbar : "list",
					callback : function(e) {
						this.rootNode.reload();
					},
					command : null
				},				
				"reloadtree" : {
					label : "Reload Node",
					icon : "org.argeo.slc.ria/view-refresh.png",
					shortcut : null,
					enabled : false,
					menu : null,
					toolbar : null,
					callback : function(e) {
						if (this.tree.isSelectionEmpty()) {	return;	}						
						var selected = this.tree.getSelection()[0];
						if (selected.classname == "org.argeo.ria.components.DynamicTreeFolder") {
							if (selected.getUserData("moduleData")) {
								// It's a "module" node, first trigger the
								// reloadBundle.service
								selected.setUserData("dataModel", {});
								selected.setEnabled(false);
								selected.setOpen(false);
								var moduleData = selected
										.getUserData("moduleData");
								var bundleService = org.argeo.slc.ria.SlcApi
										.getReloadBundleService(
												moduleData.name,
												moduleData.version);
								bundleService.addListener("completed",
										function(response) {
											selected.setEnabled(true);
											selected.setOpen(true);
											selected.reload();
										}, this);
								// bundleService.send();
								// Do not send, not implemented yet, false timer
								// instead.
								qx.event.Timer.once(function(response) {
											selected.setEnabled(true);
											selected.setOpen(true);
											selected.reload();
										}, this, 2000);
							} else {
								selected.reload();
							}
						}
					},
					selectionChange : function(viewId, selection) {
						if (viewId != "form:tree")
							return;
						if (!selection)
							return;
						if (selection.length > 1) {
							this.setEnabled(false);
							return;
						}
						var item = selection[0];
						if (!qx.Class.isSubClassOf(qx.Class
										.getByName(item.classname),
								qx.ui.tree.AbstractTreeItem))
							return;
						this.setEnabled(false);
						if (qx.Class.isSubClassOf(qx.Class
										.getByName(item.classname),
								org.argeo.ria.components.DynamicTreeFolder)) {
							this.setEnabled(true);
						}
					},
					command : null
				}
			}
		}
	},

	statics : {		
		
		riaPreferences : {
			"flowSelector.preferred.hosts" : {
				label : "Preferred Hosts (Execution View)",
				type  : "string"
			}
		},
		
		/**
		 * Static loader for the "agent" level (first level)
		 * 
		 * @param folder
		 *            {org.argeo.ria.components.PersistentTreeFolder} The root Tree Folder.
		 */
		agentLoader : function(folder) {

			var preferredHosts = org.argeo.ria.session.MPrefHolder.loadRiaPreferenceValue("flowSelector.preferred.hosts");
			if(preferredHosts && preferredHosts!=""){
				preferredHosts = preferredHosts.split(",");
			}
			var req = org.argeo.slc.ria.SlcApi.getListAgentsService("agents");			
			var agents = {};
			if(folder.getState() == "loaded" && folder.getUserData("agentsMap")){
				// Diff loading, just add new nodes.
				agents = folder.getUserData("agentsMap");
				var newAgents = {};
			}
			req.addListener("completed", function(response) {
				var xmlDoc = response.getContent();
				var nodes = org.argeo.ria.util.Element.selectNodes(xmlDoc,
						"/slc:object-list/slc:slc-agent-descriptor");
				var modulesLoader = org.argeo.slc.ria.FlowsSelectorView.modulesLoader;
				
				for (var i = 0; i < nodes.length; i++) {
					var uuid = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "@uuid");
					var host = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "slc:host");
					if(agents[uuid]){
						newAgents[uuid] = host;
						continue;
					}
					agents[uuid] = host;
					if(newAgents) newAgents[uuid] = host;
					var agentFolder = new org.argeo.ria.components.DynamicTreeFolder(
							host, modulesLoader, "Loading Modules...", folder.getDragData());
					org.argeo.slc.ria.FlowsSelectorView.attachToolTip(agentFolder, uuid);
					agentFolder.setPersistentTreeID(folder.getPersistentTreeID()+"_"+uuid);
					agentFolder.setUserData("agentHost", host); // Used by bookmark system
					agentFolder.setUserData("agentUuid", uuid);
					if(preferredHosts && preferredHosts instanceof Array && qx.lang.Array.contains(preferredHosts, host)){
						folder.addAtBegin(agentFolder);
						agentFolder.setIcon("org.argeo.slc.ria/computer_bookmarked.png");
					}else{
						folder.add(agentFolder);
						agentFolder.setIcon("org.argeo.slc.ria/computer.png");
					}
				}
				if(newAgents){
					// Make sure some agents should not be removed
					for(var agentKey in agents){
						if(!newAgents[agentKey]){
							var node = org.argeo.slc.ria.FlowsSelectorView.findAgentNodeById(folder, agentKey);
							if(node) folder.remove(node);
							delete agents[agentKey];
							var batchView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("batch").getContent();
							if(batchView) batchView.clearBatchForAgentId(agentKey);
						}
					}
				}
				folder.setUserData("agentsMap", agents);
				folder.setLoaded(true);
				folder.getTree().fireEvent("changeSelection");				
			});
			req.addListener("failed", function(response) {
						folder.setLoaded(true);
					});
			req.send();

		},

		/**
		 * Loader for the "modules" level : takes any tree folder, currently the
		 * root folder.
		 * 
		 * @param folder
		 *            {org.argeo.ria.components.PersistentTreeFolder} The root folder
		 */
		modulesLoader : function(folder) {
			var agentId = folder.getUserData("agentUuid");
			var req = org.argeo.slc.ria.SlcApi.getListModulesService(agentId);
			req.addListener("completed", function(response) {
				var descriptors = org.argeo.ria.util.Element.selectNodes(
						response.getContent(),
						"slc:object-list/" + org.argeo.slc.ria.execution.Module.XPATH_ROOT);
				var mods = {};
				for (var i = 0; i < descriptors.length; i++) {
					var tmpModule = new org.argeo.slc.ria.execution.Module();
					try{						
						tmpModule.setXmlNode(descriptors[i]);
					}catch(e){
						qx.log.Logger.error(e);
					}
					var name = tmpModule.getName();
					var version = tmpModule.getVersion();
					if (!mods[name])
						mods[name] = [];
					mods[name].push(tmpModule);
				}
				var flowLoader = org.argeo.slc.ria.FlowsSelectorView.flowLoader;
				for (var key in mods) {
					for (var i = 0; i < mods[key].length; i++) {
						var module = mods[key][i];
						var versionFolder = new org.argeo.ria.components.DynamicTreeFolder(
								module.getLabel(), flowLoader,
								"Loading Flows", folder.getDragData());
						versionFolder.setUserData("moduleData", {
									name : key,
									version : module.getVersion()
								});
						versionFolder.setIcon("org.argeo.slc.ria/archive.png");
						versionFolder.setUserData("agentUuid", agentId);
						var sep = (module.getDescription()!=""?" - ":"");
						org.argeo.slc.ria.FlowsSelectorView.attachToolTip(versionFolder, key + ' (' + module.getVersion() + ')'+sep+module.getDescription());
						// Warning, we must add it AFTER setting the user data, 
						// because of the persistent loading mechanism.
						folder.add(versionFolder);
					}
				}
				folder.setLoaded(true);
				folder.getTree().fireEvent("changeSelection");
			});
			req.addListener("failed", function(response) {
				folder.setLoaded(true);
			});
			req.send();
		},

		/**
		 * Loader for the "flow" level : takes a folder containing "moduleData"
		 * and create its children.
		 * 
		 * @param folder
		 *            {org.argeo.ria.components.PersistentTreeFolder} A Tree folder containing in the
		 *            key "moduleData" of its user data a map containing the
		 *            keys {name,version}
		 */
		flowLoader : function(folder) {
			var moduleData = folder.getUserData("moduleData");
			var agentUuid = folder.getUserData("agentUuid");

			var req = org.argeo.slc.ria.SlcApi
					.getLoadExecutionDescriptorService(agentUuid,
							moduleData.name, moduleData.version);
			req.addListener("completed", function(response) {
				var executionModule = new org.argeo.slc.ria.execution.Module();
				try {
					executionModule.setXmlNode(response.getContent());
				} catch (e) {
					this.error(e);
				}
				var execFlows = executionModule.getExecutionFlows();
				for (var key in execFlows) {
					var path = execFlows[key].getPath();
					var name = execFlows[key].getName();
					var nodeLabel = key;
					var sep = "\/";
					if(name && name.indexOf(sep)>-1 && !path){
						// Use given name to build the path.
						// split dirname and basename
						var parts = name.split(sep);
						var basename = parts.pop();
						var dirname = parts.join("/");
						path = dirname;
						nodeLabel = basename;
					}
					var file = new qx.ui.tree.TreeFile(nodeLabel);
					if(execFlows[key].getDescription() != ""){
						org.argeo.slc.ria.FlowsSelectorView.attachToolTip(file, execFlows[key].getDescription());
					}
					file.setIcon("org.argeo.slc.ria/system.png");
					file.setUserData("executionModule",	executionModule);
					file.setUserData("executionFlow", execFlows[key]);
					file.setUserData("agentUuid", agentUuid);
					org.argeo.slc.ria.FlowsSelectorView.attachNodeByPath(folder, path, file, {
						agentUuid : folder.getUserData("agentUuid")
					});
					folder.appendDragData(file);
				}
				folder.setLoaded(true);
				folder.getTree().fireEvent("changeSelection");
			});
			req.addListener("failed", function(response) {
				folder.setLoaded(true);
			});
			req.send();
		},

		attachToolTip : function(nodeItem, description){
			var tt = new qx.ui.tooltip.ToolTip(description);
			tt.setShowTimeout(0);
			nodeItem.setToolTip(tt);			
		},
		
		/**
		 * Parse a string path and search if there is a root node.
		 * 
		 * @param rootNode
		 *            {org.argeo.ria.components.DynamicTreeFolder} The parent
		 *            node (containing data model)
		 * @param path
		 *            {String} The path of the node to attach.
		 * @param childNode
		 *            {qx.ui.tree.TreeFile} The leaf node
		 * @param userData
		 *            {Map} User data to attach at all levels.
		 */
		attachNodeByPath : function(rootNode, path, childNode, userData) {
			if (!path || path == "" || path == "/") {
				rootNode.add(childNode);
				return;
			}
			var model = rootNode.getUserData("dataModel");
			if (!model) {
				model = {};
				rootNode.setUserData("dataModel", model);
			}
			var parts = path.split("/");
			var keys = qx.lang.Object.getKeys(model);
			var crtPath = "";
			var crtFolder = rootNode;			
			for (var i = 0; i < parts.length; i++) {
				if (parts[i] == "")
					continue;
				crtPath += "/" + parts[i];				
				if (!model[crtPath]) {
					var virtualFolder = new org.argeo.ria.components.PersistentTreeFolder(parts[i]);
					if (userData && qx.lang.Object.getLength(userData)) {
						for (var key in userData) {
							virtualFolder.setUserData(key, userData[key]);
						}
					}
					rootNode.appendDragData(virtualFolder);
					model[crtPath] = virtualFolder;
					crtFolder.add(virtualFolder);
					crtFolder = virtualFolder;
				} else {
					crtFolder = model[crtPath];
				}
			}
			crtFolder.add(childNode);
		},
		
		findAgentNodeById : function(node, agentId){
			var nodeAgents = node.getItems();
			for(var i=0;i<nodeAgents.length;i++){
				if(nodeAgents[i].getUserData("agentUuid") == agentId){
					return nodeAgents[i];
				}
			}
		}		
	},

	members : {
		/**
		 * Called at applet creation. Just registers viewPane.
		 * 
		 * @param viewPane
		 *            {org.argeo.ria.components.ViewPane} The viewPane.
		 */
		init : function(viewPane) {
			this.setView(viewPane);
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));
			this.remoteNotifier = new org.argeo.ria.remote.RemoteNotifier(
					"/org.argeo.slc.webapp/", "pollEvent.service",
					"addEventListener.service", "removeEventListener.service");
			this.remoteNotifier.setEventParamName("slc_eventType");
			this.remoteNotifier.setEventXPath("/slc:slc-event");
			this.remoteNotifier
					.setEventTypeXPath('slc:headers/slc:header[@name="slc_eventType"]');
			this.remoteNotifier
					.setEventDataXPath('slc:headers/slc:header[@name="slc_agentId"]');
			this.remoteNotifier.setAnswerStatusXPath("slc:execution-answer/slc:status");
			this.remoteNotifier.startPolling();
			this.UIBus = org.argeo.ria.event.UIBus.getInstance();
			this.UIBus.registerNotifier(this.remoteNotifier);
		},

		/**
		 * 
		 */
		load : function() {
			this._createLayout();
			this.getView().setViewTitle("Available Scripts");
			this.UIBus.addListener("agentRegistered", this._addAgentHandler, this);
			this.UIBus.addListener("agentUnregistered", this._removeAgentHandler,	this);
		},

		_addAgentHandler : function(agentId){
			this.rootNode.load();
		},
		
		_removeAgentHandler : function(agentId){
			var treeNode = this.self(arguments).findAgentNodeById(this.rootNode, agentId);
			if(treeNode){
				this.rootNode.remove(treeNode);
			}
			var agentsMap = this.getAgentsMap();
			if(agentsMap[agentId]){				
				delete agentsMap[agentId];
			}
			var batchView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("batch").getContent();
			if(batchView){
				batchView.clearBatchForAgentId(agentId);
			}
		},
		
		addScroll : function() {
			return false;
		},

		close : function() {
			this.UIBus.removeListener("agentRegistered", this._addAgentHandler, this);
			this.UIBus.removeListener("agentUnregistered", this._removeAgentHandler, this);
			this.remoteNotifier.stopPolling();
		},

		/**
		 * 
		 * @param agentNode {qx.ui.tree.AbstractTreeItem}
		 */
		togglePreferredHost : function(agentNode){
			var hostName = agentNode.getUserData("agentHost");
			var pref = this.getRiaPreferenceValue("flowSelector.preferred.hosts");
			var prefArray = [];
			if(pref){
				prefArray = pref.split(",");
			}
			if(qx.lang.Array.contains(prefArray, hostName)){
				qx.lang.Array.remove(prefArray, hostName);
				agentNode.setIcon("org.argeo.slc.ria/computer.png");
			}else{
				prefArray.push(hostName);
				agentNode.setIcon("org.argeo.slc.ria/computer_bookmarked.png");
				var parent = agentNode.getParent();
				parent.remove(agentNode);
				parent.addAtBegin(agentNode);
			}
			this.setRiaPreferenceValue("flowSelector.preferred.hosts", prefArray.join(","));
		},
		
		/**
		 * Creates the main applet layout.
		 */
		_createLayout : function() {

			this.tree = new qx.ui.tree.Tree();
			this.tree.setDecorator(null);
			this.tree.setSelectionMode("multi");
			var dragData = {
				"file" : {
					"type" : ["items"],
					"action" : ["move"]
				},
				"folder" : {
					"type" : ["items"],
					"action" : ["move"]
				}
			};

			this.rootNode = new org.argeo.ria.components.DynamicTreeFolder(
					"Tests", this.self(arguments).agentLoader,
					"Loading Agents", dragData);
			this.tree.setRoot(this.rootNode);
			this.tree.setHideRoot(true);
			this.rootNode.setPersistentTreeID("org.argeo.slc.ria.FlowsSelector")
			this.rootNode.setOpen(true);
			this.tree.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["addtobatch",
							"reloadtree", "preferredHost"]));

			this.tree.addListener("changeSelection", function(e) {
				var viewSelection = this.getViewSelection();
				viewSelection.setViewId("form:tree");
				viewSelection.clear();
				var sel = this.tree.getSortedSelection();
				for (var i = 0; i < sel.length; i++) {
					viewSelection.addNode(sel[i]);
				}
			}, this);

			this.tree.addListener("dblclick", function(e){
				var sel = this.tree.getSortedSelection();
				if(sel && sel.length!=1)  return;
				var origin = sel[0];
				if(origin.classname == "qx.ui.tree.TreeFile"){
					this.setExecuteAfterAdd(true);
					this.getCommands()["addtobatch"].command.execute();
				}
			}, this);

			this.add(this.tree);
		},
		
		getAgentsMap : function(){
			return this.rootNode.getUserData("agentsMap");
		}		
	}
});