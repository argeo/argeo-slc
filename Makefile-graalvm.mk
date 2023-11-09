include sdk.mk

## THE BUNDLES MUST FIRST HAVE BEEN BUILT

A2_CATEGORY = org.argeo.slc

#GRAALVM_HOME = /opt/graalvm-community-openjdk-17
GRAALVM_HOME = /opt/mandrel-java17

DEP_CATEGORIES = \
log/syslogger/org.argeo.tp \
org.argeo.tp \
org.argeo.tp.httpd \
osgi/api/org.argeo.tp.osgi \
osgi/equinox/org.argeo.tp.eclipse \
swt/rap/org.argeo.tp.swt \
org.argeo.cms \
swt/org.argeo.cms \
swt/rap/org.argeo.cms \
swt/rap/org.argeo.slc \

#	$(foreach a2_base, $(A2_BASE), # TODO when ThinLogging is fixed, use deployed jars
DEP_JARS = $(foreach category, $(DEP_CATEGORIES), \
	$(foreach a2_base, $(A2_OUTPUT), \
		$(shell find $(a2_base)/$(category) -name '*.jar') \
	) \
)
DEP_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(DEP_JARS)))

graalvm-custom:
	$(GRAALVM_HOME)/bin/java -jar $(ECJ_JAR) @$(SDK_SRC_BASE)/sdk/argeo-build/ecj.args -cp $(A2_CLASSPATH) \
		graalvm/org.argeo.slc.graalvm/src[-d $(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin]

#tool-server: osgi graalvm-custom
#:$(SDK_BUILD_BASE)/$(A2_CATEGORY)/graalvm/bin

LIBEXEC_DIR=$(A2_OUTPUT)/libexec/linux/x86_64/$(A2_CATEGORY)

tool-server: 
	mkdir -p $(LIBEXEC_DIR)
	cd $(LIBEXEC_DIR) && $(GRAALVM_HOME)/bin/native-image \
		-cp $(DEP_CLASSPATH) \
		--enable-url-protocols=http,https \
		-H:AdditionalSecurityProviders=sun.security.jgss.SunProvider \
		--initialize-at-build-time=org.argeo.init.logging.ThinLogging,org.argeo.init.logging.ThinLogging$LogEntryPublisher,org.slf4j.LoggerFactory \
		--no-fallback \
		-Dargeo.logging.synchronous=true \
		 org.argeo.tool.server.ArgeoServer \
		 argeo

#		--static \

#		-H:+StaticExecutableWithDynamicLibC \
# 		-H:Name=argeo \

# Use --verbose in order to see whcih configurations are used 
		 
include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk