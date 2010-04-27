qx.Class.define("org.argeo.jcr.ria.model.DataModel", {
	extend : qx.core.Object,
	
	properties : {
		rootNode : {
			check : "org.argeo.jcr.ria.model.Node",
			event : "changeRootNode"			
		},
		contextNode : {
			check : "org.argeo.jcr.ria.model.Node",
			event : "changeContextNode"
		},
		selection : {
			check : "org.argeo.jcr.ria.model.Node[]",
			event : "changeSelection"
		}
	},
	
	construct : function(rootNode){
		this.base(arguments);
		this.setRootNode(rootNode);
	}
	
});