qx.Class.define("org.argeo.jcr.ria.model.Property", {
	extend : org.argeo.jcr.ria.model.AbstractItem,
	
	properties : {
		node : {
			refine : true,
			init : false
		},
		value : {
			event : "changeValue"
		},
		local : {
			check : "Boolean",
			init : false
		}
	},
	
	construct : function(propertyName){
		this.base(arguments);
		this.setName(propertyName);
	},
	
	members : {
		remove : function(){
			this.getParent().removeProperty(this.getName());
		},
		
		fromDomElement : function(domElement){
			if(domElement.nodeType != 2) return;
			this.setValue(domElement.nodeValue);
		},
		toXmlString : function(){
			if(this.isLocal()) return "";
			return this.getName()+"="+'"'+this.getValue()+'"';
		}
		
	}
});