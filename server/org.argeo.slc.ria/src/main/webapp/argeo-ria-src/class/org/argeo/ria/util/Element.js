/**
 * Cross browser XML Element API
 * 
 * Overrides the Qooxdoo qx.xml.Element to handle the namespace prefixes
 *
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/xmlsdk/html/81f3de54-3b79-46dc-8e01-73ca2d94cdb5.asp
 * http://developer.mozilla.org/en/docs/Parsing_and_serializing_XML
 */
qx.Class.define("org.argeo.ria.util.Element",
{
		
  statics :
  {
  	
  	DEFAULT_NAMESPACE_MAP : null,
  	
     /**
     * Selects the first XmlNode that matches the XPath expression.
     *
     * @param element {Element | Document} root element for the search
     * @param query {String} XPath query
     * @param NSMap (Object) A map matching namespace prefixes to namespace URIS;
     * @return {Element} first matching element
     * @signature function(element, query, NSMap)
     */
    selectSingleNode : qx.core.Variant.select("qx.client",
    {
      "mshtml|opera": function(element, query, NSMap) {
      	NSMap = NSMap || org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP;
      	if(NSMap){      		
      		var namespaces = [];
      		var i=0;
	      	for(var prefix in NSMap){	      		
	      		namespaces[i] = 'xmlns:'+prefix+'="'+NSMap[prefix]+'"';
	      		i++;
	      	}
	      	var doc = element.ownerDocument || element;
	      	doc.setProperty('SelectionNamespaces', namespaces.join(" "));
      	}
      	try{
	        return element.selectSingleNode(query);
      	}catch(err){}
      },

      "default": function(element, query, NSMap)
      {
      	NSMap = NSMap || org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP;
        if(!this.__xpe) {
          this.__xpe = new XPathEvaluator();
        }

        var xpe = this.__xpe;

        try {
        	var resolver;
        	if(NSMap){
	        	resolver = function(prefix){
	        		return NSMap[prefix] || null;
	        	}
        	}else{
        		resolver = xpe.createNSResolver(element);
        	}
        	//return xpe.evaluate(query, element, xpe.createNSResolver(element), XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
        	return xpe.evaluate(query, element, resolver, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
        } catch(err) {
          throw new Error("selectSingleNode: query: " + query + ", element: " + element + ", error: " + err);
        }
      }
    }),


    /**
     * Selects a list of nodes matching the XPath expression.
     *
     * @param element {Element | Document} root element for the search
     * @param query {String} XPath query
     * @param NSMap {Map} Mapping between namespaces prefixes and URI.
     * @return {Element[]} List of matching elements
     * @signature function(element, query, NSMap)
     */
    selectNodes : qx.core.Variant.select("qx.client",
    {
      "mshtml|opera": function(element, query, NSMap) {
      	NSMap = NSMap || org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP;
      	if(NSMap){
      		var namespaces = [];
      		var i=0;
	      	for(var prefix in NSMap){	      		
	      		namespaces[i] = 'xmlns:'+prefix+'="'+NSMap[prefix]+'"';
	      		i++;
	      	}
	      	var doc = element.ownerDocument || element;
	      	doc.setProperty('SelectionNamespaces', namespaces.join(" "));
      	}      	
        return element.selectNodes(query);
      },

      "default": function(element, query, NSMap)
      {
      	NSMap = NSMap || org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP;
        var xpe = this.__xpe;

        if(!xpe) {
          this.__xpe = xpe = new XPathEvaluator();
        }

        try {
        	var resolver;
        	if(NSMap){
	        	resolver = function(prefix){
	        		return NSMap[prefix] || null;
	        	}
        	}else{
        		resolver = xpe.createNSResolver(element);
        	}        	
          //var result = xpe.evaluate(query, element, xpe.createNSResolver(element), XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
          var result = xpe.evaluate(query, element, resolver, XPathResult.ORDERED_NODE_SNAPSHOT_TYPE, null);
        } catch(err) {
          throw new Error("selectNodes: query: " + query + ", element: " + element + ", error: " + err);
        }

        var nodes = [];
        for (var i=0; i<result.snapshotLength; i++) {
          nodes[i] = result.snapshotItem(i);
        }

        return nodes;
      }
    }),


    /**
     * Returns a list of elements with the given tag name belonging to the given namespace (http://developer.mozilla.org/en/docs/DOM:element.getElementsByTagNameNS).
     *
     * @param element {Element | Document} the element from where the search should start.
     *       Note that only the descendants of this element are included in the search, not the node itself.
     * @param namespaceURI {var} is the namespace URI of elements to look for . For example, if you need to look
     *       for XHTML elements, use the XHTML namespace URI, <tt>http://www.w3.org/1999/xhtml</tt>.
     * @param tagname {String} the tagname to look for
     * @return {Element[]} a list of found elements in the order they appear in the tree.
     * @signature function(element, namespaceURI, tagname)
     */
    getElementsByTagNameNS : qx.core.Variant.select("qx.client",
    {    	
      "mshtml": function(element, namespaceURI, tagname)
      {
        var doc = element.ownerDocument || element;

        doc.setProperty("SelectionLanguage", "XPath");
        doc.setProperty("SelectionNamespaces", "xmlns:ns='" + namespaceURI + "'");

        return qx.xml.Element.selectNodes(element, 'descendant-or-self::ns:' + tagname);
      },

      "default": function(element, namespaceURI, tagname) {
        return element.getElementsByTagNameNS(namespaceURI, tagname);
      }
    }),


    /**
     * Selects the first XmlNode that matches the XPath expression and returns the text content of the element
     *
     * @param element {Element|Document} root element for the search
     * @param query {String}  XPath query
     * @param NSMap {Object} Mapping between NS prefix / uri
     * @return {String} the joined text content of the found element or null if not appropriate.
     * @signature function(element, query)
     */
    getSingleNodeText : function(element, query, NSMap)
    {
      NSMap = NSMap || org.argeo.ria.util.Element.DEFAULT_NAMESPACE_MAP;
      var node = org.argeo.ria.util.Element.selectSingleNode(element, query, NSMap);
      return qx.dom.Node.getText(node);
    }
  }
});
