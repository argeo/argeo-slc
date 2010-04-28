qx.Class.define("org.argeo.jcr.ria.views.TreeView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 

	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
				"zoom_in" : {
					label : "Zoom To Node",
					icon : "org.argeo.slc.ria/media-playback-start.png",
					shortcut : null,
					enabled : true,
					menu : "Context",
					toolbar : "context",
					callback : function(e) {
						var selection = this.tree.getSelection();
						if(!selection.length) return;
						var path = selection[0].getJcrNode().getPath();
						this.getDataModel().requireContextChange(path);
					},
					selectionChange : function(viewId, selection){
						if(viewId != "treeview") return;
						if(!selection || !selection.length) return;
						var treeNode = selection[0];
						if(treeNode.getParent()!=null){
							this.setEnabled(true);
						}else{
							this.setEnabled(false);
						}
					}
				},
				"zoom_out" : {
					label : "Zoom Out",
					icon : "org.argeo.slc.ria/media-playback-start.png",
					shortcut : null,
					enabled : true,
					menu : "Context",
					toolbar : "context",
					submenu : [],
					callback : function(e) {
					},
					submenuCallback : function(commandId){
						this.getDataModel().requireContextChange(commandId);
					},
					selectionChange : function(viewId, selection){
						if(viewId != "treeview") return;
						if(!selection || !selection.length) return;
						var treeNode = selection[0];
						if(treeNode.getParent()!=null || treeNode.getJcrNode().itemIsRoot()){
							this.setEnabled(false);
							return;
						}
						this.setEnabled(true);
						var nodePath = treeNode.getJcrNode().getPath();
						var parts = nodePath.split("\/");
						var pathes = [];
						parts.pop();
						if(parts.length > 1){
							for(var i=0;i<parts.length;i++){
								var newPath = parts.join("/");
								pathes.push({label:newPath,icon:'', commandId:newPath});
								parts.pop();
							}
						}
						this.setMenu(pathes);
					}
				}				
			}
		},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
	  	instanceId : {
	  		init:"treeView",
	  		event : "changeInstanceId"
	  	},
	  	instanceLabel : {
	  		init:"Tree View",
	  		event : "changeInstanceLabel"
	  	},
	  	dataModel : {
	  		
	  	}
	},
	
	construct : function(){
		this.base(arguments);
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
		init : function(viewPane, dataModel){
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  			
			this.setLayout(new qx.ui.layout.VBox());
			this.setDataModel(dataModel);
			
			this.tree = new qx.ui.tree.Tree();			
			this.add(this.tree, {flex:1});
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			var dataModel = this.getDataModel();
			dataModel.addListener("changeContextNode", function(event){
				var contextNode = event.getData();
				var loader = function(){};
				var newRoot = new org.argeo.jcr.ria.views.JcrTreeFolder(contextNode);
				this.tree.setRoot(newRoot);
				this.tree.setSelection([newRoot]);
			}, this);
			this.tree.addListener("changeSelection", function(e){
				var sel = this.tree.getSelection();
				var selection = [];
				var viewSelection = this.getViewSelection();
				viewSelection.clear();				
				for(var i=0;i<sel.length;i++){
					selection.push(sel[i].getJcrNode());
					viewSelection.addNode(sel[i]);
				}
				this.getDataModel().setSelection(selection);
			}, this);
			this.tree.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["zoom_in", "zoom_out"]));				
		},
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){
			return false;
		},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){
			
		}		
		
	}
});