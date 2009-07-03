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
		hasEditableValues : function(){
			var values = this.getValues();
			if(!values) return false;
			var editables = 0;
			for(var key in values){
				var valueObj = values[key];
				if(!valueObj.getHidden() && !valueObj.getFrozen()){
					editables ++;
				}
			}
			return (editables > 0);
		},
		
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
			return org.argeo.ria.util.Element.getXMLString(this.getXmlNode());
		}
	}	
	
});