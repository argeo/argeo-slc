/**
 * This interface defines the main methods of an application Perpective.
 * See the org.argeo.ria package documentation for more info on how to build an Application
 * with this framework. 
 * 
 * @author Charles du Jeu
 */
qx.Interface.define("org.argeo.ria.components.IPerspective", {
	
	statics : {
		LABEL : "",
		ICON  : ""
	},
	
	members : {
		/**
		 * Initialize the available zones that will later contain IView implementations.
		 * This method is <b>in charge</b> of your panel to the main application zone 
		 * (just below the toolbar).
		 * 
		 * @param viewsManager {org.argeo.components.ViewsManager} the pane manager
		 * 
		 */
		initViewPanes : function(viewsManager){return true;},
		/**
		 * Once the zones are available and initialized, initialize the views here
		 * and add them to viewPanes. Trigger initial data loading, etc.
		 * 
		 * @param viewsManager {org.argeo.components.ViewsManager} the pane manager
		 * 
		 */
	  	initViews : function(viewsManager){return true},
	  	/**
	  	 * Remove and destroy the perspective
	  	 * @param viewsManager {org.argeo.components.ViewsManager} the pane manager
	  	 */
	  	remove : function(viewsManager){return true}
	}
});