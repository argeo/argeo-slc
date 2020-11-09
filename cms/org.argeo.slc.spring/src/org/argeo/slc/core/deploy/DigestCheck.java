package org.argeo.slc.core.deploy;

import java.io.File;
import java.io.IOException;

import org.argeo.slc.SlcException;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;

/** Add Spring capabilities to {@link DigestUtils} */
public class DigestCheck extends DigestUtils {
	public static String digest(String algorithm, Resource resource) {
		try {
			File file = resource.getFile();
			return org.argeo.util.DigestUtils.digest(algorithm, file);
		} catch (IOException e) {
			try {
				return org.argeo.util.DigestUtils.digest(algorithm,
						resource.getInputStream());
			} catch (IOException e1) {
				throw new SlcException("Cannot digest " + resource
						+ " with algorithm " + algorithm, e);
			}
		}
	}
}
