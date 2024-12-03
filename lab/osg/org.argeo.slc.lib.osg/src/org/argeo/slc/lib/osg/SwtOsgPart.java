package org.argeo.slc.lib.osg;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class SwtOsgPart {
	static {
		System.loadLibrary("Java_org_argeo_slc_lib_osg");
		doBackendInit();
	}

	private final GLCanvas canvas;

	private long pointer;

	private volatile int currentCanvasHeight;

	public SwtOsgPart(Composite parent, int style) {
		Composite cmp = new Composite(parent, style);
		cmp.setLayout(new FillLayout());

		GLData data = new GLData();
		data.doubleBuffer = true;
		canvas = new GLCanvas(cmp, SWT.NONE, data);

		canvas.setCurrent();
	}

	private static native void doBackendInit();

	private static native long doInit(int x, int y, int width, int height, float scale);

	private static native void doDestroy(long pointer);

	private static native void doPaint(long pointer);

	private static native void doResize(long pointer, int x, int y, int width, int height);

	private static native void doMouseMoveEvent(long pointer, int x, int y, int button);

	private static native void doMousePressEvent(long pointer, int x, int y, int button);

	private static native void doMouseReleaseEvent(long pointer, int x, int y, int button);

	private static native void doMouseDoubleClickEvent(long pointer, int x, int y, int button);

	private static native void doMouseWheelEvent(long pointer, int x, int y, int orientation, int delta);

	public synchronized void close() {
		canvas.dispose();
		doDestroy(pointer);
	}

	private synchronized void drawOsg() {
		if (!canvas.isDisposed()) {
			doPaint(pointer);
		}
	}

	void run() {
		Rectangle area = canvas.getClientArea();
		canvas.setCurrent();
//		float zoom = Float.parseFloat(System.getProperty("org.eclipse.swt.internal.deviceZoom", "100"));
		float zoom = canvas.getDisplay().getPrimaryMonitor().getZoom();
		float scale = zoom / 100;
		pointer = doInit(area.x, area.y, area.width, area.height, scale);

		canvas.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				canvas.getDisplay().asyncExec(() -> {
					Rectangle bounds = canvas.getBounds();
					doResize(pointer, 0, 0, bounds.width, bounds.height);
					currentCanvasHeight = bounds.height;
				});
			}
		});

		canvas.addPaintListener((e) -> {
			canvas.setCurrent();
			drawOsg();
			canvas.swapBuffers();
		});

		// for RAP compatibility
		canvas.addListener(SWT.MouseMove, (e) -> {
			doMouseMoveEvent(pointer, e.x, e.y, e.button);
			canvas.redraw();
		});
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				doMouseReleaseEvent(pointer, e.x, e.y, e.button);
				canvas.redraw();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				doMousePressEvent(pointer, e.x, e.y, e.button);
				canvas.redraw();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				doMouseDoubleClickEvent(pointer, e.x, e.y, e.button);
				canvas.redraw();
			}
		});

		// for RAP compatibility
		canvas.addListener(SWT.MouseWheel, (e) -> {
			doMouseWheelEvent(pointer, e.x, e.y, 0, -e.count * 10);
			canvas.redraw();
		});
	}

	private int currentCanvasHeight() {
		return currentCanvasHeight;
	}

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		SwtOsgPart osgPart = new SwtOsgPart(shell, SWT.NONE);

		shell.setText("OSG SWT");
		shell.setSize(640, 480);
		shell.open();

		osgPart.run();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.readAndDispatch();// make sure we don't have mouse events left

		osgPart.close();
		display.dispose();
	}
}