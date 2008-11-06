/**
 * @author Charles
 */
qx.Class.define("org.argeo.slc.web.components.Applet",
{
  extend : qx.ui.container.Composite,

  construct : function(){
  	this.base(arguments);
	this.setLayout(new qx.ui.layout.Dock());
  },

  properties : 
  {
  },

  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
  	initData : function(xmlNode){
  		this.data = xmlNode;
  		// Load XML or Whatever
  		var testType = qx.dom.Node.getText(qx.xml.Element.selectSingleNode(this.data, "param[@name='testType']"));
  		var service;
  		if(testType == "qtp"){
	  		var testColl = qx.dom.Node.getText(qx.xml.Element.selectSingleNode(this.data, "param[@name='testCollection']"));
  			service = "/com.capco.sparta.web/spartaQtpView.xslt?collectionId="+testColl;
  		}else{
  			var testId = qx.dom.Node.getText(qx.xml.Element.selectSingleNode(this.data, "param[@name='uuid']"));
  			service = "/com.capco.sparta.web/spartaCsvView.xslt?uuid="+testId;
  		}
  		var request = new qx.io2.HttpRequest(service);
  		request.addListener("load", function(e){
  			var responseText = e.getTarget().getResponseText();
  			this.createGui(responseText);  			
  		}, this);
  		request.send();
  	},
  	  	
  	
  	createGui : function(responseText){
  		var htmlElement = new qx.ui.embed.Html(responseText);
  		var scroller = new qx.ui.container.Scroll(htmlElement);
  		this.add(scroller, {edge:"center", height:'100%'});
  	}
  	
  }
});