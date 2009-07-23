qx.Class.define("org.argeo.slc.ria.monitor.UploadView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 
	
	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {}
		},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
		view : {
			init : null
		},		  	
	  	instanceId : {init:""},
	  	instanceLabel : {init:""}
	},

	construct : function(){
		this.base(arguments);
		this.setLayout(new qx.ui.layout.Basic);	
	},	
	
	members : {
		/**
		 * The implementation should contain the GUI initialisation.
		 * This is the role of the manager to actually add the graphical component to the pane, 
		 * so it's not necessary to do it here. 
		 * @param viewPane {org.argeo.ria.components.ViewPane} The pane manager
		 * @param data {Mixed} Any object or data passed by the initiator of the view
		 * @return {Boolean}
		 */
		init : function(viewPane, data){
			this.setView(viewPane);			
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));
			this.form = new org.argeo.ria.components.upload.UploadForm("uploadFrm", org.argeo.slc.ria.SlcApi.getInstallModuleService());
			this.form.setLayout(new qx.ui.layout.HBox(5));
			this.fileWidget = new org.argeo.ria.components.upload.UploadField("uploadFile", "Choose a file");
			
			this.form.addListener("completed", function(e){
				this.fileWidget.setFieldValue("");
				var response = this.form.getIframeHtmlContent();
				this.debug(response);
				this.displayMessage(response, 4000);
			}, this);
			this.form.addListener("sending", function(e){
				this.debug("Sending...");
				this.displayMessage("Sending...");
			}, this);
			this.fileWidget.addListener("changeFieldValue", function(e){
				if(e.getData()!=""){
					this.submitButton.setEnabled(true);
				}
			}, this);
			
			this.submitButton = new qx.ui.form.Button("Upload");
			this.submitButton.setEnabled(false);
			this.submitButton.addListener("execute", function(e){
				if(this.fileWidget.getFieldValue()!=""){
					this.form.send();
				}
			}, this);

			this.fileWidget.getTextField().setWidth(200);
			this.form.add(this.fileWidget);
			this.form.add(this.submitButton);
			this.messageLabel = new qx.ui.basic.Label("");
			this.messageLabel.setRich(true);
			this.messageLabel.setPadding(4,4,4,20);
			this.form.add(this.messageLabel);
			this.add(this.form, {left : 20, top:20});
		},
		
		displayMessage : function(message, timer){
			this.messageLabel.setContent("<i>"+qx.lang.String.stripTags(message)+"</i>");
			if(timer){
				qx.event.Timer.once(function(){
					this.messageLabel.setContent("");
				}, this, timer);
			}
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){			
		},
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){return true;},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){return true;}
	}
});