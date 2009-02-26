/**
 * Wrapper for ExecutionSpec server object
 */
qx.Class.define("org.argeo.slc.ria.module.ExecutionSpec", {
	
	extend : qx.core.Object,
	
	properties : {
		/**
		 * Unique name of this spec 
		 */
		name : {
			check : "String",
			init : ""
		},
		/**
		 * Defined parameters 
		 */
		values : {
			check : "Map", 
			init : {}
		},
		/**
		 * Castor representation of this object
		 */
		xmlNode : {
			apply : "_applyXmlNode"			
		}
	},
	
	statics : {
		XPATH_NAME : "@name",
		XPATH_VALUES : "slc:values"
	},
	
	construct : function(){},
	
	members : {
		/**
		 * Init the object from an XML representation
		 * @param xmlNode {Node} Castor representation of this object
		 */		
		_applyXmlNode : function(xmlNode){
			// Parse now
			this.setName(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_NAME));
		}		
	}	
	
});