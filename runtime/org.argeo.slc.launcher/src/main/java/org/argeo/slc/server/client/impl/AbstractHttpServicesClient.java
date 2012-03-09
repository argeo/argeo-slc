/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.server.client.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.Condition;
import org.argeo.slc.SlcException;
import org.argeo.slc.server.client.HttpServicesClient;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;

public abstract class AbstractHttpServicesClient implements HttpServicesClient {
	private final static Log log = LogFactory
			.getLog(AbstractHttpServicesClient.class);

	private String user;
	private String password;

	private Unmarshaller unmarshaller;
	private Marshaller marshaller;
	private String baseUrl;
	private String encoding = "UTF-8";

	private Long retryPeriod = 1000l;
	private Long defaultTimeout = 30 * 1000l;

	public void init() {
		if (user != null && password != null)
			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password
							.toCharArray());
				}
			});
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T> T callService(String path, Map<String, String> parameters) {
		return (T) callService(path, parameters, null);
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T> T callService(String path, Map<String, String> parameters,
			Object body) {
		try {
			return (T) callServiceLowLevel(path, parameters, body);
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
			while (System.currentTimeMillis() - begin < timeout(timeout)) {
				try {
					obj = callServiceLowLevel(path, parameters, null);
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
								+ timeout(timeout) + " ms.");
			return (T) obj;
		} catch (Exception e) {
			throw new SlcException(
					"Unexpected exception when safely calling service " + path
							+ " on " + baseUrl, e);
		}
	}

	protected Object callServiceLowLevel(String path,
			Map<String, String> parameters, Object body) throws IOException {

		Assert.notNull(baseUrl, "base url");
		HttpURLConnection connection = null;
		Writer writer = null;
		Reader reader = null;
		try {
			URL url = createUrl(path, parameters);
			connection = (HttpURLConnection) url.openConnection();

			if (body != null) {
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setDoInput(true);
				connection.setUseCaches(false);
				connection.setAllowUserInteraction(false);
				connection.setRequestProperty("Content-type",
						"text/xml; charset=" + encoding);
			}

			// Establish the connection
			connection.connect();

			if (body != null) {
				writer = new OutputStreamWriter(connection.getOutputStream(),
						encoding);
				StreamResult result = new StreamResult(writer);
				marshaller.marshal(body, result);
				writer.flush();
				IOUtils.closeQuietly(writer);
			}

			// Read answer
			reader = new InputStreamReader(connection.getInputStream(),
					encoding);
			Source source = new StreamSource(reader);
			Object obj = unmarshaller.unmarshal(source);
			return obj;
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(writer);
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	protected URL createUrl(String service, Map<String, String> parameters) {
		// URL encoded with UTF-8, as recommended by W3C
		final String urlEncoding = "UTF-8";

		StringBuffer buf = new StringBuffer(baseUrl + service);
		try {
			if (parameters != null && parameters.size() != 0) {
				buf.append('?');
				boolean first = true;
				for (String key : parameters.keySet()) {
					String value = parameters.get(key);
					if (value != null) {
						if (first)
							first = false;
						else
							buf.append('&');
						String keyEncoded = URLEncoder.encode(key, urlEncoding);
						String valueEncoded = URLEncoder.encode(value,
								urlEncoding);
						buf.append(keyEncoded).append('=').append(valueEncoded);
					}
				}
			}

			return new URL(buf.toString());
		} catch (Exception e) {
			throw new SlcException("Cannot create URL: " + buf, e);
		}
	}

	public Long timeout(Long timeout) {
		if (timeout == null)
			timeout = getDefaultTimeout();
		return timeout;
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

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	/** Default is UTF-8. */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/** Default is 30s */
	public void setDefaultTimeout(Long defaultTimeout) {
		this.defaultTimeout = defaultTimeout;
	}

	public Long getDefaultTimeout() {
		return defaultTimeout;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
