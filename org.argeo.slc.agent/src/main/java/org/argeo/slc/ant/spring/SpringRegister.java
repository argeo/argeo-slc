package org.argeo.slc.ant.spring;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;

public class SpringRegister extends AbstractSpringTask {
	private static final Log log = LogFactory.getLog(SpringRegister.class);

	private List<BeanArg> beans = new Vector<BeanArg>();

	@Override
	public void execute() throws BuildException {
		for (BeanArg bean : beans) {
			Object instance = bean.getBeanInstance();
			if (bean.getAntid() != null) {
				getProject().addReference(bean.getAntid(), instance);
			} else {
				if (bean.getAntref() != null) {
					log
							.warn("Cannot register beans with antref (Ant reference "
									+ bean.getAntref() + ")");
				} else {
					getProject().addReference(bean.getBean(), instance);
				}
			}
		}
	}

	public BeanArg createObject() {
		BeanArg bean = new BeanArg();
		beans.add(bean);
		return bean;
	}

	protected static class BeanArg extends SpringArg<Object> {
		private String antid;

		public String getAntid() {
			return antid;
		}

		public void setAntid(String antid) {
			this.antid = antid;
		}

	}
}
