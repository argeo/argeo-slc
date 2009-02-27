/**
 * A "dynamic" implementation of the standard TreeFolder class. 
 *
 */
qx.Class.define("org.argeo.ria.components.DynamicTreeFolder", {
	extend : qx.ui.tree.TreeFolder,
	
	properties : {
		"state" : {
			check : "String",
			init  : "empty",
			apply : "_applyState"
		},
		"loadingString" : {
			check : "String",
			init  : "Loading..."
		},
		"loader" : {
			check : "Function",
			init : function(treeFolder){treeFolder.setLoaded();}
		},
		"dragData": {
			check : "Map",
			init : {}
		}
	},
	
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
		
		add : function(varargs){
			this.base(arguments, varargs);
			for (var i=0, l=arguments.length; i<l; i++)
			{				
				var treeItem = arguments[i];
				if(treeItem == this.loadingChild) continue;
				this.appendDragData(treeItem);
			}
		},
		
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
		
		setLoaded : function(){
			this.setState("loaded");
		},
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
		addLoadingChild : function(){
			this.loadingChild = new qx.ui.tree.TreeFile(this.getLoadingString());
			this.add(this.loadingChild);
		},
		load : function(){			
			var loaderFunc = this.getLoader();
			loaderFunc(this);
		},
		reload : function(){
			this.removeAll();
			this.setState("loading");
			this.load();
		}
	}
});