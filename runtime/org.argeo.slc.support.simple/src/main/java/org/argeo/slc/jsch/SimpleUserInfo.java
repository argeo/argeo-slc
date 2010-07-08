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

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.SlcException;

import com.jcraft.jsch.UserInfo;

public class SimpleUserInfo implements UserInfo {
	private Boolean permissive = true;
	private Boolean verbose = false;

	private final static Log log = LogFactory.getLog(SimpleUserInfo.class);

	protected String password;
	protected char[] passwordSafe;
	protected String passphrase;
	protected char[] passphraseSafe;

	public void reset() {
		if (passwordSafe != null)
			Arrays.fill(passwordSafe, (char) 0);
		passwordSafe = null;
		if (passphraseSafe != null)
			Arrays.fill(passphraseSafe, (char) 0);
		passphraseSafe = null;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	public String getPassphrase() {
		if (passphraseSafe != null)
			return new String(passphraseSafe);
		return passphrase;
	}

	public String getPassword() {
		if (passwordSafe != null)
			return new String(passwordSafe);
		return password;
	}

	public boolean promptPassphrase(String message) {
		if (permissive)
			return true;
		else {
			log.info(message);
			passwordSafe = readPassword(System.in);
			return passwordSafe != null;
		}
	}

	public boolean promptPassword(String message) {
		if (permissive)
			return true;
		else {
			log.info(message);
			passwordSafe = readPassword(System.in);
			return passwordSafe != null;
		}
	}

	public boolean promptYesNo(String message) {
		String msg = message + " (y/n): ";
		if (permissive) {
			if (verbose)
				log.info(msg + "y");
			return true;
		} else {
			log.info(msg);
			char c;
			try {
				c = (char) System.in.read();
			} catch (IOException e) {
				throw new SlcException("Cannot read stdin", e);
			}
			if (c == 'y')
				return true;
			else
				return false;
		}
	}

	public void showMessage(String message) {
		log.info(message);
	}

	public void setPermissive(Boolean permissive) {
		this.permissive = permissive;
	}

	public void setVerbose(Boolean verbose) {
		this.verbose = verbose;
	}

	protected char[] readPassword(InputStream in) {

		try {
			char[] lineBuffer;
			char[] buf;
			// int i;

			buf = lineBuffer = new char[128];

			int room = buf.length;
			int offset = 0;
			int c;

			loop: while (true) {
				switch (c = in.read()) {
				case -1:
				case '\n':
					break loop;

				case '\r':
					int c2 = in.read();
					if ((c2 != '\n') && (c2 != -1)) {
						if (!(in instanceof PushbackInputStream)) {
							in = new PushbackInputStream(in);
						}
						((PushbackInputStream) in).unread(c2);
					} else
						break loop;

				default:
					if (--room < 0) {
						buf = new char[offset + 128];
						room = buf.length - offset - 1;
						System.arraycopy(lineBuffer, 0, buf, 0, offset);
						Arrays.fill(lineBuffer, ' ');
						lineBuffer = buf;
					}
					buf[offset++] = (char) c;
					break;
				}
			}

			if (offset == 0) {
				return null;
			}

			char[] ret = new char[offset];
			System.arraycopy(buf, 0, ret, 0, offset);
			Arrays.fill(buf, ' ');

			return ret;
		} catch (IOException e) {
			throw new SlcException("Cannot read password.", e);
		}
	}

}
