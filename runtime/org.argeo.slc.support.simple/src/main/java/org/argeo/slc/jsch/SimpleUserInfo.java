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
	private final static Log log = LogFactory.getLog(SimpleUserInfo.class);

	private String password;
	private char[] passwordSafe;

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassphrase() {
		return null;
	}

	public String getPassword() {
		if (passwordSafe != null)
			return new String(passwordSafe);
		return password;
	}

	public boolean promptPassphrase(String message) {
		return true;
	}

	public boolean promptPassword(String message) {
		log.info(message);
		if (permissive)
			return true;
		else {
			passwordSafe = readPassword(System.in);
			return passwordSafe != null;
		}
	}

	public boolean promptYesNo(String message) {
		String msg = message + " (y/n): ";
		if (permissive) {
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

	protected char[] readPassword(InputStream in) {

		try {
			char[] lineBuffer;
			char[] buf;
			//int i;

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
