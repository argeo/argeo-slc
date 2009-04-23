/**
 * A simple Hello World applet for documentation purpose. 
 * The only associated command is the "Close" command.
 */
qx.Class.define("org.argeo.ria.sample.Applet",
{
  extend : qx.ui.container.Composite,
  implement : [org.argeo.ria.components.IView], 

  construct : function(){
  	this.base(arguments);
	this.setLayout(new qx.ui.layout.VBox());
  	this.passedStatus = "PASSED";
  	this.failedStatus = "FAILED";
  },

  properties : 
  {
  	/**
  	 * The viewPane inside which this applet is added. 
  	 */
  	view : {
  		init : null
  	},
  	/**
  	 * Commands definition, see {@link org.argeo.ria.event.CommandsManager#definitions} 
  	 */
  	commands : {
  		init : {
  			"close" : {
  				label	 	: "Close Result", 
  				icon 		: "resource/slc/window-close.png",
  				shortcut 	: "Control+w",
  				enabled  	: true,
  				menu	   	: "Applet",
  				toolbar  	: "result",
  				callback	: function(e){
  					// Call service to delete
  					this.getView().empty();  					
  				},
  				command 	: null
  			}  			
  		}
  	},
  	viewSelection : {
  		nullable:false, 
  		check:"org.argeo.ria.components.ViewSelection"
  	},
  	instanceId : {init:"Hello Applet"},
  	instanceLabel : {init:"Hello"}
  	
  },

  members :
  {
  	/**
  	 * Called at applet creation. Just registers viewPane.
  	 * @param viewPane {org.argeo.ria.components.ViewPane} The viewPane.
  	 */
  	init : function(viewPane, data){
  		this.setView(viewPane);
  		this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));
  		this.data = data;
  		this.setInstanceId(data[0]);
  		this.setInstanceLabel("Hello applet #"+data[0]);
  	},
  	
  	/**
  	 * Load a given row : the data passed must be a simple data array.
  	 * @param data {Element} The text xml description. 
  	 */
  	load : function(){  		
  		this.createHtmlGui("<b>Hello World ! Data ID is : "+this.getInstanceId()+"</b>");
  	},
  	 
	addScroll : function(){
		return false;
	},
	
	close : function(){
		return false;
	},
	  	
  	/**
  	 * Create a simple HtmlElement and and insert the html string..
  	 * Not used but sample.
  	 * @param htmlText {String} Html code to display.
  	 */
  	createHtmlGui : function(htmlText){
  		var htmlElement = new qx.ui.embed.Html(htmlText);
  		htmlElement.setOverflowX("auto");
  		htmlElement.setOverflowY("auto");
  		this.add(htmlElement, {flex:1});
  	}
  	
  }
});