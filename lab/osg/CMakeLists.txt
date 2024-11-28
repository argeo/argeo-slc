cmake_minimum_required(VERSION 3.14)

project("argeo-slc-osg"
DESCRIPTION "Argeo CMS bindings for OpenScenGraph"
VERSION 0.1.0
)
set(A2_JAVA_RELEASE 17)

list(APPEND CMAKE_MODULE_PATH "${CMAKE_SOURCE_DIR}/../../sdk/argeo-build")
find_package(ArgeoBuild)

include(GNUInstallDirs)

set(A2_BASE "/home/mbaudier/dev/git/unstable/output/a2")

set(A2_CATEGORY org.argeo.slc)
set(BUNDLES 
org.argeo.slc.lib.osg
)
set(DEP_CATEGORIES 
org.argeo.tp.sys
lib/x86_64-linux-gnu/swt/rcp/org.argeo.tp.swt
)

a2_build_bundles(${BUNDLES})

add_subdirectory(org_argeo_slc_lib_osg)
