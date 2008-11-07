/**
 * @author Charles
 * 
 */
qx.Class.define("org.argeo.slc.web.event.CommandsManager",
{
  extend : qx.core.Object,

  construct : function(application){
  	this.base(arguments);
  	this.application = application;
  	this.setInitialDefinitions(qx.lang.Object.copy(this.getDefinitions()));
  	this.addListener("changedCommands", this.createCommands, this);
  },

  properties : 
  {
  	definitions : {
  		init : {
  			"loadtestlist" : {
  				label		: "Load Tests", 
  				icon 		: "resource/slc/view-refresh.png",
  				shortcut 	: "Control+l",
  				enabled  	: true,
  				menu	   	: "File",
  				toolbar  	: "list",
  				callback 	: function(e){
  					this.loadTable("/org.argeo.slc.webapp/resultList.ui");
  				}, 
  				command 	: null
  			},
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
  			"opentest" : {
  				label	 	: "Open", 
  				icon 		: "resource/slc/document-open.png",
  				shortcut 	: "Control+o",
  				enabled  	: false,
  				menu	   	: "Test",
  				toolbar  	: "test",
  				callback	: function(e){
  					var xmlNodes = this.getSelectionForView("list").getNodes();
  					this.createTestApplet(xmlNodes[0]);
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId != "list") return;
  					this.setEnabled(false);
  					if(xmlNodes == null || !xmlNodes.length) return;
  					var applet = org.argeo.slc.web.util.Element.selectSingleNode(xmlNodes[0],'report[@type="applet"]'); 
  					if(applet != null && qx.dom.Node.getText(applet) != ""){
  						this.setEnabled(true);  						
  					}  					
  				},
  				command 	: null
  			},
  			"download" : {
  				label	 	: "Download as...", 
  				icon 		: "resource/slc/go-down.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Test",
  				toolbar  	: "test",
  				callback	: function(e){ },
  				command 	: null,
  				submenu 	: {},
  				submenuCallback : function(commandId){
  					var xmlNodes = this.getSelectionForView("list").getNodes();
  					// Single selection
  					var uuid = qx.xml.Element.getSingleNodeText(xmlNodes[0], 'param[@name="uuid"]');
  					var urls = {
  						xsl : "resultView.xslt",
  						xml : "resultViewXml.xslt",
  						xls : "resultView.xls",
  						pdf : "resultView.pdf"
  					};
  					var url = "../"+urls[commandId]+"?uuid="+uuid;
  					if(commandId == "xls" || commandId == "pdf"){
	  					document.location.href = url;
  					}else{
  						var win = window.open(url);
  					}
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId!="list")return;
  					this.clearMenus();
  					this.setEnabled(false);
  					if(xmlNodes == null) return;
  					
  					var reports = qx.xml.Element.selectNodes(xmlNodes[0],'report[@type="download"]');
  					if(reports == null || !reports.length)return;
  					
  					for(var i=0; i<reports.length;i++){
  						var report = reports[i];
  						var commandId = qx.dom.Node.getText(org.argeo.slc.web.util.Element.selectSingleNode(report, "@commandid")); 
  						this.addSubMenuButton(
  							qx.dom.Node.getText(report),
  							"resource/slc/mime-"+commandId+".png",
  							commandId
						);
  					}
  					this.setEnabled(true);
  					this.fireDataEvent("changeMenu", this.getMenu());
  				}
  			},
  			"deletetest" : {
  				label	 	: "Delete", 
  				icon 		: "resource/slc/edit-delete.png",
  				shortcut 	: "Control+d",
  				enabled  	: false,
  				menu	   	: "Test",
  				toolbar  	: "test",
  				callback	: function(e){
  					// Call service to delete
  				},
  				command 	: null
  			},
  			"copytocollection" : {
  				label	 	: "Copy to...", 
  				icon 		: "resource/slc/edit-copy.png",
  				shortcut 	: "Control+c",
  				enabled  	: false,
  				menu	   	: "Test",
  				toolbar  	: "test",
  				callback	: function(e){
  					// Call service to copy
  				},
  				command 	: null
  			},
  			"log" : {
  				label		: "Toggle Console", 
  				icon 		: "resource/slc/help-contents.png",
  				shortcut 	: "",
  				enabled  	: true,
  				menu	   	: "Help",
  				toolbar  	: false,
  				callback 	: function(e){
  					qx.log.appender.Console.toggle();
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
					var wm1 = new qx.ui.window.Window("About SLC");
					wm1.set({
						showMaximize : false,
						showMinimize : false,
						width: 200,
						height: 150
					});
					wm1.setLayout(new qx.ui.layout.Dock());
					wm1.add(new qx.ui.basic.Label("SLC is a product from Argeo."), {edge:'center', width:'100%'});
					var closeButton = new qx.ui.form.Button("Close");
					closeButton.addListener("execute", function(e){
						this.hide();
						this.destroy();
					}, wm1);
					wm1.add(closeButton, {edge:'south'});
					wm1.setModal(true);
					wm1.center();
					this.getRoot().add(wm1);
					wm1.show();
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
	  			command = new org.argeo.slc.web.event.Command(key, definition.label, definition.icon, definition.shortcut);
	  			if(definition.submenu){
	  				var menu = new qx.ui.menu.Menu();
	  				command.setMenu(menu);
	  				if(definition.submenuCallback){
	  					command.setMenuCallback(definition.submenuCallback);
	  					command.setMenuContext(this.application);
	  				}
	  			}
	  			command.setEnabled(definition.enabled);
	  			command.addListener("execute", definition.callback, this.application);
	  			definition.command = command;
  			}else{
  				command = definition.command;
  			}
  			if(definition.menu){
  				if(!this.menus[definition.menu]) this.menus[definition.menu] = [];
  				this.menus[definition.menu].push(command);
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
  		for(var key in this.menus){
  			var menu = new qx.ui.menu.Menu();
  			var button = new qx.ui.menubar.Button(key, null, menu);
  			for(var i=0; i<this.menus[key].length;i++){
  				menu.add(this.menus[key][i].getMenuButton());
  			}
  			menuBar.add(button);
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
  			var command = definition.command;
  			contextMenu.add(command.getMenuButton());
  		}
  		return contextMenu;
  	},
  	
  	addCommands : function(definitions){
  		var crtDefs = this.getDefinitions();  		
  		for(var key in definitions){
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