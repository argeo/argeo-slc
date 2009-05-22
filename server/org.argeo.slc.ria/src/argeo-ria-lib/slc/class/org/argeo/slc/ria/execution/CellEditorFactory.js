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

			var cellEditor = new qx.ui.form.TextField;
			cellEditor.setAppearance("table-editor-textfield");
			cellEditor.originalValue = cellInfo.value;			
			if (cellInfo.value === null) {
				cellInfo.value = "";
			}
			cellEditor.setValue("" + cellInfo.value);

			cellEditor.addListener("appear", function() {
				cellEditor.selectAll();
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
			
			return cellEditor;
		},
		
    	// interface implementation
		getCellEditorValue : function(cellEditor) {
			var value = cellEditor.getValue();
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
