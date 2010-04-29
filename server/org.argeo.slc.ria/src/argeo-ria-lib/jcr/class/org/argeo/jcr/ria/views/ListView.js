qx.Class.define("org.argeo.jcr.ria.views.ListView", {
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
	  		init:"listView",
	  		event : "changeInstanceId"
	  	},
	  	instanceLabel : {
	  		init:"Nodes List",
	  		event : "changeInstanceLabel"
	  	},
	  	dataModel : {
	  		
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
		init : function(viewPane, dataModel){
			this.setViewSelection(new org.argeo.ria.components.ViewSelection(viewPane.getViewId()));	  			
			this.setLayout(new qx.ui.layout.VBox());
			this.setDataModel(dataModel);
			
			var split = new qx.ui.splitpane.Pane("vertical");
			split.setDecorator(null);
			this.add(split, {flex:1});
			
			this.list = new qx.ui.form.List();			
			split.add(this.list,1);
			
			this.propInput = new qx.ui.form.TextArea();
			split.add(this.propInput,1);
			var call = new qx.util.DeferredCall(function(){
				qx.bom.element.Style.set(this.propInput.getContentElement().getDomElement(), "lineHeight", "1.8");
			}, this);
			call.schedule();
		},
		/**
		 * The implementation should contain the real data loading (i.o. query...)
		 * @return {Boolean}
		 */
		load : function(){
			var dataModel = this.getDataModel();
			dataModel.addListener("changeSelection", function(event){
				if(dataModel.getSelectionSource() == this) return;
				var selection = event.getData();
				this.list.removeAll();
				if(!selection.length) {
					return;
				}
				var contextNode = selection[0];
				this._parseNode(contextNode);
			}, this);
		},
		
		_parseNode : function (jcrNode){
			if(jcrNode.getLoadState() == "loaded"){
				this.list.removeAll();
				if(jcrNode.getParent()){
					this.list.add(this._initListItem(jcrNode.getParent(), ".."));
				}
				var children = jcrNode.getChildren();
				for(var i=0;i<children.length;i++){
					this.list.add(this._initListItem(children[i]));
				}
				if(this.list.hasChildren()){
					this.list.setSelection([this.list.getChildren()[0]]);
				}
				var properties = jcrNode.getProperties();
				var propString = "";
				for(var i=0;i<properties.length;i++){
					propString += properties[i].getName()+"="+properties[i].getValue()+"\n";
				}
				this.propInput.setValue(propString);
			}else{
				var listener = function(event){
					if(event.getData() == "loaded"){
						this._parseNode(jcrNode);
						jcrNode.removeListener("changeLoadState", listener, this);
					}
				};
				jcrNode.addListener("changeLoadState", listener, this);
				if(jcrNode.getLoadState() != "loading"){
					jcrNode.load();
				}
			}
		},
		
		_initListItem : function(jcrNode, label){
			var li = new qx.ui.form.ListItem((label?label:jcrNode.getName()));
			li.setModel(jcrNode);
			li.addListener("dblclick", function(){
				this.getDataModel().setSelectionWithSource([jcrNode], this);
			}, this);
			return li;
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
