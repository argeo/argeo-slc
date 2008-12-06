/* ************************************************************************

  Copyright: 2008 Argeo

   License: 

   Authors: Charles du Jeu

************************************************************************ */
qx.Class.define("org.argeo.slc.web.components.Modal",
{
	extend : qx.ui.window.Window,
  
	construct : function(caption, icon, text){
	  	this.base(arguments);
		this.set({
			showMaximize : false,
			showMinimize : false,
			width: 200,
			height: 150
		});
		this.setLayout(new qx.ui.layout.Dock());
		var closeButton = new qx.ui.form.Button("Close");
		closeButton.addListener("execute", function(e){
			this.hide();
			this.destroy();
		}, this);
		this.add(closeButton, {edge:'south'});
		this.setModal(true);
		this.center();
		if(text){
			this.addLabel(text);
		}
	},
	
	members : {
		addLabel:function(text){
			this.add(new qx.ui.basic.Label(text), {edge:'center', width:'100%'});		
		},
		addContent: function(panel){
			this.add(panel, {edge:'center', width:'100%'});
		},
		attachAndShow:function(){
			org.argeo.slc.web.components.ViewsManager.getInstance().getApplicationRoot().add(this);			
			this.show();
		}
	}
});