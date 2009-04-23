/**
 * 
 * Any component implementing this interface will generally be a user-interface indicating 
 * a "loading" status (button enabled/disabled, animated loading gif, etc...).
 * The RequestManager can handle such an array of ILoadStatusable at the beginning/end of a Request.
 * 
 * @author Charles du Jeu
 */
qx.Interface.define("org.argeo.ria.components.ILoadStatusable", {
	
	members : {
		/**
		 * Sets the current status of the component.
		 * @param status {boolean} load status
		 * @return {Boolean}
		 */
		setOnLoad : function(status){return true;}
	}
});