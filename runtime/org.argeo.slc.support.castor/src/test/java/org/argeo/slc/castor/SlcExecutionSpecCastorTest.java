package org.argeo.slc.castor;

import org.argeo.slc.process.SlcExecutionSpec;
import org.argeo.slc.process.SlcExecutionSpecField;

public class SlcExecutionSpecCastorTest extends AbstractCastorTestCase {
	public void testMarshalling() throws Exception {
		SlcExecutionSpec spec = new SlcExecutionSpec();
		
		SlcExecutionSpecField field1 = new SlcExecutionSpecField();
		field1.setLabel("Field1");
		field1.setType("text");
		field1.setDefaultValue("def");
		spec.getExecutionSpecFields().put("field1", field1);
		
		SlcExecutionSpecField field2 = new SlcExecutionSpecField();
		field2.setLabel("Field2");
		field2.setType("list");
		field2.setListValues("def,nodef");
		field2.setDefaultValue("def");
		spec.getExecutionSpecFields().put("field2", field2);
		
		SlcExecutionSpec specUnm = marshUnmarsh(spec,false);		
		
		
	}
}
