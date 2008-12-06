/* ************************************************************************

  Copyright: 2008 Argeo

   License: 

   Authors: Charles du Jeu

************************************************************************ */
qx.Class.define("org.argeo.slc.web.event.ReloadEvent",
{
	extend : qx.event.type.Event,
  
	construct : function(){
		this.base(arguments);
	},
	members : {
		init: function(dataType, content){
			this.setDataType(dataType);
			this.setContent(content);			
		}
	},
	properties :{
		dataType : {init:null},
		content : {init:null}
	}
});