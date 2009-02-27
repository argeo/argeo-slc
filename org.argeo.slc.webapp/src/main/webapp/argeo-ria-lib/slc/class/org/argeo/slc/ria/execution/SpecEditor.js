/**
 * Generic modal popup window.
 * It is layed out with a dock layout. When adding components to it, they are added as "center".
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.slc.ria.execution.SpecEditor",
{
	extend : qx.ui.window.Window,
  
	events : {
		/**
		 * Triggered when the user clicks the "ok" button. 
		 */
		"ok" : "qx.event.type.Event"
	},
	/**
	 * 
	 * @param caption {String} Title of the window
	 * @param icon {String} Icon of the window
	 * @param text {String} Default content of the window.
	 */
	construct : function(executionFlow){
	  	this.base(arguments, "Spec Editor");
		this.set({
			showMaximize : false,
			showMinimize : false,
			width: parseInt(qx.bom.Viewport.getWidth()*80/100),
			height: parseInt(qx.bom.Viewport.getHeight()*80/100)
		});
		this.setLayout(new qx.ui.layout.Dock());
		this.setModal(true);
		this.center();
		if(executionFlow){
			this.addContent(new qx.ui.basic.Label("Editing specs for flow : "+executionFlow.getName()));
		}else{
			this.addCloseButton();
		}
	},
	
	members : {
		/**
		 * Display a component (panel) in the center of the popup
		 * @param panel {qx.ui.core.Widget} A gui component (will be set at width 100%).
		 */
		addContent: function(panel){
			this.add(panel, {edge:'center', width:'100%'});
			this.addCloseButton();
		},
		/**
		 * Automatically attach to the application root, then show.
		 */
		attachAndShow:function(){
			org.argeo.ria.components.ViewsManager.getInstance().getApplicationRoot().add(this);			
			this.show();
		},
		/**
		 * Adds a close button bottom-center aligned to the popup
		 */
		addCloseButton : function(){
			this.closeButton = new qx.ui.form.Button("Close");
			this.closeButton.addListener("execute", this._closeAndDestroy, this);
			this.add(this.closeButton, {edge:'south'});			
		},
		/**
		 * Adds two buttons bottom-center aligned (Ok and Cancel). 
		 * Ok button has no listener by default, Cancel will close and destroy the popup.
		 */
		addOkCancel : function(){
			var buttonPane = new qx.ui.container.Composite(new qx.ui.layout.HBox(5, 'right'));
			buttonPane.setAlignX("center");
			this.add(buttonPane, {edge:"south"});
			this.okButton = new qx.ui.form.Button("Ok");
			this.okButton.addListener("execute", function(e){
				this.fireEvent("ok");
				this._closeAndDestroy();
			}, this);
			this.cancelButton = new qx.ui.form.Button("Cancel");
			this.cancelButton.addListener("execute", this._closeAndDestroy, this);
			buttonPane.add(this.okButton);
			buttonPane.add(this.cancelButton);
		},
		/**
		 * Close this modal window and destroy it.
		 */
		_closeAndDestroy : function(){
			this.hide();
			this.destroy();			
		}
	}
});