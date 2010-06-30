/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.jsch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

public class SshShell extends AbstractJschTask {
	private final static Log log = LogFactory.getLog(SshShell.class);
	private Resource input;

	@Override
	void run(Session session) {
		try {
			final Channel channel = session.openChannel("shell");

			// Enable agent-forwarding.
			// ((ChannelShell)channel).setAgentForwarding(true);

			// channel.setInputStream(System.in);
			// channel.setInputStream(input.getInputStream());
			/*
			 * // a hack for MS-DOS prompt on Windows.
			 * channel.setInputStream(new FilterInputStream(System.in){ public
			 * int read(byte[] b, int off, int len)throws IOException{ return
			 * in.read(b, off, (len>1024?1024:len)); } });
			 */

			// channel.setOutputStream(System.out);

			/*
			 * // Choose the pty-type "vt102".
			 * ((ChannelShell)channel).setPtyType("vt102");
			 */

			/*
			 * // Set environment variable "LANG" as "ja_JP.eucJP".
			 * ((ChannelShell)channel).setEnv("LANG", "ja_JP.eucJP");
			 */

			// Writer thread
			final BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(channel.getOutputStream()));

			// channel.connect();
			channel.connect(3 * 1000);

			// while (!channel.isConnected())
			// try {
			// Thread.sleep(500);
			// } catch (InterruptedException e1) {
			// // silent
			// }

			Thread writerThread = new Thread("Shell writer " + getSshTarget()) {

				@Override
				public void run() {

					if (log.isDebugEnabled())
						log.debug("Start writing to shell");

					BufferedReader reader = null;
					try {
						reader = new BufferedReader(new InputStreamReader(input
								.getInputStream()));
						String line = null;
						while ((line = reader.readLine()) != null) {
							if (!StringUtils.hasText(line))
								continue;
							writer.write(line);
							writer.newLine();
						}
						writer.append("exit");
						writer.newLine();
						writer.flush();
						// channel.disconnect();
					} catch (IOException e) {
						throw new SlcException("Cannot write to shell on "
								+ getSshTarget(), e);
					} finally {
						IOUtils.closeQuietly(reader);
					}
				}
			};
			writerThread.start();

			BufferedReader execIn = null;
			try {
				execIn = new BufferedReader(new InputStreamReader(channel
						.getInputStream()));
				String line = null;
				while ((line = execIn.readLine()) != null) {
					if (!line.trim().equals(""))
						log.info(line);
				}
			} catch (Exception e) {
				throw new SlcException("Cannot read from shell on "
						+ getSshTarget(), e);
			} finally {
				IOUtils.closeQuietly(execIn);
			}

		} catch (Exception e) {
			throw new SlcException("Cannot use SSH shell on " + getSshTarget(),
					e);
		}
	}

	public void setInput(Resource input) {
		this.input = input;
	}

}
