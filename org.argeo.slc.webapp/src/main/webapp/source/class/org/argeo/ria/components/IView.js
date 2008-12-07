/**
 * Interface for a standard 'view' of an argeo RIA. A view is an independant applet that 
 * will be integrated inside a ViewPane. 
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
		commands : {}
	},
	
	members : {
		/**
		 * The implementation should contain the GUI initialisation.
		 * @param viewPane {org.argeo.ria.components.ViewPane} The pane manager
		 * @return {Boolean}
		 */
		init : function(viewPane){return true;},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @param data {mixed} Any data in any format
		 * @return {Boolean}
		 */
		load : function(data){return true;},
		/**
		 * Whether this component is already contained in a scroller 
		 * (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){return true;}
	}
});