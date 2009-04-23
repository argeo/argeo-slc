/**
 * A "dynamic" implementation of the standard TreeFolder class. 
 *
 */
qx.Class.define("org.argeo.ria.components.DynamicTreeFolder", {
	extend : qx.ui.tree.TreeFolder,
	
	properties : {
		/**
		 * The current state of the folder, usually "empty" => "loading" => "loaded"
		 */
		"state" : {
			check : "String",
			init  : "empty",
			apply : "_applyState"
		},
		/**
		 * String to display as a child node during loading
		 */
		"loadingString" : {
			check : "String",
			init  : "Loading..."
		},
		/**
		 * Function that will load the children of this folder
		 */
		"loader" : {
			check : "Function",
			init : function(treeFolder){treeFolder.setLoaded();}
		},
		/**
		 * Optionnal data describing the "drag" behaviour of the created children.
		 * First level is "file" or "folder", and for each of them, supported keys are "type" and "action".  
		 */
		"dragData": {
			check : "Map",
			init : {}
		}
	},
	
	/**
	 * Creates a new instance of DynamicTreeFolder
	 * @param label {String} Label of the folder
	 * @param loader {Function} Function that will load the children 
	 * @param loadingString {String} String to display as a child node during loading
	 * @param dragData {Map} Optionnal data describing the "drag" behaviour of the created children.
	 */
	construct : function(label, loader, loadingString, dragData){
		this.base(arguments, label);
		if(loader) this.setLoader(loader);
		if(loadingString) this.setLoadingString(loadingString);
		if(dragData) this.setDragData(dragData);
		this.addListener("changeOpen", function(e){
			if(e.getData() && this.getState() == "loading"){
				this.load();
			}
		}, this);
		this.setState("loading");
	},
	
	members : {
		/**
		 * Add an item to the folder 
		 * @param varargs {Mixed} One or many children to add
		 */
		add : function(varargs){
			this.base(arguments, varargs);
			for (var i=0, l=arguments.length; i<l; i++)
			{				
				var treeItem = arguments[i];
				if(treeItem == this.loadingChild) continue;
				this.appendDragData(treeItem);
			}
		},
		
		/**
		 * If there is dragData set, init the drag behaviour of a child
		 * @param treeItem {qx.ui.tree.AbstractTreeItem} Newly created child
		 */
		appendDragData : function(treeItem){
			var dragData = this.getDragData();
			var nodeTypeDetected = false;
			if(qx.Class.isSubClassOf(qx.Class.getByName(treeItem.classname), qx.ui.tree.TreeFile) && dragData["file"]){
				nodeTypeDetected = "file";
			}
			if(qx.Class.isSubClassOf(qx.Class.getByName(treeItem.classname), qx.ui.tree.TreeFolder) && dragData["folder"]){
				nodeTypeDetected = "folder";
			}			
			if(!nodeTypeDetected) return;
			
			treeItem.setDraggable(true);
			treeItem.addListener("dragstart", function(e){						
				if(dragData[nodeTypeDetected]["type"]){
					for(var j=0;j<dragData[nodeTypeDetected]["type"].length;j++){
						e.addType(dragData[nodeTypeDetected]["type"][j]);
					}
				}
				if(dragData[nodeTypeDetected]["action"]){
					for(var j=0;j<dragData[nodeTypeDetected]["action"].length;j++){
						e.addAction(dragData[nodeTypeDetected]["action"][j]);
					}
				}
			});				
			
		},
		
		/**
		 * Set the state to "loaded"
		 */
		setLoaded : function(){
			this.setState("loaded");
		},
		/**
		 * Called when "state" is set to a new value
		 * @param state {String} the new state
		 */
		_applyState : function(state){
			if(state == "loaded"){
				if(this.loadingChild){
					this.remove(this.loadingChild);
					delete this.loadingChild;
				}
			}else if(state == "loading" && !this.loadingChild){
				this.addLoadingChild();
			}
		},
		/**
		 * Create a temporary child with the loadingString label and add it.
		 */
		addLoadingChild : function(){
			this.loadingChild = new qx.ui.tree.TreeFile(this.getLoadingString());
			this.add(this.loadingChild);
		},
		/**
		 * Call loader function.
		 */
		load : function(){			
			var loaderFunc = this.getLoader();
			loaderFunc(this);
		},
		/**
		 * Empty then call loader function.
		 */
		reload : function(){
			this.removeAll();
			this.setState("loading");
			this.load();
		}
	}
});