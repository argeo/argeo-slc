/**
 * The canonical SLC applet for test result viewing. It will display a TreeTestResult in a TreeVirtual component
 * composed of four columns : test name, state (passed/failed/error), message and date.
 * 
 * It makes use of the StatusCellRenderer class for the "state" cell being a background-colored cell, the color depending on the FAILED or PASSED state message. 
 * The only associated command is the "Close" command.
 */
qx.Class.define("org.argeo.slc.ria.Applet",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	this.base(arguments);
	this.setLayout(new qx.ui.layout.VBox());
  	this.passedStatus = "PASSED";
  	this.failedStatus = "FAILED";
  	this.loaded = false;
  },

  properties : 
  {
  	/**
  	 * The viewPane inside which this applet is added. 
  	 */
  	view : {
  		init : null
  	},
  	viewSelection : {
  		nullable:false, 
  		check:"org.argeo.ria.components.ViewSelection"
  	},  
  	instanceId : {
  		init : ""
  	},
  	instanceLabel : {
  		init : ""
  	},
  	/**
  	 * Commands definition, see {@link org.argeo.ria.event.CommandsManager#definitions} 
  	 */
  	commands : {
  		init : {
  			"close" : {
  				label	 	: "Close Current", 
  				icon 		: "resource/slc/window-close.png",
  				shortcut 	: "Control+w",
  				enabled  	: true,
  				menu	   	: "Test",
  				toolbar  	: "result",
  				callback	: function(e){
  					this.getView().closeCurrent();
  					//this.getView().empty();
  					
  				},
  				command 	: null
  			}
  			/*, 		
  			"copytocollection" : {
  				shared 		: true,
  				selectionChange : function(viewId, xmlNode){
  					if(viewId != "applet") return;
  					if(!xmlNode){
  						this.setEnabled(false);
  					}else{
  						this.setEnabled(true);
  					}  					
  				}  				
  			},
  			"deletetest" : {
  				shared 		: true,
  				callback	: function(e){
  					//this.debug(this);
  					alert("Should delete applet selection in " + this.getInstanceId());
  				},
  				selectionChange : function(viewId, xmlNode){
  					if(viewId != "applet") return;
  					if(!xmlNode){
  						this.setEnabled(false);
  					}else{
  						this.setEnabled(true);
  					}  					
  				}
  			}
  			*/
  		}
  	}
  },

  members :
  {
  	/**
  	 * Called at applet creation. Just registers viewPane.
  	 * @param viewPane {org.argeo.ria.components.ViewPane} The viewPane.
  	 */
  	init : function(viewPane, xmlNode){
  		this.setView(viewPane);
		this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));
  		this.data = xmlNode;
  		if(this.data){
			this.testId = org.argeo.ria.util.Element.getSingleNodeText(this.data, "slc:uuid");
			this.setInstanceId("test:"+this.testId);
			this.setInstanceLabel("Test " + this.testId.substring(0,4)+"...");
  		}
  	},
  	
  	/**
  	 * Load a given test : the data passed must be an XML node containing the test unique ID.
  	 * @param xmlNode {Element} The text xml description. 
  	 */
  	load : function(){
  		if(!this.data) return;
		if(this.loaded) return;
  		// Load XML or Whatever
  		var service;  		
		//this.testId = qx.dom.Node.getText(org.argeo.ria.util.Element.selectSingleNode(this.data, "slc:uuid"));		
		this.getView().setViewTitle("Test "+this.testId);
  		var request = org.argeo.slc.ria.SlcApi.getLoadResultService(this.testId);  		
  		request.addListener("completed", function(response){
			this.createXmlGui(response.getContent());
			this.getView().setOnLoad(false);
			this.loaded = true;
  		}, this);
  		this.getView().setOnLoad(true);
  		request.send();
  		
  	},
  	 
	addScroll : function(){
		return false;
	},
	
	close : function(){
		
	},
	
	/**
	 * Creates the GUI.
	 * @param responseXml {Document} The xml response of the "load" query.
	 */
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
  		columnModel.setDataCellRenderer(1, new org.argeo.slc.ria.StatusCellRenderer());
  		
	    this.tree.getSelectionManager().getSelectionModel().addListener("changeSelection", function(e){
			var viewSelection = this.getViewSelection();
			viewSelection.clear();
			var nodes = this.tree.getDataModel().getSelectedNodes();
			if(nodes.length){
				viewSelection.addNode(nodes[0]);
			}
			this.getView().focus();
	  	}, this);
  		
	  	var contextMenu = org.argeo.ria.event.CommandsManager.getInstance().createMenuFromIds(["close"]);
	  	this.tree.setContextMenu(contextMenu);
	  		  	
  	},
  	
  	/**
  	 * Goes up the parents recursively to set a whole tree branch in "failed" mode.
  	 * @param id {Integer} The id of the child node.
  	 */
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
  	
  	/**
  	 * Recursively make sur the last children are of qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF type.
  	 * 
  	 * @param tree {qx.ui.treevirtual.TreeVirtual} The main tree of the applet.
  	 * @param nodeId {Integer} Current node id. 
  	 */
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
  	
  	/**
  	 * Alternatively to the createXmlGui, create a simple HtmlElement and append the query responseText.
  	 * Not used but sample.
  	 * @param responseText {String} Html code to display.
  	 */
  	createHtmlGui : function(responseText){
  		var htmlElement = new qx.ui.embed.Html(responseText);
  		htmlElement.setOverflowX("auto");
  		htmlElement.setOverflowY("auto");
  		this.add(htmlElement, {flex:1});
  	}
  	
  }
});