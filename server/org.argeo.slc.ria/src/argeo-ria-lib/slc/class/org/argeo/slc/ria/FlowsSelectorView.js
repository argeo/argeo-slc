/**
 * The selector view
 * 
 */
qx.Class.define("org.argeo.slc.ria.FlowsSelectorView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView],

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
		/**
		 * Commands definition, see
		 * {@link org.argeo.ria.event.CommandsManager#definitions}
		 */
		commands : {
			init : {
				"addtobatch" : {
					label : "Add to batch",
					icon : "resource/slc/list-add.png",
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
							case "qx.ui.tree.TreeFolder" :
								if (item.getTree().getRoot() == item)
									break;
								this.setEnabled(true);
								break;
							case "org.argeo.ria.components.DynamicTreeFolder" :
								if (item.getTree().getRoot() == item)
									break;	
								if (item.getState() == "loaded")
									this.setEnabled(true);
								break;
						}
					},
					command : null
				},
				"reloadtree" : {
					label : "Reload",
					icon : "resource/slc/view-refresh.png",
					shortcut : "Control+m",
					enabled : false,
					menu : "Launcher",
					toolbar : "launcher",
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
		/**
		 * Static loader for the "agent" level (first level)
		 * 
		 * @param folder
		 *            {qx.ui.tree.TreeFolder} The root Tree Folder.
		 */
		agentLoader : function(folder) {

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
						"//slc:slc-agent-descriptor");
				var modulesLoader = org.argeo.slc.ria.FlowsSelectorView.modulesLoader;
				
				for (var i = 0; i < nodes.length; i++) {
					var uuid = org.argeo.ria.util.Element.getSingleNodeText(
							nodes[i], "@uuid");
					if(agents[uuid]){
						newAgents[uuid] = host;
						continue;
					}
					var host = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "slc:host");
					agents[uuid] = host;
					if(newAgents) newAgents[uuid] = host;
					var agentFolder = new org.argeo.ria.components.DynamicTreeFolder(
							host + ' (' + uuid + ')', modulesLoader,
							"Loading Modules...", folder.getDragData());
					agentFolder.setUserData("agentUuid", uuid);
					agentFolder.setIcon("resource/slc/mime-xsl-22.png");
					folder.add(agentFolder);
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
		 *            {qx.ui.tree.TreeFolder} The root folder
		 */
		modulesLoader : function(folder) {
			var agentId = folder.getUserData("agentUuid");
			var req = org.argeo.slc.ria.SlcApi.getListModulesService(agentId);
			req.addListener("completed", function(response) {
				var descriptors = org.argeo.ria.util.Element.selectNodes(
						response.getContent(),
						"slc:object-list/slc:execution-module-descriptor");
				var mods = {};
				for (var i = 0; i < descriptors.length; i++) {
					var name = org.argeo.ria.util.Element.getSingleNodeText(
							descriptors[i], "slc:name");
					var version = org.argeo.ria.util.Element.getSingleNodeText(
							descriptors[i], "slc:version");
					if (!mods[name])
						mods[name] = [];
					mods[name].push(version);
				}
				var flowLoader = org.argeo.slc.ria.FlowsSelectorView.flowLoader;
				for (var key in mods) {
					for (var i = 0; i < mods[key].length; i++) {
						var versionFolder = new org.argeo.ria.components.DynamicTreeFolder(
								key + ' (' + mods[key][i] + ')', flowLoader,
								"Loading Flows", folder.getDragData());
						folder.add(versionFolder);
						versionFolder.setUserData("moduleData", {
									name : key,
									version : mods[key][i]
								});
						versionFolder.setUserData("agentUuid", agentId);
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
		 *            {qx.ui.tree.TreeFolder} A Tree folder containing in the
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
					var file = new qx.ui.tree.TreeFile(key);
					var path = execFlows[key].getPath();
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
					var virtualFolder = new qx.ui.tree.TreeFolder(parts[i]);
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
			this.rootNode.setOpen(true);
			this.tree.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["addtobatch",
							"reloadtree"]));

			this.tree.addListener("changeSelection", function(e) {
				var viewSelection = this.getViewSelection();
				viewSelection.setViewId("form:tree");
				viewSelection.clear();
				var sel = this.tree.getSortedSelection();
				for (var i = 0; i < sel.length; i++) {
					viewSelection.addNode(sel[i]);
				}
			}, this);


			this.add(this.tree);
		},
		
		getAgentsMap : function(){
			return this.rootNode.getUserData("agentsMap");
		}		
	}
});