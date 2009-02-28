/**
 * Generic modal popup window.
 * It is layed out with a dock layout. When adding components to it, they are added as "center".
 * @author Charles du Jeu
 */
qx.Class.define("org.argeo.slc.ria.execution.SpecEditor",
{
	extend : qx.ui.window.Window,
  
	properties : {
		batchEntrySpec : {
			check : "org.argeo.slc.ria.execution.BatchEntrySpec"
		}
	},
	
	events : {
		/**
		 * Triggered when the user clicks the "save" button. 
		 */
		"save" : "qx.event.type.Event",
		"modified" : "qx.event.type.Event"

	},
	/**
	 * 
	 * @param caption {String} Title of the window
	 * @param icon {String} Icon of the window
	 * @param text {String} Default content of the window.
	 */
	construct : function(batchEntrySpec){
		var editorLabel = "Edit Specs for "+batchEntrySpec.getLabel();
	  	this.base(arguments, editorLabel);
		this.set({
			batchEntrySpec : batchEntrySpec,
			showMaximize : false,
			showMinimize : false,
			width: Math.min(parseInt(qx.bom.Viewport.getWidth()*90/100), 400),
			height: parseInt(qx.bom.Viewport.getHeight()*60/100)
		});
		this.setLayout(new qx.ui.layout.Dock());
		this.setModal(true);
		this.center();
		this._initFormObject(this.getBatchEntrySpec().getLabel());
		this._addFormHeader(this.formObject, editorLabel);
		this.createFormFromSpec();
		this.addContent(this.formObject.pane);
		this.addOkCancel();
		this.addListener("save", function(e){
			this.saveFormToSpec();
		}, this);
	},
	
	members : {
		createFormFromSpec : function(){
			var values = this.getBatchEntrySpec().getValues();
			for(var key in values){
				var valueObj = values[key];
				var label = key;
				var hidden = valueObj.getHidden();
				var disabled = valueObj.getFrozen();
				var value = valueObj.getValue();
				var type = valueObj.getSpecType();
				var subType = valueObj.getSpecSubType();
				if(type == "primitive" && !hidden){
					this._addFormInputText(this.formObject, key, key, value, disabled, subType);
				}
			}
		},
		
		saveFormToSpec : function(){
			var values = this.getBatchEntrySpec().getValues();
			for(var key in values){
				var valueObj = values[key];
				var hidden = valueObj.getHidden();
				var disabled = valueObj.getFrozen();
				if(valueObj.getSpecType() == "primitive"){
					if(!hidden && !disabled){
						valueObj.setValue(this.formObject.fields[key].getValue());
					}
				}
			}			
		},
		
		/**
		 * Display a component (panel) in the center of the popup
		 * @param panel {qx.ui.core.Widget} A gui component (will be set at width 100%).
		 */
		addContent: function(panel){
			this.add(new qx.ui.container.Scroll(panel), {edge:'center', width:'100%'});
		},
		/**
		 * Automatically attach to the application root, then show.
		 */
		attachAndShow:function(){
			org.argeo.ria.components.ViewsManager.getInstance().getApplicationRoot().add(this);			
			this.show();
		},
		/**
		 * Init a form part : creates a pane, a set of fields, etc.
		 * @param label {String} A label
		 * @return {Map} The form part.
		 */
		_initFormObject : function(label){
			this.formObject = {};
			this.formObject.hiddenFields = {};
			this.formObject.freeFields = [];
			this.formObject.fields = {};
			this.formObject.label = label;
			this.formObject.pane = new qx.ui.container.Composite(new qx.ui.layout.VBox(5));
			return this.formObject;
		},
				
		/**
		 * Creates a simple label/input form entry.
		 * @param formObject {Map} The form part
		 * @param fieldName {String} Name
		 * @param fieldLabel {String} Label of the field
		 * @param defaultValue {String} The default value
		 * @param choiceValues {Map} An map of values
		 */
		_addFormInputText : function(formObject, fieldName, fieldLabel, defaultValue, disabled, subType, choiceValues){
			var labelElement;
			if(choiceValues){
				var fieldElement = new qx.ui.form.SelectBox();
				for(var key in choiceValues){
					fieldElement.add(new qx.ui.form.ListItem(choiceValues[key], null, key));
				}
				fieldElement.addListener("changeSelected", function(e){this.fireEvent("modified")}, this);
			}else{
				var fieldElement = new qx.ui.form.TextField();
				if(subType == "integer"){
					fieldElement.addListener("changeValue", function(e){
						var isNum = !isNaN(e.getData() * 1);
						if(!isNum){
							alert("Warning, this field only accepts Integers!");
						}
					}, this);					
				}
				fieldElement.addListener("input", function(e){this.fireEvent("modified")}, this);			
			}
			if(defaultValue){
				fieldElement.setValue(defaultValue);
			}
			if(fieldName && fieldLabel){
				labelElement = new qx.ui.basic.Label(fieldLabel);
				formObject.fields[fieldName] = fieldElement;
			}else{
				labelElement = new qx.ui.form.TextField();
				formObject.freeFields.push({
					labelEl:labelElement, 
					valueEl:fieldElement
				});
			}
			if(disabled) fieldElement.setEnabled(false);
			this._addFormEntry(formObject, labelElement, fieldElement);
		},
		
		/**
		 * Add an header
		 * @param formObject {Map} The form part
		 * @param content {Mixed} Content to add.
		 * @param additionnalButton {Mixed} Any widget to add on the east.
		 */
		_addFormHeader : function(formObject, content, additionnalButton){
			var header = new qx.ui.basic.Label('<big><b>'+content+'</b></big>');
			header.setRich(true);		
			if(!additionnalButton){
				header.setPaddingBottom(10);
				formObject.pane.add(header);
			}else{
				var pane = new qx.ui.container.Composite(new qx.ui.layout.Dock());
				pane.setPaddingBottom(10);
				pane.setPaddingRight(10);
				pane.add(header, {edge:'center'});
				pane.add(additionnalButton, {edge:'east'});
				formObject.pane.add(pane);
			}
		},
		
		/**
		 * Adds a label/input like entry in the form.
		 * @param formObject {Map} The form part
		 * @param labelElement {Object} Either a label or an input 
		 * @param fieldElement {Object} Any form input.
		 */
		_addFormEntry : function(formObject, labelElement, fieldElement){
			var entryPane = new qx.ui.container.Composite(new qx.ui.layout.HBox(5));
			labelElement.setWidth(150);
			labelElement.setTextAlign("right");		
			entryPane.add(labelElement);
			entryPane.add(new qx.ui.basic.Label(':'));
			fieldElement.setWidth(150);
			entryPane.add(fieldElement);
			formObject.pane.add(entryPane);
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
			this.okButton = new qx.ui.form.Button("Save");
			this.okButton.setEnabled(false);
			this.addListener("modified", function(e){
				this.okButton.setEnabled(true);
			}, this);
			this.okButton.addListener("execute", function(e){
				this.fireEvent("save");
				this.okButton.setEnabled(false);
			}, this);
			this.cancelButton = new qx.ui.form.Button("Close");
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