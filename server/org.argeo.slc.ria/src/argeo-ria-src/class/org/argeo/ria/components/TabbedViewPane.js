/**
 * A more elaborate views container than ViewPane, as it can handle multiple contents
 * at once via a TabView.
 * See {@link org.argeo.ria.components.ViewPane}.
 */
qx.Class.define("org.argeo.ria.components.TabbedViewPane",
{
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.ILoadStatusable],

  /**
   * @param viewId {String} Unique id of this viewPane
   * @param viewTitle {String} Readable Title of this viewPane
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
		/**
		 * Checks if the pane already contains a given view, identified by its instance id
		 * @param contentId {Mixed} The instance id to check
		 * @return {Boolean}
		 */
		contentExists : function(contentId){
			if(this.pageIds[contentId]){
				this.tabView.setSelected(this.pageIds[contentId]);
				return this.pageIds[contentId].getUserData("argeoria.iview");
			}						
		},
		/**
		 * Sets a new instance in the tabbed pane.
		 * @param content {org.argeo.ria.components.IView} The applet to add.
		 */
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
		/**
		 * Get the currently selected tab content, if any.
		 * @return {org.argeo.ria.components.IView} The currently selected view.
		 */
		getContent : function(){
			if(this._getCrtPage()){
				return this._getCrtPage().getUserData("argeoria.iview");
			}
			return null;
		},
		/**
		 * Get the currently selected tab ViewSelection object.
		 * @return {org.argeo.ria.components.ViewSelection} The view selection object of the currently selected view.
		 */
		getViewSelection : function(){
			if(!this.getContent()) return null;
			return this.getContent().getViewSelection();
		},
		/**
		 * Return the currently selected tab Page.
		 * @return {qx.ui.tabview.Page} The page
		 */
		_getCrtPage : function(){
			return this.tabView.getSelected();
		},
		/**
		 * Closes the currently selected view and remove all tabs components (button, page).
		 */
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
		/**
		 * Call closeCurrent() recursively until there is no more page.
		 */
		empty : function(){
			var crtPage = this._getCrtPage();
			while(crtPage){
				this.closeCurrent();
				crtPage = this._getCrtPage();
			}
		},
		/**
		 * Sets the tabView on "load" state. Nothing is done at the moment.
		 * @param load {Boolean} Load status
		 */
		setOnLoad : function(load){
			
		},
		/**
		 * Sets a graphical indicator that this pane has the focus. A blue border.
		 */
		focus : function(){
			if(this.hasFocus) return;
			this.fireEvent("changeSelection");
			this.setDecorator(this.focusedDecorator);
			this.hasFocus = true;
		}, 
		/**
		 * Remove a graphical focus indicator on this pane.
		 */
		blur : function(){
			this.hasFocus = false;
			this.setDecorator(this.blurredDecorator);
		}
	}
});