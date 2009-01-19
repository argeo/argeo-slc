/**
 * A generic JMS slcExecution message encapsulator.
 */
qx.Class.define("org.argeo.slc.ria.SlcExecutionMessage", {
	extend : qx.core.Object,
	construct : function(uuid){
		this.base(arguments);
		this.setUuid(uuid);
	},
	properties : {
		/**
		 * The unique id identifying the message
		 */
		uuid : {
			check : "String"
		},
		/**
		 * Execution status
		 */
		status : {
			check : "String",
			init : "STARTED"
		},
		/**
		 * Execution type
		 */
		type : {
			check : "String",
			init : "slcAnt"
		},
		/**
		 * Execution Host
		 */
		host : {
			check : "String",
			init : "localhost"
		},
		/**
		 * Execution User
		 */
		user : {
			check : "String",
			init : "user"
		},
		date : {
			check : "String", 
			init : new Date().toString()
		},
		/**
		 * Additionnal attributes as map of key/values
		 */
		attributes : {
			check : "Map",
			init : {}
		}
	},
	members : {
		
		addAttribute: function(attName, attValue){
			var attr = this.getAttributes();
			attr[attName] = attValue;
			this.setAttributes(attr);
		},
		/**
		 * Build the xml formatted message body to send
		 * 
		 * @return {String} The message content as Xml
		 */
		toXml : function (){
			var builder = new qx.util.StringBuilder();
			builder.add('<slc:slc-execution  xmlns:slc="http://argeo.org/projects/slc/schemas" uuid="'+this.getUuid()+'">');
			builder.add('<slc:status>'+this.getStatus()+'</slc:status>');
			builder.add('<slc:type>'+this.getType()+'</slc:type>');
			builder.add('<slc:host>'+this.getHost()+'</slc:host>');
			builder.add('<slc:user>'+this.getUser()+'</slc:user>');
			var attr = this.getAttributes();
			if(qx.lang.Object.getLength(attr)){
				builder.add('<slc:attributes>');
				for(var key in attr){
					builder.add('<slc:attribute name="'+key+'">'+attr[key]+'</slc:attribute>');
				}
				builder.add('</slc:attributes>');
			}
			builder.add('</slc:slc-execution>');
			return builder.get();
		},
		
		fromXml : function(slcExecXml){
			var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};			
			this.setStatus(org.argeo.ria.util.Element.getSingleNodeText(slcExecXml, "slc:status", NSMap));
			this.setType(org.argeo.ria.util.Element.getSingleNodeText(slcExecXml, "slc:type", NSMap));
			this.setHost(org.argeo.ria.util.Element.getSingleNodeText(slcExecXml, "slc:host", NSMap));
			this.setUser(org.argeo.ria.util.Element.getSingleNodeText(slcExecXml, "slc:user", NSMap));
			var attributes = org.argeo.ria.util.Element.selectNodes(slcExecXml, "slc:attribute", NSMap);
			for(var i=0;i<attributes.length;i++){
				this.addAttribute(attribute.getAttribute("name"), attribute.firstChild);
			}
			var stepsDates = org.argeo.ria.util.Element.selectNodes(slcExecXml, "slc:steps/slc:slc-execution-step/slc:begin", NSMap);
			if(stepsDates.length){
				this.setDate(org.argeo.ria.util.Element.getSingleNodeText(stepsDates[stepsDates.length-1], ".", NSMap));
			}
		}
	}	
});