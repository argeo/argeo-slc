/**
 * Generic modal popup window.
 * It is layed out with a dock layout. When adding components to it, they are added as "center".
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.ria.components.Modal",
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
			var label = new qx.ui.basic.Label(text);
			label.setRich(true);
			label.setTextAlign("center");
			this.add(label, {edge:'center', width:'100%'});
			this.addCloseButton();
		},
		/**
		 * Add a question and ok / cancel buttons
		 * @param text {String} The question to ask to the user
		 */
		addConfirm : function(text){
			var label = new qx.ui.basic.Label(text);
			label.setRich(true);
			label.setTextAlign("center");
			this.add(label, {edge:'center', width:'100%'});
			this.addOkCancel();
		},
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
		 * Adds a prompt form to the popup : a question, followed by a text input.
		 * @param questionString {String} The question to ask to the user 
		 * @param validationCallback {Function} Callback to apply : takes the text input value as unique argument.
		 * @param callbackContext {Object} Context for the callback, optional.
		 */
		makePromptForm:function(questionString, validationCallback, callbackContext){
			var label = new qx.ui.basic.Label(questionString);
			label.setRich(true);
			label.setTextAlign("center");
			this.add(label, {edge:'north'});
			var textField = new qx.ui.form.TextField();
			textField.setMarginTop(10);
			textField.setMarginBottom(10);
			this.add(textField, {edge:'center'});
			this.addOkCancel();
			if(callbackContext){
				validationCallback = qx.lang.Function.bind(validationCallback, callbackContext);
			}
			this.okButton.addListener("execute", function(e){
				var valid = validationCallback(textField.getValue());
				if(valid) this._closeAndDestroy();
			}, this);
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