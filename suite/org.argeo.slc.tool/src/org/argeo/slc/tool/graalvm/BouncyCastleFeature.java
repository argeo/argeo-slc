package org.argeo.slc.tool.graalvm;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeClassInitialization;
import org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport;

import com.oracle.svm.core.annotate.AutomaticFeature;

import net.i2p.crypto.eddsa.EdDSASecurityProvider;

/**
 * BounvyCastle JCE provider needs to be registered at build time.
 * 
 * @see https://github.com/oracle/graal/issues/2800#issuecomment-702480444
 * @see https://github.com/micronaut-projects/micronaut-oracle-cloud/pull/17/files#r498585779
 */
@AutomaticFeature
public class BouncyCastleFeature implements Feature {

	@Override
	public void afterRegistration(AfterRegistrationAccess access) {
		RuntimeClassInitialization.initializeAtBuildTime("org.bouncycastle");
		Security.addProvider(new BouncyCastleProvider());
		
		RuntimeClassInitialization.initializeAtBuildTime("net.i2p.crypto.eddsa");
		Security.addProvider(new EdDSASecurityProvider());

		RuntimeClassInitializationSupport rci = ImageSingletons.lookup(RuntimeClassInitializationSupport.class);
		rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$Default", "dependency with native random");
		rci.rerunInitialization("org.bouncycastle.jcajce.provider.drbg.DRBG$NonceAndIV",
				"dependency with native random");
	}

}
