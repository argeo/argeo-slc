qx.Class.define("org.argeo.jcr.ria.views.ContextNodeInputView", {
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
	  	instanceId : {
	  		init:"inputViewer",
	  		event : "changeInstanceId"
	  	},
	  	instanceLabel : {
	  		init:"Xml Editor",
	  		event : "changeInstanceLabel"
	  	},
	  	dataModel : {
	  		
	  	}
	},
	
	construct : function(){
		this.base(arguments);
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
		init : function(viewPane, dataModel){
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));			
			this.set({
				layout : new qx.ui.layout.HBox(),
				dataModel : dataModel,
				paddingTop: 5, 
				paddingLeft: 5, 
				decorator : 'toolbar'
			});			
			
			this.contextInput = new qx.ui.form.TextField();
			this.contextInput.setWidth(100);
			this.contextInput.setTextAlign("right");
			this.add(this.contextInput);

			var sep = new qx.ui.basic.Label(":");
			sep.set({paddingTop:3, paddingLeft:2,paddingRight:2});
			this.add(sep);
			
			this.selectionInput = new qx.ui.form.TextField();
			this.selectionInput.setWidth(300);
			this.selectionInput.setReadOnly(true);
			this.add(this.selectionInput);
			
			this._attachInputsToDM();			
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
		},
				
		_attachInputsToDM : function(){
			var dm = this.getDataModel();
			this.contextInput.addListener("keypress", function(event){
				if(event.getKeyIdentifier() != "Enter") return;
				var path = this.contextInput.getValue();
				dm.requireContextChange(path);
			}, this);
			dm.addListener("changeContextNode", function(event){
				var ctxtNode = event.getData();
				this.contextInput.setValue(ctxtNode.getPath());
			}, this);
			dm.addListener("changeSelection", function(event){
				var sel = event.getData();
				if(!sel.length){
					this.selectionInput.setValue("/");
				}else{
					this.selectionInput.setValue(sel[0].getPath().substring(this.contextInput.getValue().length));
				}
			},this);
		},
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){
			return false;
		},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){
			
		}		
	}
	
	
});