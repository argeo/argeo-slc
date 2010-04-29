qx.Class.define("org.argeo.jcr.ria.views.PlainXmlViewer", {
	extend : qx.ui.container.Composite,
	implement : [org.argeo.ria.components.IView], 

	properties : {
		/**
		 * The commands definition Map that will be automatically added and wired to the menubar and toolbar.
		 * See {@link org.argeo.ria.event.CommandsManager#definitions} for the keys to use for defining commands.
		 */
		commands : {
			init : {
	  			"close" : {
	  				label	 	: "Close",
	  				shared		: true,
	  				icon 		: "org.argeo.slc.ria/window-close.png",
	  				shortcut 	: "Control+w",
	  				enabled  	: true,
	  				menu	   	: null,
	  				toolbar  	: null,
	  				callback	: function(e){
	  					org.argeo.ria.components.ViewsManager.getInstance().getViewPaneById("editor").closeCurrent();
	  				},
	  				command 	: null
	  			}				
			}
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
		init : function(viewPane, jcrNode){
			this._viewPane = viewPane;
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  			
			this.setLayout(new qx.ui.layout.VBox());
			
			this.setInstanceId("edit:" + jcrNode.getPath());
			this.setInstanceLabel("Node '" + jcrNode.getName()+"'");
			
			this.htmlPane = new qx.ui.embed.Html();
			this.htmlPane.setOverflow("auto", "auto");
			this.htmlPane.setDecorator("input");
			this.add(this.htmlPane, {flex:1});
			
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(jcrNode){
			// Load full XML (deep)			
			//jcrNode.load(-1);
			var fakeNode = new org.argeo.jcr.ria.model.Node(jcrNode.getName(), jcrNode.getNodeProvider(), true);
			fakeNode.setPath(jcrNode.getPath());
			fakeNode.addListener("changeLoadState", function(event){
				if(event.getData() == "loaded"){
					var xmlString = fakeNode.toXmlString(true);
					this._loadXmlString(xmlString);
				}
			}, this);
			fakeNode.load(-1);
		},
		
		_loadXmlString : function(xmlString){
		    var TAG_START_PATTERN = new RegExp("<([0-9a-zA-Z\.:]+)([^>]*)>", "gi");
		    var TAG_END_PATTERN = new RegExp("</([0-9a-zA-Z\.:]+)>", "gi");
		    var TAG_CLOSE_PATTERN = new RegExp("(/?>)", "gi");
		    var TAG_ATTRIBUTE = new RegExp("\\s([0-9a-zA-Z:_]+)\\=\"([^\"]*)\"", "gi");
		    // Not implemented yet
		    var TAG_COMMENT = new RegExp("(<!--.*-->)", "gi");
		    var TAG_CDATA_START = new RegExp("(\\<!\\[CDATA\\[).*", "gi");
		    var TAG_CDATA_END = new RegExp(".*(]]>)", "gi");
			
		    xmlString = xmlString.replace(TAG_START_PATTERN, 'xml_div_begin&lt;xml_tagname_begin$1xml_tagname_end$2&gt;');
		    xmlString = xmlString.replace(TAG_END_PATTERN, '&lt;/xml_tagname_begin$1xml_tagname_end&gt;xml_div_end');
		    xmlString = xmlString.replace(TAG_CLOSE_PATTERN, '&gt;');
		    
		    xmlString = xmlString.replace(TAG_ATTRIBUTE, ' xml_attname_begin$1xml_attname_end="xml_attvalue_begin$2xml_attvalue_end"');
			
			//xmlString = qx.xml.String.escape(xmlString);
			xmlString = xmlString.replace(new RegExp("(xml_div_begin)", "g"), '<div style="padding:1px;line-height:17px;padding-left:15px;">');
			xmlString = xmlString.replace(new RegExp("(xml_div_end)", "g"), '</div>');
			xmlString = xmlString.replace(new RegExp("(xml_tagname_begin)", "g"), '<b style="color:rgb(63,127,127);">');
			xmlString = xmlString.replace(new RegExp("(xml_tagname_end)", "g"), '</b>');
			xmlString = xmlString.replace(new RegExp("(xml_attname_begin)", "g"), '<b>');
			xmlString = xmlString.replace(new RegExp("(xml_attname_end)", "g"), '</b>');
			xmlString = xmlString.replace(new RegExp("(xml_attvalue_begin)", "g"), '<span style="color:rgb(0,0,255)">');
			xmlString = xmlString.replace(new RegExp("(xml_attvalue_end)", "g"), '</span>');
			xmlString = '<div style="margin-left:-10px;">' + xmlString + '</div>';
			this.htmlPane.setHtml(xmlString);
			var call = new qx.util.DeferredCall(function(){
				var htmlDom = this.htmlPane.getContentElement().getDomElement();
				var spans = qx.bom.Selector.query("span", htmlDom);
				for(var i=0;i<spans.length;i++){
					var oThis = this;
					spans[i].onclick = function(){oThis._setAttributesSpanEditable(this)};
				}
			}, this);
			call.schedule();			
		},
		
		_setAttributesSpanEditable : function(span){
			var width = qx.bom.element.Dimension.getWidth(span);
			var value = span.innerHTML;
			qx.bom.element.Style.set(span, "display", "none");
			var input = qx.bom.Element.create("input", {value:value, style:'width:'+width+'px;height:14px;border-width:1px;padding:0px;font-size:11px;'});
			//var input = new qx.html.Input("text");
			//input.setStyles({width:width+'px', height:'14px', borderWidth:'1px', padding:'0px', fontSize:'11px'});			
			qx.dom.Element.insertAfter(input, span);
			//input.setTextSelection(0);
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