include sdk.mk
.PHONY: clean all osgi

all: osgi

A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \
org.argeo.slc.runtime \

BOOTSTRAP_BASE=$(SDK_BUILD_BASE)/bootstrap

distribution: bootstrap
	$(JVM) -cp \
	 $(BOOTSTRAP_BASE)/bndlib.jar:$(BOOTSTRAP_BASE)/slf4j-api.jar:$(BOOTSTRAP_BASE)/org.argeo.slc.api/bin:$(BOOTSTRAP_BASE)/org.argeo.slc.factory/bin \
	 tp/Make.java $(A2_OUTPUT)
	
bootstrap :
	mkdir -p $(SDK_BUILD_BASE)/bootstrap
	wget -c -O $(BOOTSTRAP_BASE)/ecj.jar https://repo1.maven.org/maven2/org/eclipse/jdt/ecj/3.28.0/ecj-3.28.0.jar
	wget -c -O $(BOOTSTRAP_BASE)/slf4j-api.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.28/slf4j-api-1.7.28.jar
	wget -c -O $(BOOTSTRAP_BASE)/bndlib.jar https://repo1.maven.org/maven2/biz/aQute/bnd/biz.aQute.bndlib/5.3.0/biz.aQute.bndlib-5.3.0.jar
	$(JVM) -cp $(BOOTSTRAP_BASE)/ecj.jar org.eclipse.jdt.internal.compiler.batch.Main -11 -nowarn -time -cp \
	 $(BOOTSTRAP_BASE)/bndlib.jar:$(BOOTSTRAP_BASE)/slf4j.jar \
	 org.argeo.slc.api/src[-d $(BOOTSTRAP_BASE)/org.argeo.slc.api/bin] \
	 org.argeo.slc.factory/src[-d $(BOOTSTRAP_BASE)/org.argeo.slc.factory/bin] \

clean:
	rm -rf $(BUILD_BASE)
	rm -rf $(BOOTSTRAP_BASE)

A2_OUTPUT = $(SDK_BUILD_BASE)/a2
A2_BASE = $(A2_OUTPUT)

DEP_CATEGORIES = org.argeo.tp org.argeo.tp.apache org.argeo.tp.sdk org.argeo.tp.jcr

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk