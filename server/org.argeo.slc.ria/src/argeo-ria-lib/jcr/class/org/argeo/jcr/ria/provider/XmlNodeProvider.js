qx.Class.define("org.argeo.jcr.ria.provider.XmlNodeProvider", {
	extend : qx.core.Object,
	implement : [org.argeo.jcr.ria.provider.INodeProvider],
	
	properties : {
		settings :{
			check : "Object"
		},
		xmlDoc : {
			
		},
		xmlDocLoaded : {
			check : "Boolean",
			init : false,
			event : "changeXmlDocLoaded"
		}
	},
	
	members : {
		_xmlDoc : null,
		initProvider : function(properties){
			this.setSettings(properties);			
		},
		/**
		 * 
		 * @param node org.argeo.jcr.ria.model.Node
		 * @param nodeCallback Function
		 * @param childCallback Function
		 */
		loadNode : function(node, nodeCallback, childCallback){
			if(node.getLoadState() == "loaded") return;
			
			if(this.getXmlDocLoaded()){
				// Parse document and load
				node.setName(this._xmlDoc.documentElement.nodeName);
				var properties = this.getSettings();				
				// STUB : prune sub children, load only level 1
				/*
				var children = this._xmlDoc.documentElement.childNodes;
				for(var i=0;i<children.length;i++){
					var subchildren = children[i].childNodes;
					for(var j=0;j<subchildren.length;j++){
						children[i].removeChild(subchildren[j]);
					}
				}
				*/
				// END STUB
				node.fromDomElement(this._xmlDoc.documentElement);
				if(properties.dynamic){
					this.setXmlDocLoaded(false);
				}
			}else{
				this.addListenerOnce("changeXmlDocLoaded", function(){
					this.loadNode(node, nodeCallback, childCallback);
				}, this);
				node.setLoadState("loading");
				this.loadXmlDoc(node);
			}
		},
				
		loadXmlDoc : function(node){
			var properties = this.getSettings();
			if(!properties.xmlSrc && !properties.xmlString) return;
			if(properties.xmlSrc){
				var request = new org.argeo.ria.remote.Request(properties.xmlSrc, 'GET', 'application/xml');
				if(properties.dynamic && properties.pathParameter){
					request.setParameter(properties.pathParameter, (node.getPath()|| "/"));
				}
				request.addListener("completed", function(response){
					this._xmlDoc = response.getContent();
					this.setXmlDocLoaded(true);
				}, this);
				request.send();
			}else{
				this._xmlDoc = qx.xml.Document.fromString(properties.xmlString);
				this.setXmlDocLoaded(true);
			}
		}
	}
});