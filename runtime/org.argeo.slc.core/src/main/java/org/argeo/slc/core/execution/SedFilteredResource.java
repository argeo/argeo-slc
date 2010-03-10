package org.argeo.slc.core.execution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/** Experimental and suboptimal */
public class SedFilteredResource implements FactoryBean, InitializingBean {
	private Resource source;

	private List<String> filters = new ArrayList<String>();
	private Map<Pattern, String> patterns = new HashMap<Pattern, String>();

	private String charset = "UTF-8";
	private Charset cs;
	private CharsetDecoder decoder;
	private CharsetEncoder encoder;

	public Object getObject() throws Exception {
		if (filters.size() == 0)
			return source;

		//int capacity = 100 * 1024;// 100 KB
		ByteBuffer bb;
		if (source instanceof ByteArrayResource) {
			bb = ByteBuffer.wrap(((ByteArrayResource) source).getByteArray());
		} else {
			try {
				File file = source.getFile();
				FileInputStream fis = new FileInputStream(file);
				FileChannel fc = fis.getChannel();

				// Get the file's size and then map it into memory
				int sz = (int) fc.size();
				bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
			} catch (IOException e) {
				// ReadableByteChannel channel = Channels.newChannel(source
				// .getInputStream());
				// bb = ByteBuffer.allocateDirect(capacity);
				// int read = 0;
				// do {
				// read = channel.read(bb);
				// } while (read > 0);
				// FIXME : use nio to parse the stream as it goes
				bb = ByteBuffer.wrap(IOUtils.toByteArray(source
						.getInputStream()));
			}
		}
		CharBuffer cb = decoder.decode(bb);
		for (Pattern pattern : patterns.keySet()) {
			Matcher matcher = pattern.matcher(cb);
			String output = matcher.replaceAll(patterns.get(pattern));
			cb = CharBuffer.wrap(output);
		}
//		ByteBuffer bbout = encoder.encode(cb);
//		ByteArrayOutputStream out = new ByteArrayOutputStream(capacity);
//		WritableByteChannel wchannel = Channels.newChannel(out);
//		wchannel.write(bbout);
		ByteArrayResource res = new ByteArrayResource(cb.toString().getBytes());
		return res;
	}

	public Class<?> getObjectType() {
		return Resource.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public void afterPropertiesSet() throws Exception {
		cs = Charset.forName(charset);
		decoder = cs.newDecoder();
		encoder = cs.newEncoder();

		for (String sedStr : filters) {
			sedStr = sedStr.trim();
			if (sedStr.length() < 4)
				throw new SlcException(sedStr + " not properly formatted.");
			if (sedStr.charAt(0) != 's')
				throw new SlcException(sedStr + " not properly formatted.");
			Character sep = sedStr.charAt(1);
			List<String> tokens = new ArrayList<String>(4);
			StringTokenizer st = new StringTokenizer(sedStr, sep.toString());
			while (st.hasMoreTokens())
				tokens.add(st.nextToken());
			if (tokens.size() != 3 && tokens.size() != 4)
				throw new SlcException(sedStr + " not properly formatted.");
			patterns.put(Pattern.compile(tokens.get(1)), tokens.get(2));
		}
	}

	public void setSource(Resource source) {
		this.source = source;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
