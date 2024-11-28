#include <iostream>

#include <osgGA/TrackballManipulator>

#include "osg_widget.h"

OsgWidget::OsgWidget(int x, int y, int width, int height, float s) {
	scale = s;
	graph_win_embed_rp_ = setUpViewerAsEmbeddedInWindow(x * scale, y * scale,
			width * scale, height * scale);

	auto camera = getCamera();
	camera->setGraphicsContext(graph_win_embed_rp_);
	camera->setClearMask(camera->getClearMask() | GL_STENCIL_BUFFER_BIT);
	camera->setClearColor(osg::Vec4(0.176f, 0.18f, 0.157f, 0.9f));
	camera->setProjectionMatrixAsPerspective(30.0, (double) width / height, 1.0,
			10000.0);

	setCameraManipulator(new osgGA::TrackballManipulator);
}

float OsgWidget::devicePixelRatio() {
	return scale;
}

void OsgWidget::paintGL() {
	if (!isRealized()) {
		realize();
	}

	double minFrameTime = _runMaxFrameRate > 0.0 ? 1.0 / _runMaxFrameRate : 0.0;
	if (_runFrameScheme == ON_DEMAND) {
		if (checkNeedToDoFrame()) {
			frame();
		} else {
			// we don't need to render a frame but we don't want to spin the run loop so make sure the minimum
			// loop time is 1/100th of second, if not otherwise set, so enabling the frame microSleep below to
			// avoid consume excessive CPU resources.
			if (minFrameTime == 0.0)
				minFrameTime = 0.01;
		}
	} else {
		frame();
	}

//	frame();
//	update();
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
			event->x() * devicePixelRatio(), event->x() * devicePixelRatio());
}

void OsgWidget::mouseDoubleClickEvent(OsgMouseEvent *event) {
	graph_win_embed_rp_->getEventQueue()->mouseDoubleButtonPress(
			event->x() * devicePixelRatio(), event->y() * devicePixelRatio(),
			GetOsgMouseButton(event->button()));
}

void OsgWidget::wheelEvent(OsgWheelEvent *event) {
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
