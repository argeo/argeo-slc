include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
swt/rcp/org.argeo.tool.rcp.cli \
swt/rcp/org.argeo.tool.desktop \

DEP_CATEGORIES = \
org.argeo.tp \
osgi/api/org.argeo.tp.osgi \
swt/rcp/org.argeo.tp.swt \
lib/linux/x86_64/swt/rcp/org.argeo.tp.swt \
swt/rcp/org.argeo.tp.swt.workbench \
org.argeo.cms \
swt/org.argeo.cms \
swt/rcp/org.argeo.cms \

clean:
	rm -rf $(BUILD_BASE)

GRAALVM_HOME = /opt/graalvm-ce
A2_BUNDLES_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(A2_BUNDLES)))

graalvm-custom:
	$(GRAALVM_HOME)/bin/java -jar $(ECJ_JAR) @$(SDK_SRC_BASE)/sdk/argeo-build/ecj.args -cp $(A2_CLASSPATH) \
		graalvm/org.argeo.slc.graalvm/src[-d $(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin]

tool-desktop: graalvm-custom
	mkdir -p $(A2_OUTPUT)/libexec/$(A2_CATEGORY)
	cd $(A2_OUTPUT)/libexec/$(A2_CATEGORY) && $(GRAALVM_HOME)/bin/native-image \
		-cp $(A2_CLASSPATH):$(A2_BUNDLES_CLASSPATH):$(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin \
		--features=org.argeo.slc.graalvm.feature.ArgeoToolFeature \
		--enable-url-protocols=http,https \
		-H:AdditionalSecurityProviders=sun.security.jgss.SunProvider,org.bouncycastle.jce.provider.BouncyCastleProvider,net.i2p.crypto.eddsa.EdDSASecurityProvider \
		--initialize-at-build-time=org.argeo.init.logging.ThinLogging,org.slf4j.LoggerFactory \
		--trace-object-instantiation=java.lang.Thread \
		-H:+ReportExceptionStackTraces \
		--no-fallback \
		 org.argeo.tool.desktop.ArgeoDesktop \
		 argeo-desktop
 

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk