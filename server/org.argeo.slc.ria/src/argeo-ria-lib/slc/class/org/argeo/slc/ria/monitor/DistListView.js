qx.Class.define("org.argeo.slc.ria.monitor.DistListView", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 
	
	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
				"reload" : {
					label : "Reload",
					icon : "org/argeo/slc/ria/view-refresh.png",
					shortcut : "control+h",
					enabled : true,
					menu : "Distributions",
					toolbar : "list",
					callback : function(e) {
						this.load();
					},
					command : null
				},
				"uninstall" : {
					label 	: "Uninstall",
					icon	: "org/argeo/slc/ria/window-close.png",
					shortcut: null,
					enabled : false,
					menu	: "Distributions",
					toolbar	: "list",
					callback: function(e){
						var selection = this.getViewSelection();
						var node = selection.getNodes()[0];
						var request = org.argeo.slc.ria.SlcApi.getUninstallModuleService(node[0], node[1]);
						request.addListener("completed", this.load, this);
						request.send();
					},
					selectionChange : function(viewId, selection){
						if(viewId != "distrib") return;
						this.setEnabled((selection!=null && selection.length==1));
					},
					command	: null
				}
			}
		},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
		view : {
			init : null
		},		  	
	  	instanceId : {init:""},
	  	instanceLabel : {init:""}
	},

	construct : function(){
		this.base(arguments);
		this.setLayout(new qx.ui.layout.Canvas());	
		this.xmlStub = '<slc:object-list xmlns:slc="http://argeo.org/projects/slc/schemas">' +
				'<slc:modular-distribution-descriptor name="name" version="0.1.0">' +
				'<slc:modulesDescriptors><slc:modulesDescriptor type="modularDistribution" url="http://localhost/modularDistribution" >toto</slc:modulesDescriptor>' +
				'<slc:modulesDescriptor type="eclipse" url="http://localhost/updateSite" />' +
				'</slc:modulesDescriptors></slc:modular-distribution-descriptor>' +
				'<slc:modular-distribution-descriptor name="name2" version="0.1.1">' +
				'<slc:modulesDescriptors><slc:modulesDescriptor type="modularDistribution" url="http://localhost/modularDistribution2" />' +
				'<slc:modulesDescriptor type="eclipse" url="http://localhost/updateSite2" />' +
				'</slc:modulesDescriptors></slc:modular-distribution-descriptor>' +
				'</slc:object-list>';
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
		init : function(viewPane, data){
			this.setView(viewPane);			
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));
			this.tableModel = new qx.ui.table.model.Simple();			
			this.tableModel.setColumns(["Name", "Version", "Modular Distribution", "Eclipse Update Site"], ["name","version","modularDistribution","eclipse"]);
			this.list = new qx.ui.table.Table(this.tableModel, {
			  	tableColumnModel: function(obj){
					return new qx.ui.table.columnmodel.Resize(obj)
				}				
			});
			this.list.set({
				decorator:null,
				statusBarVisible : false
			});
			this.tableModel.setColumnEditable(2, true);
			this.tableModel.setColumnEditable(3, true);
			var columnModel = this.list.getTableColumnModel();
			columnModel.getBehavior().setWidth(0, "15%");
			columnModel.getBehavior().setWidth(1, "15%");
			columnModel.getBehavior().setWidth(2, "35%");
			columnModel.getBehavior().setWidth(3, "35%");
			var factory = function(cellInfo){
				var tField = new qx.ui.table.celleditor.TextField();
				tField.setValidationFunction(function(newValue, oldValue){return oldValue;});
				return tField;
			};
			columnModel.setCellEditorFactory(2, new qx.ui.table.celleditor.Dynamic(factory));
			columnModel.setCellEditorFactory(3, new qx.ui.table.celleditor.Dynamic(factory));
			
			var selectionModel = this.list.getSelectionModel();
			selectionModel.addListener("changeSelection", function(e){
				var viewSelection = this.getViewSelection();
				viewSelection.clear();
				selectionModel.iterateSelection(function(index){
					viewSelection.addNode(this.tableModel.getRowData(index));
				}, this);
			}, this);			
			
			this.add(this.list, {top:0,left:0,width:'100%',height:'100%'});			
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			this.tableModel.setData([]);
			var req = org.argeo.slc.ria.SlcApi.getListModularDistributionsService();
			req.addListener("completed", function(response){
				this.parseData(response.getContent());
			}, this);
			req.addListener("failed", function(){
				var xmlDoc = qx.xml.Document.fromString(this.xmlStub);
				this.parseData(xmlDoc);							
			}, this);
			req.send();			
		},
		
		parseData : function(xmlDoc){
			var data = [];
			var descriptorPath = 'slc:object-list/slc:modular-distribution-descriptor';
			var namePath = '@name';
			var versionPath = '@version';
			var distribPath = 'slc:modulesDescriptors/slc:modulesDescriptor[@type="modularDistribution"]/@url';
			var eclipsePath = 'slc:modulesDescriptors/slc:modulesDescriptor[@type="eclipse"]/@url';
			var nodes = org.argeo.ria.util.Element.selectNodes(xmlDoc, descriptorPath);			
			for(var i=0;i<nodes.length;i++){
				var name = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], namePath);
				var version = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], versionPath);
				var distrib = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], distribPath);
				var eclipse = org.argeo.ria.util.Element.getSingleNodeText(nodes[i], eclipsePath);
				data.push([name,version,distrib,eclipse]);
			}
			this.tableModel.setData(data);
		},
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){return true;},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){return true;}
	}
});