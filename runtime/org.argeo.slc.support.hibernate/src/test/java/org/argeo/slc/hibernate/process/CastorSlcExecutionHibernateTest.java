package org.argeo.slc.hibernate.process;

import java.sql.SQLException;

import org.argeo.slc.dao.process.SlcExecutionDao;
import org.argeo.slc.hibernate.unit.HibernateTestCase;
import org.argeo.slc.msg.ObjectList;
import org.argeo.slc.process.SlcExecution;
import org.argeo.slc.unit.process.SlcExecutionTestUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

public class CastorSlcExecutionHibernateTest extends HibernateTestCase {

	public void testSaveWithRealizedFlowsXml() throws Exception {
		SlcExecutionDao dao = getBean(SlcExecutionDao.class);

		SlcExecution slcExec = SlcExecutionTestUtils
				.createSlcExecutionWithRealizedFlows();

		ObjectList ol = new ObjectList(slcExec.getRealizedFlows());
		StringResult result = new StringResult();
		getBean(Marshaller.class).marshal(ol, result);
		slcExec.setRealizedFlowsXml(result.toString());

		dao.create(slcExec);

		SlcExecution slcExecPersisted = dao.getSlcExecution(slcExec.getUuid());
		assertSlcExecution(slcExec, slcExecPersisted);

		StringSource source = new StringSource(slcExecPersisted
				.getRealizedFlowsXml());
		ObjectList ol2 = (ObjectList) getBean(Unmarshaller.class).unmarshal(
				source);
		ol2.fill(slcExec.getRealizedFlows());

	}

	public void assertSlcExecution(final SlcExecution expected,
			final SlcExecution persisted) {
		getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				session.refresh(persisted);
				SlcExecutionTestUtils.assertSlcExecution(expected, persisted);
				return null;
			}
		});
	}

	@Override
	protected String getApplicationContextLocation() {
		return "org/argeo/slc/hibernate/withCastor.xml";
	}
}
