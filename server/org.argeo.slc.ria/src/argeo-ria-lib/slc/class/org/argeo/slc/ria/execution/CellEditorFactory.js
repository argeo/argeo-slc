/*******************************************************************************
 * 
 * Argeo
 * 
 ******************************************************************************/

/**
 * A cell editor factory creating text fields or disabled text fields.
 * 
 */
qx.Class.define("org.argeo.slc.ria.execution.CellEditorFactory", 
{
  	extend : qx.ui.table.cellrenderer.Default,
	implement : qx.ui.table.ICellEditorFactory,

	/*
	 * ****************************************************************************
	 * CONSTRUCTOR
	 * ****************************************************************************
	 */

	construct : function() {
		this.base(arguments);
	},

	/*
	 * ****************************************************************************
	 * PROPERTIES
	 * ****************************************************************************
	 */

	properties : {

		/**
		 * function that validates the result the function will be called with
		 * the new value and the old value and is supposed to return the value
		 * that is set as the table value.
		 */
		validationFunction : {
			check : "Function",
			nullable : true,
			init : null
		}

	},

	/*
	 * ****************************************************************************
	 * MEMBERS
	 * ****************************************************************************
	 */
	members : {
				
	    // overridden
	    _getContentHtml : function(cellInfo) {
			var table = cellInfo.table;
			var tableModel = table.getTableModel();
			var rowData = tableModel.getRowData(cellInfo.row);
			var metaData = rowData[2];
			if (metaData.disabled) {
				return '<span style="color:#999;">' + qx.bom.String.escape(this._formatValue(cellInfo)) + '</span>';
			}else{
				return qx.bom.String.escape(this._formatValue(cellInfo));
			}
	    },
		
		
		// interface implementation
		createCellEditor : function(cellInfo) {
			var table = cellInfo.table;
			var tableModel = table.getTableModel();
			var rowData = tableModel.getRowData(cellInfo.row);
			var metaData = rowData[2];
			if (metaData.disabled) {
				return null; // var cellEditor = new
							 // qx.ui.table.celleditor.TextField();
			}
			if(metaData.type == "primitive"){
				var cellEditor = new qx.ui.form.TextField;
				cellEditor.setAppearance("table-editor-textfield");
				cellEditor.originalValue = cellInfo.value;			
				if (cellInfo.value === null) {
					cellInfo.value = "";
				}
				cellEditor.setValue("" + cellInfo.value);
	
				cellEditor.addListener("appear", function() {
					cellEditor.selectAllText();
				});
	
				var validationFunc;
				if (metaData.subType == "integer") {
					validationFunc = function(newValue, oldValue){
						var isNum = !isNaN(newValue * 1);
						if (!isNum) {
							alert("Warning, this field only accepts Integers!");
							return oldValue;
						}
						return newValue;
					};
				}
				cellEditor.setUserData("validationFunc", validationFunc);
			}else if(metaData.type == "ref"){
			      var cellEditor = new qx.ui.form.SelectBox().set({
			        appearance: "table-editor-selectbox"
			      });
			      cellEditor.setUserData("validationFunc", function(value, oldValue){
			      		if(value == "__empty__"){
			      			alert("Warning, value cannot be empty! You must select at least one option below.");
			      			return oldValue;
			      		}
			      		return value;
			      });
			
			      var value = cellInfo.value;
			      cellEditor.originalValue = value;
			      // replace null values
			      if ( value === null ) {
			        value = "";
			      }
				  if(value == ""){
				  	var li = new qx.ui.form.ListItem("");
				  	li.setModel("__empty__");
				  	cellEditor.add(li);
				  }
			      var list = metaData.refList;
			      if (list)
			      {
			        var item;
			
			        for (var i=0,l=list.length; i<l; i++)
			        {
			          var row = list[i];
			          if ( row instanceof Array ) {
			          	// Array [key, description] where description can be null
			            item = new qx.ui.form.ListItem(row[0]);
			            item.setModel(row[0]);
			            if(row[1]){
			            	item.setToolTip(new qx.ui.tooltip.ToolTip(row[1]));
			            }
			          } else {
			            item = new qx.ui.form.ListItem(row);
			            item.setModel(row);
			          }
			          cellEditor.add(item);
			          if(value == item.getModel()){
			          	cellEditor.setSelection([item]);
			          }
			        };
			      }
			
			      cellEditor.setModelSelection(["" + value]);
			      cellEditor.addListener("appear", function() {
			        cellEditor.open();
			      });
			}
			
			return cellEditor;
		},
		
    	// interface implementation
		getCellEditorValue : function(cellEditor) {
			var value;
			if(cellEditor.classname == "qx.ui.form.TextField"){
				value = cellEditor.getValue();
			}else{
				var sel = cellEditor.getModelSelection();
				value = sel[0];
			}
			var validationFunc = cellEditor.getUserData("validationFunc");
			
			// validation function will be called with new and old value
			// var validationFunc = this.getValidationFunction();
			if (validationFunc) {
				value = validationFunc(value, cellEditor.originalValue);				
			}

			if (typeof cellEditor.originalValue == "number") {
				value = parseFloat(value);
			}

			return value;
		}		
	}
});
