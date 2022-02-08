include sdk.mk
.PHONY: clean all osgi

all: osgi

BUNDLE_PREFIX = org.argeo.slc
A2_CATEGORY = org.argeo.slc

BUNDLES = \
org.argeo.slc.api \
org.argeo.slc.factory \

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

DISTRIBUTION_CLASSPATH = \
$(SDK_BUILD_BASE)/a2/org.argeo.slc/org.argeo.slc.api.$(MAJOR).$(MINOR).jar:$\
$(SDK_BUILD_BASE)/a2/org.argeo.slc/org.argeo.slc.factory.$(MAJOR).$(MINOR).jar:$\
/usr/share/java/bndlib.jar:$\
/usr/share/java/slf4j-api.jar

# TODO relativize from SDK_SRC_BASE
BUILD_BASE = $(SDK_BUILD_BASE)

distribution: osgi
	$(JVM) -cp $(DISTRIBUTION_CLASSPATH) tp/Make.java

#
# GENERIC
#
JVM := /usr/bin/java
JAVADOC := /usr/bin/javadoc
ECJ_JAR := /usr/share/java/ecj.jar
BND_TOOL := /usr/bin/bnd

WORKSPACE_BNDS := $(shell cd $(SDK_SRC_BASE) && find cnf -name '*.bnd')
#BND_WORKSPACES := $(foreach bundle, $(BUNDLES), ./$(dir $(bundle)))
BUILD_WORKSPACE_BNDS := $(WORKSPACE_BNDS:%=$(SDK_BUILD_BASE)/%)

cnf: $(BUILD_WORKSPACE_BNDS)

A2_BUNDLES = $(BUNDLES:%=$(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/%.$(MAJOR).$(MINOR).jar)

#JAVA_SRCS = $(shell find $(BUNDLE_PREFIX).* -name '*.java')
JAVA_SRCS = $(foreach bundle, $(BUNDLES), $(shell find $(bundle) -name '*.java'))
ECJ_SRCS = $(foreach bundle, $(BUNDLES), $(bundle)/src[-d $(BUILD_BASE)/$(bundle)/bin])

osgi: cnf $(A2_BUNDLES)

clean:
	rm -rf $(BUILD_BASE)/*-compiled
	rm -rf $(BUILD_BASE)/{cnf,a2}
	rm -rf $(BUILD_BASE)/$(BUNDLE_PREFIX).* 

# SDK level
$(SDK_BUILD_BASE)/cnf/%.bnd: cnf/%.bnd
	mkdir -p $(dir $@)
	cp $< $@
	
$(SDK_BUILD_BASE)/eclipse/cnf/%.bnd: cnf/%.bnd
	mkdir -p $(dir $@)
	cp $< $@

$(SDK_BUILD_BASE)/rcp/cnf/%.bnd: cnf/%.bnd
	mkdir -p $(dir $@)
	cp $< $@

$(SDK_BUILD_BASE)/a2/$(A2_CATEGORY)/%.$(MAJOR).$(MINOR).jar : $(BUILD_BASE)/%/bundle.jar
	mkdir -p $(dir $@)
	cp $< $@

# Build level
$(BUILD_BASE)/%/bundle.jar : %/bnd.bnd $(BUILD_BASE)/java-compiled 
	rsync -r --exclude "*.java" $(dir  $<)src/ $(dir $@)bin
	rsync -r $(dir  $<)src/ $(dir $@)src
	if [ -d "$(dir  $<)OSGI-INF" ]; then rsync -r $(dir  $<)OSGI-INF/ $(dir $@)/OSGI-INF; fi
	cp $< $(dir $@)
	cd $(dir $@) && $(BND_TOOL) build
	mv $(dir $@)generated/*.jar $(dir $@)bundle.jar

$(BUILD_BASE)/java-compiled : $(JAVA_SRCS)
	$(JVM) -cp $(ECJ_JAR) org.eclipse.jdt.internal.compiler.batch.Main -11 -nowarn -time -cp $(BUILD_CLASSPATH) \
	$(ECJ_SRCS)
	touch $@
	
null  :=
space := $(null) #
pathsep := :

#WITH_LIST    := $(subst $(space),$(pathsep),$(strip $(WITH_LIST)))
	
