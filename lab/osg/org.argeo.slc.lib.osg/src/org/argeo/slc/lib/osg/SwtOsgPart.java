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
//	final GLContext context;

	private long pointer;

	private volatile int currentCanvasHeight;

	public SwtOsgPart(Composite parent, int style) {
		Composite cmp = new Composite(parent, style);
		cmp.setLayout(new FillLayout());

		GLData data = new GLData();
		data.doubleBuffer = true;
		canvas = new GLCanvas(cmp, SWT.NONE, data);

		canvas.setCurrent();
//		context = GLDrawableFactory.getFactory(GLProfile.getGL2GL3()).createExternalGLContext();

//		System.out.println(System.getProperty("org.eclipse.swt.internal.deviceZoom"));
//		run();
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

	public void close() {
		canvas.dispose();
		doDestroy(pointer);
	}

	private void drawOsg() {
		doPaint(pointer);
	}

	void run() {
		Rectangle area = canvas.getClientArea();
		canvas.setCurrent();
//		context.makeCurrent();
		String zoom = System.getProperty("org.eclipse.swt.internal.deviceZoom");
		float scale = zoom != null ? Float.parseFloat(zoom) / 100 : 1;
		pointer = doInit(area.x, area.y, area.width, area.height, scale);
//		context.release();

		canvas.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Rectangle bounds = canvas.getBounds();
				canvas.getDisplay().asyncExec(() -> {
					canvas.setCurrent();
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
			canvas.setCurrent();
//			System.out.println(e);
			doMouseMoveEvent(pointer, e.x, e.y, e.button);
			canvas.redraw();
		});
//		canvas.addMouseMoveListener((e) -> {
//			canvas.setCurrent();
////			System.out.println(e);
//			doMouseMoveEvent(pointer, e.x, e.y, e.button);
//			canvas.redraw();
//		});
		canvas.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				canvas.setCurrent();
				doMouseReleaseEvent(pointer, e.x, currentCanvasHeight() - e.y, e.button);
				canvas.redraw();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				canvas.setCurrent();
				doMousePressEvent(pointer, e.x, currentCanvasHeight() - e.y, e.button);
				canvas.redraw();
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				canvas.setCurrent();
				doMouseDoubleClickEvent(pointer, e.x, currentCanvasHeight() - e.y, e.button);
				canvas.redraw();
			}
		});

		// for RAP compatibility
		canvas.addListener(SWT.MouseWheel, (e) -> {
			canvas.setCurrent();
			doMouseWheelEvent(pointer, e.x, currentCanvasHeight() - e.y, 0, -e.count * 10);
			canvas.redraw();
		});
//		canvas.addMouseWheelListener((e) -> {
//			canvas.setCurrent();
//			doMouseWheelEvent(pointer, e.x, currentCanvasHeight() - e.y, 0, -e.count * 10);
//			canvas.redraw();
//		});
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

		osgPart.close();
		display.dispose();
	}
}