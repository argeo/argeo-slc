/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2007 OpenHex SPRL, http://www.openhex.org

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Dirk Wellmann (dw(at)piponline.net)

************************************************************************ */

/**
 * This Cellrender is for test status use. It create green or red background
 * cells depending on the status value (PASSED or FAILED).
 */
qx.Class.define("org.argeo.slc.web.StatusCellRenderer",
{
  extend : qx.ui.table.cellrenderer.Html,

  construct : function(){
  	this.base(arguments);
	      var style =
	      [
	        ".slc-status-passed{background-color:#8fc98f;color:#008300;border-bottom:1px solid #cccccc;text-align:center;filter:alpha(opacity=80);opacity: 0.8;-moz-opacity:0.8;}",
	        ".slc-status-failed{background-color:#cb8f8f;color:#830000;border-bottom:1px solid #cccccc;text-align:center;filter:alpha(opacity=80);opacity: 0.8;-moz-opacity:0.8;}"
	      ];	
	      // Include stylesheet
	      qx.bom.Stylesheet.createElement(style.join(""));
  },
  
  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getContentHtml : function(cellInfo) {
      return (cellInfo.value || "");
    },

    // overridden
    _getCellClass : function(cellInfo) {
    	if(cellInfo.value == "PASSED"){
	      return "qooxdoo-table-cell slc-status-passed";
    	}else if(cellInfo.value == "FAILED"){
    	  return "qooxdoo-table-cell slc-status-failed";
    	}else{
    	  return "qooxdoo-table-cell";
    	}
    }
  }
});
