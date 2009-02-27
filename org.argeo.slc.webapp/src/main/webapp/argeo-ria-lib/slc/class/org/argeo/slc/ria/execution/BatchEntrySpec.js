/**
 * Data model for an entry of the Batch list : original Spec, flow and module, and currently computed value.
 */
qx.Class.define("org.argeo.slc.ria.execution.BatchEntrySpec", {
	extend : org.argeo.slc.ria.execution.Spec,
	
	properties : {
		module :{},
		flow : {},
		originalSpec : {}		
	},
	
	construct : function(module, flow){
		this.base(arguments);
		this.setModule(module);
		this.setFlow(flow);
		this.setOriginalSpec(flow.getExecutionSpec());
	}
});