package org.argeo.slc.core.execution.http;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;

public class WebServiceTask implements Callable<Integer> {
	private String url;
	private String requContentType;
	private String respContentType;

	private AsynchronousByteChannel channel;

	public WebServiceTask(AsynchronousByteChannel channel, String url) {
		this(url, "application/json", "application/json", channel);
	}

	public WebServiceTask(String url, String requContentType, String respContentType, AsynchronousByteChannel channel) {
		this.url = url;
		this.requContentType = requContentType;
		this.respContentType = respContentType;
		this.channel = channel;
	}

	@Override
	public Integer call() throws Exception {
		// Webservice
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		InputStream in = Channels.newInputStream(channel);
		RequestEntity requestEntity = new InputStreamRequestEntity(in, requContentType);
		// StringRequestEntity requestEntity = new
		// StringRequestEntity(payloadStr, "application/json", "UTF-8");
		postMethod.setRequestEntity(requestEntity);
		httpClient.executeMethod(postMethod);
		InputStream answerIn = postMethod.getResponseBodyAsStream();
		ReadableByteChannel answer = Channels.newChannel(answerIn);
		ByteBuffer buffer = ByteBuffer.allocate(8 * 1024);
		int read = 0;
		Integer writeRes = 0;
		while (read != -1) {
			read = answer.read(buffer);
			if (read <= 0)
				break;
			buffer.flip();
			Future<Integer> f = channel.write(buffer);
			writeRes = writeRes + f.get();
			buffer.clear();
		}
		return writeRes;
	}

	public String getUrl() {
		return url;
	}

	public String getRequContentType() {
		return requContentType;
	}

	public String getRespContentType() {
		return respContentType;
	}

}
