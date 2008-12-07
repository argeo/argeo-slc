/**
 * A standard view container, referenced in the application by its unique id.
 * It is managed by the ViewsManager singleton that works as the "View" part of an MVC model.
 * @see org.argeo.ria.components.ViewsManager
 * @author Charles
 */
qx.Class.define("org.argeo.ria.components.ViewPane",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.ILoadStatusable],

  /**
   * @param viewId {String} Unique id of this viewPane
   * @param viewTitle {String} Readable Title of this viewPane
   * @param splitPaneData {Map} Additionnal data to be used by splitpanes implementations.
   */
  construct : function(viewId, viewTitle, splitPaneData){
  	this.base(arguments);
  	this.setViewId(viewId);
  	this._defaultViewTitle = viewTitle;
	this.setViewTitle(viewTitle);
	var viewSelection = new org.argeo.ria.components.ViewSelection(viewId);
	this.setViewSelection(viewSelection);
	if(splitPaneData){
		this.setSplitPaneData(splitPaneData);
	}
	this.createGui();
  },

  properties : 
  {
  	/**
  	 * Unique id of the pane
  	 */
  	viewId : {init:""},
  	/**
  	 * Human-readable title for this view
  	 */
  	viewTitle : {init:"", event:"changeViewTitle"},
  	/**
  	 * Selection model for this view
  	 */
  	viewSelection : { nullable:false, check:"org.argeo.ria.components.ViewSelection" },
  	/**
  	 * Has its own scrollable content 
  	 */
	ownScrollable : {init: false, check:"Boolean"},
	/**
	 *  Data concerning the split pane
	 */
	splitPaneData : {init  : null, check:"Map"},
	/**
	 * Map of commands definition
	 * @see org.argeo.ria.event.Command 
	 */
	commands : {init : null, nullable:true, check:"Map"}
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	/**
  	 * Creates a standard GUI for the viewPane, including a container for an IView.
  	 */
  	createGui : function(){
		this.setLayout(new qx.ui.layout.VBox());
		this.header = new qx.ui.container.Composite();
		this.header.setLayout(new qx.ui.layout.Dock());
		this.header.set({appearance:"app-header"});
		this.headerLabel = new qx.ui.basic.Label(this.getViewTitle()); 
		this.header.add(this.headerLabel, {edge:"west"});
		this.addListener("changeViewTitle", function(e){
			this.headerLabel.setContent(e.getData());
		}, this);
		this.add(this.header);
		this.setDecorator(new qx.ui.decoration.Single(1,"solid","#000"));
		/*
		// Open close button of splitPane, not very useful at the moment.
		if(this.getSplitPaneData()){
			var data = this.getSplitPaneData();
			var imgName = (data.orientation=="horizontal"?"go-left":"go-bottom");
			var image = new qx.ui.basic.Image("resource/slc/"+imgName+".png");
			image.addListener("click", function(e){
				var image = e.getTarget();
				var data = this.getSplitPaneData();
				var functionDim = (data.orientation=="horizontal"?"Width":"Height");
				var objectToResize = data.object || this;
				var crtDim = objectToResize["get"+functionDim]();
				var minimize = (data.orientation=="horizontal"?"go-right":"go-top");
				var maximize = (data.orientation=="horizontal"?"go-left":"go-bottom");
				if(crtDim > data.min){
					objectToResize["set"+functionDim](data.min);
					image.setSource("resource/slc/"+minimize+".png");
					this.origDimension = crtDim;
				}else{
					if(this.origDimension){
						objectToResize["set"+functionDim](this.origDimension);
						image.setSource("resource/slc/"+maximize+".png");
					}
				}
			}, this);
			this.header.add(image,{edge:"east"});
		}
		*/
	},
	
	/**
	 * Sets the content of this pane.
	 * @param content {org.argeo.ria.components.IView} An IView implementation
	 */
	setContent : function(content){
		var addScrollable = (content.addScroll?content.addScroll():false);
		if(addScrollable){
			this.setOwnScrollable(true);
			this.scrollable = new qx.ui.container.Scroll(content);
			this.add(this.scrollable, {flex: 1});
		}else{
			this.content = content;
			this.add(this.content, {flex:1});
		}
	},
	
	/**
	 * Implementation of the ILoadStatusable interface.
	 * @see org.argeo.ria.components.ILoadStatusable
	 * @param load {Boolean} The loading status
	 */
	setOnLoad : function(load){
		if(!this.loadImage){
			this.loadImage = new qx.ui.basic.Image('resource/slc/ajax-loader.gif');
		}
		if(load){
			this.header.add(this.loadImage, {edge:"east"});
		}else{
			this.header.remove(this.loadImage);
		}
	},
	
	/**
	 * Removes and destroy the IView content of this viewPane.
	 */
	empty: function(){
		if(this.getOwnScrollable() && this.scrollable){
			this.remove(this.scrollable);
		}else if(this.content){
			this.remove(this.content);
		}
		if(this.getCommands()){
			org.argeo.ria.event.CommandsManager.getInstance().removeCommands(this.getCommands());
			this.setCommands(null);
		}
		this.setViewTitle(this._defaultViewTitle);
	}

  }
});