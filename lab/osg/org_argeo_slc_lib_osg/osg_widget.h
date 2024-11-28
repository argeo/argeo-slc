#ifndef OSG_WIDGET_H
#define OSG_WIDGET_H

#include <osgViewer/Viewer>

class OsgMouseEvent {
public:
	OsgMouseEvent(float x, float y, int button) {
		_x = x;
		_y = y;
		_button = button;
	}
	float x() {
		return _x;
	}

	float y() {
		return _y;
	}

	int button() {
		return _button;
	}
private:
	float _x;
	float _y;
	int _button;
};

class OsgWheelEvent {
public:
	OsgWheelEvent(int orientation, int delta) {
		_orientation = orientation;
		_delta = delta;
	}
	int orientation() {
		return _orientation;
	}
	int delta() {
		return _delta;
	}
private:
	int _orientation;
	int _delta;
};

class OsgWidget: public osgViewer::Viewer {
public:
	OsgWidget(int x, int y, int width, int height, float scale);
	void paintGL();
	void resizeGL(int x, int y, int w, int h);

	void mousePressEvent(OsgMouseEvent *event);
	void mouseReleaseEvent(OsgMouseEvent *event);
	void mouseMoveEvent(OsgMouseEvent *event);
	void mouseDoubleClickEvent(OsgMouseEvent *event);
	void wheelEvent(OsgWheelEvent *event);

//  void paintEvent(QPaintEvent* event) override;

//	void update();

private:
	static unsigned int GetOsgMouseButton(int qt_mouse_btn);
//	int x();
//	int y();
//	int width();
//	int height();
	float devicePixelRatio();

private:
	osg::ref_ptr<osgViewer::GraphicsWindowEmbedded> graph_win_embed_rp_;
	float scale;
};

#endif  // OSG_WIDGET_H
