#include <iostream>

#include <osgGA/TrackballManipulator>

#include "osg_widget.h"

OsgWidget::OsgWidget(int x, int y, int width, int height, float s) {
	scale = s;

	setThreadingModel(SingleThreaded);

	osg::ref_ptr<osg::GraphicsContext::Traits> traits =
			new osg::GraphicsContext::Traits;

	// Setup the traits parameters
	traits->x = x * scale;
	traits->y = y * scale;
	traits->width = width * scale; //put here the width of the window view
	traits->height = height * scale; //put here the height of the window view
	traits->depth = 24; //keep memory down, you can put 16
	traits->alpha = 1;
//	traits->windowDecoration = false;
//	traits->doubleBuffer = true;
//	traits->sharedContext = 0;
//	traits->setInheritedWindowPixelFormat = true;
//	graph_win_embed_rp_ = new osgViewer::GraphicsWindowEmbedded(x * scale,
//			y * scale, width * scale, height * scale);
	graph_win_embed_rp_ = new osgViewer::GraphicsWindowEmbedded(traits);
//	graph_win_embed_rp_ = new osgViewer::GraphicsWindowEmbedded(x * scale,
//			y * scale, width * scale, height * scale);
	getCamera()->setViewport(
			new osg::Viewport(0, 0, width * scale, height * scale));
	getCamera()->setProjectionMatrixAsPerspective(30.0f,
			static_cast<double>(width * scale)
					/ static_cast<double>(height * scale), 1.0f, 10000.0f);
	getCamera()->setGraphicsContext(graph_win_embed_rp_);

	//getCamera()->setClearColor(osg::Vec4(0.0f, 1.0f, 1.0f, 0.5f));
//	getCamera()->setRenderOrder(osg::Camera::POST_RENDER);
	getCamera()->setAllowEventFocus(true);
//	getCamera()->setClearMask(GL_DEPTH_BUFFER_BIT);

//	graph_win_embed_rp_ = setUpViewerAsEmbeddedInWindow(x * scale, y * scale,
//			width * scale, height * scale);
//	auto camera = getCamera();
//	camera->setGraphicsContext(graph_win_embed_rp_);
//	camera->setClearMask(camera->getClearMask() | GL_STENCIL_BUFFER_BIT);
//	camera->setClearColor(osg::Vec4(0.176f, 0.18f, 0.157f, 0.9f));
//	camera->setProjectionMatrixAsPerspective(30.0, (double) width / height, 1.0,
//			10000.0);

	setCameraManipulator(new osgGA::TrackballManipulator);
}

float OsgWidget::devicePixelRatio() {
	return scale;
}

void OsgWidget::paintGL() {
	if (!isRealized()) {
		realize();
//		std::cerr << "realize" << std::endl;
	}

	double minFrameTime = _runMaxFrameRate > 0.0 ? 1.0 / _runMaxFrameRate : 0.0;
	if (_runFrameScheme == ON_DEMAND) {
		if (checkNeedToDoFrame()) {
			frame();
//			std::cerr << "frame (needed)" << std::endl;
		} else {
			// we don't need to render a frame but we don't want to spin the run loop so make sure the minimum
			// loop time is 1/100th of second, if not otherwise set, so enabling the frame microSleep below to
			// avoid consume excessive CPU resources.
			if (minFrameTime == 0.0)
				minFrameTime = 0.01;
		}
	} else {
		frame();
//		std::cerr << "frame" << std::endl;
	}
}

void OsgWidget::resizeGL(int x, int y, int w, int h) {
	auto scale = devicePixelRatio();
	getEventQueue()->windowResize(x * scale, y * scale, w * scale, h * scale);
	graph_win_embed_rp_->resized(x * scale, y * scale, w * scale, h * scale);
	getCamera()->setViewport(0, 0, w * scale, h * scale);
}

void OsgWidget::mousePressEvent(OsgMouseEvent *event) {
	graph_win_embed_rp_->getEventQueue()->mouseButtonPress(
			event->x() * devicePixelRatio(), event->y() * devicePixelRatio(),
			GetOsgMouseButton(event->button()));
}

void OsgWidget::mouseReleaseEvent(OsgMouseEvent *event) {
	graph_win_embed_rp_->getEventQueue()->mouseButtonRelease(
			event->x() * devicePixelRatio(), event->y() * devicePixelRatio(),
			GetOsgMouseButton(event->button()));
}

void OsgWidget::mouseMoveEvent(OsgMouseEvent *event) {
	graph_win_embed_rp_->getEventQueue()->mouseMotion(
			event->x() * devicePixelRatio(), event->y() * devicePixelRatio());
}

void OsgWidget::mouseDoubleClickEvent(OsgMouseEvent *event) {
	graph_win_embed_rp_->getEventQueue()->mouseDoubleButtonPress(
			event->x() * devicePixelRatio(), event->y() * devicePixelRatio(),
			GetOsgMouseButton(event->button()));
}

void OsgWidget::wheelEvent(OsgWheelEvent *event) {
	//graph_win_embed_rp_->getEventQueue()->mouseScroll2D(event->x(), event->y());

	// TODO Understand why osgEarth is not refreshed on scroll
	graph_win_embed_rp_->getEventQueue()->mouseScroll(
	// TODO use SWT.VERTICAL
			event->orientation() == 0 ?
					(event->delta() > 0 ?
							osgGA::GUIEventAdapter::SCROLL_UP :
							osgGA::GUIEventAdapter::SCROLL_DOWN) :
					(event->delta() > 0 ?
							osgGA::GUIEventAdapter::SCROLL_LEFT :
							osgGA::GUIEventAdapter::SCROLL_RIGHT));
}

unsigned int OsgWidget::GetOsgMouseButton(const int qt_mouse_btn) {
	return qt_mouse_btn;
//  if (Qt::LeftButton == qt_mouse_btn) return 1;
//  if (Qt::MiddleButton == qt_mouse_btn) return 2;
//  if (Qt::RightButton == qt_mouse_btn) return 3;
//
//  return 0;
}
