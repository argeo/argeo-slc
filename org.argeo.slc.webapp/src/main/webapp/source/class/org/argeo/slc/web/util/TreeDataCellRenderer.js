/* ************************************************************************

   qooxdoo - the new era of web development

   http://qooxdoo.org

   Copyright:
     2007 Derrell Lipman

   License:
     LGPL: http://www.gnu.org/licenses/lgpl.html
     EPL: http://www.eclipse.org/org/documents/epl-v10.php
     See the LICENSE file in the project's top-level directory for details.

   Authors:
     * Derrell Lipman (derrell)
     * David Perez Carmona (david-perez)

************************************************************************ */

/* ************************************************************************

#require(qx.theme.Modern)
#require(qx.theme.Classic)
#require(qx.log.Logger)

************************************************************************ */

/**
 * A data cell renderer for the tree column of a simple tree
 */
qx.Class.define("org.argeo.slc.web.util.TreeDataCellRenderer",
{
  extend : qx.ui.treevirtual.SimpleTreeDataCellRenderer,


  construct : function()
  {
    this.base(arguments);

    this.__am = qx.util.AliasManager.getInstance();
    this.__rm = qx.util.ResourceManager;
    this.__tm = qx.theme.manager.Appearance.getInstance();

    // Base URL used for indentation
    this.BLANK = this.__rm.toUri(this.__am.resolve("static/blank.gif"));
  },


  statics :
  {
    __icon : { }
  },




  /*
  *****************************************************************************
     MEMBERS
  *****************************************************************************
  */

  members :
  {
    // overridden
    _getCellStyle : function(cellInfo)
    {
      var node = cellInfo.value;

      // Return the style for the div for the cell.  If there's cell-specific
      // style information provided, append it.
      var html =
        this.base(arguments, cellInfo) +
        (node.cellStyle ? node.cellStyle + ";" : "");
      return html;
    },

    // overridden
    _getContentHtml : function(cellInfo)
    {
      var html = "";

      // Horizontal position
      var pos = 0;

      // If needed, add extra content before indentation
      var extra = this._addExtraContentBeforeIndentation(cellInfo, pos);
      html += extra.html;
      pos = extra.pos;

      // Add the indentation (optionally with tree lines)
      var indentation = this._addIndentation(cellInfo, pos);
      html += indentation.html
      pos = indentation.pos;

      // If needed, add extra content before icon
      extra = this._addExtraContentBeforeIcon(cellInfo, pos);
      html += extra.html;
      pos = extra.pos;

      // Add the node icon
      var icon = this._addIcon(cellInfo, pos);
      html += icon.html;
      pos = icon.pos;

      // If needed, add extra content before label
      extra = this._addExtraContentBeforeLabel(cellInfo, pos);
      html += extra.html;
      pos = extra.pos;

      // Add the node's label
      html += this._addLabel(cellInfo, pos);

      return html;
    },

    /**
     * Add an image to the tree.  This might be a visible icon or it may be
     * part of the indentation.
     *
     * @param imageInfo {Map}
     *   How to display the image.  It optionally includes any of the
     *   following:
     *   <dl>
     *     <dt>position {Map}</dt>
     *     <dd>
     *       If provided, a div is created to hold the image.  The div's top,
     *       right, bottom, left, width, and/or height may be specified with
     *       members of this map.  Each is expected to be an integer value.
     *     </dd>
     *     <dt>imageWidth, imageHeight</dt>
     *     <dd>
     *       The image's width and height.  These are used only if both are
     *       specified.
     *     </dd>
     *   </dl>
     *
     * @return {String}
     *   The html for this image, possibly with a surrounding div (see
     *   'position', above).
     */
    _addImage : function(imageInfo)
    {
      var html = [];

      // Resolve the URI
      var source = this.__rm.toUri(this.__am.resolve(imageInfo.url));

      // If we've been given positioning attributes, enclose image in a div
      if (imageInfo.position)
      {
        var pos = imageInfo.position;

        html.push('<div style="position:absolute;');

        if (!qx.core.Variant.isSet("qx.client", "mshtml"))
        {
          html.push(qx.bom.element.BoxSizing.compile("content-box"));
        }

        if (pos.top !== undefined)
        {
          html.push('top:' + pos.top + 'px;');
        }

        if (pos.right !== undefined)
        {
          html.push('right:' + pos.right + 'px;');
        }

        if (pos.bottom !== undefined)
        {
          html.push('bottom:' + pos.bottom + 'px;');
        }

        if (pos.left !== undefined)
        {
          html.push('left:' + pos.left + 'px;');
        }

        if (pos.width !== undefined)
        {
          html.push('width:' + pos.width + 'px;');
        }

        if (pos.height !== undefined)
        {
          html.push('height:' + pos.height + 'px;');
        }

        html.push('">');
      }

      // Don't use an image tag.  They render differently in Firefox and IE7
      // even if both are enclosed in a div specified as content box.  Instead,
      // add the image as the background image of a div.
      html.push('<div style="');
      html.push('background-image:url(' + source + ');');
      html.push('background-repeat:no-repeat;');

      if (imageInfo.imageWidth && imageInfo.imageHeight)
      {
        html.push(
          ';width:' +
          imageInfo.imageWidth +
          'px' +
          ';height:' +
          imageInfo.imageHeight +
          'px');
      }

      var tooltip = imageInfo.tooltip;

      if (tooltip != null)
      {
        html.push('" title="' + tooltip);
      }

      html.push('">&nbsp;</div>');

      if (imageInfo.position)
      {
        html.push('</div>');
      }

      return html.join("");
    },


    /**
     * Add the indentation for this node of the tree.
     *
     * The indentation optionally includes tree lines.  Whether tree lines are
     * used depends on (a) the properties 'useTreeLines' and
     * 'excludeFirstLevelTreelines' within this class; and (b) the widget
     * theme in use (some themes don't support tree lines).
     *
     * @param cellInfo {Map} The information about the cell.
     *   See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {Map}
     *   The returned map contains an 'html' member which contains the html for
     *   the indentation, and a 'pos' member which is the starting position
     *   plus the width of the indentation.
     */
    _addIndentation : function(cellInfo, pos)
    {
      var node = cellInfo.value;
      var imageData;
      var html = "";

      // Generate the indentation.  Obtain icon determination values once
      // rather than each time through the loop.
      var bUseTreeLines = this.getUseTreeLines();
      var bExcludeFirstLevelTreeLines = this.getExcludeFirstLevelTreeLines();
      var bAlwaysShowOpenCloseSymbol = this.getAlwaysShowOpenCloseSymbol();

      for (var i=0; i<node.level; i++)
      {
        imageData = this._getIndentSymbol(i, node, bUseTreeLines,
                                          bAlwaysShowOpenCloseSymbol,
                                          bExcludeFirstLevelTreeLines);

        html += this._addImage(
        {
          url         : imageData.icon,
          position    :
          {
            top         : 0 + (imageData.paddingTop || 5),
            left        : pos + (imageData.paddingLeft || 3),
            width       : 19,
            height      : 16
          }
        });
        pos += 19;
      }

      return (
        {
          html : html,
          pos  : pos
        });
    },

    /**
     * Add the icon for this node of the tree.
     *
     * @param cellInfo {Map} The information about the cell.
     *   See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {Map}
     *   The returned map contains an 'html' member which contains the html for
     *   the icon, and a 'pos' member which is the starting position plus the
     *   width of the icon.
     */
    _addIcon : function(cellInfo, pos)
    {
      var node = cellInfo.value;

      // Add the node's icon
      var imageUrl = (node.bSelected ? node.iconSelected : node.icon);

      if (!imageUrl)
      {
        if (node.type == qx.ui.treevirtual.SimpleTreeDataModel.Type.LEAF)
        {
          var o = this.__tm.styleFrom("treevirtual-file");
        }
        else
        {
          var states = { opened : node.bOpened };
          var o = this.__tm.styleFrom("treevirtual-folder", states);
        }

        imageUrl = o.icon;
      }

      var html = this._addImage(
      {
        url         : imageUrl,
        position    :
        {
          top         : 0,
          left        : pos,
          width       : 19,
          height      : 16
        }
      });

      return (
        {
          html : html,
          pos  : pos + 19
        });
    },

    /**
     * Add the label for this node of the tree.
     *
     * @param cellInfo {Map} The information about the cell.
     *   See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *   Additionally, if defined, the labelSpanStyle member is used to apply
     *   style to the span containing the label.  (This member is for use by
     *   subclasses; it's not otherwise used by this class.)
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {String}
     *   The html for the label.
     */
    _addLabel : function(cellInfo, pos)
    {
      var node = cellInfo.value;

      // Add the node's label.  We calculate the "left" property with: each
      // tree line (indentation) icon is 19 pixels wide; the folder icon is 16
      // pixels wide, there are two pixels of padding at the left, and we want
      // 2 pixels between the folder icon and the label
      var html =
        '<div style="position:absolute;' +
        'left:' + pos + 'px;' +
        'top:0;' +
        (node.labelStyle ? node.labelStyle + ";" : "") +
        '">' +
        '<span' + (cellInfo.labelSpanStyle
                   ? 'style="' + cellInfo.labelSpanStyle + ';"'
                   : "") + '>' +
        node.label +
        '</span>' +
        '</div>';

      return html;
    },

    /**
     * Adds extra content just before the indentation.
     *
     * @param cellInfo {Map} The information about the cell.
     *      See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {Map}
     *   The returned map contains an 'html' member which contains the html for
     *   the indentation, and a 'pos' member which is the starting position
     *   plus the width of the indentation.
     */
    _addExtraContentBeforeIndentation : function(cellInfo, pos)
    {
      return { html: '', pos: pos };
    },

    /**
     * Adds extra content just before the icon.
     *
     * @param cellInfo {Map} The information about the cell.
     *      See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {Map}
     *   The returned map contains an 'html' member which contains the html for
     *   the indentation, and a 'pos' member which is the starting position
     *   plus the width of the indentation.
     */
    _addExtraContentBeforeIcon : function(cellInfo, pos)
    {
      return { html: '', pos: pos };
    },

    /**
     * Adds extra content just before the label.
     *
     * @param cellInfo {Map} The information about the cell.
     *      See {@link qx.ui.table.cellrenderer.Abstract#createDataCellHtml}.
     *
     * @param pos {Integer}
     *   The position from the left edge of the column at which to render this
     *   item.
     *
     * @return {Map}
     *   The returned map contains an 'html' member which contains the html for
     *   the indentation, and a 'pos' member which is the starting position
     *   plus the width of the indentation.
     */
    _addExtraContentBeforeLabel : function(cellInfo, pos)
    {
      return { html: '', pos: pos };
    },


    /**
     * Determine the symbol to use for indentation of a tree row, at a
     * particular column.  The indentation to use may be just white space or
     * may be a tree line.  Tree lines come in numerous varieties, so the
     * appropriate one is selected.
     *
     * @type member
     *
     * @param column {Integer}
     *   The column of indentation being requested, zero-relative
     *
     * @param node {Node}
     *   The node being displayed in the row.  The properties of a node are
     *   described in {@link qx.ui.treevirtual.SimpleTreeDataModel}
     *
     * @param bUseTreeLines {Boolean}
     *   Whether to find an appropriate tree line icon, or simply provide
     *   white space.
     *
     * @param bAlwaysShowOpenCloseSymbol {Boolean}
     *   Whether to display the open/close icon for a node even if it has no
     *   children.
     *
     * @param bExcludeFirstLevelTreeLines {Boolean}
     *   If bUseTreeLines is enabled, then further filtering of the left-most
     *   tree line may be specified here.  If <i>true</i> then the left-most
     *   tree line, between top-level siblings, will not be displayed.
     *   If <i>false</i>, then the left-most tree line wiill be displayed
     *   just like all of the other tree lines.
     *
     * @return {var} TODOC
     */
    _getIndentSymbol : function(column,
                                node,
                                bUseTreeLines,
                                bAlwaysShowOpenCloseSymbol,
                                bExcludeFirstLevelTreeLines)
    {
      var STDCR = org.argeo.slc.web.util.TreeDataCellRenderer;

      // If we're in column 0 and excludeFirstLevelTreeLines is enabled, then
      // we treat this as if no tree lines were requested.
      if (column == 0 && bExcludeFirstLevelTreeLines)
      {
        bUseTreeLines = false;
      }

      // If we're not on the final column...
      if (column < node.level - 1)
      {
        // then return either a line or a blank icon, depending on
        // bUseTreeLines
        return (bUseTreeLines && ! node.lastChild[column]
                ? STDCR.__icon.line
                : { icon : this.BLANK });
      }

      var bLastChild = node.lastChild[node.lastChild.length - 1];

      // Is this a branch node that does not have the open/close button hidden?
      if (node.type == qx.ui.treevirtual.SimpleTreeDataModel.Type.BRANCH &&
          ! node.bHideOpenClose)
      {
        // Does this node have any children, or do we always want the
        // open/close symbol to be shown?
        if (node.children.length > 0 || bAlwaysShowOpenCloseSymbol)
        {
          // If we're not showing tree lines...
          if (!bUseTreeLines)
          {
            // ... then just use a expand or contract
            return (node.bOpened
                    ? STDCR.__icon.contract
                    : STDCR.__icon.expand);
          }

          // Are we looking at a top-level, first child of its parent?
          if (column == 0 && node.bFirstChild)
          {
            // Yup.  If it's also a last child...
            if (bLastChild)
            {
              // ... then use no tree lines.
              return (node.bOpened
                      ? STDCR.__icon.onlyContract
                      : STDCR.__icon.onlyExpand);
            }
            else
            {
              // otherwise, use descender lines but no ascender.
              return (node.bOpened
                      ? STDCR.__icon.startContract
                      : STDCR.__icon.startExpand);
            }
          }

          // It's not a top-level, first child.  Is this the last child of its
          // parent?
          if (bLastChild)
          {
            // Yup.  Return an ending expand or contract.
            return (node.bOpened
                    ? STDCR.__icon.endContract
                    : STDCR.__icon.endExpand);
          }

          // Otherwise, return a crossing expand or contract.
          return (node.bOpened
                  ? STDCR.__icon.crossContract
                  : STDCR.__icon.crossExpand);
        }
      }

      // This node does not have any children.  Return an end or cross, if
      // we're using tree lines.
      if (bUseTreeLines)
      {
        // If this is a child of the root node...
        if (node.parentNodeId == 0)
        {
          // If this is the only child...
          if (bLastChild && node.bFirstChild)
          {
            // ... then return a blank.
            return { icon : this.BLANK };
          }

          // Otherwise, if this is the last child...
          if (bLastChild)
          {
            // ... then return an end line.
            return STDCR.__icon.end;
          }

          // Otherwise if this is the first child...
          if (node.bFirstChild)
          {
            // ... then return a start line.
            return STDCR.__icon.start;
          }
        }

        // If this is a last child, return and ending line; otherwise cross.
        return (bLastChild
                ? STDCR.__icon.end
                : STDCR.__icon.cross);
      }

      return { icon : this.BLANK };
    }
  },

  defer : function()
  {
    // Ensure that the theme is initialized
    qx.theme.manager.Meta.getInstance().initialize();

    var STDCR = org.argeo.slc.web.util.TreeDataCellRenderer;

    var ImageLoader = qx.io2.ImageLoader;

    var am = qx.util.AliasManager.getInstance();
    var rm = qx.util.ResourceManager;
    var tm = qx.theme.manager.Appearance.getInstance();

    var loadImage = function(f)
    {
      ImageLoader.load(rm.toUri(am.resolve(f)));
    };

    STDCR.__icon.line = tm.styleFrom("treevirtual-line");
    loadImage(STDCR.__icon.line.icon);

    STDCR.__icon.contract = tm.styleFrom("treevirtual-contract");
    loadImage(STDCR.__icon.contract.icon);

    STDCR.__icon.expand = tm.styleFrom("treevirtual-expand");
    loadImage(STDCR.__icon.expand.icon);

    STDCR.__icon.onlyContract = tm.styleFrom("treevirtual-only-contract");
    loadImage(STDCR.__icon.onlyContract.icon);

    STDCR.__icon.onlyExpand = tm.styleFrom("treevirtual-only-expand");
    loadImage(STDCR.__icon.onlyExpand.icon);

    STDCR.__icon.startContract = tm.styleFrom("treevirtual-start-contract");
    loadImage(STDCR.__icon.startContract.icon);

    STDCR.__icon.startExpand = tm.styleFrom("treevirtual-start-expand");
    loadImage(STDCR.__icon.startExpand.icon);

    STDCR.__icon.endContract = tm.styleFrom("treevirtual-end-contract");
    loadImage(STDCR.__icon.endContract.icon);

    STDCR.__icon.endExpand = tm.styleFrom("treevirtual-end-expand");
    loadImage(STDCR.__icon.endExpand.icon);

    STDCR.__icon.crossContract = tm.styleFrom("treevirtual-cross-contract");
    loadImage(STDCR.__icon.crossContract.icon);

    STDCR.__icon.crossExpand = tm.styleFrom("treevirtual-cross-expand");
    loadImage(STDCR.__icon.crossExpand.icon);

    STDCR.__icon.end = tm.styleFrom("treevirtual-end");
    loadImage(STDCR.__icon.end.icon);

    STDCR.__icon.cross = tm.styleFrom("treevirtual-cross");
    loadImage(STDCR.__icon.cross.icon);
  },

  destruct : function()
  {
    this._disposeFields(
      "__am",
      "__rm",
      "__tm",
      "BLANK");
  }
});