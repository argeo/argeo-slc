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
		description : {
			check : "String",
			init : ""
		},
		/**
		 * An optional path describing this flow
		 */
		path : {
			check : "String",
			nullable : true
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
		 * The values to init the ExecutionSpec
		 */
		values : {
			check : "Node",
			nullable : true
		},
		/**
		 * Castor representation of the object 
		 */
		xmlNode : {
			apply : "_applyXmlNode"
		}
	},
	
	statics : {
		/**
		 * Xpath to the name 
		 */
		XPATH_NAME : "@name",
		/**
		 * Path to the description
		 */
		XPATH_DESCRIPTION : "slc:description",
		/**
		 * XPath to the ExecutionSpec name
		 */
		XPATH_EXEC_SPEC_NAME : "@executionSpec",
		/**
		 * XPath to the values
		 */
		XPATH_VALUES : "slc:values",
		/**
		 * An optional hierarchical path
		 */
		XPATH_PATH : "@path"
	},
	
	construct : function(){
		this.base(arguments);
	},
	
	members : {		
		/**
		 * Init the object from an XML representation
		 * @param xmlNode {Node} Castor representation of this object
		 */
		_applyXmlNode : function(xmlNode){
			this.set({
				name : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_NAME),
				path : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_PATH),
				description : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_DESCRIPTION) || "",
				executionSpecName : org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_EXEC_SPEC_NAME)
			});
			var values = org.argeo.ria.util.Element.selectNodes(xmlNode, this.self(arguments).XPATH_VALUES);
			if(values[0]){
				this.setValues(values[0]);
			}
		},
		/**
		 * Get a given value inside the values map
		 * @param key {String} The key of the value 
		 * @param specType {String} Expected type (currently "primitive" and "ref" are supported)
		 * @param specSubType {String} Expected subtype (depends on the type)
		 * @return {String} Value if it is set.
		 */
		getValue: function(key, specType, specSubType){
			var xpath;
			if(specType == "primitive"){
				xpath = 'slc:value[@key="'+key+'"]/slc:primitive-value[@type="'+specSubType+'"]';
			}else if(specType == "ref"){
				xpath = 'slc:value[@key="'+key+'"]/slc:ref-value/@ref';
			}
			return org.argeo.ria.util.Element.getSingleNodeText(this.getValues(), xpath);
		}
	}	
	
});