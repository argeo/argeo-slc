include sdk.mk
.PHONY: clean all osgi jni

all: osgi jni
	$(MAKE) -f Makefile-rcp.mk all

jni:
	$(MAKE) -C jni

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.runtime \
org.argeo.slc.cms \
org.argeo.slc.repo \
org.argeo.slc.rpmfactory \
org.argeo.slc.jcr \
lib/linux/org.argeo.slc.systemd \
swt/org.argeo.tool.swt \
swt/org.argeo.tool.devops.e4 \
swt/rap/org.argeo.tool.rap.cli \
swt/rap/org.argeo.tool.server \

DEP_CATEGORIES = \
org.argeo.tp \
org.argeo.tp.sdk \
org.argeo.tp.build \
org.argeo.tp.crypto \
org.argeo.tp.jetty \
org.argeo.tp.sql \
org.argeo.tp.utils \
org.argeo.tp.jcr \
org.argeo.tp.gis \
osgi/api/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
swt/rap/org.argeo.tp.swt.workbench \
org.argeo.cms \
org.argeo.cms.jcr \
swt/org.argeo.cms \
swt/org.argeo.cms.jcr \
swt/rap/org.argeo.cms \

clean:
	rm -rf $(BUILD_BASE)
	$(MAKE) -C jni clean
	$(MAKE) -f Makefile-rcp.mk clean

GRAALVM_HOME = /opt/graalvm-ce
A2_BUNDLES_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(A2_BUNDLES)))

graalvm-custom:
	$(GRAALVM_HOME)/bin/java -jar $(ECJ_JAR) @$(SDK_SRC_BASE)/sdk/argeo-build/ecj.args -cp $(A2_CLASSPATH) \
		graalvm/org.argeo.slc.graalvm/src[-d $(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin]

tool-server: osgi graalvm-custom
	mkdir -p $(A2_OUTPUT)/libexec/$(A2_CATEGORY)
	cd $(A2_OUTPUT)/libexec/$(A2_CATEGORY) && $(GRAALVM_HOME)/bin/native-image \
		-cp $(A2_CLASSPATH):$(A2_BUNDLES_CLASSPATH):$(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin \
		--enable-url-protocols=http,https \
		-H:AdditionalSecurityProviders=sun.security.jgss.SunProvider \
		--initialize-at-build-time=org.argeo.init.logging.ThinLogging,org.slf4j.LoggerFactory \
		--no-fallback \
		-Dargeo.logging.synchronous=true \
		 org.argeo.tool.server.ArgeoServer \
		 argeo


include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk