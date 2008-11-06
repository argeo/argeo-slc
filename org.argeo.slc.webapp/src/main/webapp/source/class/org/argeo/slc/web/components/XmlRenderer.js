qx.Class.define("org.argeo.slc.web.components.XmlRenderer",
{
  extend : qx.ui.table.cellrenderer.String,

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getContentHtml : function(cellInfo) {
    	var xmlNode = cellInfo.rowData;
    	var xPath;
    	switch(cellInfo.col){
    		case 0 :
    			xPath = "param[@name='testName']";
		    	var nameParam = qx.xml.Element.selectSingleNode(xmlNode, xPath);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || "Not Found");
    			break;
    		case 1 : 
    			xPath = "param[@name='date']";
		    	var nameParam = qx.xml.Element.selectSingleNode(xmlNode, xPath);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || 0);
		    	var splits = value.split(".");		    	
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
    _getCellClass : function(cellInfo) {
   		return this.base(arguments, cellInfo);
    }
    
  }
});