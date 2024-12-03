#include <osg/Geode>
#include <osg/Geometry>
#include <osg/ShapeDrawable>
#include <osg/TriangleFunctor>
#include <osgDB/ReadFile>
#include <osgUtil/SmoothingVisitor>
#include <osgUtil/Tessellator>

#include <osgViewer/Viewer>
#include <osgEarth/EarthManipulator>
#include <osgEarth/ExampleResources>
#include <osgEarth/MapNode>
#include <osgEarth/GLUtils>
#include <osgEarth/PhongLightingEffect>
#include <osgGA/TrackballManipulator>
#include <iostream>

#include <osgTerrain/Terrain>

#include "org_argeo_slc_lib_osg_SwtOsgPart.h"  // IWYU pragma: keep

#include "osg_widget.h"

using namespace osgEarth;
using namespace osgEarth::Util;

static osg::ref_ptr<osg::Geode> sceneDataShapes() {
	// example
	osg::ref_ptr<osg::ShapeDrawable> box = new osg::ShapeDrawable(
			new osg::Box( { -3.f, 0.f, 0.f }, 2.f, 2.f, 1.f));
	box->setColor( { 1.f, 0.f, 0.f, 1.f });
	osg::ref_ptr<osg::ShapeDrawable> sphere = new osg::ShapeDrawable(
			new osg::Sphere( { 3.0, 0.f, 0.f }, 1.f));
	sphere->setColor( { 0.f, 0.f, 1.f, 1.f });
	osg::ref_ptr<osg::ShapeDrawable> cone = new osg::ShapeDrawable(
			new osg::Cone( { 0.f, 0.f, 0.f }, 1.f, 1.f));
	cone->setColor( { 0.f, 1.f, 0.f, 1.f });
	osg::ref_ptr<osg::Geode> root = new osg::Geode;
	root->addDrawable(box);
	root->addDrawable(sphere);
	root->addDrawable(cone);
	return root;
}

static osg::ref_ptr<osg::Node> sceneDataEarth(osgViewer::Viewer *viewer,
		int argc, char **argv) {
	osg::ArgumentParser arguments(&argc, argv);
//    if ( arguments.read("--help") )
//        return usage(argv[0]);

// start up osgEarth
//	osgEarth::initialize(arguments);

// create a simple view
//	osgViewer::Viewer viewer(arguments);

#ifndef OSG_GL3_AVAILABLE
	// If your OSG is build with a GL2 profile, install our custom realize op
	// to get all the shaders working:
	viewer->setRealizeOperation(new osgEarth::GL3RealizeOperation());
#endif

// install our default manipulator (do this before calling load)
	viewer->setCameraManipulator(new EarthManipulator(arguments));

	// disable the small-feature culling; necessary for some feature rendering
	viewer->getCamera()->setSmallFeatureCullingPixelSize(-1.0f);

	// load an earth file, and support all or our example command-line options
	auto node = MapNodeHelper().load(arguments, viewer);
	if (node.valid()) {
		if (MapNode::get(node)) {
			// viewer.setSceneData(node);
			return node;
		} else {
			// not an earth file? Just view as a normal OSG node or image with basic lighting
			viewer->setCameraManipulator(new osgGA::TrackballManipulator);

			osg::LightSource *sunLS = new osg::LightSource();
			sunLS->getLight()->setPosition(osg::Vec4d(1, -1, 1, 0));
			auto group = new osg::Group();
			group->addChild(sunLS);
			group->addChild(node);
			auto phong = new PhongLightingEffect();
			phong->attach(group->getOrCreateStateSet());
			ShaderGenerator gen;
			gen.run(group);

			return group;
		}
	}
	return nullptr;
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doBackendInit(
		JNIEnv*, jclass) {
	int argc = 2;
	const char *argv[argc] = { "main", "../tests/readymap.earth" };
	osg::ArgumentParser arguments(&argc, (char**) argv);
	osgEarth::initialize(arguments);
}

JNIEXPORT jlong JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doInit(JNIEnv*,
		jclass, jint x, jint y, jint width, jint height, jfloat scale) {
	OsgWidget *osgWidget = new OsgWidget(x, y, width, height, scale);

//	osgWidget->setSceneData(sceneDataShapes());
//	osgWidget->setCameraManipulator(new osgGA::TrackballManipulator);

	int argc = 2;
	const char *argv[argc] = { "main", "../tests/readymap.earth" };
	osgWidget->setSceneData(sceneDataEarth(osgWidget, argc, (char**) argv));

	osgWidget->realize();
	return (jlong) osgWidget;
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doDestroy(JNIEnv*,
		jclass, jlong pointer) {
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	delete osgWidget;
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doPaint(JNIEnv*,
		jclass, jlong pointer) {
	OsgWidget *osgWidget = (OsgWidget*) pointer;

	osgWidget->paintGL();

}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doResize(JNIEnv*,
		jclass, jlong pointer, jint x, jint y, jint width, jint height) {
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	osgWidget->resizeGL(x, y, width, height);
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doMouseMoveEvent(
		JNIEnv*, jclass, jlong pointer, jint x, jint y, jint button) {
//	std::cerr << "Mouse move " << x << "," << y << " " << button
//			<< std::endl;
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	OsgMouseEvent event(x, y, button);
	osgWidget->mouseMoveEvent(&event);
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doMousePressEvent(
		JNIEnv*, jclass, jlong pointer, jint x, jint y, jint button) {
//	std::cerr << "Mouse press " << x << "," << y << " " << button
//			<< std::endl;
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	OsgMouseEvent event(x, y, button);
	osgWidget->mousePressEvent(&event);
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doMouseReleaseEvent(
		JNIEnv*, jclass, jlong pointer, jint x, jint y, jint button) {
//	std::cerr << "Mouse release " << x << "," << y << " " << button
//			<< std::endl;
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	OsgMouseEvent event(x, y, button);
	osgWidget->mouseReleaseEvent(&event);
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doMouseDoubleClickEvent(
		JNIEnv*, jclass, jlong pointer, jint x, jint y, jint button) {
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	OsgMouseEvent event(x, y, button);
	osgWidget->mouseDoubleClickEvent(&event);
}

JNIEXPORT void JNICALL Java_org_argeo_slc_lib_osg_SwtOsgPart_doMouseWheelEvent(
		JNIEnv*, jclass, jlong pointer, jint x, jint y, jint orientation,
		jint delta) {
	OsgWidget *osgWidget = (OsgWidget*) pointer;
	OsgWheelEvent wheelEvent(x, y, orientation, delta);
	osgWidget->wheelEvent(&wheelEvent);
}

