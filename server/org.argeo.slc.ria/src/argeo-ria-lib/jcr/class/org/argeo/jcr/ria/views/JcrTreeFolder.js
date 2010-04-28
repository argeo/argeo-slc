qx.Class.define("org.argeo.jcr.ria.views.JcrTreeFolder", {
	extend : org.argeo.ria.components.DynamicTreeFolder,
	properties : {
		jcrNode : {
			
		}
	},
	construct : function(jcrNode){
		this.base(arguments, jcrNode.getName(), org.argeo.jcr.ria.views.JcrTreeFolder.loader);
		this.setJcrNode(jcrNode);
	},
	
	statics : {
		loader : function(treeFolder){
			if(treeFolder.getState() == "loaded") return;
			var jcrNode = treeFolder.getJcrNode();
			jcrNode.addListener("changeLoadState", function(event){
				if(event.getData() == "loaded"){
					org.argeo.jcr.ria.views.JcrTreeFolder.jcrNodeToTreeFolder(jcrNode, treeFolder);
				}
			});
			if(jcrNode.getLoadState()=="loaded"){
				org.argeo.jcr.ria.views.JcrTreeFolder.jcrNodeToTreeFolder(jcrNode, treeFolder);
			}else if(jcrNode.getLoadState() == "empty"){
				//treeFolder.removeAll();
				jcrNode.load();
			}
		},
		
		jcrNodeToTreeFolder : function(loadedNode, parentTreeFolder){
			var children = loadedNode.getChildren();
			for(var i=0;i<children.length;i++){
				var newFolder = new org.argeo.jcr.ria.views.JcrTreeFolder(children[i]);
				parentTreeFolder.add(newFolder);
			}
			parentTreeFolder.setState("loaded");
		}		
	},
	members : {
		openListener : function(e){
		}		
	}
});