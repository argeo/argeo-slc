/**
 * @author Charles du Jeu
 * 
 */
qx.Interface.define("org.argeo.slc.web.components.IView", {
	
	properties : {
		commands : {}
	},
	
	members : {
		init : function(viewPane){
			return true;
		},
		load : function(data){
			return true;
		},
		addScroll : function(){
			return true;
		}
	}
});