/* ************************************************************************

  Copyright: 2008 Argeo

   License: LGPL

   Authors: Charles du Jeu

************************************************************************ */

/* ************************************************************************

#asset(slc/*)

************************************************************************ */

/**
 * This is the main application class of an Argeo RIA.
 */
qx.Class.define("org.argeo.ria.Application",
{
  extend : qx.application.Standalone,
  
  statics : {
  	INSTANCE : null 
  },
  
  properties : {
  	perspectives : {
  		check : "Map",
  		init : {}
  	},
  	activePerspectiveName : {
  		check : "String",
  		init : ""
  	},
  	activePerspective : {
  		init : null
  	},
  	commandsDefinitions : {
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
  			"switchperspective" : {
  				label 		: "Switch Perspective", 
  				icon		: "resource/slc/view-pane-tree.png",
  				shortcut	: "",
  				enabled		: true,
  				menu		: "View",
  				toolbar		: false,
  				submenu 	: [],
  				submenuCallback : function(commandId){
  					// Defer execution to assure that the submenu is closed 
  					// before it is rebuilt.
  					qx.event.Timer.once(function(){
  						org.argeo.ria.Application.INSTANCE.loadPerspective(commandId);
  					}, this, 10);  					
  				},
  				callback	: function(e){},
  				command		: null  				
  			},  			
  			"log" : {
  				label		: "Show Console", 
  				icon 		: "resource/slc/help-contents.png",
  				shortcut 	: "",
  				enabled  	: true,
  				menu	   	: "View",
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
  				menu	   	: "View",
  				toolbar  	: false,
  				callback 	: function(e){
					var win = new org.argeo.ria.components.Modal("About SLC", null, "SLC is a product from Argeo.");
					win.attachAndShow();
  				}, 
  				command 	: null
  			}
  		}
  	}
  },

  members :
  {
    /**
     * This method contains the initial application code and gets called 
     * during startup of the application
     */
    main : function()
    {
      // Call super class
      this.base(arguments);
      this.self(arguments).INSTANCE = this;
      this.views = {};
      
      var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
      viewsManager.setApplicationRoot(this.getRoot());
      
      // Enable logging in debug variant
      if (qx.core.Variant.isSet("qx.debug", "on"))
      {
        qx.log.appender.Native;
        qx.log.appender.Console;
      }
      var winLogger = org.argeo.ria.components.Logger.getInstance();
      this.getRoot().add(winLogger);
      qx.log.Logger.register(winLogger);

      // Main layout
      var layout = new qx.ui.layout.VBox();
      var container = new qx.ui.container.Composite(layout);
      viewsManager.setViewPanesContainer(container);
      // Document is the application root      
      this.getRoot().add(container, {left:0,right:0,top:0,bottom:0});
      
      // Find available perspectives
      var allPerspectives = {};
      for(var key in qx.Bootstrap.$$registry){
      	if(qx.Class.hasInterface(qx.Bootstrap.$$registry[key], org.argeo.ria.components.IPerspective)){
      		allPerspectives[key] = qx.Bootstrap.$$registry[key];
      	}
      }
      var perspectiveNumber = qx.lang.Object.getLength(allPerspectives);
      if(!perspectiveNumber){
      	this.error("Cannot find a perspective for startup!");
      	return;
      }
      this.setPerspectives(allPerspectives);
      // Choose startup perspective, delete switch menu if only one perspective.
      if(perspectiveNumber <= 1){
		 delete this.getCommandsDefinitions()["switchperspective"];
		 this.setActivePerspectiveName(qx.lang.Object.getKeys(allPerspectives)[0]);
      }
      else{
      	var startupSetting;
      	try{
      		startupSetting = qx.core.Setting.get("ria.StartupPerspective");
      	}catch(e){}
      	if(startupSetting && allPerspectives[startupSetting]){
      		this.setActivePerspectiveName(startupSetting);
      	}else{
      		this.setActivePerspectiveName(qx.lang.Object.getKeys(allPerspectives)[0]);
      	}
      	this.rebuildPerspectiveMenus();
      }
	  
      var menuBar = new qx.ui.menubar.MenuBar();
      var toolbar = new qx.ui.toolbar.ToolBar();
      var commandManager = org.argeo.ria.event.CommandsManager.getInstance();
      commandManager.init(this.getCommandsDefinitions());
      commandManager.createCommands();
      commandManager.registerMenuBar(menuBar);
      commandManager.registerToolBar(toolbar);
      toolbar.setShow("both");
      commandManager.addToolbarContextMenu(toolbar);

      var stopCommand = commandManager.getCommandById("stop");
      var serviceManager = org.argeo.ria.remote.RequestManager.getInstance();
      serviceManager.setStopCommand(stopCommand);

      container.add(menuBar);
      container.add(toolbar);      

      this.loadPerspective();
    },
    
    loadPerspective : function(perspectiveName){
    	if(perspectiveName){
    		this.setActivePerspectiveName(perspectiveName);
    		this.rebuildPerspectiveMenus();
    	}else{
    		perspectiveName = this.getActivePerspectiveName();
    	}
		var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
		if(this.getActivePerspective()){
			this.getActivePerspective().remove(viewsManager);
		}
    	var allPerspectives = this.getPerspectives();    	
		var perspectiveClass = allPerspectives[perspectiveName];
		if(!perspectiveClass){
			this.error("Cannot find class for startup perspective : "+perspectiveName);
		  	return;
		}
		var perspective = new perspectiveClass;
		perspective.initViewPanes(viewsManager);
		perspective.initViews(viewsManager);
		this.setActivePerspective(perspective);
    },
    
    rebuildPerspectiveMenus : function(){
	     var switchCommand = this.getCommandsDefinitions()["switchperspective"];
	     switchCommand.submenu = [];
	     var allPerspectives = this.getPerspectives();
	     for(var key in allPerspectives){
	     	switchCommand.submenu.push({
	     		"label":(allPerspectives[key].LABEL || key)+(key==this.getActivePerspectiveName()?" (current)":""),
	     		"icon" :(allPerspectives[key].ICON || null),
	     		"commandId":key,
	     		"disabled" : (key==this.getActivePerspectiveName()?true:false)
	     	});
	     }
		if(switchCommand.command){ // Command already created : force reload
			switchCommand.command.clearMenus();
			switchCommand.command.setMenu(switchCommand.submenu);
		}
    }
    	        
  }
});