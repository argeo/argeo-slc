package org.argeo.slc.graalvm.feature;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;

import net.i2p.crypto.eddsa.EdDSASecurityProvider;

/** Required native image customisations. */
public class ArgeoToolFeature implements Feature {

	@Override
	public void afterRegistration(AfterRegistrationAccess access) {
		// JCE providers need to be registered at build time.
		// https://github.com/oracle/graal/issues/2800#issuecomment-702480444
		// https://github.com/micronaut-projects/micronaut-oracle-cloud/pull/17/files#r498585779
		RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle");
		Security.addProvider(new BouncyCastleProvider());

		RuntimeClassInitialization.initializeAtBuildTime("net.i2p.crypto.eddsa");
		Security.addProvider(new EdDSASecurityProvider());

		// required for Tomcat JNI DLL to load properly
		RuntimeClassInitialization.initializeAtBuildTime("org.apache.tomcat.jni");

		RuntimeClassInitializationSupport rci = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
		rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$Default", "dependency with native random");
		rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$NonceAndIV",
				"dependency with native random");
	}

}
