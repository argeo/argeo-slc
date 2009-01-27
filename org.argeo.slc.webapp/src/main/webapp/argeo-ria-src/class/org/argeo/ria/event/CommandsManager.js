/**
 * The main controller (in a standard MVC point of view) of the application. It is a singleton
 * thus can be called by any part of the application.
 * This will wire all the commands that can be defined dynamically by any IView, and add their
 * corresponding buttons to the application menubar and toolbars.
 * See the "definitions" property documentation below for more info on how to define new commands.
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
	 * The commands definitions is a Map described as below
	 * <pre>
	 * {
	 * 	<b>label : "",</b> 
	 * 	 | The label of the action
	 * 
	 * 	<b>icon	: "",</b> 
	 * 	 | The icon image
	 * 
	 * 	<b>shortcut : "",</b>
	 * 	 | The keyboard shortcut, as defined in qooxdoo (Control+s, Alt+k, etc.). Warning, the letter must be lowercase.
	 * 
	 * 	<b>enabled : true,</b>
	 * 	 | Whether it is enabled or disabled at creation
	 * 
	 * 	<b>menu : ""|null,</b>
	 * 	 | The menu group to which the command will be added. If null, will not appear in the menus.
	 * 
	 * 	<b>menuPosition : "first"|"last"</b>
	 *	 | Optional : force the menu group to be first or last in the menubar.
	 *   
	 * 	<b>toolbar : ""|null,</b>
	 * 	 | The toolbar group to which the command will be added. If null, will not appear in the toolbars.
	 * 
	 * 	<b>init : function(){},</b>
	 * 	 | Optional function called at command creation.
	 * 	 | Function context : the command itself
	 * 
	 * 	<b>callback : function(e){},</b>
	 * 	 | The main callback to be triggered when command is executed.
	 * 	 | Function context : the current class (not the command!)
	 *  
	 * 	<b>selectionChange : function(viewPaneId, xmlNodes){},</b>
	 * 	 | Optional function called each time a selectionChange is detected in one of the active viewPane.
	 * 	 | The origin viewPaneId and the new selection as a map of nodes are passed as arguments.
	 * 	 | Function context : the command itself.
	 * 
	 * 	<b>submenu : [{label:"", icon:"", commandId:""}, ...],</b>
	 * 	 | If set, the command will create a submenu, being in a menu or in the toolbar.
	 * 	 | The submenu is created with the various array entries, and the submenuCallback function
	 * 	 | will be called with the 'commandId' parameter when a submenu entry is selected.
	 * 
	 * 	<b>submenuCallback : function(commandId){},</b>
	 * 	 | Callback if command is a submenu (cf. above).
	 * 	 | Function context : the current class/
	 * 
	 * 	<b>command : null</b>
	 * 	 | For internal use only, caching the actual org.argeo.ria.event.Command object.
	 * }
	 * </pre>
	 * @see org.argeo.ria.event.Command for the definition Map details. 
	 */
  	definitions : {
  		init : {},
  		check : "Map"
  	},
  	/**
  	 * For internal use 
  	 */
  	initialDefinitions : {
  		init : {},
  		check : "Map"
  	},
  	/**
  	 * Special command definitions that are shared between focusable parts. 
  	 */
  	sharedDefinitions : {
  		init: {},
  		check: "Map"
  	}
  },

  events : {
  	/**
  	 * Triggered when the whole commands list is changed. Mainly used internally by the manager.
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
  	 * Initialize the manager with basic definitions.
  	 * @param initDefinitions {Map} A map of commands definitions.
  	 */
  	init : function(initDefinitions){
  		this.setDefinitions(initDefinitions);
  		this.setInitialDefinitions(qx.lang.Object.copy(initDefinitions));
  	},
  	
  	/**
  	 * Creates all the objects (if they are not already existing) from the definitions maps.
  	 */
  	createCommands : function(){
  		this.menus = {};
  		this.toolbars = {};
  		var defs = this.getDefinitions();
  		var shared = this.getSharedDefinitions();
  		for(var key in defs){
  			var definition = defs[key];
  			var command;
  			if(!definition.command){
	  			command = new org.argeo.ria.event.Command(key, definition.label, definition.icon, definition.shortcut);
	  			if(definition.submenu){
	  				command.setMenu(definition.submenu);
	  				if(definition.submenuCallback){
	  					command.setMenuCallback(definition.submenuCallback);
	  					command.setMenuContext((definition.callbackContext?definition.callbackContext:null));
	  				}
	  			}
	  			command.setEnabled(definition.enabled);
	  			if(definition.toggle){
	  				command.setToggle(true);
	  			}
	  			this._attachListener(command, definition.callback, definition.callbackContext);
	  			if(definition.init){
	  				var binded = qx.lang.Function.bind(definition.init, command);
	  				binded();
	  			}
	  			definition.command = command;
  			}else{
  				command = definition.command;
	  			if(shared[key]){
	  				
	  				for(var focusPartId in shared[key]){
		  				var sharedCommand = shared[key][focusPartId];
		  				if(sharedCommand.callback){
							var split = sharedCommand.callbackContext.split(":");
							var focusPart = split[0];
							var viewId = split[1];							
							command.registerCallback(sharedCommand.callback, split[1]);		  					
		  					//this._attachListener(command, sharedCommand.callback, sharedCommand.callbackContext);
		  				}	  					
	  				}
	  				
	  			}
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
  		var shared = this.getSharedDefinitions();
  		var xmlNodes = null;
  		if(viewSelection.getCount() > 0){
  			var xmlNodes = viewSelection.getNodes();
  		}
  		for(var key in defs){
  			var definition = defs[key];
  			if(!definition.selectionChange) continue;
  			if(shared[key]){
  				var currentFocus = org.argeo.ria.components.ViewsManager.getInstance().getCurrentFocus();
  				//this.debug(currentFocus);
  				if(!currentFocus) continue;
  				var sharedComm = shared[key][currentFocus.getViewId()];
  				if(sharedComm && sharedComm.selectionChange){
		  			var binded = qx.lang.Function.bind(sharedComm.selectionChange, definition.command);
	  				binded(viewSelection.getViewId(), xmlNodes);
  				}
  			}
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
  	 * Add a new set of commands definitions. See the definitions property of this class.
  	 * @param definitions {Map} a set of commands definitions.
  	 * @param callbackContext {qx.ui.core.Object} The context used inside the commands callbacks.
  	 * @param focusablePartId {String} A string identifying the associated focusable part, like "view:viewId". 
  	 */
  	addCommands : function(definitions, callbackContext, focusablePartId){
  		var crtDefs = this.getDefinitions();  		
  		for(var key in definitions){
  			if(callbackContext) definitions[key]['callbackContext'] = callbackContext;
  			if(crtDefs[key] && definitions[key]['shared']){
				if(focusablePartId) {
					definitions[key]['focusablePartId'] = focusablePartId;
					if(!this.getSharedDefinitions()[key]){
						this.getSharedDefinitions()[key] = {};
					}
					this.getSharedDefinitions()[key][focusablePartId] = definitions[key];
				}
				
  			}else{
	  			crtDefs[key] = definitions[key];
  			}
  		}
  		this.setDefinitions(crtDefs);
  		this.fireEvent("changedCommands");
  	},
  	/**
  	 * Removes a whole set of commands by their definitions maps.
  	 * @param definitions {Map} a set of commands definitions
  	 * @param focusablePartId {String} A string identifying the associated focusable part, like "view:viewId".  
  	 */
  	removeCommands : function(definitions, focusablePartId){
  		var crtDefs = this.getDefinitions();
  		var initDefs = this.getInitialDefinitions();
  		var sharedDefs = this.getSharedDefinitions();
  		for(var key in definitions){
  			if(!crtDefs[key]) continue;
  			if(initDefs[key]){
  				crtDefs[key] = initDefs[key];
  			}else{
  				if(sharedDefs[key] && sharedDefs[key][focusablePartId]){
  					crtDefs[key].command.removeCallback(focusablePartId);
  					delete sharedDefs[key][focusablePartId];
  				}else{
	  				delete crtDefs[key];
  				}
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
  		mgr.setSelected(both);
  		toolbar.setContextMenu(menu);  		
  		mgr.addListener("changeValue", function(e){
  			this.setShow(e.getData());
  		}, toolbar);
  		
  	},
  	/**
  	 * Attach a listener to a command, with a context.
  	 * The context can be an object, a string like "view:viewId" or null. 
  	 * If a string, the viewPaneId content will be retrieved at runtime. If null, "this" will be used
  	 * as default context.
  	 * @param command {org.argeo.ria.event.Command} The command
  	 * @param callback {Function} The function to execute
  	 * @param callbackContext {Object|String} The context in which the function will be executed.  
  	 */
  	_attachListener:function(command, callback, callbackContext){  		
		if(!callbackContext){
			command.addListener("execute", callback, this);
			return;
		}
		if(typeof(callbackContext) == "object"){
			command.addListener("execute", callback, callbackContext);
			return;
		}		
		if(typeof(callbackContext) == "string"){
			
			var split = callbackContext.split(":");
			var focusPart = split[0];
			var viewId = split[1];
			if(command.getCallbacks()[viewId]) return;
			command.registerCallback(callback, split[1]);
			command.addListener("execute", function(event){
				var target = event.getTarget();
				var callbacks = target.getCallbacks();
				if(qx.lang.Object.getLength(callbacks) == 0) return;
  				var view = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById(viewId).getContent();
				if(qx.lang.Object.getLength(callbacks) == 1){
	  				var binded = qx.lang.Function.bind(callbacks[qx.lang.Object.getKeys(callbacks)[0]], view);
	  				binded(event);
	  				return;
				}
				var currentFocus = org.argeo.ria.components.ViewsManager.getInstance().getCurrentFocus();
				if(currentFocus && currentFocus.getViewId() && callbacks[currentFocus.getViewId()]){
					var currentViewId = currentFocus.getViewId();
					view = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById(currentViewId).getContent();
					var binded = qx.lang.Function.bind(callbacks[currentFocus.getViewId()], view);
					binded(event);
					return;
				}
			});
			
			
			/*
			if(callbackContext.split(":")[0] == "view"){
				var viewId = callbackContext.split(":")[1];
				command.addListener("execute", function(event){
					if(event.getTarget().getCheckFocusAtCallback()){
						var currentFocus = org.argeo.ria.components.ViewsManager.getInstance().getCurrentFocus();
						if(currentFocus.getViewId() != viewId) return;
					}
	  				var view = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById(viewId).getContent();
	  				var binded = qx.lang.Function.bind(callback, view);
	  				binded(event);
				});
			}else{
				command.addListener("execute", callback, callbackContext);
			}
			*/
		}
  	}
  }
});
