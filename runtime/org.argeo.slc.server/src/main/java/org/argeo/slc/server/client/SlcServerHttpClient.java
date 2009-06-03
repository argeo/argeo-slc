package org.argeo.slc.server.client;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.springframework.oxm.Unmarshaller;

public class SlcServerHttpClient {
	private Unmarshaller unmarshaller;
	private String baseUrl = "http://localhost:7070/org.argeo.slc.webapp/";

	@SuppressWarnings(value = { "unchecked" })
	public <T> T callService(String path, Map<String, String> parameters) {

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
			return (T) obj;
		} catch (Exception e) {
			throw new SlcException("Cannot call service " + path + " on "
					+ baseUrl, e);
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

}
