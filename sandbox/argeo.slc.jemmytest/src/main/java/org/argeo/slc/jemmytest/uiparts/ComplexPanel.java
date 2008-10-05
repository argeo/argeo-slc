package org.argeo.slc.jemmytest.uiparts;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JTextField;

import org.argeo.slc.detached.DetachedContext;
import org.argeo.slc.detached.DetachedRequest;
import org.argeo.slc.detached.ui.UiPart;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.operators.ContainerOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;

public class ComplexPanel extends UiPart {
	private JButtonOperator buttonTime = null;
	private JTextFieldOperator textTime = null;

	protected void initUi(DetachedContext context, DetachedRequest request) {
		ContainerOperator complexPanel = new ContainerOperator(
				(Container) context.getDynamicRef(MainFrame.PROP_CONTENT_PANE));
		buttonTime = new JButtonOperator(complexPanel, "Now!");
		textTime = new JTextFieldOperator(complexPanel, new ComponentChooser() {

			public boolean checkComponent(Component comp) {
				if (comp instanceof JTextField) {
					try {
						Long.parseLong(((JTextField) comp).getText());
						return true;
					} catch (Exception e) {
						return false;
					}
				} else
					return false;
			}

			public String getDescription() {
				return "Find based on text format: has to be a long";
			}
		});
	}

	public void computeTime() {
		buttonTime.push();
	}

	public String getTime() {
		return textTime.getText();
	}
}
