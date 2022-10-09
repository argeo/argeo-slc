include sdk.mk
.PHONY: clean all

all: distribution
	$(MAKE) -C ext


BOOTSTRAP_BASE=$(SDK_BUILD_BASE)/bootstrap

A2_OUTPUT = $(SDK_BUILD_BASE)/a2

distribution: bootstrap
	$(JVM) -cp \
	 $(BOOTSTRAP_BASE)/bndlib.jar:$(BOOTSTRAP_BASE)/slf4j-api.jar:$(BOOTSTRAP_BASE)/org.argeo.slc.api/bin:$(BOOTSTRAP_BASE)/org.argeo.slc.factory/bin \
	 tp/Make.java $(A2_OUTPUT)
	
bootstrap :
	mkdir -p $(SDK_BUILD_BASE)/bootstrap
	wget -c -O $(BOOTSTRAP_BASE)/ecj.jar https://repo1.maven.org/maven2/org/eclipse/jdt/ecj/3.29.0/ecj-3.29.0.jar
	wget -c -O $(BOOTSTRAP_BASE)/slf4j-api.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.28/slf4j-api-1.7.28.jar
	wget -c -O $(BOOTSTRAP_BASE)/bndlib.jar https://repo1.maven.org/maven2/biz/aQute/bnd/biz.aQute.bndlib/5.3.0/biz.aQute.bndlib-5.3.0.jar
	$(JVM) -cp $(BOOTSTRAP_BASE)/ecj.jar org.eclipse.jdt.internal.compiler.batch.Main -11 -nowarn -time -cp \
	 $(BOOTSTRAP_BASE)/bndlib.jar:$(BOOTSTRAP_BASE)/slf4j.jar \
	 org.argeo.slc.api/src[-d $(BOOTSTRAP_BASE)/org.argeo.slc.api/bin] \
	 org.argeo.slc.factory/src[-d $(BOOTSTRAP_BASE)/org.argeo.slc.factory/bin] \

clean:
	rm -rf $(BOOTSTRAP_BASE)
	rm -rf $(A2_OUTPUT)/org.argeo.tp
	rm -rf $(A2_OUTPUT)/org.argeo.tp.*
	$(MAKE) -f Makefile-ext.mk clean

include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk