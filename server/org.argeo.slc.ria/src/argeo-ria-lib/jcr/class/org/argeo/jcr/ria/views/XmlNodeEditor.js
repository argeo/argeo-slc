qx.Class.define("org.argeo.jcr.ria.views.XmlNodeEditor", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 

	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {}
		},
	  	viewSelection : {
	  		nullable:false, 
	  		check:"org.argeo.ria.components.ViewSelection"
	  	},
	  	instanceId : {
	  		init:"XmlEditor",
	  		event : "changeInstanceId"
	  	},
	  	instanceLabel : {
	  		init:"Xml Editor",
	  		event : "changeInstanceLabel"
	  	}
	},
	
	construct : function(){
		this.base(arguments);
		this.setLayout(new qx.ui.layout.VBox());
		this.htmlPane = new qx.ui.embed.Html();
		this.htmlPane.setOverflow("auto", "auto");
		this.add(this.htmlPane, {flex:1});
	},
	
	members : {
		/**
		 * The implementation should contain the GUI initialisation.
		 * This is the role of the manager to actually add the graphical component to the pane, 
		 * so it's not necessary to do it here. 
		 * @param viewPane {org.argeo.ria.components.ViewPane} The pane manager
		 * @param data {Mixed} Any object or data passed by the initiator of the view
		 * @return {Boolean}
		 */
		init : function(viewPane, data){
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  			
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(rootNode){
			rootNode.load();
			rootNode.addListener("changeLoadState", function(){
				var xmlString = rootNode.toXmlString(true);
			    var TAG_START_PATTERN = new RegExp("<([0-9a-zA-Z\.]+)([^>]*)>", "gi");
			    var TAG_END_PATTERN = new RegExp("</([0-9a-zA-Z\.]+)>", "gi");
			    var TAG_CLOSE_PATTERN = new RegExp("(/?>)", "gi");
			    var TAG_ATTRIBUTE = new RegExp("\\s([0-9a-zA-Z:]+)\\=\"([^\"]*)\"", "gi");
			    // Not implemented yet
			    var TAG_COMMENT = new RegExp("(<!--.*-->)", "gi");
			    var TAG_CDATA_START = new RegExp("(\\<!\\[CDATA\\[).*", "gi");
			    var TAG_CDATA_END = new RegExp(".*(]]>)", "gi");
				
			    xmlString = xmlString.replace(TAG_START_PATTERN, 'xml_div_begin&lt;xml_tagname_begin$1xml_tagname_end$2&gt;');
			    xmlString = xmlString.replace(TAG_END_PATTERN, '&lt;/xml_tagname_begin$1xml_tagname_end&gt;xml_div_end');
			    xmlString = xmlString.replace(TAG_CLOSE_PATTERN, '&gt;');
			    
			    xmlString = xmlString.replace(TAG_ATTRIBUTE, ' xml_attname_begin$1xml_attname_end="xml_attvalue_begin$2xml_attvalue_end"');
				
				//xmlString = qx.xml.String.escape(xmlString);
				xmlString = xmlString.replace(new RegExp("(xml_div_begin)", "g"), '<div style="padding-top:2px;padding-left:15px;">');
				xmlString = xmlString.replace(new RegExp("(xml_div_end)", "g"), '</div>');
				xmlString = xmlString.replace(new RegExp("(xml_tagname_begin)", "g"), '<b style="color:rgb(63,127,127);">');
				xmlString = xmlString.replace(new RegExp("(xml_tagname_end)", "g"), '</b>');
				xmlString = xmlString.replace(new RegExp("(xml_attname_begin)", "g"), '<b>');
				xmlString = xmlString.replace(new RegExp("(xml_attname_end)", "g"), '</b>');
				xmlString = xmlString.replace(new RegExp("(xml_attvalue_begin)", "g"), '<span style="color:rgb(0,0,255)">');
				xmlString = xmlString.replace(new RegExp("(xml_attvalue_end)", "g"), '</span>');
				xmlString = '<div style="margin-left:-10px;">' + xmlString + '</div>';
				this.htmlPane.setHtml(xmlString);
			}, this);
		},
		/**
		 * Whether this component is already contained in a scroller (return false) or not (return true).
		 * @return {Boolean}
		 */
		addScroll : function(){
			return false;
		},
		/**
		 * Called at destruction time
		 * Perform all the clean operations (stopping polling queries, etc.) 
		 */
		close : function(){
			
		}		
	}
	
	
});