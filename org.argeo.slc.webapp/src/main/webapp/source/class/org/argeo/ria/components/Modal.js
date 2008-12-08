/**
 * Generic modal popup window.
 * It is layed out with a dock layout. When adding components to it, they are added as "center".
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.components.Modal",
{
	extend : qx.ui.window.Window,
  
	/**
	 * 
	 * @param caption {String} Title of the window
	 * @param icon {String} Icon of the window
	 * @param text {String} Default content of the window.
	 */
	construct : function(caption, icon, text){
	  	this.base(arguments, caption, icon);
		this.set({
			showMaximize : false,
			showMinimize : false,
			width: 200,
			height: 150
		});
		this.setLayout(new qx.ui.layout.Dock());
		this.setModal(true);
		this.addCloseButton();
		this.center();
		if(text){
			this.addLabel(text);
		}
	},
	
	members : {
		/**
		 * Display text inside the popup
		 * @param text {String} A string content for the popup
		 */
		addLabel:function(text){
			this.add(new qx.ui.basic.Label(text), {edge:'center', width:'100%'});		
		},
		/**
		 * Display a component (panel) in the center of the popup
		 * @param panel {qx.ui.core.Widget} A gui component (will be set at width 100%).
		 */
		addContent: function(panel){
			this.add(panel, {edge:'center', width:'100%'});
		},
		/**
		 * Automatically attach to the application root, then show.
		 */
		attachAndShow:function(){
			org.argeo.ria.components.ViewsManager.getInstance().getApplicationRoot().add(this);			
			this.show();
		},
		addCloseButton : function(){
			this.closeButton = new qx.ui.form.Button("Close");
			this.closeButton.addListener("execute", this._closeAndDestroy, this);
			this.add(this.closeButton, {edge:'south'});			
		},
		makePromptForm:function(questionString, validationCallback, callbackContext){
			this.add(new qx.ui.basic.Label(questionString), {edge:'north'});
			var textField = new qx.ui.form.TextField();
			this.add(textField, {edge:'center'});
			this.closeButton.removeListener("execute", this._closeAndDestroy, this);
			if(callbackContext){
				validationCallback = qx.lang.Function.bind(validationCallback, callbackContext);
			}
			this.closeButton.addListener("execute", function(e){
				var valid = validationCallback(textField.getValue());
				if(valid) this._closeAndDestroy();
			}, this);
		},
		_closeAndDestroy : function(){
			this.hide();
			this.destroy();			
		}
	}
});