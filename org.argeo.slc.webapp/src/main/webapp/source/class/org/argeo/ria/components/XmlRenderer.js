/**
 * Basic helper for SLC XML results to be displayed in a qx.ui.table.
 * Overrides the html of the standard qx.ui.table.cellrenderer.String
 *  
 * TODO : put in org.argeo.slc.ria package
 * 
 */
qx.Class.define("org.argeo.ria.components.XmlRenderer",
{
  extend : qx.ui.table.cellrenderer.String,

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	/**
  	 * Overrides the parent method.
  	 * @param cellInfo {Map} The current cell data 
  	 * @return {String}
  	 */
    _getContentHtml : function(cellInfo) {
    	var xmlNode = cellInfo.rowData;
    	if(!xmlNode) return "";
    	var xPath;
    	switch(cellInfo.col){
    		case 0 :
    			xPath = "param[@name='testName']";
		    	var nameParam = org.argeo.ria.util.Element.selectSingleNode(xmlNode, xPath);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || "Not Found");
    			break;
    		case 1 : 
    			xPath = 'param[@name="date"]';
		    	var nameParam = org.argeo.ria.util.Element.selectSingleNode(xmlNode, xPath);
		    	//qx.log.Logger.info(nameParam);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || 0);
		    	if(value == "NOT CLOSED") return value;
		    	var splits = value.split(".");
				//qx.log.Logger.info(value);
		    	var dateFormat = new qx.util.format.DateFormat("yyyy-MM-dd HH:mm:ss");
		    	try {
			    	var date = dateFormat.parse(splits[0]);
			    	var dateFormat2 = new qx.util.format.DateFormat("MMM d, yy HH:mm:ss");
			    	return dateFormat2.format(date);
		    	}catch(e){
		    		qx.log.Logger.info(e);
		    	}
    			break;
    		default :
    			return "";
    			break;
    	}
    	
      	return value;
    },
    
    // overridden
    /**
     * Overrides parent method
     * @param cellInfo {Map} Current cell data
     * @return {String}
     */
    _getCellClass : function(cellInfo) {
   		return this.base(arguments, cellInfo);
    }
    
  }
});