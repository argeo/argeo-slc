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
				}
				this.set({
					parameter : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isParameter")=="true"?true:false),
					frozen : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isFrozen")=="true"?true:false),
					hidden : (org.argeo.ria.util.Element.getSingleNodeText(child, "@isHidden")=="true"?true:false)
				});				
			}
		},
				
		/**
		 * Create an XML Representation of this value
		 * @return {String} The XML String
		 */
		toAttributeXml : function(){
			var valueTag = '';
			var specAttribute = '';
			if(this.getSpecType() == "primitive"){
				valueTag =  (this.getValue()?this.getValue():'');
				specAttribute = '<slc:primitive-spec-attribute isParameter="'+(this.getParameter()?"true":"false")+'" isFrozen="'+(this.getFrozen()?"true":"false")+'" isHidden="'+(this.getHidden()?"true":"false")+'" type="'+this.getSpecSubType()+'">'+valueTag+'</slc:primitive-spec-attribute>';
			}else if(this.getSpecType() == "ref"){
				valueTag = (this.getValue()?'<slc:label>'+this.getValue()+'</slc:label>':'');
				specAttribute = '<slc:ref-spec-attribute isParameter="'+(this.getParameter()?"true":"false")+'" isFrozen="'+(this.getFrozen()?"true":"false")+'" isHidden="'+(this.getHidden()?"true":"false")+'" targetClassName="'+this.getSpecSubType()+'">'+valueTag+'</slc:ref-spec-attribute>';
			}
			return '<slc:value key="'+this.getKey()+'">'+specAttribute+'</slc:value>';
		},
		
		toValueXml : function(){
			var valueTag = '';
			var specAttribute = '';
			if(this.getSpecType() == "primitive"){
				valueTag =  this.getValue();
				specAttribute = '<slc:primitive-value type="'+this.getSpecSubType()+'">'+valueTag+'</slc:primitive-value>';
			}else if(this.getSpecType() == "ref"){
				valueTag = '<slc:label>'+this.getValue()+'</slc:label>';
				specAttribute = '<slc:ref-value >'+valueTag+'</slc:ref-value>';
			}
			return '<slc:value key="'+this.getKey()+'">'+specAttribute+'</slc:value>';			
		}
	}	
	
});