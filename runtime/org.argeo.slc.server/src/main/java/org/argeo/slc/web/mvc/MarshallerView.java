package org.argeo.slc.web.mvc;

import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.oxm.Marshaller;
import org.springframework.web.servlet.view.AbstractView;

/** Marshal one of the object of the map to the output. */
public class MarshallerView extends AbstractView {
	
	private final static Log log = LogFactory
	.getLog(MarshallerView.class);

	
	private String modelKey = null;
	private final Marshaller marshaller;

	public MarshallerView(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public MarshallerView(Marshaller marshaller, String modelKey) {
		this(marshaller);
		this.modelKey = modelKey;
	}

	@Override
	@SuppressWarnings(value={"unchecked"})
	protected void renderMergedOutputModel(Map model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final Object answer;
	
		if (log.isDebugEnabled()){
			//log.debug("In MarshallerView :: renderMergedOutputModel");
			if (modelKey != null )
				log.debug("Model key : ["+modelKey+"]");
			else log.debug("ModelKey is null");
		}
		
		//TODO : remove this loop
		if (log.isDebugEnabled()){
			Iterator it = model.keySet().iterator();
			int i = 0;
			while (it.hasNext())
					log.debug(i++ + " [" + it.next().toString()+ "]");
			}
		
		if (modelKey != null) {
			if (!model.containsKey(modelKey)){
			/** //TODO : remove this loop
				if (log.isDebugEnabled()){
					log.debug("Key not found in Model. Available keys are : ");
					for (Object key : model.keySet()){
						log.debug(key.toString());
					}
				}
			**/
				throw new SlcException("Key " + modelKey
						+ " not found in model.");
				}
			answer = model.get(modelKey);
		} else {
			if (model.size() != 1)
				throw new SlcException(
						"Model has a size different from 1. Specify a modelKey.");
			answer = model.values().iterator().next();
		}

		StreamResult streamResult = new StreamResult(response.getOutputStream());
		marshaller.marshal(answer, streamResult);
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

}
