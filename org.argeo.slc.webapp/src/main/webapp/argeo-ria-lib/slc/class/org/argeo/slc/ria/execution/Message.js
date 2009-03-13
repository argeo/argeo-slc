/**
 * A generic JMS slcExecution message encapsulator.
 */
qx.Class.define("org.argeo.slc.ria.execution.Message", {
	extend : qx.core.Object,
	/**
	 * New instance
	 * @param uuid {String} The Uuid of the message. If none is passed, one is generated.
	 */
	construct : function(uuid){
		this.base(arguments);
		if(uuid){
			this.setUuid(uuid);
		}else{
			var s = [];
			var itoh = '0123456789ABCDEF';
			for (var i = 0; i <36; i++) s[i] = Math.floor(Math.random()*0x10);
			s[14] = 4;  // Set 4 high bits of time_high field to version
			s[19] = (s[19] & 0x3) | 0x8;  // Specify 2 high bits of clock sequence
			for (var i = 0; i <36; i++) s[i] = itoh[s[i]];
			s[8] = s[13] = s[18] = s[23] = '-';
			this.setUuid(s.join('').toLowerCase());			
		}
		this.setBatchEntrySpecs([]);
		this.setAttributes({});
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
		 * Date of the message. now() by default.
		 */
		date : {
			check : "String", 
			init : new Date().toString()
		},
		/**
		 * Additionnal attributes as map of key/values
		 */
		attributes : {
			check : "Map"
		},
		batchEntrySpecs : {
			check : "Array"
		}
	},
	members : {
		
		/**
		 * Add a free attribute to the message
		 * @param attName {String} Name
		 * @param attValue {String} Value
		 */
		addAttribute: function(attName, attValue){
			var attr = this.getAttributes();
			attr[attName] = attValue;
			this.setAttributes(attr);
		},
		
		addBatchEntrySpec : function(entrySpec){
			this.getBatchEntrySpecs().push(entrySpec);
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
			var flows = this.getBatchEntrySpecs();
			if(flows.length){
				builder.add('<realized-flows>');
				for(var i=0;i<flows.length;i++){
					builder.add(flows[i].toXml());	
				}
				builder.add('</realized-flows>');
			}			
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
		
		/**
		 * Parse an XML answer and fill the object with it.
		 * @param slcExecXml {String} An slcExecMessage mapped in XML.
		 */
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