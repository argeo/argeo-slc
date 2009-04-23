/**
 * Interface for a standard 'view' of an argeo RIA. A view is an independant applet that 
 * will be integrated inside a ViewPane. 
 * If this view is to implement a selection (a list, a tree, etc) that will trigger changes on commands, 
 * it must trigger a viewSelection#changeSelection event.
 * 
 * The typical lifecycle of an IView will be the following :
 * <br>+ init(viewPane) : initialize basic GUI in the viewPane
 * <br>+ getCommands() : wire the commands and add them to the toolbars/menubars
 * <br>+ load(data) : loads the data itself.
 * 
 * @author Charles du Jeu
 */
qx.Interface.define("org.argeo.ria.components.IView", {
	
	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
	  	instanceId : {init:""},
	  	instanceLabel : {init:""}
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
		init : function(viewPane, data){return true;},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){return true;},
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