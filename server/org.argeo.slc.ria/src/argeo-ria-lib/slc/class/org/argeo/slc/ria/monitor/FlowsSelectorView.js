/**
 * The selector view
 * 
 */
qx.Class.define("org.argeo.slc.ria.monitor.FlowsSelectorView", {
	extend : org.argeo.slc.ria.FlowsSelectorView,
	implement : [org.argeo.ria.components.IView],

	construct : function() {
		this.base(arguments);
	},

	properties : {
		/**
		 * Commands definition, see
		 * {@link org.argeo.ria.event.CommandsManager#definitions}
		 */
		commands : {
			refine : true,
			init : {
				"reloadtree" : {
					label : "Reload",
					icon : "org.argeo.slc.ria/view-refresh.png",
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
		 *            {org.argeo.ria.components.PersistentTreeFolder} The root Tree Folder.
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
					var uuid = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "@uuid");
					var host = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], "slc:host");
					if(agents[uuid]){
						newAgents[uuid] = host;
						continue;
					}
					agents[uuid] = host;
					if(newAgents) newAgents[uuid] = host;
					var agentFolder = new org.argeo.ria.components.DynamicTreeFolder(
							host + ' (' + uuid + ')', modulesLoader,
							"Loading Modules...", folder.getDragData());
					agentFolder.setUserData("agentUuid", uuid);
					agentFolder.setIcon("org.argeo.slc.ria/mime-xsl-22.png");
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
		 *            {org.argeo.ria.components.PersistentTreeFolder} The root folder
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
						versionFolder.setUserData("moduleData", {
									name : key,
									version : mods[key][i]
								});
						versionFolder.setUserData("agentUuid", agentId);
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
		}		
	},

	members : {
		
		/**
		 * 
		 */
		load : function() {
			this._createLayout();
			this.UIBus.addListener("agentRegistered", this._addAgentHandler, this);
			this.UIBus.addListener("agentUnregistered", this._removeAgentHandler,	this);
		},		

		/**
		 * Creates the main applet layout.
		 */
		_createLayout : function() {

			this.tree = new qx.ui.tree.Tree();
			this.tree.setDecorator(null);
			this.tree.setSelectionMode("multi");

			this.rootNode = new org.argeo.ria.components.DynamicTreeFolder(
					"Tests", this.self(arguments).agentLoader,
					"Loading Agents");
			this.tree.setRoot(this.rootNode);
			this.rootNode.setPersistentTreeID("org.argeo.slc.ria.monitor.FlowsSelector")
			this.rootNode.setOpen(true);
			this.tree.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["reloadtree"]));

			this.tree.addListener("changeSelection", function(e) {
				var viewSelection = this.getViewSelection();
				viewSelection.setViewId("form:tree");
				viewSelection.clear();
				var sel = this.tree.getSortedSelection();
				for (var i = 0; i < sel.length; i++) {
					viewSelection.addNode(sel[i]);
				}
				if(sel && sel[0]){
					var propViewPane = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("properties");
					propViewPane.getContent().updateData(sel[0]);
				}
			}, this);


			this.add(this.tree);
		}		
	}
});