/**
 * The main controller (in a standard MVC point of view) of the application. It is a singleton
 * thus can be called by any part of the application.
 * This will wire all the commands that can be defined dynamically by any IView, and add their
 * corresponding buttons to the application menubar and toolbars.
 * 
 * @author Charles du Jeu
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
	/**
	 * Commands definitions
	 * @see org.argeo.ria.event.Command for the definition Map details. 
	 */
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
  	/**
  	 * For internal use 
  	 */
  	initialDefinitions : {
  		init : {}
  	}
  },

  events : {
  	/**
  	 * Triggered when the whole commands list is changed.
  	 */
  	"changedCommands" : "qx.event.type.Event"
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	/**
  	 * Creates all the objects (if they are not already existing) from the definitions maps.
  	 */
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
  	  
  	/**
  	 * Refresh the current commands status depending on the viewSelection.
  	 * @param viewSelection {org.argeo.ria.components.ViewSelection} The current ViewSelection
  	 */
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
  	
  	/**
  	 * Record a menubar for the application
  	 * @param menuBar {qx.ui.menubar.MenuBar} The application menubar
  	 */
  	registerMenuBar : function(menuBar){
  		this.addListener("changedCommands", function(){
  			this.createMenuButtons(menuBar);
  		}, this);
  		this.createMenuButtons(menuBar);
  	},

  	/**
  	 * Record a toolbar for the application
  	 * @param toolBar {qx.ui.toolbar.ToolBar} The application toolbar
  	 */
  	registerToolBar : function(toolBar){
  		this.addListener("changedCommands", function(){
  			this.createToolbarParts(toolBar);
  		}, this);
  		this.createToolbarParts(toolBar);
  	},  	
  	
  	/**
  	 * Creates the real buttons and add them to the passed menuBar. 
  	 * @param menuBar {qx.ui.menubar.MenuBar} The application menubar
  	 */
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
  	
  	/**
  	 * Creates the real buttons and add them to the passed toolbar. 
  	 * @param toolbar {qx.ui.toolbar.ToolBar} The application toolbar
  	 */
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
  	/**
  	 * Creates a context menu from an array of commands ids.
  	 * @param commandIdsArray {Array} An array of string
  	 * @return {qx.ui.menu.Menu}
  	 */
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
  	/**
  	 * Add a new set of commands definitions
  	 * @param definitions {Map} a set of commands definitions.
  	 * @param callbackContext {qx.ui.core.Object} The context used inside the commands callbacks.
  	 */
  	addCommands : function(definitions, callbackContext){
  		var crtDefs = this.getDefinitions();  		
  		for(var key in definitions){
  			if(callbackContext) definitions[key]['callbackContext'] = callbackContext;
  			crtDefs[key] = definitions[key];
  		}
  		this.setDefinitions(crtDefs);
  		this.fireEvent("changedCommands");
  	},
  	/**
  	 * Removes a whole set of commands by their definitions maps.
  	 * @param definitions {Map} a set of commands definitions
  	 */
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
  	/**
  	 * Executes a command by its id.
  	 * @param commandId {String} The command id.
  	 */
  	executeCommand : function(commandId){
  		var defs = this.getDefinitions();
  		if(defs[commandId] && defs[commandId].command.getEnabled()){
  			defs[commandId].command.execute();
  		}
  	},
  	/**
  	 * Retrieves a command by its id.
  	 * @param commandId {String} The command id.
  	 */
  	getCommandById : function(commandId){
  		var defs = this.getDefinitions();
  		if(defs[commandId] && defs[commandId].command){
  			return defs[commandId].command;
  		}  		
  	},
  	/**
  	 * Add a standard context menu to a toolbar for button look and feel (show icon, text, both).
  	 * @param toolbar {qx.ui.toolbar.ToolBar} The toolbar
  	 */
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