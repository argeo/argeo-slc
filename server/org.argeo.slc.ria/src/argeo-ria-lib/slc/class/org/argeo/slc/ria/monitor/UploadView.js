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
		this.setLayout(new qx.ui.layout.Dock());	
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
			this.label = new qx.ui.basic.Label("Upoad");
			this.add(this.label, {edge : "center"});
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			
		},
		
		updateData : function(node){
			this.label.setContent("Properties for : " + node.getLabel());
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