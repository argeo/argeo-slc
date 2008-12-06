/**
 * @author Charles
 * 
 */
qx.Class.define("org.argeo.ria.event.CommandsManager",
{
	type : "singleton",
  extend : qx.core.Object,

  construct : function(){
  	this.base(arguments);
  	this.setInitialDefinitions(qx.lang.Object.copy(this.getDefinitions()));
  	this.addListener("changedCommands", this.createCommands, this);
  },

  properties : 
  {
  	definitions : {
  		init : {
  			"stop" : {
  				label 		: "Stop", 
  				icon		: "resource/slc/process-stop.png",
  				shortcut	: "Control+s",
  				enabled		: false,
  				menu		: null,
  				toolbar		: "list",
  				callback	: function(e){},
  				command		: null
  			},
	  		"quit" : {
  				label		: "Quit", 
  				icon 		: "resource/slc/system-shutdown.png",
  				shortcut 	: "Control+q",
  				enabled  	: true,
  				menu	   	: "File",
  				toolbar  	: false,
  				callback 	: function(e){}, 
  				command 	: null
  			},
  			"log" : {
  				label		: "Show Console", 
  				icon 		: "resource/slc/help-contents.png",
  				shortcut 	: "",
  				enabled  	: true,
  				menu	   	: "Help",
  				menuPosition: "last",
  				toolbar  	: false,
  				callback 	: function(e){  					
  					org.argeo.ria.components.Logger.getInstance().toggle();
  				}, 
  				command 	: null
  			},
  			"help" : {
  				label		: "About...", 
  				icon 		: "resource/slc/help-about.png",
  				shortcut 	: "Control+h",
  				enabled  	: true,
  				menu	   	: "Help",
  				toolbar  	: false,
  				callback 	: function(e){
					var win = new org.argeo.ria.components.Modal("About SLC", null, "SLC is a product from Argeo.");
					win.attachAndShow();
  				}, 
  				command 	: null
  			}
  		}
  	},
  	initialDefinitions : {
  		init : {}
  	}
  },

  events : {
  	"changedCommands" : "qx.event.type.Event"
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	createCommands : function(){
  		this.menus = {};
  		this.toolbars = {};
  		var defs = this.getDefinitions();
  		for(var key in defs){
  			var definition = defs[key];
  			var command;
  			if(!definition.command){
	  			command = new org.argeo.ria.event.Command(key, definition.label, definition.icon, definition.shortcut);
	  			if(definition.submenu){
	  				var menu = new qx.ui.menu.Menu();
	  				command.setMenu(menu);
	  				if(definition.submenuCallback){
	  					command.setMenuCallback(definition.submenuCallback);
	  					command.setMenuContext((definition.callbackContext?definition.callbackContext:null));
	  				}
	  			}
	  			command.setEnabled(definition.enabled);
	  			command.addListener("execute", definition.callback, (definition.callbackContext?definition.callbackContext:this));
	  			definition.command = command;
  			}else{
  				command = definition.command;
  			}
  			if(definition.menu){
  				if(!this.menus[definition.menu]) this.menus[definition.menu] = [];
  				this.menus[definition.menu].push(definition);
  			}
  			if(definition.toolbar){
  				if(!this.toolbars[definition.toolbar]) this.toolbars[definition.toolbar] = [];
  				this.toolbars[definition.toolbar].push(command);
  			}
  		}
  		this.setDefinitions(defs);
  	},
  	  	
  	refreshCommands : function(viewSelection){
  		var defs = this.getDefinitions();
  		var xmlNodes = null;
  		if(viewSelection.getCount() > 0){
  			var xmlNodes = viewSelection.getNodes();
  		}
  		for(var key in defs){
  			var definition = defs[key];
  			if(!definition.selectionChange) continue;
  			var binded = qx.lang.Function.bind(definition.selectionChange, definition.command);
  			binded(viewSelection.getViewId(), xmlNodes);
  		}
  	},
  	
  	registerMenuBar : function(menuBar){
  		this.addListener("changedCommands", function(){
  			this.createMenuButtons(menuBar);
  		}, this);
  		this.createMenuButtons(menuBar);
  	},

  	registerToolBar : function(toolBar){
  		this.addListener("changedCommands", function(){
  			this.createToolbarParts(toolBar);
  		}, this);
  		this.createToolbarParts(toolBar);
  	},  	
  	
  	createMenuButtons : function(menuBar){
  		menuBar.removeAll();
  		var anchors = {};
  		for(var key in this.menus){
  			var menu = new qx.ui.menu.Menu();
  			var button = new qx.ui.menubar.Button(key, null, menu);
  			var anchorDetected = false;
  			for(var i=0; i<this.menus[key].length;i++){
  				var def = this.menus[key][i]; 
  				menu.add(def.command.getMenuButton());
  				if(!anchorDetected && def.menuPosition){
  					anchorDetected = true;
  					anchors[def.menuPosition] = button;
  				}
  			}
  			if(!anchorDetected){
	  			menuBar.add(button);
  			}
  		}
  		// Add specific anchored buttons
  		if(anchors.first) menuBar.addAt(anchors.first, 0);
  		else if(anchors.last){
  			menuBar.add(anchors.last);
  		}
  	},
  	createToolbarParts : function(toolbar){
  		toolbar.removeAll();
  		for(var key in this.toolbars){
  			var tPart = new qx.ui.toolbar.Part();
  			toolbar.add(tPart);
  			this.toolbars[key].map(function(command){
  				tPart.add(command.getToolbarButton());
  			});
  		}
  	},
  	createMenuFromIds : function(commandIdsArray){
  		var defs = this.getDefinitions();
  		var contextMenu = new qx.ui.menu.Menu();
  		for(var i=0;i<commandIdsArray.length;i++){
  			var definition = defs[commandIdsArray[i]];
  			if(definition){
	  			var command = definition.command;
	  			contextMenu.add(command.getMenuButton());
  			}
  		}
  		return contextMenu;
  	},
  	
  	addCommands : function(definitions, callbackContext){
  		var crtDefs = this.getDefinitions();  		
  		for(var key in definitions){
  			if(callbackContext) definitions[key]['callbackContext'] = callbackContext;
  			crtDefs[key] = definitions[key];
  		}
  		this.setDefinitions(crtDefs);
  		this.fireEvent("changedCommands");
  	},
  	removeCommands : function(definitions){
  		var crtDefs = this.getDefinitions();
  		var initDefs = this.getInitialDefinitions();
  		for(var key in definitions){
  			if(!crtDefs[key]) continue;
  			if(initDefs[key]){
  				crtDefs[key] = initDefs[key];
  			}else{
  				delete crtDefs[key];
  			}
  		}
  		this.setDefinitions(crtDefs);
  		this.fireEvent("changedCommands");
  	},
  	
  	executeCommand : function(commandId){
  		var defs = this.getDefinitions();
  		if(defs[commandId] && defs[commandId].command.getEnabled()){
  			defs[commandId].command.execute();
  		}
  	},
  	getCommandById : function(commandId){
  		var defs = this.getDefinitions();
  		if(defs[commandId] && defs[commandId].command){
  			return defs[commandId].command;
  		}  		
  	},
  	addToolbarContextMenu : function(toolbar){
  		var menu = new qx.ui.menu.Menu();
  		var icon = new qx.ui.menu.RadioButton("Show Icons");
  		icon.setValue("icon");
  		var text = new qx.ui.menu.RadioButton("Show Text");
  		text.setValue("label");
  		var both = new qx.ui.menu.RadioButton("Show Both");
  		both.setValue("both");
  		var mgr = new qx.ui.form.RadioGroup(icon, text, both);
  		menu.add(icon);
  		menu.add(text);
  		menu.add(both);
  		toolbar.setContextMenu(menu);  		
  		mgr.addListener("changeValue", function(e){
  			this.setShow(e.getData());
  		}, toolbar);
  		
  	}
  }
});