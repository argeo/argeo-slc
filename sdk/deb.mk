DIST_PKGS = \
argeo-tp-base \
argeo-tp-equinox \
argeo-tp-jetty \
argeo-tp-rap \
argeo-tp-jcr \
argeo-tp-sdk \

DEB_DIRS = $(DIST_PKGS:%=$(SDK_BUILD_BASE)/build/deb/%)
DEB_PKGS = $(DIST_PKGS:%=$(SDK_BUILD_BASE)/deb/%.deb)


deb: $(DEB_PKGS)
	cd $(SDK_BUILD_BASE)/deb && dpkg-scanpackages . | gzip > Packages.gz

$(SDK_BUILD_BASE)/deb/%.deb : $(SDK_BUILD_BASE)/build/deb/%/DEBIAN/control
	echo Build $@

$(SDK_BUILD_BASE)/build/deb/%/DEBIAN/control : $(SDK_SRC_BASE)/sdk/deb/%.control prepare-deb
	cp $< $@
	dpkg-deb --build --root-owner-group $(dir $@)/.. $(SDK_BUILD_BASE)/deb

prepare-deb: 
	mkdir -p $(foreach deb_dir, $(DEB_DIRS), $(deb_dir)/DEBIAN)
	mkdir -p $(foreach deb_dir, $(DEB_DIRS), $(deb_dir)/usr/share/a2)
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp $(SDK_BUILD_BASE)/build/deb/argeo-tp-base/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.apache $(SDK_BUILD_BASE)/build/deb/argeo-tp-base/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.eclipse.equinox $(SDK_BUILD_BASE)/build/deb/argeo-tp-equinox/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.jetty $(SDK_BUILD_BASE)/build/deb/argeo-tp-jetty/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.eclipse.rap $(SDK_BUILD_BASE)/build/deb/argeo-tp-rap/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.jcr $(SDK_BUILD_BASE)/build/deb/argeo-tp-jcr/usr/share/a2
	rsync -av $(SDK_BUILD_BASE)/a2/org.argeo.tp.sdk $(SDK_BUILD_BASE)/build/deb/argeo-tp-sdk/usr/share/a2
	
	
