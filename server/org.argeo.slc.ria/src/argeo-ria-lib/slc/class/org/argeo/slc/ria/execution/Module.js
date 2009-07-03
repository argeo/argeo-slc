/**
 * Wrapper for ExecutionModule server object
 */
qx.Class.define("org.argeo.slc.ria.execution.Module", {
	
	extend : qx.core.Object,
	
	properties : {
		/**
		 * The name of the module
		 */
		name : {
			check : "String",
			init : ""
		},
		label : {
			check : "String",
			init : ""			
		},
		description : {
			check : "String",
			init : ""			
		},
		/**
		 * The version of the module
		 */
		version : {
			check : "String",
			init : ""
		},
		/**
		 * All execution flows registered by their name
		 */
		executionFlows : {
			check : "Map"
		},
		/**
		 * All execution specs registered by their name
		 */
		executionSpecs : {
			check : "Map"
		},
		/**
		 * XML description (castor)
		 */
		xmlNode : {
			apply : "_applyXmlNode"
		}
	},
	
	statics : {
		XPATH_ROOT : "slc:execution-module-descriptor",		
		XPATH_NAME : "slc:name",
		XPATH_LABEL : "slc:label",
		XPATH_DESCRIPTION : "slc:description",
		XPATH_VERSION : "slc:version",
		XPATH_EXECUTION_FLOWS : "slc:execution-flows/slc:execution-flow-descriptor",
		XPATH_EXECUTION_SPECS : "slc:execution-specs/slc:default-execution-spec"
	},
	
	construct : function(){
		this.base(arguments);
		this.setExecutionFlows({});
		this.setExecutionSpecs({});
	},
	
	members : {
		/**
		 * Add an execution flow to this module
		 * @param executionFlow {org.argeo.slc.ria.execution.Flow} An instance of execution.Flow
		 */
		addExecutionFlow : function(executionFlow){
			var spec = this.getExecutionSpecByName(executionFlow.getExecutionSpecName());
			if(spec){
				executionFlow.setExecutionSpec(spec);
			}else{
				this.error("Warning, reference to an unknown ExecutionSpec : "+executionFlow.getExecutionSpecName());
			}
			this.getExecutionFlows()[executionFlow.getName()] = executionFlow;
		},
		
		/**
		 * Add an execution Spec to this module
		 * @param executionSpec {org.argeo.slc.ria.execution.Spec} An instance of ExecutionSpec
		 */
		addExecutionSpec : function(executionSpec){
			this.getExecutionSpecs()[executionSpec.getName()] = executionSpec;
		},
		/**
		 * Find an execution spec by its name
		 * @param name {String} Name of the spec
		 * @return {org.argeo.slc.ria.execution.Spec} The spec
		 */
		getExecutionSpecByName : function(name){
			return this.getExecutionSpecs()[name];
		},
		
		/**
		 * Find an execution flow by its name
		 * @param name {String} name of the flow
		 * @return {org.argeo.slc.ria.execution.Flow} The flow
		 */
		getExecutionFlowByName : function(name){
			return this.getExecutionFlows()[name];
		},
		
		moduleDataToXml : function(){
			var xmlData = '<slc:module-name>'+this.getName()+'</slc:module-name>';
			xmlData += '<slc:module-version>'+this.getVersion()+'</slc:module-version>';
			return xmlData;
		},
		
		/**
		 * An xml node containing the castor mapped description of this object
		 * @param xmlNode {Node}
		 */
		_applyXmlNode : function(xmlNode){
			var appendRoot = "";
			if(xmlNode.nodeName != this.self(arguments).XPATH_ROOT){
				appendRoot = this.self(arguments).XPATH_ROOT+"/";
			}
			// Parse now			
			this.setName(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, appendRoot + this.self(arguments).XPATH_NAME) || "Not Found");
			this.setVersion(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, appendRoot + this.self(arguments).XPATH_VERSION));
			this.setLabel(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, appendRoot + this.self(arguments).XPATH_LABEL) || this.getName());
			this.setDescription(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, appendRoot + this.self(arguments).XPATH_DESCRIPTION) || "");
			
			// Parse Specs first
			var specs = org.argeo.ria.util.Element.selectNodes(xmlNode, appendRoot + this.self(arguments).XPATH_EXECUTION_SPECS);
			if(specs){
				for(i=0; i< specs.length;i++){
					var execSpec = new org.argeo.slc.ria.execution.Spec();
					execSpec.setXmlNode(specs[i]);
					this.addExecutionSpec(execSpec);
				}
			}
				// Now parse Flows : to do AFTER specs
			var flows = org.argeo.ria.util.Element.selectNodes(xmlNode, appendRoot + this.self(arguments).XPATH_EXECUTION_FLOWS);
			if(flows){
				for(var i=0;i<flows.length;i++){
					var execFlow = new org.argeo.slc.ria.execution.Flow();
					execFlow.setXmlNode(flows[i]);
					this.addExecutionFlow(execFlow);
				}
			}
		}
		
	}	
	
});