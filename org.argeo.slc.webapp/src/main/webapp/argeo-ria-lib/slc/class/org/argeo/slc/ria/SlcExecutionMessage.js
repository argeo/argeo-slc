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
			builder.add('<slc:slc-execution uuid="'+this.getUuid()+'">');
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
		}
	}	
});