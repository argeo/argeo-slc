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
			check : "Map",
			init : {}
		},
		/**
		 * All execution specs registered by their name
		 */
		executionSpecs : {
			check : "Map", 
			init : {}
		},
		/**
		 * XML description (castor)
		 */
		xmlNode : {
			apply : "_applyXmlNode"
		}
	},
	
	statics : {
		XPATH_NAME : "slc:execution-module-descriptor/slc:name",
		XPATH_VERSION : "slc:execution-module-descriptor/slc:version",
		XPATH_EXECUTION_FLOWS : "slc:execution-module-descriptor/slc:executionFlows/slc:execution-flow-descriptor",
		XPATH_EXECUTION_SPECS : "slc:execution-module-descriptor/slc:executionSpecs/slc:simple-execution-spec"
	},
	
	construct : function(){
		this.base(arguments);
	},
	
	members : {
		/**
		 * Add an execution flow to this module
		 * @param executionFlow {org.argeo.slc.ria.execution.Flow}
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
		 * @param executionSpec {org.argeo.slc.ria.execution.Spec}
		 */
		addExecutionSpec : function(executionSpec){
			this.getExecutionSpecs()[executionSpec.getName()] = executionSpec;
		},
		
		getExecutionSpecByName : function(name){
			return this.getExecutionSpecs()[name];
		},
		
		getExecutionFlowByName : function(name){
			return this.getExecutionFlows()[name];
		},
		
		/**
		 * An xml node containing the castor mapped description of this object
		 * @param xmlNode {Node}
		 */
		_applyXmlNode : function(xmlNode){
			// Parse now
			this.setName(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_NAME));
			this.setVersion(org.argeo.ria.util.Element.getSingleNodeText(xmlNode, this.self(arguments).XPATH_VERSION));
			// Parse Specs first
			var specs = org.argeo.ria.util.Element.selectNodes(xmlNode, this.self(arguments).XPATH_EXECUTION_SPECS);
			for(i=0; i< specs.length;i++){
				var execSpec = new org.argeo.slc.ria.execution.Spec();
				execSpec.setXmlNode(specs[i]);
				this.addExecutionSpec(execSpec);
			}
			// Now parse Flows : to do AFTER specs
			var flows = org.argeo.ria.util.Element.selectNodes(xmlNode, this.self(arguments).XPATH_EXECUTION_FLOWS);
			for(var i=0;i<flows.length;i++){
				var execFlow = new org.argeo.slc.ria.execution.Flow();
				execFlow.setXmlNode(flows[i]);
				this.addExecutionFlow(execFlow);
			}
		}
		
	}	
	
});