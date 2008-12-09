/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.Applet",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	this.base(arguments);
	this.setLayout(new qx.ui.layout.VBox());
  	//this.passedStatus = "<div align=\"center\"><img src=\"resource/slc/dialog-ok.png\" height=\"16\" width=\"16\"></div>";
  	//this.failedStatus = "<div align=\"center\"><img src=\"resource/slc/flag.png\" height=\"16\" width=\"16\"></div>";
  	this.passedStatus = "PASSED";
  	this.failedStatus = "FAILED";
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
  					//org.argeo.slc.web.components.ViewsManager.getInstance().getViewPaneById("applet").empty();
  					this.getView().empty();  					
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
  	init : function(viewPane){
  		this.setView(viewPane);
  	},
  	
  	load : function(xmlNode){
  		this.data = xmlNode;
  		if(!xmlNode) return;
  		// Load XML or Whatever
  		var service;  		
		var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
		var testId = qx.dom.Node.getText(org.argeo.ria.util.Element.selectSingleNode(this.data, "slc:uuid"));		
		this.getView().setViewTitle("Test "+testId);
  		var request = org.argeo.slc.web.SlcApi.getLoadResultService(testId);  		
  		request.addListener("completed", function(response){
			this.createXmlGui(response.getContent());
			this.getView().setOnLoad(false);
  		}, this);
  		this.getView().setOnLoad(true);
  		request.send();
  		
  	},
  	 
	addScroll : function(){
		return false;
	},
		
  	createXmlGui : function(responseXml){
  		var NSMap = {
  			"slc" : "http://argeo.org/projects/slc/schemas"
  		}
  		if(!qx.Class.hasMixin(qx.ui.treevirtual.TreeVirtual, qx.ui.treevirtual.MNode)){
			qx.Class.include(qx.ui.treevirtual.TreeVirtual,qx.ui.treevirtual.MNode);
  		}
  		this.tree = new qx.ui.treevirtual.TreeVirtual(["Test", "State", "Message", "Id"]);
  		this.tree.getTableColumnModel().setDataCellRenderer(0, new org.argeo.ria.util.TreeDataCellRenderer());
  		this.tree.getDataRowRenderer().setHighlightFocusRow(false); // Default row renderer
  		this.tree.setRowHeight(18);
  		this.tree.setStatusBarVisible(false);
  		this.tree.setDecorator(new qx.ui.decoration.Background("#fff"));
  		var model = this.tree.getDataModel();
  		var resNodes = org.argeo.ria.util.Element.selectNodes(responseXml, "//slc:result-part", NSMap);
  		window.result = responseXml;
  		var resultParts = {};
  		var addedPaths = {};
  		for(var i=0;i<resNodes.length;i++){
  			var currentParentId = null;
  			var node = resNodes[i];
  			var pathAttr = qx.xml.Element.getSingleNodeText(node, "@path");  			
			var pathParts = pathAttr.split("/");
			if(pathParts[0] == ""){
				pathParts.shift();
			}
			var crtPath = "";
			for(var j=0;j<pathParts.length;j++){
				//if(pathParts[j] == "") continue;
				crtPath = crtPath.concat("/", pathParts[j]);
				if(addedPaths[crtPath]) {
					currentParentId = addedPaths[crtPath];
					continue; // Node already exists, set as parent and go to next!
				}
				var element = org.argeo.ria.util.Element.selectSingleNode(responseXml, '//slc:element[@path="'+crtPath+'"]', NSMap);
				var label;
				if(element != null){
					label = org.argeo.ria.util.Element.getSingleNodeText(element, "*/slc:label", NSMap);
				}else{
					label = crtPath;
				}
				var simpleResults = org.argeo.ria.util.Element.selectNodes(node, "slc:part-sub-list/slc:parts/slc:simple-result-part", NSMap);

				var newId;
				//newId = model.addBranch(currentParentId, label, false);
				
				// Test Leaf Node
				if(!simpleResults || !simpleResults.length){
					newId = model.addBranch(currentParentId, label, false);
					addedPaths[crtPath] = newId;
					currentParentId = newId;
					continue;
				}
				if(simpleResults.length == 1){
					//newId = model.addBranch(currentParentId, label, false);
					var sResNode = simpleResults[0];
					newId = model.addBranch(currentParentId, label);
					model.setColumnData(newId, 3, org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:test-run-uuid", NSMap));
					model.setColumnData(newId, 2, org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:message", NSMap));
					var status = org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:status", NSMap);
					if(status != "PASSED"){
						status = this.failedStatus ;
						this._setParentBranchAsFailed(newId);
					}else{
						status = this.passedStatus;
					}
					model.setColumnData(newId, 1, status);											
					addedPaths[crtPath] = newId;
					currentParentId = newId;
					continue;
				}
				newId = model.addBranch(currentParentId, label, false);
				for(var k=0;k<simpleResults.length;k++){
					var sResNode = simpleResults[k];
					resId = model.addLeaf(currentParentId, label);
					model.setColumnData(resId, 3, org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:test-run-uuid", NSMap));
					model.setColumnData(resId, 2, org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:message", NSMap));
					var status = org.argeo.ria.util.Element.getSingleNodeText(sResNode, "slc:status", NSMap);
					if(status != "PASSED"){
						status = this.failedStatus ;
						this._setParentBranchAsFailed(resId);
					}else{
						status = this.passedStatus;
					}
					model.setColumnData(resId, 1, status);						
				}
				
				addedPaths[crtPath] = newId;
				currentParentId = newId;
			}
  		}
  		this._refineLeaves(this.tree, 0);
  		this.add(this.tree, {flex:1});
  		model.setData();
  		var columnModel = this.tree.getTableColumnModel();
  		var resize = columnModel.getBehavior();
  		resize.set(0, {width:250, minWidth:250});
  		resize.set(1, {width:55});
  		resize.set(2, {width:"1*"});
  		resize.set(3, {width:150});
  		columnModel.setDataCellRenderer(1, new org.argeo.slc.web.StatusCellRenderer());
  		
	    this.tree.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
			var viewSelection = this.getView().getViewSelection();
			viewSelection.clear();
			var nodes = this.tree.getDataModel().getSelectedNodes();
			if(nodes.length){
				viewSelection.addNode(nodes[0]);
				this.getView().setViewSelection(viewSelection);
			}
	  	}, this);
  		
	  	var contextMenu = org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["close"]);
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
  	
  	_refineLeaves : function(tree, nodeId){
  		var node = tree.nodeGet(nodeId);  		
  		if(node.children && node.children.length){
  			for(var i=0;i<node.children.length;i++){
  				this._refineLeaves(tree, node.children[i]);
  			}
  		}else{
  			node.type = qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF;
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