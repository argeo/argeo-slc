/* ************************************************************************

  Copyright: 2008 Argeo

   License: 

   Authors: Charles du Jeu

************************************************************************ */
qx.Class.define("org.argeo.slc.web.components.Logger",
{
	type : "singleton",
	extend : qx.ui.window.Window,
  
	construct : function(){
	  	this.base(arguments, "Logs", "resource/slc/help-contents.png");
		this.set({
			showMaximize : true,
			showMinimize : false,
			width: 550,
			height: 300
		});
		this.setLayout(new qx.ui.layout.Dock(0,5));
		var buttonPane = new qx.ui.container.Composite(new qx.ui.layout.Canvas());
		var closeButton = new qx.ui.form.Button("Close");		
		closeButton.addListener("execute", function(e){
			this.hide();			
		}, this);
		buttonPane.add(closeButton, {width:'20%',left:'40%'});
		this.add(buttonPane, {edge:'south'});
		this.setModal(false);
		
		var layout = new qx.ui.layout.VBox(2);		
		this._logPane = new qx.ui.container.Composite(layout);
		var deco = new qx.ui.decoration.Single(1, 'solid', '#000000');
		deco.setBackgroundColor("#ffffff")
		var scroller = new qx.ui.container.Scroll(this._logPane);
		scroller.setDecorator(deco);
		this.add(scroller, {edge:'center', width:'100%', height:'100%'});
	      // Build style sheet content
	      var style =
	      [
	        '.messages{font-size:0.9em}',
	        '.messages div{padding:0px 4px;}',
	        '.messages .offset{font-weight:bold;}',
	        '.messages .object{font-style:italic;}',
	
	        '.messages .user-command{color:blue}',
	        '.messages .user-result{background:white}',
	        '.messages .user-error{background:#FFE2D5}',
	        '.messages .level-debug{background:white}',
	        '.messages .level-info{background:#DEEDFA}',
	        '.messages .level-warn{background:#FFF7D5}',
	        '.messages .level-error{background:#FFE2D5}',
	        '.messages .level-user{background:#E3EFE9}',
	        '.messages .type-string{color:black;font-weight:normal;}',
	        '.messages .type-number{color:#155791;font-weight:normal;}',
	        '.messages .type-boolean{color:#15BC91;font-weight:normal;}',
	        '.messages .type-array{color:#CC3E8A;font-weight:bold;}',
	        '.messages .type-map{color:#CC3E8A;font-weight:bold;}',
	        '.messages .type-key{color:#565656;font-style:italic}',
	        '.messages .type-class{color:#5F3E8A;font-weight:bold}',
	        '.messages .type-instance{color:#565656;font-weight:bold}',
	        '.messages .type-stringify{color:#565656;font-weight:bold}'
	      ];	
	      // Include stylesheet
	      qx.bom.Stylesheet.createElement(style.join(""));
		
	},
	
	members : {
		process : function(entry){
			var wrapper = qx.log.appender.Util.toHtml(entry);
			var label = new qx.ui.basic.Label('<div class="messages"><div class="'+wrapper.className+'">'+wrapper.innerHTML+'</div></div>');
			label.setRich(true);
			if(entry.level == "error"){
				var alert = new org.argeo.slc.web.components.Modal("Error");
				alert.addContent(label.clone());				
				alert.attachAndShow();
			}
			this._logPane.addAt(label, 0);
		},
		toggle : function(){
			this.show();
			this.center();
		}
	},

	destruct : function()
    {
      qx.log.Logger.unregister(this);
    }
	
});
