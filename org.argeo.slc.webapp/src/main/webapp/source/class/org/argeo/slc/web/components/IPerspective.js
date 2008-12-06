/**
 * @author Charles du Jeu
 * 
 */
qx.Interface.define("org.argeo.slc.web.components.IPerspective", {
	
	members : {
		initViewPanes : function(viewsManager){return true;},
	  	initViews : function(viewsManager){return true}
	}
});