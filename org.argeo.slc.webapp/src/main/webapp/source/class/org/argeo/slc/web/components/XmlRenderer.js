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
    	if(!xmlNode) return "";
    	var xPath;
    	switch(cellInfo.col){
    		case 0 :
    			xPath = "param[@name='testName']";
		    	var nameParam = org.argeo.slc.web.util.Element.selectSingleNode(xmlNode, xPath);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || "Not Found");
    			break;
    		case 1 : 
    			xPath = 'param[@name="date"]';
		    	var nameParam = org.argeo.slc.web.util.Element.selectSingleNode(xmlNode, xPath);
		    	//qx.log.Logger.info(nameParam);
		    	var value = qx.bom.String.escape(qx.dom.Node.getText(nameParam) || 0);
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
    _getCellClass : function(cellInfo) {
   		return this.base(arguments, cellInfo);
    }
    
  }
});