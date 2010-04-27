qx.Class.define("org.argeo.jcr.ria.model.AbstractItem", 
{
	type : "abstract",
	extend : qx.core.Object,
	
	properties : {
		path : {
			check : "String"
		},
		name : {
			check : "String"
		},
		newItem : {
			check : "Boolean",
			event : "changeNew"
		},
		modified : {
			check : "Boolean",
			event : "changeModified"
		},
		node : {
			check : "Boolean",
			init : true
		},
		parent : {
			check : "org.argeo.jcr.ria.model.AbstractItem", 
			nullable : true,
			init : null
		},
		root : {
			check : "org.argeo.jcr.ria.model.AbstractItem"			
		}
	},
	
	members : {
		itemIsRoot : function(){
			return (this.getRoot() == this);
		},
		fromDomElement : function(domElement){
			// TO BE IMPLEMENTED BY SUBCLASSES
		},
		toXmlString : function(){
			// TO BE IMPLEMENTED BY SUBCLASSES			
		},
		remove : function(){
			// TO BE IMPLEMENTED BY SUBCLASSES			
		}
	}

});