/**
 * Basic helper for SLC XML results to be displayed in a qx.ui.table.
 * Overrides the html of the standard qx.ui.table.cellrenderer.String
 *  
 * TODO : put in org.argeo.slc.ria package
 * 
 */
qx.Class.define("org.argeo.slc.web.XmlRenderer",
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
    	var NSMap = {slc:"http://argeo.org/projects/slc/schemas"};
    	switch(cellInfo.col){
    		case 0 :
    			xPath = "slc:uuid";
		    	var nameParam = org.argeo.ria.util.Element.selectSingleNode(xmlNode, xPath, NSMap);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || "Not Found");
    			break;
    		case 1 : 
    			xPath = "slc:close-date";
		    	var nameParam = org.argeo.ria.util.Element.selectSingleNode(xmlNode, xPath, NSMap);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || "NOT CLOSED");
		    	if(value == "NOT CLOSED") return value;
		    	var splits = value.split(".");
		    	value = splits[0].replace("T", " ");				
		    	var dateFormat = new qx.util.format.DateFormat("yyyy-MM-dd HH:mm:ss");
		    	try {
			    	var date = dateFormat.parse(value);
			    	var dateFormat2 = new qx.util.format.DateFormat("MMM d, yy HH:mm:ss");
			    	return dateFormat2.format(date);
		    	}catch(e){}
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