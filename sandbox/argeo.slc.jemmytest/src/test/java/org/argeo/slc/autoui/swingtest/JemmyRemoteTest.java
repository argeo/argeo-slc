package org.argeo.slc.autoui.swingtest;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.argeo.slc.autoui.swingtest.rmi.AutoUiContext;
import org.argeo.slc.autoui.swingtest.rmi.AutoUiServer;
import org.argeo.slc.autoui.swingtest.rmi.AutoUiTask;
import org.netbeans.jemmy.ClassReference;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JFrameOperator;
import org.netbeans.jemmy.operators.JLabelOperator;

import junit.framework.TestCase;

public class JemmyRemoteTest extends TestCase implements Serializable {
	static final long serialVersionUID = 1l;

	public void testRemote() throws Exception {
		 if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		String name = "AutoUiServer";
		Registry registry = LocateRegistry.getRegistry("localhost");
		AutoUiServer server = (AutoUiServer) registry.lookup(name);
		AutoUiTask startFrame = new AutoUiTask() {
			static final long serialVersionUID = 1l;

			public Object execute(AutoUiContext context) throws Exception {
				// Start application
				ClassReference classReference = new ClassReference(
						SwingTestUi.class.getName());
				String[] args = { "noExitOnClose" };
				classReference.startApplication(args);

				// Find components
				JFrameOperator jFrameOperator = new JFrameOperator(
						"HelloWorldSwing");
				JButtonOperator jButtonOperator = new JButtonOperator(
						jFrameOperator, "Button");
				JLabelOperator jLabelOperator = new JLabelOperator(
						jFrameOperator, "Hello World");

				context.setLocalRef("label", jLabelOperator);
				context.setLocalRef("button", jButtonOperator);
				return null;
			}

		};

		AutoUiTask pushButton = new AutoUiTask() {
			static final long serialVersionUID = 1l;

			public Object execute(AutoUiContext context) throws Exception {
				JButtonOperator jButtonOperator = (JButtonOperator) context
						.getLocalRef("button");
				JLabelOperator jLabelOperator = (JLabelOperator) context
						.getLocalRef("label");

				// Execute actions
				jButtonOperator.push();

				// Performs checks
				String textAfterPush = jLabelOperator.getText();
				Boolean pressed = new Boolean(textAfterPush.equals("Pressed!!"));
				return pressed;
			}

		};

		server.executeTask(startFrame);
		Boolean pressed = (Boolean) server.executeTask(pushButton);
		assertTrue("Has been pressed", pressed.booleanValue());
	}

	public static void main(String[] args){
		try {
			JemmyRemoteTest jemmyRemoteTest = new JemmyRemoteTest();
			jemmyRemoteTest.testRemote();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
