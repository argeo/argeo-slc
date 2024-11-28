cmake_minimum_required(VERSION 3.14) # for add_link_options and implicit target directories.

# Java
find_package(JNI)

# OpenScenGraph
find_package(OpenSceneGraph REQUIRED COMPONENTS osgDB osgGA osgUtil osgViewer osgTerrain osgGA osgFX)
include_directories(${OPENSCENEGRAPH_INCLUDE_DIRS})

link_directories(/usr/local/lib64)

# osgEarth as external library
#find_library(OSGEARTH_LOCATION osgEarth)
#message (STATUS "Found osgEarth library: ${OSGEARTH_LOCATION}")
set(OSGEARTH_LOCATION /usr/local/lib64/libosgEarth.so)
add_library(osgEarth SHARED IMPORTED GLOBAL)
set_target_properties(osgEarth PROPERTIES IMPORTED_LOCATION ${OSGEARTH_LOCATION})
set(OSGEARTH_LIBRARIES osgEarth)

include(GNUInstallDirs)
include(CMakePackageConfigHelpers)

add_executable(osgTerrain
	osg_terrain.cpp
)
target_link_libraries(osgTerrain PRIVATE	${OPENSCENEGRAPH_LIBRARIES})

add_executable(osgEarthArgeo
	osgearth_viewer.cpp
)
target_link_libraries(osgEarthArgeo PRIVATE	${OPENSCENEGRAPH_LIBRARIES} ${OSGEARTH_LIBRARIES})


set(TARGET Java_org_argeo_slc_lib_osg)

add_library(${TARGET} SHARED 
	osg_widget.cpp
	osg_part.cpp
)
target_include_directories(${TARGET} PRIVATE 
	../include/Java_org_argeo_slc_lib_osg ../include ${JNI_INCLUDE_DIRS})
target_link_libraries(${TARGET} PRIVATE	${OPENSCENEGRAPH_LIBRARIES} ${OSGEARTH_LIBRARIES})

set_target_properties(${TARGET} PROPERTIES POSITION_INDEPENDENT_CODE ON)
target_compile_features(${TARGET} PRIVATE cxx_std_11)

set_target_properties(${TARGET} PROPERTIES LIBRARY_OUTPUT_DIRECTORY
	"${A2_OUTPUT}/${A2_TARGET_ARCH_CATEGORY_PREFIX}/${A2_CATEGORY}"
)

install(TARGETS ${TARGET}
LIBRARY DESTINATION lib/${A2_TARGET_ARCH_CATEGORY_PREFIX}/a2/${A2_CATEGORY}
)

