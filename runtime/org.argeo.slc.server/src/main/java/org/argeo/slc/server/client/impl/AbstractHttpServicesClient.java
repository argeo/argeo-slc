package org.argeo.slc.server.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.Condition;
import org.argeo.slc.SlcException;
import org.argeo.slc.server.client.HttpServicesClient;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

public abstract class AbstractHttpServicesClient implements HttpServicesClient {
	private final static Log log = LogFactory
			.getLog(AbstractHttpServicesClient.class);
	private Unmarshaller unmarshaller;
	private String baseUrl;

	private Long retryPeriod = 1000l;
	@SuppressWarnings(value = { "unchecked" })
	public <T> T callService(String path, Map<String, String> parameters) {
		try {
			return (T) callServiceLowLevel(path, parameters);
		} catch (Exception e) {
			throw new SlcException("Cannot call service " + path + " on "
					+ baseUrl, e);
		}
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T> T callServiceSafe(String path, Map<String, String> parameters,
			Condition<T> condition, Long timeout) {
		long begin = System.currentTimeMillis();
		try {
			Object obj = null;
			long duration = System.currentTimeMillis() - begin;
			while (duration < timeout) {
				try {
					obj = callServiceLowLevel(path, parameters);
				} catch (IOException e) {
					if (log.isTraceEnabled())
						log.trace("Exception when calling service " + path
								+ " on " + baseUrl, e);
				}

				if (obj != null) {
					if (condition == null)
						break;
					else {
						if (condition.check((T) obj))
							break;
					}
				}
				// wait a bit
				try {
					Thread.sleep(retryPeriod);
				} catch (InterruptedException e) {
					// silent
				}
			}

			if (obj == null)
				throw new SlcException(
						"Service "
								+ path
								+ " on "
								+ baseUrl
								+ " did not return an answer after calling it safely for "
								+ duration + " ms.");
			return (T) obj;
		} catch (Exception e) {
			throw new SlcException(
					"Unexpected exception when safely calling service " + path
							+ " on " + baseUrl, e);
		}
	}

	protected Object callServiceLowLevel(String path,
			Map<String, String> parameters) throws IOException {
		Assert.notNull(baseUrl, "base url");
		InputStream in = null;
		try {
			URL url = new URL(baseUrl + path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					connection.addRequestProperty(key, parameters.get(key));
				}
			}

			connection.connect();

			in = connection.getInputStream();
			StreamSource source = new StreamSource(in);
			Object obj = unmarshaller.unmarshal(source);
			return obj;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Long getRetryPeriod() {
		return retryPeriod;
	}

	/** Retry period in ms when accessing service safely. Default is 1000 ms. */
	public void setRetryPeriod(Long retryPeriod) {
		this.retryPeriod = retryPeriod;
	}
}
