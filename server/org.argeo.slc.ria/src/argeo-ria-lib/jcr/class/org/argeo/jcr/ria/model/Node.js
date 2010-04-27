qx.Class.define("org.argeo.jcr.ria.model.Node", {
	extend : org.argeo.jcr.ria.model.AbstractItem,
	
	events : {
		"clear" : "qx.event.type.Event",
		"childNodeAdded" : "qx.event.type.Data",
		"childNodeRemoved" : "qx.event.type.Data",
		"childrenChanged" : "qx.event.type.Data",
		
		"propertyAdded" : "qx.event.type.Data",
		"propertyRemoved" : "qx.event.type.Data",
		"propertiesChanged" : "qx.event.type.Data"
	},
	properties : {
		loadState : {
			check : "String",
			init : "empty", // Can be "empty" => "loading" => "loaded"
			event : "changeLoadState"
		},
		nodeProvider : {
			check : "org.argeo.jcr.ria.provider.INodeProvider"			
		}
	},
	
	construct : function(nodeName, isRoot){
		this.base(arguments);
		this._children = {};
		this._properties = {};	
		this.setName(nodeName);
		if(isRoot){
			this.setPath("");
			this.setRoot(this);
		}
	},

	members : {
		_children : null,
		_properties : null,
		
		load : function(){
			this.getNodeProvider().loadNode(this);
		},
		
		remove : function(){
			if(this.itemIsRoot()) return;
			this.getParent().removeChild(this.getName());
		},
		
		fromXmlString : function(xmlString){
			var domDocument = qx.xml.Document.fromString(xmlString);
			var root = domDocument.documentElement;
			this.fromDomElement(root);
		},
		
		fromDomElement : function(domElement){
			if(domElement.nodeType != 1) return;
			for(var i=0;i<domElement.attributes.length;i++){
				var att = domElement.attributes[i];
				var property = new org.argeo.jcr.ria.model.Property(att.nodeName);
				this.addProperty(property);
				property.fromDomElement(att);
			}
			for(var i=0;i<domElement.childNodes.length;i++){
				var child = domElement.childNodes[i];
				if(child.nodeType != 1) continue;
				var jcrChild = new org.argeo.jcr.ria.model.Node(child.nodeName);
				this.addChild(jcrChild);
				jcrChild.fromDomElement(child);
			}			
		},
		
		toXmlString : function(recurse, childrenWriter){
			var string = "<"+this.getName();
			var props = this.getProperties();
			for(var i=0;i<props.length;i++){
				string += " " + props[i].toXmlString();
			}
			string += ">";
			if(recurse){
				var childs = this.getChildren();
				var childrenString = "";
				for(var j=0;j<childs.length;j++){
					childrenString += childs[j].toXmlString(recurse, childrenWriter);
				}
				if(childrenWriter){
					string += childrenWriter(childrenString);
				}else{
					string += childrenString;
				}
			}
			string += "</"+this.getName()+">";
			return string;
		},		
		
		getChild : function(childName){
			return this._children[childName];
		},
		getProperty : function(propertyName){
			return this._properties[propertyName];
		},
		addChild : function(childNode){
			this._children[childNode.getName()] = childNode;
			
			childNode.setParent(this);
			childNode.setRoot(this.getRoot());
			childNode.setPath(this.getPath() + "/" + childNode.getName());
			
			this.fireDataEvent("childNodeAdded", childNode);
			this.fireDataEvent("childNodeChanged");
		},
		removeChild : function(childName){
			delete(this._children[childName]);
			this.fireDataEvent("childNodeRemoved", childName);
			this.fireDataEvent("childNodeChanged");
		},
		addProperty : function(property){
			this._properties[property.getName()] = property;
			
			property.setParent(this);
			property.setRoot(this.getRoot());
			property.setPath(this.getPath() + "/" + property.getName());
			
			this.fireDataEvent("propertyAdded", property);
			this.fireDataEvent("propertiesChanged");
		},
		removeProperty : function(propertyName){
			delete(this._properties[propertyName]);
			this.fireDataEvent("propertyRemoved", propertyName);
			this.fireDataEvent("propertiesChanged");
		},
		getChildren : function(){
			return qx.lang.Object.getValues(this._children);
		},
		getProperties : function(){
			return qx.lang.Object.getValues(this._properties);
		},
		getChildrenCount : function(){
			return qx.lang.Object.getLength(this._children);
		},
		getPropertiesCount : function(){
			return qx.lang.Object.getLength(this._properties);
		}
	}
});