/**
 * This can be triggered at the end of a IO Request. In that case, it contains
 * a data type as an identifier, and the request response itself.
 * Can be used this way to listen for data changes from various parts of the application.
 */
qx.Class.define("org.argeo.ria.event.ReloadEvent",
{
	extend : qx.event.type.Event,
  
	construct : function(){
		this.base(arguments);
	},
	members : {
		/**
		 * Basic initialisation of event
		 * @param dataType {String} a unique data identifier
		 * @param content {mixed} the retrieved data
		 */
		init: function(dataType, content){
			this.setDataType(dataType);
			this.setContent(content);			
		}
	},
	properties :{
		/**
		 * A unique data identifier 
		 */
		dataType : {init:null, check:"String"},
		/**
		 * The new data content 
		 */
		content : {init:null}
	}
});