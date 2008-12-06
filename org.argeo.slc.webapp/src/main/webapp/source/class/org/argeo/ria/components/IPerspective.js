/**
 * @author Charles du Jeu
 * 
 */
qx.Interface.define("org.argeo.ria.components.IPerspective", {
	
	members : {
		initViewPanes : function(viewsManager){return true;},
	  	initViews : function(viewsManager){return true}
	}
});