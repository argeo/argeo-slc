/*
 * Copyright (C) 2007-2012 Mathieu Baudier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.jcr.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.jcr.NamespaceRegistry;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;

import org.argeo.ArgeoException;
import org.argeo.jcr.JcrUtils;
import org.argeo.jcr.NodeMapper;
import org.argeo.jcr.NodeMapperProvider;
import org.argeo.slc.runtime.SlcAgentDescriptor;
import org.argeo.slc.test.TestResult;
import org.argeo.slc.test.TestRunDescriptor;

public abstract class AbstractSlcJcrDao {

	private Session session;

	// protected Workspace workspace;
	// protected QueryManager queryManager;
	protected NodeMapper nodeMapper;

	// We inject the nodeMapperProvider that define a default node mapper as an
	// entry point of the NodeMapper
	private NodeMapperProvider nodeMapperProvider;

	public void init() {
		nodeMapper = getNodeMapperProvider().findNodeMapper(null);
		// JcrUtils.registerNamespaceSafely(session, "slc",
		// "http://www.argeo.org/slc/0.12");

	}

	public void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}

	// IoC
	public void setNodeMapperProvider(NodeMapperProvider nodeMapperProvider) {
		this.nodeMapperProvider = nodeMapperProvider;
	}

	// TODO : define a strategy to define basePathes
	protected String basePath(TestResult testResult) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		return "/slc/testresults/" + JcrUtils.dateAsPath(cal) + "testresult";
	}

	protected String basePath(SlcAgentDescriptor slcAgentDescriptor) {
		return "/slc/agents/"
				+ JcrUtils.hostAsPath(slcAgentDescriptor.getHost()) + "/agent";
	}

	protected String basePath(TestRunDescriptor testRunDescriptor) {
		return "/slc/testruns/" + testRunDescriptor.getSlcExecutionUuid()
				+ "/testrun";
	}

	protected NodeMapperProvider getNodeMapperProvider() {
		return this.nodeMapperProvider;
	}

	protected Query createQuery(String query, String type) {
		try {
			return getSession().getWorkspace().getQueryManager().createQuery(
					query, type);
		} catch (RepositoryException e) {
			throw new ArgeoException("Cannot create query " + query, e);
		}
	}
}
