/**
 * Data model for an entry of the Batch list : original Spec, flow and module, and currently computed value.
 */
qx.Class.define("org.argeo.slc.ria.execution.BatchEntrySpec", {
	extend : org.argeo.slc.ria.execution.Spec,
	
	properties : {
		/**
		 * Reference module
		 */
		module :{},
		/**
		 * Reference flow
		 */
		flow : {},
		/**
		 * Original Spec (values shall stay untouched).
		 */
		originalSpec : {}		
	},
	
	/**
	 * Instance of BatchEntrySpec
	 * @param module {org.argeo.slc.ria.execution.Module} Reference module
	 * @param flow {org.argeo.slc.ria.execution.Flow} Reference flow
	 */
	construct : function(module, flow){
		this.base(arguments);
		this.setModule(module);
		this.setFlow(flow);
		this.setOriginalSpec(flow.getExecutionSpec());
		this.setName(flow.getExecutionSpec().getName());
		this.fetchInstanceValues();
	},
	
	members :  {
		/**
		 * Create a label to display in the batch list.
		 * @return {String} The label
		 */
		getLabel : function(){
			var label = this.getModule().getName();
			label += "/" + this.getModule().getVersion();
			label += "/" + this.getFlow().getName();
			return label;
		},
				
		/**
		 * Fetch the Spec Values with the Flow Values to make the current instance value
		 */
		fetchInstanceValues : function(){
			var specValues = this.getOriginalSpec().getValues();
			var flow = this.getFlow();
			var instanceValues = {};
			for(var key in specValues){
				var flowValue = flow.getValue(
									key, 
									specValues[key].getSpecType(), 
									specValues[key].getSpecSubType()
								);
				var instValue = specValues[key].clone();
				if(flowValue){
					instValue.setValue(flowValue);
				}
				instanceValues[key] = instValue;
			}
			this.setValues(instanceValues);
			//this.debug(instanceValues);
		}
	}
});