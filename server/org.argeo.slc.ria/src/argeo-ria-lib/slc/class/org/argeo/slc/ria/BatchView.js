/**
 * Applet for the batch manager
 */
qx.Class.define("org.argeo.slc.ria.BatchView",
{
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 
  	include : [org.argeo.ria.session.MPrefHolder],
  	statics : {
		riaPreferences : {
			"slc.batch.delete.confirm" : {
				label : "Confirm on batch deletion",
				type  : "boolean",
				defaultValue  : true
			},
			"slc.batch.autoclear" : {
				label : "Autoclear batch on execution",
				type : "boolean",
				defaultValue : false
			}
		}
  	},
	properties : 
	{
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
				"submitform" : {
					label : "Execute Batch",
					icon : "org.argeo.slc.ria/media-playback-start.png",
					shortcut : null,
					enabled : false,
					menu : "Launcher",
					toolbar : "batch",
					callback : function(e) {
						var batchAgentId = this.getBatchAgentId();
						if (!batchAgentId) {
							return;							
						}
						var prefName = "slc.batch.autoclear";
						var prefValue = this.getRiaPreferenceValue(prefName);
						if(prefValue !== null){
							this.executeBatchOnAgent(batchAgentId, prefValue);
							return;
						}
						var modal = new org.argeo.ria.components.Modal("Clear?", null);
						modal.addYesNoReminder("Do you want to clear the batch automatically after execution?", prefName);
						modal.addListener("cancel", function(e){
							this.executeBatchOnAgent(batchAgentId, false);
						}, this);
						modal.addListener("ok", function(e){
							this.executeBatchOnAgent(batchAgentId, true);
						}, this);
						modal.attachAndShow();						
					},
					command : null
				},
				/*
				"toggleopenonadd" : {
					label : "Auto edit on Add",
					icon : "org.argeo.slc.ria/document-open.png",
					shortcut : null,
					enabled : true,
					toggle : true,
					toggleInitialState : true,
					menu : "Launcher",
					toolbar : "launcher",
					callback : function(event) {
						var state = event.getTarget().getUserData("slc.command.toggleState");
						this.setAutoOpen(state);
					},
					command : null
				},
				"editexecutionspecs" : {
					label : "Edit Execution Specs",
					icon : "org.argeo.slc.ria/document-open.png",
					shortcut : null,
					enabled : false,
					menu : "Launcher",
					toolbar : "batch",
					callback : function(e) {
						var sel = this.list.getSortedSelection();
						var spec = sel[0].getUserData("batchEntrySpec");
						if (spec.hasEditableValues()) {
							var specEditor = new org.argeo.slc.ria.execution.SpecEditor(spec);
							specEditor.attachAndShow();
						}
					},
					selectionChange : function(viewId, selection) {
						if (viewId != "batch:list")
							return;
						this.setEnabled(false);
						if ((selection && selection.length == 1)) {
							var selectedItemSpec = selection[0].getUserData("batchEntrySpec");
							if (selectedItemSpec.hasEditableValues()) {
								this.setEnabled(true);
							}
						}
					},
					command : null
				},
				*/
				"removefrombatch" : {
					label : "Remove from batch",
					icon : "org.argeo.slc.ria/edit-delete.png",
					shortcut : null,
					enabled : false,
					menu : "Launcher",
					toolbar : "batch",
					callback : function(e) {
						var sel = this.list.getSortedSelection();
						var confirmPref = this.getRiaPreferenceValue("slc.batch.delete.confirm");						
						var execution = function() {
							for (var i = 0; i < sel.length; i++) {
								this.list.remove(sel[i]);
							}
							if (!this.list.hasChildren()) {
								this.setBatchAgentId(null);
							}
						}
						if(confirmPref){
							var modal = new org.argeo.ria.components.Modal("Confirm", null);
							modal.addConfirm("Are you sure you want to remove<br> the selected test"
											+ (sel.length > 1 ? "s" : "")
											+ " from the Batch?");
							modal.addListener("ok", execution, this);
							modal.attachAndShow();
						}else{
							execution = qx.lang.Function.bind(execution, this);
							execution();
						}
					},
					selectionChange : function(viewId, selection) {
						if (viewId != "batch:list")
							return;
						this.setEnabled(false);
						if ((selection && selection.length > 0))
							this.setEnabled(true);
					},
					command : null
				},
				"clearbatch" : {
					label : "Clear batch",
					icon : "org.argeo.slc.ria/user-trash-full.png",
					shortcut : null,
					enabled : true,
					menu : "Launcher",
					toolbar : "batch",
					callback : function(e) {
						if(!this.list.hasChildren()) return;
						this.list.selectAll();
						this.getCommands()["removefrombatch"].command.execute();
					},
					selectionChange : function(viewId, selection) {
					},
					command : null
				}								
			}
		},
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
		 * A boolean registering whether the SpecEditor must autoOpen or not
		 * when a spec is added to the Batch.
		 */
		autoOpen : {
			init : true,
			check : "Boolean"
		},
		batchAgentId : {
			init : null,
			nullable : true,
			check : "String",
			event : "changeBatchAgentId"
		}	  	
	},
	  
	construct : function(){
		this.base(arguments);
		this.setLayout(new qx.ui.layout.Dock());	
	},
  
	members : {
		/**
		 * The implementation should contain the GUI initialisation.
		 * This is the role of the manager to actually add the graphical component to the pane, 
		 * so it's not necessary to do it here. 
		 * @param viewPane {org.argeo.ria.components.ViewPane} The pane manager
		 * @param data {Mixed} Any object or data passed by the initiator of the view
		 * @return {Boolean}
		 */
		init : function(viewPane, data){
			this.setView(viewPane);			
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));			
			this._emptyAgentString = "Empty Batch (Drop scripts here)";
			this._crtAgentString = "Batch Execution on Agent ";
			
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			this._createLayout();
			this.getView().setViewTitle(this._emptyAgentString);
			//this.getView().setViewTitle("");
		},
		
		/**
		 * Creates the main applet layout.
		 */
		_createLayout : function() {
			this.listPane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
			this.addListener("changeBatchAgentId", function(event) {
				var value = event.getData();
				if (value == null) {
					this.getView().setViewTitle(this._emptyAgentString);
				} else {
					var selectorView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("selector").getContent();
					if(selectorView){
						var agentsMap = selectorView.getAgentsMap();
						this.getView().setViewTitle(this._crtAgentString + "'" + agentsMap[value] + "'");
					}
				}
			}, this);
			
			var indicator = new qx.ui.core.Widget();
			indicator.setDecorator(new qx.ui.decoration.Single().set({
						top : [1, "solid", "#33508D"]
					}));
			indicator.setHeight(0);
			indicator.setOpacity(0.5);
			indicator.setZIndex(100);
			indicator.setLayoutProperties({
						left : -1000,
						top : -1000
					});
			org.argeo.ria.Application.INSTANCE.getRoot().add(indicator);

			this.list = new qx.ui.form.List();
			this.list.setDecorator(null);
			this.list.setSelectionMode("multi");
			this.list.setDroppable(true);
			this.list.setDraggable(true);
			this.list.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["removefrombatch"]));

			this.list.addListener("dragstart", function(e) {
						e.addType(["items"]);
						e.addAction(["move"]);
					}, this);
			this.list.addListener("dragend", function(e) {
						indicator.setDomPosition(-1000, -1000);
					});
			this.list.addListener("dragover", function(e) {
						var orig = e.getOriginalTarget();
						var origCoords = orig.getContainerLocation();
						indicator.setWidth(orig.getBounds().width);
						indicator.setDomPosition(origCoords.left,
								origCoords.bottom);
					});
			this.list.addListener("drag", function(e) {
						var orig = e.getOriginalTarget();
						var origCoords = orig.getContainerLocation();
						indicator.setWidth(orig.getBounds().width);
						indicator.setDomPosition(origCoords.left,
								origCoords.bottom);
					});

			this.list.addListener("drop", function(e) {
						var target = e.getRelatedTarget();
						var afterItem = e.getOriginalTarget();
						indicator.setDomPosition(-1000, -1000);
						if (afterItem.classname != "qx.ui.form.ListItem")
							afterItem = null;
						if (!target) {
							target = this.list.getSortedSelection()[0];
							this.addFlowToBatch(target, afterItem);
						} else {
							org.argeo.ria.event.CommandsManager.getInstance().executeCommand("addtobatch");
						}
					}, this);
			this.listPane.add(this.list, {
						edge : "center"
					});

			this.list.addListener("changeSelection", function(e) {
						var viewSelection = this.getViewSelection();
						viewSelection.setViewId("batch:list");
						viewSelection.clear();
						var listSel = this.list.getSortedSelection();
						for (var i = 0; i < listSel.length; i++) {
							viewSelection.addNode(listSel[i]);
						}
					}, this);

			this.dropDecorator = new qx.ui.decoration.Background();			
			this.dropDecorator.set({
				backgroundImage : "org.argeo.slc.ria/drophere.gif",
				backgroundRepeat : "no-repeat"
			});

					
			listChangeListener = function() {
				var command = org.argeo.ria.event.CommandsManager.getInstance()
						.getCommandById("submitform");
				command.setEnabled(this.list.hasChildren());
				var command2 = org.argeo.ria.event.CommandsManager.getInstance()
						.getCommandById("clearbatch");
				command2.setEnabled(this.list.hasChildren());
				this.list.setDecorator((this.list.hasChildren()?null:this.dropDecorator));
			};
			this.list.addListener("addItem", listChangeListener, this);
			this.list.addListener("removeItem", listChangeListener, this);
				
			this.list.setDecorator(this.dropDecorator);
						
			this.add(this.listPane);
		},		
		
		
		/**
		 * Adds a given ExecutionFlow to the batch
		 * 
		 * @param target
		 *            {mixed} The dropped target, can be a TreeFile (add) or a
		 *            ListItem (reorder).
		 * @param after
		 *            {qx.ui.form.ListItem} Optional list item : if set, the
		 *            flow will be added as a new list item positionned after
		 *            this one.
		 * @param skipAutoOpen
		 *            {boolean} Whether the formular should open or not.
		 */
		addFlowToBatch : function(target, after, skipAutoOpen) {
			if (target && target.classname == "qx.ui.form.ListItem") {
				if (!after)
					return;
				if (after == "first")
					this.list.addAt(target, 0);
				else
					this.list.addAfter(target, after);
				return;
			}

			// Folder case
			if (qx.Class.isSubClassOf(qx.Class.getByName(target.classname),
					qx.ui.tree.TreeFolder)) {
				var allChildren = target.getItems(true);
				for (var i = 0; i < allChildren.length; i++) {
					if (allChildren[i].getUserData("executionFlow")) {
						try{
							this.addFlowToBatch(allChildren[i], null, true);
						}catch(e){
							return;
						}
					}
				}
				return;
			}

			// Check agent Uuid against current batch agent Id.
			var agentUuid = target.getUserData("agentUuid");
			if (!this.getBatchAgentId()) {
				this.setBatchAgentId(agentUuid);
			} else if (this.getBatchAgentId() != agentUuid) {
				this.error("Batch can contain tests only of the same agent!");				
				throw new Error("Batch can contain tests only of the same agent!");
			}

			var executionModule = target.getUserData("executionModule");
			var executionFlow = target.getUserData("executionFlow");
			var batchEntry = new org.argeo.slc.ria.execution.BatchEntrySpec(
					executionModule, executionFlow);

			this.appendBatchEntrySpec(batchEntry, target.getIcon(), after);		
		},
		
		appendBatchEntrySpec: function(batchEntrySpec, icon, after){
						
			var item = new qx.ui.form.ListItem(batchEntrySpec.getLabel(), icon || "org.argeo.slc.ria/system.png");
			item.setUserData("batchEntrySpec", batchEntrySpec);
			item.setPaddingTop(1);
			item.setPaddingBottom(2);
			if (after) {
				if (after == "first")
					this.list.addAt(item, 0);
				else
					this.list.addAfter(item, after);
			} else {
				this.list.add(item);
			}
			this.list.select(item);
			
		},

		/**
		 * Called at execution
		 * 
		 * @param agentUuid
		 *            {String} The id of the target agent
		 */
		executeBatchOnAgent : function(agentUuid, clearBatch) {
			var selection = this.list.getChildren();
			if (!selection.length)
				return;
			// Get Host
			var agentsMap = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("selector").getContent().getAgentsMap();
			var host = agentsMap[agentUuid];
			var slcExecMessage = new org.argeo.slc.ria.execution.Message();
			slcExecMessage.setHost(host);
			for (var i = 0; i < selection.length; i++) {
				var batchEntrySpec = selection[i].getUserData("batchEntrySpec");
				slcExecMessage.addBatchEntrySpec(batchEntrySpec);
			}
			try{
				var xmlMessage = slcExecMessage.toXml();
				if(!window.xmlExecStub){
					window.xmlExecStub = {};
				}
				window.xmlExecStub[slcExecMessage.getUuid()] = qx.xml.Document.fromString(xmlMessage);
				var req = org.argeo.slc.ria.SlcApi.getNewSlcExecutionService(
						agentUuid, xmlMessage);
				req.send();
				// Force logs refresh right now!
				qx.event.Timer.once(function() {
							var command = org.argeo.ria.event.CommandsManager
									.getInstance().getCommandById("reloadlogs");
							if (command) {
								command.execute();
							}
						}, this, 2000);
				if(clearBatch){
					req.addListener("completed", function(e){
						this.list.removeAll();
						this.setBatchAgentId(null);
					}, this);
				}						
			}catch(e){
				this.error(e);				
			}
		},
		
		clearBatchForAgentId : function(agentId){
			if(this.getBatchAgentId() == agentId){
				this.list.removeAll();				
				this.setBatchAgentId(null);
			}
		},
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){return false;},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){return true;}
	}
  

});