qx.Class.define("org.argeo.jcr.ria.views.QueriesView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 

	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
				"refresh_query" : {
					label : "Refresh",
					icon : "org.argeo.slc.ria/media-playback-start.png",
					shortcut : null,
					enabled : true,
					menu : "Query",
					toolbar : null,
					callback : function(e) {
						var selection = this.tree.getSelection();
						if(!selection.length) return;
						var treeNode = selection[0];
						this._submitQuery(treeNode);
					},
					selectionChange : function(viewId, selection){
						this.setEnabled(false);
						if(selection && selection.length && !selection[0].getJcrNode){
							this.setEnabled(true);
						}
					}
				},
				"remove_query" : {
					label : "Remove",
					icon : "org.argeo.slc.ria/media-playback-start.png",
					shortcut : null,
					enabled : true,
					menu : "Query",
					toolbar : null,
					callback : function(e) {
						var selection = this.tree.getSelection();
						if(!selection.length) return;
						var treeNode = selection[0];
						treeNode.getParent().remove(treeNode);
					},
					selectionChange : function(viewId, selection){
						this.setEnabled(false);
						if(selection && selection.length && !selection[0].getJcrNode){
							this.setEnabled(true);
						}
					}
				}												
			}
		},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
	  	instanceId : {
	  		init:"queriesView",
	  		event : "changeInstanceId"
	  	},
	  	instanceLabel : {
	  		init:"Queries",
	  		event : "changeInstanceLabel"
	  	},
	  	dataModel : {
	  		
	  	}
	},
	
	construct : function(){
		this.base(arguments);
	},
	
	members : {
		/**
		 * The implementation should contain the GUI initialisation.
		 * This is the role of the manager to actually add the graphical component to the pane, 
		 * so it's not necessary to do it here. 
		 * @param viewPane {org.argeo.ria.components.ViewPane} The pane manager
		 * @param data {Mixed} Any object or data passed by the initiator of the view
		 * @return {Boolean}
		 */
		init : function(viewPane, dataModel){
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  			
			this.setLayout(new qx.ui.layout.VBox());
			this.setDataModel(dataModel);
			
			this.radio = new qx.ui.form.RadioButtonGroup(new qx.ui.layout.HBox(5));
			var xPath = new qx.ui.form.RadioButton("XPath");
			xPath.setModel("xpath");
			var sql = new qx.ui.form.RadioButton("SQL");
			sql.setModel("sql");
			this.radio.add(xPath);
			this.radio.add(sql);
			
			var topLayout = new qx.ui.container.Composite(new qx.ui.layout.HBox(5));
			topLayout.add(new qx.ui.basic.Label("Query (Ctrl+Enter to submit):"));
			topLayout.add(new qx.ui.core.Spacer(), {flex:1});
			topLayout.add(this.radio);
			topLayout.setPadding(0,2,2,2);
			this.add(topLayout);
			
			this.textarea = new qx.ui.form.TextArea();
			this.textarea.setHeight(60);
			this.textarea.setMarginBottom(5);
			this.add(this.textarea);
			this.textarea.addListener("keypress", function(e){
				if(e.getKeyIdentifier() == "Enter" && e.isCtrlOrCommandPressed()){
					this._submitQuery();
				}
			}, this);
			
			
			var resLabel = new qx.ui.basic.Label("Results");
			resLabel.setPadding(0, 2, 2, 2);
			this.add(resLabel);
			
			this.tree = new qx.ui.tree.Tree();			
			this.add(this.tree, {flex:1});
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			var dataModel = this.getDataModel();
			
			this.treeBase = new qx.ui.tree.TreeFolder("Queries");
			this.tree.setRoot(this.treeBase);
			this.tree.setHideRoot(true);
			this.treeBase.setOpen(true);
			
			this.tree.addListener("changeSelection", function(e){
				var sel = this.tree.getSelection();
				var selection = [];
				var viewSelection = this.getViewSelection();
				viewSelection.clear();				
				for(var i=0;i<sel.length;i++){
					if(sel[i].getJcrNode){
						selection.push(sel[i].getJcrNode());
					}
					viewSelection.addNode(sel[i]);
				}
				this.getDataModel().setSelectionWithSource(selection, this);
			}, this);
			dataModel.addListener("changeSelection", function(e){
				if(this.getDataModel().getSelectionSource() == this) return;
				var selection = this.getDataModel().getSelection();
				// Arbitrary : for the moment, external select can only apply
				// to children of the current selection
				var crtSel = this.tree.getSelection();
				if(!crtSel.length || !selection.length) return;
				var crtTreeSel = crtSel[0];
				if(!crtTreeSel.getJcrNode) return;
				if(selection[0].getParent() && crtTreeSel.getJcrNode().getPath() == selection[0].getParent().getPath()){
					crtTreeSel.setOpen(true);
					var crtChildren =crtTreeSel.getChildren(); 
					for(var i=0;i<crtChildren.length;i++){
						if(crtChildren[i].getJcrNode().getPath() == selection[0].getPath()){
							this.tree.setSelection([crtChildren[i]]);
							return;
						}
					}
				}else if(crtTreeSel.getJcrNode().getParent() && crtTreeSel.getJcrNode().getParent().getPath() == selection[0].getPath()){
					this.tree.setSelection([crtTreeSel.getParent()]);
				}
				
			}, this);
			this.tree.setContextMenu(org.argeo.ria.event.CommandsManager
					.getInstance().createMenuFromIds(["open", "refresh_query", "remove_query"]));
					
		},
		
		_submitQuery : function(existingQueryFolder){
			var query;
			var language;
			if(existingQueryFolder){
				var model = existingQueryFolder.getModel();
				query = model.query;
				language = model.language;
				existingQueryFolder.removeAll();
			}else{
				query = this.textarea.getValue();
				language = this.radio.getModelSelection()[0];
			}
			var src = "/org.argeo.slc.webapp/queryJcrNodes.jcr?language="+language+"&statement="+query;
			var conn = new org.argeo.ria.remote.Request(src, "GET", "application/json");
			conn.addListener("completed", function(response){
				var json = response.getContent();
				this._addQueryResult(language, query, json, existingQueryFolder);
			}, this);
			conn.send();
		},
		
		_addQueryResult : function(language, query, results, queryFolder){
			
			var label = (language=="xpath"?"XPath":"SQL") + " query : '"+query+"' ("+results.length+")";
			if(queryFolder){
				queryFolder.setLabel(label);
				var treeQuery = queryFolder;
			}else{
				var treeQuery = new qx.ui.tree.TreeFolder(label);
				treeQuery.setModel({language:language, query:query});
				this.treeBase.add(treeQuery);
			}
			var realRoot = this.getDataModel().getRootNode();
			var provider = realRoot.getNodeProvider();
			for(var i=0;i<results.length;i++){
				var child = new org.argeo.jcr.ria.model.Node(results[i], provider, true);
				child.setPath(results[i]);
				var childTree = new org.argeo.jcr.ria.views.JcrTreeFolder(child);
				treeQuery.add(childTree);
			}			
		},
				
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){
			return false;
		},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){
			
		}		
		
	}
});