/**
 * Wrapper for SlcValue object
 */
qx.Class.define("org.argeo.slc.ria.execution.Value", {
	
	extend : qx.core.Object,
	
	properties : {
		/**
		 * Name of this Execution Flow 
		 */
		key : {
			check : "String",
			init : ""
		},
		/**
		 * The type of this value, for the moment "primitive" and "ref" are supported 
		 */
		specType : {
			check : "String",
			init : ""			
		},
		/**
		 * Subtype, depending on the "type". 
		 */
		specSubType : {
			check : "String"
		},
		/**
		 * Whether it is a parameter or not 
		 */
		parameter : {
			check : "Boolean"
		},
		/**
		 * Whether it is frozen on the server, i.e. disabled in the form 
		 */
		frozen : {
			check : "Boolean"
		},
		/**
		 * Should not be editable nor seeable, thus hidden 
		 */
		hidden : {
			check : "Boolean"
		},
		/**
		 * The real value 
		 */
		value : {
			nullable : true
		},
		/**
		 * Castor representation of the object 
		 */
		xmlSpecNode : {
			apply : "_applyXmlSpecNode"
		},
		refList : {
			check : "Array"
		}
	},
	
	statics : {
		XPATH_KEY : "@key"
	},
	
	construct : function(){
		this.base(arguments);		
	},
	
	members : {		
		/**
		 * Init the object from an XML representation
		 * @param xmlNode {Node} Castor representation of this object
		 */
		_applyXmlSpecNode : function(xmlNode){
			this.setKey(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, "@key"));
			var childs = xmlNode.childNodes;
			for(var i=0;i<childs.length;i++){
				var child = childs[i];
				if(child.nodeType != 1) continue;
				if(child.nodeName == "slc:primitive-spec-attribute"){
					this.setSpecType("primitive");
					this.setSpecSubType(org.argeo.ria.util.Element.getSingleNodeText(child, "@type")||"");
					if(org.argeo.ria.util.Element.getSingleNodeText(child, ".")){
						this.setValue(org.argeo.ria.util.Element.getSingleNodeText(child, "."));
					}
				}else if(child.nodeName == "slc:ref-spec-attribute"){
					this.setSpecType("ref");
					this.setSpecSubType(org.argeo.ria.util.Element.getSingleNodeText(child, "@targetClassName")||"");
					var choices = org.argeo.ria.util.Element.selectNodes(child, "slc:choices/slc:ref-value-choice");
					var refList = []; 
					for(var k=0;k<choices.length;k++){
						var choice = choices[k];
						var name = org.argeo.ria.util.Element.getSingleNodeText(choice, "@name");
						var description = org.argeo.ria.util.Element.getSingleNodeText(choice, "slc:description");
						refList.push([name, (description||"")]);
					}
					this.setRefList(refList);
				}
				this.set({
					parameter : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isParameter")=="true"?true:false),
					frozen : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isFrozen")=="true"?true:false),
					hidden : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isHidden")=="true"?true:false)
				});				
			}
		},
						
		toValueXml : function(){
			var valueTag = '';
			var specAttribute = '';
			if(this.getSpecType() == "primitive"){
				valueTag =  this.getValue();
				specAttribute = '<slc:primitive-value type="'+this.getSpecSubType()+'">'+valueTag+'</slc:primitive-value>';
			}else if(this.getSpecType() == "ref"){
				specAttribute = '<slc:ref-value ref="'+this.getValue()+'" />';
			}
			return '<slc:value key="'+this.getKey()+'">'+specAttribute+'</slc:value>';			
		}
	}	
	
});