/**
 * Wrapper for ExecutionFlow server object
 */
qx.Class.define("org.argeo.slc.ria.execution.Flow", {
	
	extend : qx.core.Object,
	
	properties : {
		/**
		 * Name of this Execution Flow 
		 */
		name : {
			check : "String",
			init : ""
		},
		/**
		 * Name of the associated spec, to be found in the module 
		 */
		executionSpecName : {
			check : "String"
		},
		/**
		 * Reference the actual ExecutionSpec object
		 */
		executionSpec : {
			check : "org.argeo.slc.ria.execution.Spec"
		},
		/**
		 * Castor representation of the object 
		 */
		xmlNode : {
			apply : "_applyXmlNode"
		}
	},
	
	statics : {
		XPATH_NAME : "@name",
		XPATH_EXEC_SPEC_NAME : "@executionSpec",
		XPATH_VALUES : "slc:values"
	},
	
	construct : function(){},
	
	members : {		
		/**
		 * Init the object from an XML representation
		 * @param xmlNode {Node} Castor representation of this object
		 */
		_applyXmlNode : function(xmlNode){
			this.set({
				name : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_NAME),
				executionSpecName : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_EXEC_SPEC_NAME)
			});
		}		
	}	
	
});