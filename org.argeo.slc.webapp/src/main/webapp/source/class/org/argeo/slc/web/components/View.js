/**
 * @author Charles
 */
/**
 * This is the main application class of your custom application "sparta"
 */
qx.Class.define("org.argeo.slc.web.components.View",
{
  extend : qx.ui.container.Composite,

  construct : function(viewId, viewTitle){
  	this.base(arguments);
  	this.setViewId(viewId);
	this.setViewTitle(viewTitle);
	var viewSelection = new org.argeo.slc.web.components.ViewSelection(viewId);
	this.setViewSelection(viewSelection);
	this.createGui();
  },

  properties : 
  {
  	viewId : {init:""},
  	viewTitle : {init:""},
  	viewSelection : { nullable:false },
	ownScrollable : {init: false}
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	createGui : function(){
		this.setLayout(new qx.ui.layout.VBox());
		this.header = new qx.ui.container.Composite();
		this.header.setLayout(new qx.ui.layout.HBox());
		this.header.set({appearance:"app-header"});
		this.header.add(new qx.ui.basic.Label(this.getViewTitle()));
		this.add(this.header);
		this.setDecorator(new qx.ui.decoration.Single(1,"solid","#000"));
	},
	
	setContent : function(content, addScrollable){
		if(addScrollable){
			this.setOwnScrollable(true);
			this.scrollable = new qx.ui.container.Scroll(content);
			this.add(this.scrollable, {flex: 1});
		}else{
			this.content = content;
			this.add(this.content, {flex:1});
		}
	},
	
	empty: function(){
		if(this.getOwnScrollable() && this.scrollable){
			this.remove(this.scrollable);
		}else if(this.content){
			this.remove(this.content);
		}
	}

  }
});