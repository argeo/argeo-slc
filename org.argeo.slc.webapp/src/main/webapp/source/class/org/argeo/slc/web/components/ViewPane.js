/**
 * @author Charles
 */
/**
 * This is the main application class of your custom application "sparta"
 */
qx.Class.define("org.argeo.slc.web.components.ViewPane",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.slc.web.components.ILoadStatusable],

  construct : function(application, viewId, viewTitle, splitPaneData){
  	this.base(arguments);
  	this.setApplication(application);
  	this.setViewId(viewId);
  	this._defaultViewTitle = viewTitle;
	this.setViewTitle(viewTitle);
	var viewSelection = new org.argeo.slc.web.components.ViewSelection(viewId);
	this.setViewSelection(viewSelection);
	if(splitPaneData){
		this.setSplitPaneData(splitPaneData);
	}
	this.createGui();
  },

  properties : 
  {
  	application : {init : null},
  	viewId : {init:""},
  	viewTitle : {init:"", event:"changeViewTitle"},
  	viewSelection : { nullable:false },
	ownScrollable : {init: false},
	splitPaneData : {init  : null},
	commands : {init : null, nullable:true}
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
	
	empty: function(){
		if(this.getOwnScrollable() && this.scrollable){
			this.remove(this.scrollable);
		}else if(this.content){
			this.remove(this.content);
		}
		if(this.getCommands()){
			org.argeo.slc.web.event.CommandsManager.getInstance().removeCommands(this.getCommands());
			this.setCommands(null);
		}
		this.setViewTitle(this._defaultViewTitle);
	}

  }
});