/**
 * Basic IView implementation for displaying the test results list, by collection.
 * 
 * This component creates a Table object and feed it with the results. It adds a collection chooser to its viewPane header.
 *  
 * It creates the following commands : "loadtestlist", "polllistloading", "opentest", "download", "copytocollection", "deletetest".
 */
qx.Class.define("org.argeo.slc.web.TestList",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 
  include : [org.argeo.ria.session.MPrefHolder],

  statics : {
  	riaPreferences : {
  		"slc.web.TestList.DefaultAction" : {
  			label : "Test List : Double Click default action",
  			type : "list",
  			list : ["Open","Download Xls"],
  			defaultValue : "Open"
  		}
  	}
  },
  
  construct : function(){
  	this.base(arguments, new qx.ui.layout.VBox());  	
  	var model = new qx.ui.table.model.Simple();
  	model.setColumns(["Test Case", "Date"]);
  	this.table = new qx.ui.table.Table(model, {
	  	tableColumnModel: function(obj){
			return new qx.ui.table.columnmodel.Resize(obj)
		}
	});
  },

  properties : 
  {
  	/**
  	 * The viewPane containing this applet.
  	 */
  	view : {
  		init : null
  	},
  	viewSelection : {
  		nullable:false, 
  		check:"org.argeo.ria.components.ViewSelection"
  	},  
  	instanceId : {init:""},
  	instanceLabel : {init:""},
  	/**
  	 * The load list of available collection (Map of ids => labels)
  	 */
  	collectionList : {
  		init : {},
  		check : "Map"
  	},
  	/**
  	 * The current collection id selected.
  	 */
  	collectionId:{
  		init : 'My Collection',
  		check : "String"
  	},
  	currentOpenInstanceId : {
  		check : "String",
  		nullable:true
  	},
  	/**
  	 * The applet commands.
  	 */
  	commands : {
  		init : {
  			"loadtestlist" : {
  				label		: "Load Collection", 
  				icon 		: "org.argeo.slc.ria/view-refresh.png",
  				shortcut 	: "Control+l",
  				enabled  	: true,
  				menu	   	: "Collection",
  				toolbar  	: "collection",
  				callback 	: function(e){
  					this.loadList();
  				}, 
  				command 	: null
  			},
  			"polllistloading" : {
  				label		: "Auto load", 
  				icon 		: "org.argeo.slc.ria/document-open-recent.png",
  				shortcut 	: "Control+l",
  				enabled  	: true,
  				toggle		: true,
  				menu	   	: "Collection",
  				toolbar  	: "collection",
  				callback 	: function(event){
  					this.pollListLoading(event.getTarget().getUserData("slc.command.toggleState"));
  				}, 
  				command 	: null
  			},
  			/*
  			"copyfullcollection" : {
  				label	 	: "Copy to...", 
  				icon 		: "org.argeo.slc.ria/edit-copy.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Collection",
  				toolbar  	: "collection",
  				callback	: function(e){
  					// Call service to copy
  				},
  				submenu 	: {},
  				submenuCallback : function(commandId){
  					this.copySelectionToCollection(commandId, "current_collection");
  				},
  				init : function(){
  					// Call at command creation
  					org.argeo.ria.remote.RequestManager.getInstance().addListener("reload", function(event){
  						if(event.getDataType() == "collection" || event.getDataType() == "test_cases"){
	  						var testList = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("list").getContent();
	  						testList.collectionListToMenu(this);
  						}
  					}, this);
  				},
  				command 	: null
  			},
  			*/
  			"opentest" : {
  				label	 	: "Open", 
  				icon 		: "org.argeo.slc.ria/media-playback-start.png",
  				shortcut 	: "Control+o",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){
  					var viewsManager = org.argeo.ria.components.ViewsManager.getInstance();
  					var classObj = org.argeo.slc.ria.Applet;
  					var xmlNodes = viewsManager.getViewPaneSelection("list").getNodes();
					var iView = viewsManager.initIViewClass(classObj, "applet", xmlNodes[0]);
					this.setCurrentOpenInstanceId(iView.getInstanceId());
					iView.load(xmlNodes[0]);
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId != "list") return;
  					this.setEnabled(false);
  					if(xmlNodes == null || !xmlNodes.length) return;
					this.setEnabled(true);
  				},
  				command 	: null
  			},
  			"download" : {
  				label	 	: "Download as...", 
  				icon 		: "org.argeo.slc.ria/go-down.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){ },
  				command 	: null,
  				submenu 	: [
  						{'label':"Xsl", 'icon':'org.argeo.slc.ria/mime-xsl.png', 'commandId':'xsl'},
  						{'label':"Xml", 'icon':'org.argeo.slc.ria/mime-xml.png', 'commandId':'xml'},
  						{'label':"Excel", 'icon':'org.argeo.slc.ria/mime-xls.png', 'commandId':'xls'},
  						{'label':"Pdf", 'icon':'org.argeo.slc.ria/mime-pdf.png', 'commandId':'pdf'}
  					],
  				submenuCallback : function(commandId){
  					var uuid = this.extractTestUuid();
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
  					//this.clearMenus();
  					this.setEnabled(false);
  					if(xmlNodes == null || !xmlNodes.length) return;
  					this.setEnabled(true);
  				}
  			},
  			"attachments" : {
  				label	 	: "Attachments", 
  				icon 		: "org.argeo.slc.ria/document-save-as.png",
  				shortcut 	: null,
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){ },
  				command 	: null,
  				submenu 	: [],
  				submenuCallback : function(commandId){  					
  					var split = commandId.split("__commandseparator__");
  					var uuid = split[0];
  					var contentType = split[1];
  					var name = split[2];
  					var url = org.argeo.slc.ria.SlcApi.buildGetAttachmentUrl(uuid, contentType, name);
  					var win = window.open(url);
  				},
  				selectionChange : function(viewId, xmlNodes){
  					if(viewId!="list")return;
  					this.clearMenus();
  					this.setEnabled(false);
  					if(xmlNodes == null || !xmlNodes.length || xmlNodes.length != 1) return;
  					// Check slc:simple-attachment tags
  					var attachs = org.argeo.ria.util.Element.selectNodes(xmlNodes[0], "slc:attachments/slc:simple-attachment");
  					if(attachs && attachs.length){
  						var submenus = [];
  						for(var i=0;i<attachs.length;i++){
  							var uuid = org.argeo.ria.util.Element.getSingleNodeText(attachs[i], "slc:uuid");
  							var contentType = (org.argeo.ria.util.Element.getSingleNodeText(attachs[i], "slc:content-type")||'');
  							var name = org.argeo.ria.util.Element.getSingleNodeText(attachs[i], "slc:name");
  							submenus.push({
  								label:name, 
  								icon : 'org.argeo.slc.ria/mime-text-plain.png', 
  								commandId:uuid+'__commandseparator__'+contentType+'__commandseparator__'+name
  							});
  						}
  						this.setMenu(submenus);
	  					this.setEnabled(true);
  					}
  				}
  			},
  			"copytocollection" : {
  				label	 	: "Copy to...", 
  				icon 		: "org.argeo.slc.ria/edit-copy.png",
  				shortcut 	: "Control+c",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){
  					// Call service to copy
  				},
  				submenu 	: {},
  				submenuCallback : function(commandId){
					this.copySelectionToCollection(commandId, "current_selection");  				
				},
  				init : function(){
  					// Call at command creation
  					org.argeo.ria.remote.RequestManager.getInstance().addListener("reload", function(event){
  						if(event.getDataType() == "collection" || event.getDataType() == "test_cases"){
	  						var testList = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("list").getContent();
	  						testList.collectionListToMenu(this, true);
  						}
  					}, this);
  				},
	  			selectionChange : function(viewId, xmlNodes){
					if(viewId != "list") return;
					this.setEnabled(false);
					if(xmlNodes == null || !xmlNodes.length) return;
					this.setEnabled(true);  						
	  			},
  				command 	: null
  			},
  			"deletetest" : {
  				label	 	: "Delete", 
  				icon 		: "org.argeo.slc.ria/edit-delete.png",
  				shortcut 	: "Control+d",
  				enabled  	: false,
  				menu	   	: "Selection",
  				toolbar  	: "selection",
  				callback	: function(e){
  					var modal = new org.argeo.ria.components.Modal("Confirm", null);
  					var testUuid = this.extractTestUuid();
  					modal.addConfirm("Are you sure you want to delete<br> test " + testUuid + "?");
  					modal.addListener("ok", function(){
	  					var request = org.argeo.slc.ria.SlcApi.getRemoveResultService(this.getCollectionId(), this.extractTestUuid());
						request.addListener("completed", function(response){
							if(this.getCurrentOpenInstanceId() == "test:"+testUuid){
								var appletView = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("applet");
								appletView.closeCurrent();
								this.setCurrentOpenInstanceId(null);
							}
							this.loadCollections();
							this.loadList();
							this.info("Test was successfully deleted");
						}, this);
						request.send();  					
  					}, this);
  					modal.attachAndShow();
  				},
	  			selectionChange : function(viewId, xmlNodes){
					if(viewId != "list") return;
					this.setEnabled(false);
					if(xmlNodes == null || !xmlNodes.length) return;
					this.setEnabled(true);  						
	  			},
  				command 	: null
  			}
  		}
	}
  },
  
  members : {
	init : function(viewPane, data){
		this.setView(viewPane);
		this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  
	},
	load : function(){
	  this.table.set({	  	
	  	statusBarVisible: false,
		showCellFocusIndicator:false,
		columnVisibilityButtonVisible:false,
		contextMenu : org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["opentest", "download", "copytocollection", "deletetest"]),
		decorator : new qx.ui.decoration.Background("#fff")
	  });
	  var viewPane = this.getView();
	  this.table.addListener("dblclick", function(e){
	  	var pref = this.getRiaPreferenceValue("slc.web.TestList.DefaultAction");
	  	if(pref == "Open"){
			org.argeo.ria.event.CommandsManager.getInstance().executeCommand("opentest");
	  	}else{
	  		var uuid = this.extractTestUuid();
			var url = "../resultView.xslt?uuid="+uuid;
			alert('Should download : '+url);
	  	}
	  }, this);
	  var columnModel = this.table.getTableColumnModel(); 
	  columnModel.getBehavior().setWidth(0, "60%");
	  var renderer = new org.argeo.slc.web.XmlRenderer();
	  columnModel.setDataCellRenderer(0, renderer);
	  columnModel.setDataCellRenderer(1, renderer);
	  
	  this.table.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
	  	var viewSelection = this.getViewSelection();
	  	viewSelection.clear();
	  	var selectionModel = this.table.getSelectionManager().getSelectionModel();
	  	if(!selectionModel.getSelectedCount()){
	  		return;
	  	}
	  	var ranges = this.table.getSelectionManager().getSelectionModel().getSelectedRanges();
	  	var xmlNode = this.table.getTableModel().getRowData(ranges[0].minIndex);
	  	viewSelection.addNode(xmlNode);
	  	//viewPane.setViewSelection(viewSelection);
	  }, this);		
	  
	  var select = new qx.ui.form.SelectBox();
	  this.getView().addHeaderComponent(select);
	  this.getView().setViewTitle("");
	  this.add(this.table, {flex:1});

  	  select.addListener("changeValue", this.collectionSelectorListener, this);

	  org.argeo.ria.remote.RequestManager.getInstance().addListener("reload", function(event){
	  	 if(event.getDataType()!="collection") return;
	  	 select.removeListener("changeValue", this.collectionSelectorListener, this);
	  	 var collectionList = event.getContent();
	  	 select.removeAll();
	  	 for(key in collectionList){
	  	 	var item = new qx.ui.form.ListItem(collectionList[key], "org.argeo.slc.ria/folder.png", key);
	  	 	select.add(item);
	  	 	if(key == this.getCollectionId()){
	  	 		select.setSelected(item);
	  	 	}
	  	 }
	  	 if(qx.lang.Object.getLength(collectionList)){
		  	 this.setCollectionId(select.getSelected().getValue());
	  	 }
	  	 select.addListener("changeValue", this.collectionSelectorListener, this);
	  }, this);
	  	  
	},
	
	/**
	 * Use SlcApi to load the available collections.
	 */
	loadCollections : function(){
		var request = org.argeo.slc.ria.SlcApi.getListCollectionsService();
		var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
		request.addListener("completed", function(response){
			var xml = response.getContent();
			var collections = {};
			var nodes = org.argeo.ria.util.Element.selectNodes(xml, "//slc:ref", NSMap);
			for(var i=0;i<nodes.length;i++){
				var collId = qx.xml.Element.getSingleNodeText(nodes[i], ".");
				collections[collId] = collId;
			}
			this.setCollectionList(collections);
			org.argeo.ria.remote.RequestManager.getInstance().fireReloadEvent("collection", this.getCollectionList());
		}, this);
		request.setAsynchronous(false);
		request.send();
	},
	
	/**
	 * Load the results of the currently selected collection.
	 */
	loadList : function(){
	  	var model = this.table.getTableModel();
	  	model.removeRows(0, model.getRowCount());
	  	var commandManager = org.argeo.ria.event.CommandsManager.getInstance();
	  	var request = org.argeo.slc.ria.SlcApi.getListResultsService(this.getCollectionId(), null, [commandManager.getCommandById("loadtestlist"), this.getView()]);
	  	var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
	  	request.addListener("completed", function(response){
  			var xml = response.getContent();
	  		this.debug("Successfully loaded XML");
	  		var nodes = org.argeo.ria.util.Element.selectNodes(xml, "//slc:result-attributes", NSMap);
	  		//model.addRows(nodes);
	  		
	  		for(var i=0; i<nodes.length;i++){	  			
	  			model.addRows([nodes[i]]);
	  		}	  		
	  	}, request);
	  	request.send();		
	},
	
	/**
	 * Enable/disable the automatic reloading of the list.
	 * @param state {Boolean} Whether the automatic reloading must be started or stopped.
	 */
	pollListLoading : function(state){
		if(!this.timer){
			this.timer = new qx.event.Timer(5000);
			this.timer.addListener("interval", this.loadList, this);
		}
		if(state){
			this.loadList();
			this.timer.start();
		}else{
			this.timer.stop();
		}
	},
	
	/**
	 * Creates a menu gui component from the currently loaded collectionList.
	 * @param command {qx.event.Command} The command on which to attach the created menu. 
	 * @param checkSelection {Boolean} Whether at the end, we must check the current viewSelection to enable/disable the command accordingly. 
	 */
	collectionListToMenu : function(command, checkSelection){
		command.setEnabled(false);
		command.clearMenus();
		var collectionList = this.getCollectionList();
		if(!collectionList) return;
		var submenus = [];
		for(var key in collectionList){
			if(this.getCollectionId() && key == this.getCollectionId()) continue;
			submenus.push({
				"label":collectionList[key], 
				"icon":"org.argeo.slc.ria/folder.png", 
				"commandId":key
			});
		}		
		submenus.push({'separator':true});
		submenus.push({"label":"New...", "icon":"org.argeo.slc.ria/folder-new.png", "commandId":"slc.client.create"});
		command.setMenu(submenus);
		if(checkSelection){
			var viewSelection = this.getView().getViewSelection();
			if(viewSelection.getCount()) command.setEnabled(true);
		}else{
			command.setEnabled(true);
		}
	},
	/**
	 * Use SlcApi "addResult" service to add selected results to a given collection.
	 * If collectionId is "slc.client.create", first triggers a modal dialog to enter a new collection name, then retrigger itself with the new id.
	 * @param collectionId {String} The id of the destination collection, or "slc.client.create".
	 * @param selectionType {String} "current_collection"|"current_selection". The first adds the whole collection content to the destination, the second only selected results.
	 */
	copySelectionToCollection:function(collectionId, selectionType){
		if(collectionId == "slc.client.create"){
			var modal = new org.argeo.ria.components.Modal("Create collection", "org.argeo.slc.ria/folder-new.png");
			modal.makePromptForm("Enter the new collection name", function(value){
				if(value == ""){
					alert("Please enter a name for the new collection!");
					return false;
				}
				else {
					// Create the collection now, then recall the callback with the new name.  								
					this.copySelectionToCollection(value, selectionType);
					return true;
				}
			}, this);
			modal.attachAndShow();
			return;
		}

		var currentFocus = org.argeo.ria.components.ViewsManager.getInstance().getCurrentFocus();
		if(currentFocus.getViewId() == "applet"){
			alert("Should copy data from the applet - command was " + collectionId);
			return;
		}
		
		if(selectionType == "current_collection"){
			this.error("Not implemented yet!");			
		}else if(selectionType == "current_selection"){
			// get selection ID
			var request = org.argeo.slc.ria.SlcApi.getAddResultService(collectionId,this.extractTestUuid());
			request.addListener("completed", function(response){
				this.info("Result successfully copied to collection!");
				this.loadCollections();
			}, this);
			request.send();		
		}
	},
	
	/**
	 * Utilitary function to extract test unique id from the currently selected node.
	 * @return {String} The test unique id.
	 */
	extractTestUuid: function(){
		var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
		var xmlNodes = this.getView().getViewSelection().getNodes();
		var uuid = qx.dom.Node.getText(org.argeo.ria.util.Element.selectSingleNode(xmlNodes[0], "slc:uuid"));
		return uuid;
	},
	
	/**
	 * Listener of the collection selector (select box added to the viewpane header). 
	 * @param event {qx.event.type.Event} The event.
	 */
	 collectionSelectorListener : function(event){
	  	this.setCollectionId(event.getData());
	  	this.loadList();		
	},
	
	addScroll : function(){
		return false;
	},
	
	close : function(){
		if(this.timer){
			this.pollListLoading(false);
		}
	}
	
  }
});