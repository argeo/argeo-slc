/**
 * The standard command for all actions. It registers keyboard shortcuts, centralizes 
 * command state, callback, etc. It is defined by command definitions that can be found 
 * in the CommandsManager. 
 */
 qx.Class.define("org.argeo.ria.event.Command",
{
  extend : qx.event.Command,
  implement : [org.argeo.ria.components.ILoadStatusable],

  properties : {
  	/**
  	 * Unique id of the command 
  	 */
  	id : {init:""},
  	/**
  	 * Label of the command 
  	 */
  	label : {init:""},
  	/**
  	 * Icon of the command 
  	 */
  	icon : {init:""},
  	/**
  	 * Sub menu if needed 
  	 */
  	menu : {
  		nullable: true,
  		event : "changeMenu"
  	},
  	/**
  	 * Callback associated to the submenu of the command 
  	 */
  	menuCallback : {nullable:true},
  	/**
  	 * Context used when triggering menuCallback 
  	 */
  	menuContext : {nullable:true}
  },
  
  /**
   * @param id {String} Id of the command
   * @param label {String} Label of the command
   * @param icon {String} Icon of the command
   * @param shortcut {String} Keyboard Shortcut (like alt+o, ctrl+z, etc..)
   */
  construct : function(id, label, icon, shortcut){
  	this.base(arguments, shortcut);
  	this.setId(id);
  	this.setLabel(label);
  	this.setIcon(icon);   	
  },
  
  members :
  {
  	/**
  	 * Create a Button that suits a qx.ui.menu.MenuBar linked to this command
  	 * @return {qx.ui.menu.Button}
  	 */
  	getMenuButton : function(){
  		var button = new qx.ui.menu.Button(
  			this.getLabel(), 
  			this.getIcon(), 
  			this, 
  			this.getMenu()
  		);
  		this.addTooltip(button);
		if(this.getMenu()){
			this.addListener("changeMenu", function(event){
				button.setMenu(this.getMenuClone());
			}, this);
			this.menuClones = [];
		}
  		return button;
  	},
  	
  	/**
  	 * Create a Button that suits a qx.ui.toolbar.Toolbar part linked to this command.
  	 * @return {qx.ui.toolbar.MenuButton}
  	 */
  	getToolbarButton : function(){
  		var button;
  		if(this.getMenu()){
  			button = new qx.ui.toolbar.MenuButton(
  				this.getLabel(),
  				this.getIcon(), 
  				this.getMenuClone()
  			);
  			this.addListener("changeMenu", function(event){
  				button.setMenu(this.getMenuClone());
  			}, this);
  			this.addListener("changeEnabled", function(e){
		  		this.setEnabled(e.getData());
  			}, button);
  			button.setEnabled(this.getEnabled());
  		}else{
  			button = new qx.ui.toolbar.Button(
  				this.getLabel(),
  				this.getIcon(),
  				this
  			);
  		}
  		this.addTooltip(button);
  		return button;
  	},
  	  	
  	/**
  	 * Clones the command menu
  	 * @return {qx.ui.menu.Menu}
  	 */
  	getMenuClone : function(){
  		var menuClone = new qx.ui.menu.Menu();
  		var submenus = this.getMenu();
  		for(var i=0;i<submenus.length;i++){
	  		var button = new qx.ui.menu.Button(submenus[i].label, submenus[i].icon);
	  		button.setUserData("commandId", submenus[i].commandId);
	  		button.addListener("execute", this.executeSubMenuCallback, this);
	  		menuClone.add(button);
  		}
  		this.menuClones.push(menuClone);
  		return menuClone;
  	},
  	
  	/**
  	 * Remove all existing menus and their clones.
  	 */
  	clearMenus : function(){
  		if(!this.getMenu()) return;
  		for(var i=0;i<this.menuClones.length;i++){
  			this.menuClones[i].destroy();
  		}
  		this.menuClones = [];
  	},
  	
  	/**
  	 * Add button to a given submenu.
  	 * @param label {String} The label of the button
  	 * @param icon {String} The icon of the button
  	 * @param commandId {String} The associated command id.
  	 * @param menu {qx.ui.menu.Menu} The menu to which add the button
  	 */
  	addSubMenuButton : function(label, icon, commandId, menu){
  		var button = new qx.ui.menu.Button(label, icon);
  		button.setUserData("commandId", commandId);
  		button.addListener("execute", this.executeSubMenuCallback, this);
  		if(menu){
  			menu.add(button);
  		}else{
	  		this.getMenu().add(button);
  		}
  	},
  	
  	/**
  	 * Triggers the menuCallback property in the right context.
  	 * @param event {qx.event.type.Event} The firing event.
  	 */
  	executeSubMenuCallback : function(event){
		var button = event.getTarget();
		var callback = this.getMenuCallback();
		callback = qx.lang.Function.bind(callback, this.getMenuContext() || this);
		callback(button.getUserData("commandId"));  		
  	},  	
  	/**
  	 * Adds a tooltip to a button.
  	 * @param element {qx.ui.core.Widget} The element to which the command tooltip is added. 
  	 */
  	addTooltip : function(element){
  		if(this.getShortcut() != null){
	  		element.setToolTip(new qx.ui.tooltip.ToolTip(this.getShortcut()));
  		}  		
  	},
  	
  	/**
  	 * Implementation of the ILoadStatusable interface.
  	 * Sets the whole command enabled if not loading and disabled if loading.
  	 * @param status {Boolean} The loading status of the button. 
  	 */
  	setOnLoad : function(status){
  		this.setEnabled(!status);
  	}
  	
  }
});
