qx.Class.define("org.argeo.slc.web.event.Command",
{
  extend : qx.event.Command,
  implement : [org.argeo.slc.web.components.ILoadStatusable],

  properties : {
  	id : {init:""},
  	label : {init:""},
  	icon : {init:""},
  	menu : {
  		nullable: true,
  		event : "changeMenu"
  	},
  	menuCallback : {nullable:true},
  	menuContext : {nullable:true}
  },
  
  construct : function(id, label, icon, shortcut){
  	this.base(arguments, shortcut);
  	this.setId(id);
  	this.setLabel(label);
  	this.setIcon(icon); 	
  },
  
  members :
  {
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
				this.setMenu(event.getData());
			}, button);
		}
  		return button;
  	},
  	
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
  	  	
  	getMenuClone : function(){
  		if(!this.menuClone){
  			this.menuClone = new qx.ui.menu.Menu();
  			this.menuClone.setMinWidth(110);
  		}
  		return this.menuClone;
  	},
  	
  	clearMenus : function(){
  		this.getMenu().removeAll();
  		this.getMenuClone().removeAll();
  	},
  	
  	addSubMenuButton : function(label, icon, commandId, menu){
  		var button = new qx.ui.menu.Button(label, icon);
  		button.setUserData("commandId", commandId);
  		button.addListener("execute", this.executeSubMenuCallback, this);
  		if(menu){
  			menu.add(button);
  		}else{
	  		this.getMenu().add(button);
	  		this.addSubMenuButton(label, icon, commandId, this.menuClone);
  		}
  	},
  	
  	executeSubMenuCallback : function(event){
		var button = event.getTarget();
		var callback = this.getMenuCallback();
		callback = qx.lang.Function.bind(callback, this.getMenuContext() || this);
		callback(button.getUserData("commandId"));  		
  	},  	
  	
  	addTooltip : function(element){
  		if(this.getShortcut() != null){
	  		element.setToolTip(new qx.ui.tooltip.ToolTip(this.getShortcut()));
  		}  		
  	},
  	
  	setOnLoad : function(status){
  		this.setEnabled(!status);
  	}
  	
  }
});
