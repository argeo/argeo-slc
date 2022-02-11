include sdk.mk
.PHONY: clean all osgi

all: osgi distribution

BUNDLE_PREFIX = org.argeo
A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \
org.argeo.slc.runtime \
ext/org.argeo.ext.slf4j \
ext/org.argeo.ext.equinox.jetty \

BUILD_CLASSPATH_FEDORA = \
/usr/share/java/osgi-core/osgi.core.jar:$\
/usr/share/java/osgi-compendium/osgi.cmpn.jar:$\
/usr/share/java/ecj/ecj.jar:$\
/usr/share/java/aqute-bnd/biz.aQute.bndlib.jar:$\
/usr/share/java/slf4j/api.jar:$\
/usr/share/java/commons-io.jar:$\
/usr/share/java/commons-cli.jar:$\
/usr/share/java/commons-exec.jar:$\

BUILD_CLASSPATH = \
/usr/share/java/osgi.core.jar:$\
/usr/share/java/osgi.cmpn.jar:$\
/usr/share/java/ecj.jar:$\
/usr/share/java/bndlib.jar:$\
/usr/share/java/slf4j-api.jar:$\
/usr/share/java/commons-io.jar:$\
/usr/share/java/commons-cli.jar:$\
/usr/share/java/commons-exec.jar:$\

# TODO relativize from SDK_SRC_BASE
BUILD_BASE = $(SDK_BUILD_BASE)



#
# GENERIC
#
JVM := /usr/bin/java
JAVADOC := /usr/bin/javadoc
ECJ_JAR := $(SDK_BUILD_BASE)/a2/org.argeo.tp.sdk/org.eclipse.jdt.core.compiler.batch.3.28.jar
BND_TOOL := /usr/bin/bnd

WORKSPACE_BNDS := $(shell cd $(SDK_SRC_BASE) && find cnf -name '*.bnd') 
BUILD_WORKSPACE_BNDS := $(WORKSPACE_BNDS:%=$(SDK_BUILD_BASE)/%) $(WORKSPACE_BNDS:%=$(SDK_BUILD_BASE)/ext/%)

cnf: $(BUILD_WORKSPACE_BNDS)

A2_BUNDLES = $(BUNDLES:%=$(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/%.$(MAJOR).$(MINOR).jar)

A2_JARS = $(shell find $(SDK_BUILD_BASE)/a2 -name '*.jar')
A2_CLASSPATH = $(subst $(space),$(pathsep),$(strip $(A2_JARS)))


#JAVA_SRCS = $(shell find $(BUNDLE_PREFIX).* -name '*.java')
JAVA_SRCS = $(foreach bundle, $(BUNDLES), $(shell find $(bundle) -name '*.java'))
ECJ_SRCS = $(foreach bundle, $(BUNDLES), $(bundle)/src[-d $(BUILD_BASE)/$(bundle)/bin])

osgi: cnf $(A2_BUNDLES)
	mkdir -p $(SDK_BUILD_BASE)/a2/org.argeo.tp
	mv $(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/ext/org.argeo.ext.slf4j.$(MAJOR).$(MINOR).jar $(SDK_BUILD_BASE)/a2/org.argeo.tp
	mv $(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/ext/org.argeo.ext.equinox.jetty.$(MAJOR).$(MINOR).jar $(SDK_BUILD_BASE)/a2/org.argeo.tp.eclipse.equinox
	rmdir $(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/ext

distribution: bootstrap
	$(JVM) -cp \
	 $(SDK_BUILD_BASE)/bootstrap/bndlib.jar:$(SDK_BUILD_BASE)/bootstrap/slf4j-api.jar:$(SDK_BUILD_BASE)/org.argeo.slc.api/bin:$(SDK_BUILD_BASE)/org.argeo.slc.factory/bin \
	 tp/Make.java
	
bootstrap :
	mkdir -p $(SDK_BUILD_BASE)/bootstrap
	wget -c -O $(SDK_BUILD_BASE)/bootstrap/ecj.jar https://repo1.maven.org/maven2/org/eclipse/jdt/ecj/3.28.0/ecj-3.28.0.jar
	wget -c -O $(SDK_BUILD_BASE)/bootstrap/slf4j-api.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.28/slf4j-api-1.7.28.jar
	wget -c -O $(SDK_BUILD_BASE)/bootstrap/bndlib.jar https://repo1.maven.org/maven2/biz/aQute/bnd/biz.aQute.bndlib/5.3.0/biz.aQute.bndlib-5.3.0.jar
	$(JVM) -cp $(SDK_BUILD_BASE)/bootstrap/ecj.jar org.eclipse.jdt.internal.compiler.batch.Main -11 -nowarn -time -cp \
	 $(SDK_BUILD_BASE)/bootstrap/bndlib.jar:$(SDK_BUILD_BASE)/bootstrap/slf4j.jar \
	 org.argeo.slc.api/src[-d $(SDK_BUILD_BASE)/org.argeo.slc.api/bin] \
	 org.argeo.slc.factory/src[-d $(SDK_BUILD_BASE)/org.argeo.slc.factory/bin] \

clean:
	rm -rf $(BUILD_BASE)/*-compiled
	rm -rf $(BUILD_BASE)/cnf
	rm -rf $(BUILD_BASE)/a2
	rm -rf $(BUILD_BASE)/$(BUNDLE_PREFIX).* 
	rm -rf $(BUILD_BASE)/ext
	rm -rf $(BUILD_BASE)/build
	rm -rf $(BUILD_BASE)/deb

# SDK level
$(SDK_BUILD_BASE)/cnf/%.bnd: cnf/%.bnd
	mkdir -p $(dir $@)
	cp $< $@
	
$(SDK_BUILD_BASE)/ext/cnf/%.bnd: cnf/%.bnd
	mkdir -p $(dir $@)
	cp $< $@

$(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/%.$(MAJOR).$(MINOR).jar : $(BUILD_BASE)/%/bundle.jar
	mkdir -p $(dir $@)
	cp $< $@

# Build level
$(BUILD_BASE)/%/bundle.jar : %/bnd.bnd $(BUILD_BASE)/java-compiled 
	mkdir -p $(dir $@)
	rsync -r --exclude "*.java" $(dir  $<)src/ $(dir $@)bin
	rsync -r $(dir  $<)src/ $(dir $@)src
	if [ -d "$(dir  $<)OSGI-INF" ]; then rsync -r $(dir  $<)OSGI-INF/ $(dir $@)/OSGI-INF; fi
	cp $< $(dir $@)
	cd $(dir $@) && $(BND_TOOL) build
	mv $(dir $@)generated/*.jar $(dir $@)bundle.jar

$(BUILD_BASE)/java-compiled : $(JAVA_SRCS)
	$(JVM) -jar $(ECJ_JAR) -11 -nowarn -time -cp $(A2_CLASSPATH) \
	$(ECJ_SRCS)
	touch $@
	
null  :=
space := $(null) #
pathsep := :

	
