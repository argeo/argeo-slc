/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.model.TestCase",
{
  extend : qx.core.Object,

  construct : function(xmlNode){
  	this.base(arguments);
  	this.setData(xmlNode);
  	if(xmlNode != null){
  		this.setLabel(this.getProperty('param[@name="testName"]'));
  	}
  },

  properties : 
  {
  	label : {init:""},
  	data : {nullable : true}
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	getProperty : function(xPath){
  		var xmlNode = this.getData();
  		return qx.dom.Node.getText(qx.xml.Element.selectSingleNode(xmlNode, xPath)) || "";
  	}
  }
});