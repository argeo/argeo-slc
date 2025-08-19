include ../sdk.mk
include  $(SDK_SRC_BASE)/sdk/argeo-build/osgi.mk

JMODS_BASE=$(BUILD_BASE)/jmods

JMODS=\
argeo.tp.swt

TARGET_JMODS = $(abspath $(foreach jmod, $(JMODS),$(A2_OUTPUT)/jmods/$(jmod).jmod))

all: $(TARGET_JMODS)

clean:
	rm -f $(TARGET_JMODS)
	rm -rf $(JMODS_BASE)

argeo.cms.jmod:
	rm -f argeo.cms.jmod
	jmod create --class-path argeo.cms/bin \
		--libs argeo.cms/lib \
		argeo.cms.jmod

jlink-rt:
	rm -rf rt/argeo-rt-$ARGEO_RT-amd64
	#/usr/lib/jvm/java-$ARGEO_RT-amd64/bin/jlink --module-path a2/osgi/equinox/org.argeo.tp.osgi.framework/:a2/org.argeo.cms:jmod --output rt/argeo-rt-$ARGEO_RT-amd64 --add-modules jdk.jlink --compress 2 --strip-debug
	/usr/lib/jvm/java-$ARGEO_RT-amd64/bin/jlink --module-path a2/osgi/equinox/org.argeo.tp.osgi.framework/:a2/org.argeo.cms:jmod --output rt/argeo-rt-$ARGEO_RT-amd64 --add-modules argeo.cms,jdk.localedata,jdk.jlink,jdk.jcmd --include-locales=en,fr,de,es,pt,ru,ua,el,ar,il
	du -sh rt/argeo-rt-$ARGEO_RT-amd64

$(A2_OUTPUT)/jmods/%.jmod: $(JMODS_BASE)/%/classes/module-info.class
	mkdir -p $(dir $@)
	mkdir -p $(dir $<)/../lib
	jmod create --class-path $(dir $<) \
		--libs $(dir $<)/../lib \
		$@

$(JMODS_BASE)/%/classes/module-info.class: %/module-info.java
	rm -rf $(dir $@)
	mkdir -p $(dir $@)
	unzip -q /home/mbaudier/dev/git/unstable/output/a2/lib/x86_64-linux-gnu/swt/rcp/org.argeo.tp.swt/org.eclipse.swt.gtk.linux.x86_64.3.129.jar org/* \
	 -d $(dir $@)
	javac \
	 --patch-module argeo.tp.swt=$(dir $@) \
	 -d $(dir $@) \
	 $<
