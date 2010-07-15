/**
 * Applet for the batch manager
 */
qx.Class.define("org.argeo.slc.ria.SpecsEditorView",
{
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 

	properties : 
	{
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
				"updateData" : {
					label : "Edit Execution Specs",
					icon : "org/argeo/slc/ria/document-open.png",
					shortcut : null,
					enabled : false,
					menu : null,
					toolbar : null,
					callback : function(e) {},
					selectionChange : function(viewId, selection) {
						if (viewId != "batch:list")
							return;
						var view = org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("editor").getContent();
						if ((selection && selection.length == 1)) {
							view.setBatchEntrySpec(selection[0].getUserData("batchEntrySpec"));
						}else{
							view.setBatchEntrySpec(null);
						}
					},
					command : null					
				}
			}
		},
		view : {
			init : null
		},		
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
	  	instanceId : {init:""},
	  	instanceLabel : {init:""},
	  	batchEntrySpec : {
	  		init : null, 
	  		nullable:true,
	  		event : "changeBatchEntrySpec"
	  	}
	  	
	},
	  
	construct : function(){
		this.base(arguments);
		this.setLayout(new qx.ui.layout.Dock());	
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
						
			this.addListener("changeBatchEntrySpec", this.updateData, this);
			this._emptyTitleString = "Script Parameters (select a script to edit)";
			this._editorTitleString = "Script '%1' Parameters";
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			this._createLayout();
			this.getView().setViewTitle(this._emptyTitleString);
		},
		
		/**
		 * Update the table
		 * @param event {qx.event.type.DataEvent}
		 */
		updateData : function(event){
			var batchEntry = event.getData();
			if(batchEntry == null){
				this.tableModel.setData([]);
				this.getView().setViewTitle(this._emptyTitleString);
				return;
			}
			this.getView().setViewTitle(qx.lang.String.format(this._editorTitleString, [batchEntry.getFlow().getName()]));
			var values = batchEntry.getValues();
			var data = [];
			for(var key in values){
				var valueObj = values[key];
				var hidden = valueObj.getHidden();
				var type = valueObj.getSpecType();
				if((type == "primitive" || type== "ref")&& !hidden){
					metadata = {
						key : key,
						disabled : valueObj.getFrozen(),
						type : type,
						subType : valueObj.getSpecSubType(),
						refList : (type=="ref"?valueObj.getRefList():[])
					}
					data.push([key, valueObj.getValue(), metadata]);
				}
			}			
			this.tableModel.setData(data);
		},
		
		/**
		 * Creates the main applet layout.
		 */
		_createLayout : function() {
			this.tableModel = new qx.ui.table.model.Simple();			
			this.tableModel.setColumns(["Attribute Name", "Value"]);
			this.tableModel.setColumnEditable(1, true);
			this.table = new qx.ui.table.Table(this.tableModel, {
			  	tableColumnModel: function(obj){
					return new qx.ui.table.columnmodel.Resize(obj)
				}
			});
			this.table.set({
				decorator : null,
			  	statusBarVisible: false,
				showCellFocusIndicator:true,
				columnVisibilityButtonVisible : false
			});
			
			this.table.addListener("dataEdited", function(e){
				var data = e.getData();
				var rowData = this.tableModel.getRowData(data.row);
				var metaData = rowData[2];
				var values = this.getBatchEntrySpec().getValues();
				values[metaData.key].setValue(data.value);
			}, this);
			
			var columnModel = this.table.getTableColumnModel();
			var factory = new org.argeo.slc.ria.execution.CellEditorFactory();
			columnModel.setCellEditorFactory(1, factory);
			columnModel.setDataCellRenderer(1, factory);
			columnModel.getBehavior().setWidth(0, "40%");
			this.add(this.table, {edge:"center"});
		},		
		
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){return false;},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){return true;}
	}
  

});