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
  settings : {
  	//"slc.Perspective" : "org.argeo.ria.custom.Perspective"
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

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
	  
      var menuBar = new qx.ui.menubar.MenuBar();
      var toolbar = new qx.ui.toolbar.ToolBar();
      var commandManager = org.argeo.ria.event.CommandsManager.getInstance();
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

      /**
       * Call GuiLoader to load default perspective and associated views.
       */
      var perspectiveClassName  = qx.core.Setting.get("ria.Perspective")
      if(!perspectiveClassName){
      	this.error("Cannot find default perspective setting ! Please check your config.json file! (CUSTOM_PERSPECTIVE, line 23).");
      	return;
      }
      var perspectiveClass = qx.Class.getByName(perspectiveClassName);
      if(!perspectiveClass){
      	this.error("Cannot find class for default perspective ("+perspectiveClassName+"). Please check your config.json file! (CUSTOM_PERSPECTIVE, line 23).");
      	return;
      }
      var perspective = new perspectiveClass;
      perspective.initViewPanes(viewsManager);
      perspective.initViews(viewsManager);      
    }
    	        
  }
});