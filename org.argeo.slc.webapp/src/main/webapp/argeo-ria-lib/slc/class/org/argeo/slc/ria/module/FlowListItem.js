/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2004-2008 1&1 Internet AG, Germany, http://www.1und1.de

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Sebastian Werner (wpbasti)
     * Andreas Ecker (ecker)

************************************************************************ */

/**
 * A item for a list. Could be added to all List like widgets but also
 * to the {@link qx.ui.form.SelectBox} and {@link qx.ui.form.ComboBox}.
 */
qx.Class.define("org.argeo.slc.ria.module.FlowListItem",
{
  extend : qx.ui.container.Composite,



  /*
  *****************************************************************************
     CONSTRUCTOR
  *****************************************************************************
  */

  /**
   * @param label {String} Label to use
   * @param icon {String?null} Icon to use
   * @param value {String?null} The items string value
   */
  construct : function(label, icon, value)
  {
    this.base(arguments, new qx.ui.layout.HBox(5));
	this.setLabel(label);
	if(icon){
		this.setIcon(icon);
	}
	var atom = new qx.ui.basic.Atom(label, icon);
	this.add(atom);
	
    if (value != null) {
      this.setValue(value);
    }
    this.add(new qx.ui.core.Spacer(20, 20), {flex:1});
    this.delButton = new qx.ui.form.Button("Delete", "resource/slc/edit-delete.png");
    this.formButton = new qx.ui.form.Button("Show Specs", "resource/slc/document-open.png");
    this.delButton.setShow("icon");
    this.delButton.setPadding(0);
    this.delButton.setOpacity(0.1);
    this.formButton.setShow("icon");
    this.formButton.setPadding(0);
    this.formButton.setOpacity(0.1);
    this.add(this.formButton);
    this.add(this.delButton);
    
    this.addListener("mouseover", function(e){
    	this.delButton.setOpacity(1);
    	this.formButton.setOpacity(1);
    }, this);
    this.addListener("mouseout", function(e){
    	this.delButton.setOpacity(0.1);
    	this.formButton.setOpacity(0.1);
    }, this);
  },




  /*
  *****************************************************************************
     EVENTS
  *****************************************************************************
  */

  events:
  {
    /** (Fired by {@link qx.ui.form.List}) */
    "action" : "qx.event.type.Event"
  },




  /*
  *****************************************************************************
     PROPERTIES
  *****************************************************************************
  */

  properties :
  {
    appearance :
    {
      refine : true,
      init : "listitem"
    },

    label : {
    	check : "String"
    },
    
    icon :  {
    	
    },
    
    /** The assigned qx.ui.form.RadioGroup which handles the switching between registered buttons */
    manager :
    {
      check  : "qx.ui.form.RadioGroup",
      nullable : true,
      apply : "_applyManager"
    },

    /** Fires a "changeValue" (qx.event.type.Data) event */
    value :
    {
      check : "String",
      nullable : true,
      event : "changeValue"
    }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    /*
    ---------------------------------------------------------------------------
      PROPERTY APPLY ROUTINES
    ---------------------------------------------------------------------------
    */

    // property apply
    _applyManager : function(value, old)
    {
      if (old) {
        old.remove(this);
      }

      if (value) {
        value.add(this);
      }
    },



    /*
    ---------------------------------------------------------------------------
      SEARCH VALUE
    ---------------------------------------------------------------------------
    */

    /**
     * Returns the searchable value of the list item.
     *
     * This is normally the real value with a fallback to the label like in HTML
     * select boxes.
     */
    getFormValue : function()
    {
      var value = this.getValue();
      if (value == null) {
        value = this.getLabel();
      }

      return value;
    }
  }
});
