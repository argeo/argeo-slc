qx.Class.define("org.argeo.jcr.ria.model.DataModel", {
	extend : qx.core.Object,
	
	events : {
		"changeContextNode" : "qx.event.type.Data"
	},
	
	properties : {
		rootNode : {
			check : "org.argeo.jcr.ria.model.Node",
			event : "changeRootNode"			
		},
		contextNode : {
			check : "org.argeo.jcr.ria.model.Node"
		},
		selection : {
			check : "Array",
			event : "changeSelection",
			init : []
		}
	},
	
	construct : function(rootNode){
		this.base(arguments);
		this.setRootNode(rootNode);
	},
	
	members : {
		requireContextChange : function(path){
			var targetNode;
			var rootNode = this.getRootNode();
			this.setSelection([]);
			if(!path){
				targetNode = rootNode;
			}else{
				// Create a fake node
				if(path[path.length-1] == '/'){
					path = path.substring(0, path.length-1);
				}
				var base = path.substring(path.lastIndexOf("/")+1);
				targetNode = new org.argeo.jcr.ria.model.Node(base, rootNode.getNodeProvider());
				rootNode.addChild(targetNode);
				targetNode.setPath(path);				
			}
			var observer = function(event){
				var state = event.getData();
				if(state == "loading"){
					targetNode.loadingNode = new org.argeo.jcr.ria.model.Node("loading"); 
					targetNode.addChild(targetNode.loadingNode);
				}else if(state == "loaded"){
					if(targetNode.loadingNode) {
						targetNode.loadingNode.remove();
						targetNode.loadingNode = null;
					}
					targetNode.removeListener("changeLoadState", observer, this);
				}				
				this.setContextNode(targetNode);
				this.fireDataEvent("changeContextNode", targetNode);
			};
			targetNode.addListener("changeLoadState", observer, this);
			targetNode.load();
		}
	}
	
});