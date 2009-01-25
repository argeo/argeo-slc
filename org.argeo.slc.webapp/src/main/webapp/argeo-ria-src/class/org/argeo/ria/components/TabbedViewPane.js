qx.Class.define("org.argeo.ria.components.TabbedViewPane",
{
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.ILoadStatusable],

  /**
   * @param viewId {String} Unique id of this viewPane
   * @param viewTitle {String} Readable Title of this viewPane
   * @param splitPaneData {Map} Additionnal data to be used by splitpanes implementations.
   */
	construct : function(viewId, viewTitle){
		this.base(arguments);
	  	this.setViewId(viewId);
	  	this._defaultViewTitle = viewTitle;		
		this.setLayout(new qx.ui.layout.Canvas());
		this.blurredDecorator = new qx.ui.decoration.Uniform(1, "solid", "#000");
		this.blurredDecorator.setBackgroundImage("decoration/app-header.png");
		this.blurredDecorator.setBackgroundRepeat("scale");
		this.setDecorator(this.blurredDecorator);

		this.focusedDecorator = new qx.ui.decoration.Uniform(1, "solid", "#065fb2");
		this.focusedDecorator.setBackgroundImage("decoration/app-header.png");
		this.focusedDecorator.setBackgroundRepeat("scale");
		
		this.tabView = new qx.ui.tabview.TabView();
		this.tabView.setAppearance("widget");
		// Empty mode
		this.add(this.tabView, {top: 7, width:"100%", bottom:0});
		this.tabView.setBackgroundColor("#fff");
		this.tabView.setMarginTop(27);
		
		this.tabView.addListener("changeSelected", function(){
			this.fireEvent("changeSelection");
		}, this);
		
		
		this.setFocusable(true);
		this.addListener("click", function(e){
			this.fireDataEvent("changeFocus", this);
		}, this);				
		
		this.pageIds = {};
	},

	properties : {
	  	/**
	  	 * Unique id of the pane
	  	 */
	  	viewId : {init:""},
	  	/**
	  	 * Human-readable title for this view
	  	 */
	  	viewTitle : {init:"", event:"changeViewTitle"},
	  	/**
	  	 * Has its own scrollable content 
	  	 */
		ownScrollable : {init: false, check:"Boolean"},
		/**
		 * Map of commands definition
		 * @see org.argeo.ria.event.Command 
		 */
		commands : {init : null, nullable:true, check:"Map"}
		
	},
	
	members : {
		contentExists : function(contentId){
			if(this.pageIds[contentId]){
				this.tabView.setSelected(this.pageIds[contentId]);
				return this.pageIds[contentId].getUserData("argeoria.iview");
			}						
		},
		setContent : function(content){
			if(!this.tabView.getChildren().length){
				this.tabView.setBackgroundColor("transparent");
				this.tabView.setMarginTop(0);				
			}
			var contentId = content.getInstanceId();
			var page = new qx.ui.tabview.Page(content.getInstanceLabel());
			this.pageIds[contentId] = page;
			page.setPadding(0);
			page.setLayout(new qx.ui.layout.Canvas());
			page.add(content, {width:"100%", top:0, bottom:0});
			this.tabView.add(page);	
			page.setUserData("argeoria.iview", content);			
			content.getViewSelection().addListener("changeSelection", function(e){
				this.fireEvent("changeSelection");
			}, this);
			this.tabView.setSelected(page);
		},
		getContent : function(){
			if(this._getCrtPage()){
				return this._getCrtPage().getUserData("argeoria.iview");
			}
			return null;
		},
		getViewSelection : function(){
			if(!this.getContent()) return null;
			return this.getContent().getViewSelection();
		},
		_getCrtPage : function(){
			return this.tabView.getSelected();
		},
		closeCurrent : function(){
			var crtPage = this._getCrtPage();
			if(!crtPage) return;
			var iView = crtPage.getUserData("argeoria.iview");
			var iViewInstance = iView.getInstanceId();
			iView.close();			
			this.tabView.remove(crtPage);
			delete(this.pageIds[iViewInstance]);			
			if(!this.tabView.getChildren().length){ // No more tabs : remove commands!
				if(this.getCommands()){
					org.argeo.ria.event.CommandsManager.getInstance().removeCommands(this.getCommands(), this.getViewId());
					this.setCommands(null);
				}			
				this.tabView.setBackgroundColor("#fff");
				this.tabView.setMarginTop(27);
			}						
		},
		empty : function(){
			var crtPage = this._getCrtPage();
			while(crtPage){
				this.closeCurrent();
				crtPage = this._getCrtPage();
			}
		},
		setOnLoad : function(load){
			
		},
		focus : function(){
			this.fireEvent("changeSelection");
			this.setDecorator(this.focusedDecorator);
		}, 
		blur : function(){
			this.setDecorator(this.blurredDecorator);
		}
	}
});