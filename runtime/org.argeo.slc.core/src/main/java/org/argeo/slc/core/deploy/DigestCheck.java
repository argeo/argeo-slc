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

package org.argeo.slc.core.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;

public class DigestCheck {
	private static Boolean debug = true;
	// TODO: make it writable
	private final static Integer byteBufferCapacity = 100 * 1024;// 100 KB

	public static String digest(String algorithm, Resource resource) {
		try {
			File file = resource.getFile();
			return digest(algorithm, file);
		} catch (IOException e) {
			try {
				return digest(algorithm, resource.getInputStream());
			} catch (IOException e1) {
				throw new SlcException("Cannot digest " + resource
						+ " with algorithm " + algorithm, e);
			}
		}
	}

	public static String digest(String algorithm, InputStream in) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			// ReadableByteChannel channel = Channels.newChannel(in);
			// ByteBuffer bb = ByteBuffer.allocateDirect(byteBufferCapacity);
			// while (channel.read(bb) > 0)
			// digest.update(bb);
			byte[] buffer = new byte[byteBufferCapacity];
			int read = 0;
			while ((read = in.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}

			byte[] checksum = digest.digest();
			String res = Hex.encodeHexString(checksum);
			return res;
		} catch (Exception e) {
			throw new SlcException("Cannot digest with algorithm " + algorithm,
					e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String digest(String algorithm, File file) {
		FileInputStream fis = null;
		FileChannel fc = null;
		try {
			fis = new FileInputStream(file);
			fc = fis.getChannel();

			// Get the file's size and then map it into memory
			int sz = (int) fc.size();
			ByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
			return digest(algorithm, bb);
		} catch (IOException e) {
			throw new SlcException("Cannot digest " + file + " with algorithm "
					+ algorithm, e);
		} finally {
			IOUtils.closeQuietly(fis);
			if (fc.isOpen())
				try {
					fc.close();
				} catch (IOException e) {
					// silent
				}
		}
	}

	protected static String digest(String algorithm, ByteBuffer bb) {
		long begin = System.currentTimeMillis();
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			digest.update(bb);
			byte[] checksum = digest.digest();
			String res = Hex.encodeHexString(checksum);
			long end = System.currentTimeMillis();
			if (debug)
				System.out.println((end - begin) + " ms / "
						+ ((end - begin) / 1000) + " s");
			return res;
		} catch (NoSuchAlgorithmException e) {
			throw new SlcException("Cannot digest with algorithm " + algorithm,
					e);
		}
	}

	public static void main(String[] args) {
		File file;
		if (args.length > 0)
			file = new File(args[0]);
		else {
			System.err.println("Usage: <file> [<algorithm>]"
					+ " (see http://java.sun.com/j2se/1.5.0/"
					+ "docs/guide/security/CryptoSpec.html#AppA)");
			return;
		}

		if (args.length > 1) {
			String algorithm = args[1];
			System.out.println(digest(algorithm, file));
		} else {
			String algorithm = "MD5";
			System.out.println(algorithm + ": " + digest(algorithm, file));
			algorithm = "SHA";
			System.out.println(algorithm + ": " + digest(algorithm, file));
			algorithm = "SHA-256";
			System.out.println(algorithm + ": " + digest(algorithm, file));
			algorithm = "SHA-512";
			System.out.println(algorithm + ": " + digest(algorithm, file));
		}
	}

}
