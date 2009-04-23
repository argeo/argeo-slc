/**
 * Wrapper for ExecutionSpec server object
 */
qx.Class.define("org.argeo.slc.ria.execution.Spec", {
	
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
			check : "Map"
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
		XPATH_VALUES : "slc:values/slc:value"
	},
	
	construct : function(){
		this.base(arguments);
		this.setValues({});
	},
	
	members : {
		/**
		 * Init the object from an XML representation
		 * @param xmlNode {Node} Castor representation of this object
		 */		
		_applyXmlNode : function(xmlNode){
			// Parse now
			this.setName(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_NAME));
			var values = org.argeo.ria.util.Element.selectNodes(xmlNode, this.self(arguments).XPATH_VALUES);
			var parsedValues = {};
			for(var i=0;i<values.length;i++){
				//var valueNode = values[i];
				var value = new org.argeo.slc.ria.execution.Value();
				value.setXmlSpecNode(values[i]);
				parsedValues[value.getKey()] = value;
			}
			this.setValues(parsedValues);
		},
		/**
		 * XML Representation of this object.
		 * @return {String} An XML String
		 */
		toXml : function(){
			var valuesXml = '';
			var values = this.getValues();
			for(var key in values){
				valuesXml += values[key].toAttributeXml();
			}
			return '<slc:default-execution-spec name="'+this.getName()+'"><slc:values>'+valuesXml+'</slc:values></slc:default-execution-spec>';
		}
	}	
	
});