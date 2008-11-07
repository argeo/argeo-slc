/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.components.Applet",
{
  extend : qx.ui.container.Composite,

  construct : function(containerView){
  	this.base(arguments);
  	this.setView(containerView);
	this.setLayout(new qx.ui.layout.VBox());
  	this.passedStatus = "<div align=\"center\"><img src=\"resource/slc/dialog-ok.png\" height=\"16\" width=\"16\"></div>";
  	this.failedStatus = "<div align=\"center\"><img src=\"resource/slc/flag.png\" height=\"16\" width=\"16\"></div>";
  },

  properties : 
  {
  	view : {
  		init : null
  	},
  	commands : {
  		init : {
  			"close" : {
  				label	 	: "Close Result", 
  				icon 		: "resource/slc/window-close.png",
  				shortcut 	: "Control+w",
  				enabled  	: true,
  				menu	   	: "Test",
  				toolbar  	: "result",
  				callback	: function(e){
  					// Call service to delete
  					this.views["applet"].empty();
  				},
  				selectionChange : function(viewId, xmlNode){  					
  					if(viewId != "applet") return;
  					/*
  					if(xmlNode){
  						this.setEnabled(true);
  					}else{
  						this.setEnabled(false);
  					}
  					*/
  				},
  				command 	: null
  			}  			
  		}
  	}
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	initData : function(xmlNode){
  		this.data = xmlNode;
  		if(!xmlNode) return;
  		// Load XML or Whatever
  		//var testType = qx.dom.Node.getText(qx.xml.Element.selectSingleNode(this.data, "param[@name='testType']"));
  		var service;  		
		var testId = qx.dom.Node.getText(org.argeo.slc.web.util.Element.selectSingleNode(this.data, "param[@name='uuid']"));
		service = "../resultViewXml.xslt?uuid="+testId;
		var serviceManager = org.argeo.slc.web.util.RequestManager.getInstance();
  		var request = serviceManager.getRequest(service, "GET", "application/xml");
  		request.addListener("completed", function(response){
			this.createXmlGui(response.getContent());
			serviceManager.requestCompleted(request);
  		}, this);
  		request.send();
  	},
  	 
  	createXmlGui : function(responseXml){
  		var NSMap = {
  			"slc" : "http://argeo.org/projects/slc/schemas"
  		}
  		if(!qx.Class.hasMixin(qx.ui.treevirtual.TreeVirtual, qx.ui.treevirtual.MNode)){
			qx.Class.include(qx.ui.treevirtual.TreeVirtual,qx.ui.treevirtual.MNode);
  		}
  		this.tree = new qx.ui.treevirtual.TreeVirtual(["Test", "State", "Message", "Id"]);
  		this.tree.getTableColumnModel().setDataCellRenderer(0, new org.argeo.slc.web.util.TreeDataCellRenderer());
  		this.tree.setRowHeight(18);
  		this.tree.setStatusBarVisible(false);
  		this.tree.setDecorator(new qx.ui.decoration.Background("#fff"));
  		var model = this.tree.getDataModel();
  		var resNodes = org.argeo.slc.web.util.Element.selectNodes(responseXml, "//slc:result-part", NSMap);
  		window.result = responseXml;
  		var resultParts = {};
  		var addedPaths = {};
  		for(var i=0;i<resNodes.length;i++){
  			var currentParentId = null;
  			var node = resNodes[i];
  			var pathAttr = qx.xml.Element.getSingleNodeText(node, "@path");
			var pathParts = pathAttr.split("/");
			var crtPath = "";			
			for(var j=0;j<pathParts.length;j++){
				if(pathParts[j] == "") continue;
				crtPath = crtPath.concat("/", pathParts[j]);
				if(addedPaths[crtPath]) {
					currentParentId = addedPaths[crtPath];
					continue; // Node already exists, set as parent and go to next!
				}
				var element = org.argeo.slc.web.util.Element.selectSingleNode(responseXml, '//slc:element[@path="'+crtPath+'"]', NSMap);
				var label;
				if(element != null){
					label = org.argeo.slc.web.util.Element.getSingleNodeText(element, "*/slc:label", NSMap);
				}else{
					label = crtPath;
				}
				var newId;
				if(j < pathParts.length - 1){
					newId = model.addBranch(currentParentId, label, false);
					//model.setColumnData(newId, 1, this.passedStatus);
				}else{
					newId = model.addLeaf(currentParentId, label);
					model.setColumnData(newId, 3, org.argeo.slc.web.util.Element.getSingleNodeText(node, "slc:part-sub-list/slc:parts/slc:simple-result-part/slc:test-run-uuid", NSMap));
					model.setColumnData(newId, 2, org.argeo.slc.web.util.Element.getSingleNodeText(node, "slc:part-sub-list/slc:parts/slc:simple-result-part/slc:message", NSMap));
					var status = org.argeo.slc.web.util.Element.getSingleNodeText(node, "slc:part-sub-list/slc:parts/slc:simple-result-part/slc:status", NSMap);
					if(status != "PASSED"){
						status = this.failedStatus ;
						this._setParentBranchAsFailed(newId);
					}else{
						status = this.passedStatus;
					}
					model.setColumnData(newId, 1, status);
				}
				addedPaths[crtPath] = newId;
				currentParentId = newId;
			}
  		}
  		this.add(this.tree, {flex:1});
  		model.setData();
  		var columnModel = this.tree.getTableColumnModel();
  		var resize = columnModel.getBehavior();
  		resize.set(0, {width:250, minWidth:250});
  		resize.set(1, {width:40});
  		resize.set(2, {width:"1*"});
  		resize.set(3, {width:100});
  		columnModel.setDataCellRenderer(1, new qx.ui.table.cellrenderer.Html());
  		
	    this.tree.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
			var viewSelection = this.getView().getViewSelection();
			viewSelection.clear();
			var nodes = this.tree.getDataModel().getSelectedNodes();
			if(nodes.length){
				viewSelection.addNode(nodes[0]);
				this.getView().setViewSelection(viewSelection);
			}
	  	}, this);
  		
	  	var contextMenu = this.getView().getApplication().getCommandManager().createMenuFromIds(["close"]);
	  	this.tree.setContextMenu(contextMenu);
	  	
  	},
  	
  	_setParentBranchAsFailed : function(id){
  		var model = this.tree.getDataModel();
		while(id != null && id!=0){
			var node = this.tree.nodeGet(id);
			id = node.parentNodeId;
			if(id != null && id!=0){
				model.setColumnData(id, 1, this.failedStatus);
				this.tree.nodeSetOpened(id, true);
			}
		}  		
  	},
  	
  	createHtmlGui : function(responseText){
  		var htmlElement = new qx.ui.embed.Html(responseText);
  		htmlElement.setOverflowX("auto");
  		htmlElement.setOverflowY("auto");
  		this.add(htmlElement, {flex:1});
  	}
  	
  }
});