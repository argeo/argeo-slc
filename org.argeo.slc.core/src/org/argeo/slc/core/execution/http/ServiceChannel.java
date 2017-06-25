package org.argeo.slc.core.execution.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ServiceChannel implements AsynchronousByteChannel {
	private final ReadableByteChannel in;
	private final WritableByteChannel out;

	private boolean open = true;

	private ExecutorService executor;

	public ServiceChannel(ReadableByteChannel in, WritableByteChannel out, ExecutorService executor) {
		this.in = in;
		this.out = out;
		this.executor = executor;
	}

	@Override
	public Future<Integer> read(ByteBuffer dst) {
		return executor.submit(() -> in.read(dst));
	}

	@Override
	public <A> void read(ByteBuffer dst, A attachment, CompletionHandler<Integer, ? super A> handler) {
		try {
			Future<Integer> res = read(dst);
			handler.completed(res.get(), attachment);
		} catch (Exception e) {
			handler.failed(e, attachment);
		}
	}

	@Override
	public Future<Integer> write(ByteBuffer src) {
		return executor.submit(() -> out.write(src));
	}

	@Override
	public <A> void write(ByteBuffer src, A attachment, CompletionHandler<Integer, ? super A> handler) {
		try {
			Future<Integer> res = write(src);
			handler.completed(res.get(), attachment);
		} catch (Exception e) {
			handler.failed(e, attachment);
		}
	}

	@Override
	/** NB: it does not close the underlying channels. */
	public void close() throws IOException {
		open = false;
		notifyAll();
	}

	@Override
	public boolean isOpen() {
		return open;
	}

}
