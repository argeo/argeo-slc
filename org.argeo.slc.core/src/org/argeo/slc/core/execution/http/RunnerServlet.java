package org.argeo.slc.core.execution.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.naming.ldap.LdapName;
import javax.security.auth.Subject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.cms.auth.CmsSession;
import org.argeo.jcr.JcrUtils;
import org.argeo.node.NodeUtils;
import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.SlcTypes;
import org.argeo.slc.execution.ExecutionProcess;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class RunnerServlet extends HttpServlet {
	private final static Log log = LogFactory.getLog(RunnerServlet.class);

	private static final long serialVersionUID = -317016687309065291L;

	private Path baseDir;
	private BundleContext bc;
	private ExecutorService executor;

	public RunnerServlet(BundleContext bc, Path baseDir) {
		this.bc = bc;
		this.baseDir = baseDir;
		this.executor = Executors.newFixedThreadPool(20);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		InputStream in;
		// Deal with x-www-form-urlencoded
		// FIXME make it more robust an generic
		Map<String, String[]> params = req.getParameterMap();
		if (params.size() != 0) {
			String json = params.keySet().iterator().next();
			in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
		} else {
			in = req.getInputStream();
		}

		// InputStream in = req.getInputStream();
		// Gson gson = new Gson();
		// JsonParser jsonParser = new JsonParser();
		// BufferedReader reader = new BufferedReader(new InputStreamReader(in,
		// Charset.forName("UTF-8")));
		// JsonElement payload = jsonParser.parse(reader);
		// String payloadStr = gson.toJson(payload);
		//
		// log.debug(payloadStr);
		// if (true)
		// return;

		String path = req.getPathInfo();
		// InputStream in = req.getInputStream();
		OutputStream out = resp.getOutputStream();

		String tokens[] = path.split("/");
		// first token alway empty
		String workgroup = tokens[1];

		CmsSession cmsSession = getByLocalId(req.getSession().getId());

		boolean authorized = false;
		for (String role : cmsSession.getAuthorization().getRoles()) {
			if (role.startsWith("cn=" + workgroup)) {
				authorized = true;
				break;
			}
		}
		if (!authorized) {
			resp.setStatus(403);
			return;
		}
		LdapName userDn = cmsSession.getUserDn();
		AccessControlContext acc = (AccessControlContext) req.getAttribute(HttpContext.REMOTE_USER);
		Subject subject = Subject.getSubject(acc);
		// flow path
		StringBuilder sb = new StringBuilder("");
		for (int i = 2; i < tokens.length; i++) {
			if (i != 2)
				sb.append('/');
			sb.append(tokens[i]);
		}
		String flowName = sb.toString();
		String ext = FilenameUtils.getExtension(flowName.toString());

		// JCR
		Repository repository = bc.getService(bc.getServiceReference(Repository.class));
		Session session = Subject.doAs(subject, new PrivilegedAction<Session>() {

			@Override
			public Session run() {
				try {
					return repository.login();
				} catch (RepositoryException e) {
					throw new RuntimeException("Cannot login", e);
				}
			}

		});
		UUID processUuid = UUID.randomUUID();
		GregorianCalendar started = new GregorianCalendar();
		Node groupHome = NodeUtils.getGroupHome(session, workgroup);
		String processPath = SlcNames.SLC_SYSTEM + "/" + SlcNames.SLC_PROCESSES + "/"
				+ JcrUtils.dateAsPath(started, true) + processUuid;
		Node processNode = JcrUtils.mkdirs(groupHome, processPath, SlcTypes.SLC_PROCESS);
		Node realizedFlowNode;
		try {
			processNode.setProperty(SlcNames.SLC_UUID, processUuid.toString());
			processNode.setProperty(SlcNames.SLC_STATUS, ExecutionProcess.RUNNING);
			realizedFlowNode = processNode.addNode(SlcNames.SLC_FLOW);
			realizedFlowNode.addMixin(SlcTypes.SLC_REALIZED_FLOW);
			realizedFlowNode.setProperty(SlcNames.SLC_STARTED, started);
			realizedFlowNode.setProperty(SlcNames.SLC_NAME, flowName);
			Node addressNode = realizedFlowNode.addNode(SlcNames.SLC_ADDRESS, NodeType.NT_ADDRESS);
			addressNode.setProperty(Property.JCR_PATH, flowName);
			processNode.getSession().save();
		} catch (RepositoryException e1) {
			throw new SlcException("Cannot register SLC process", e1);
		}

		if (log.isDebugEnabled())
			log.debug(userDn + " " + workgroup + " " + flowName);

		try {
			resp.setHeader("Content-Type", "application/json");
			ServiceChannel serviceChannel = new ServiceChannel(Channels.newChannel(in), Channels.newChannel(out),
					executor);
			Callable<Integer> task;
			if (ext.equals("api")) {
				String uri = Files.readAllLines(baseDir.resolve(flowName)).get(0);
				task = new WebServiceTask(serviceChannel, uri);
			} else {
				task = createTask(serviceChannel, flowName);
			}

			if (task == null)
				throw new SlcException("No task found for " + flowName);

			// execute
			Future<Integer> f = executor.submit(task);
			int written = f.get();
			if (log.isTraceEnabled())
				log.trace("Written " + written + " bytes");
			try {
				processNode.setProperty(SlcNames.SLC_STATUS, ExecutionProcess.COMPLETED);
				realizedFlowNode.setProperty(SlcNames.SLC_COMPLETED, new GregorianCalendar());
				processNode.getSession().save();
			} catch (RepositoryException e1) {
				throw new SlcException("Cannot update SLC process status", e1);
			}
		} catch (Exception e) {
			try {
				processNode.setProperty(SlcNames.SLC_STATUS, ExecutionProcess.ERROR);
				realizedFlowNode.setProperty(SlcNames.SLC_COMPLETED, new GregorianCalendar());
				processNode.getSession().save();
			} catch (RepositoryException e1) {
				throw new SlcException("Cannot update SLC process status", e1);
			}
			throw new SlcException("Task " + flowName + " failed", e);
		} finally {
			JcrUtils.logoutQuietly(session);
		}

		// JsonElement answer = jsonParser.parse(answerStr);
		// resp.setHeader("Content-Type", "application/json");
		// JsonWriter jsonWriter = gson.newJsonWriter(resp.getWriter());
		// jsonWriter.setIndent(" ");
		// gson.toJson(answer, jsonWriter);
		// jsonWriter.flush();
	}

	protected Callable<Integer> createTask(ServiceChannel serviceChannel, String flowName) {
		return null;
	}

	protected Path getBaseDir() {
		return baseDir;
	}

	public static void register(BundleContext bc, String alias, RunnerServlet runnerServlet, String httpAuthrealm) {
		try {
			ServiceTracker<HttpService, HttpService> serviceTracker = new ServiceTracker<HttpService, HttpService>(bc,
					HttpService.class, null) {

				@Override
				public HttpService addingService(ServiceReference<HttpService> reference) {
					// TODO Auto-generated method stub
					HttpService httpService = super.addingService(reference);
					try {
						httpService.registerServlet(alias, runnerServlet, null, new RunnerHttpContext(httpAuthrealm));
					} catch (Exception e) {
						throw new SlcException("Cannot register servlet", e);
					}
					return httpService;
				}

			};
			// ServiceReference<HttpService> ref =
			// bc.getServiceReference(HttpService.class);
			// HttpService httpService = bc.getService(ref);
			// httpService.registerServlet(alias, runnerServlet, null, null);
			// bc.ungetService(ref);
			serviceTracker.open();
		} catch (Exception e) {
			throw new SlcException("Cannot register servlet", e);
		}
	}

	public static void unregister(BundleContext bc, String alias) {
		try {
			ServiceReference<HttpService> ref = bc.getServiceReference(HttpService.class);
			if (ref == null)
				return;
			HttpService httpService = bc.getService(ref);
			httpService.unregister(alias);
			bc.ungetService(ref);
		} catch (Exception e) {
			throw new SlcException("Cannot unregister servlet", e);
		}
	}

	CmsSession getByLocalId(String localId) {
		// BundleContext bc =
		// FrameworkUtil.getBundle(RunnerServlet.class).getBundleContext();
		Collection<ServiceReference<CmsSession>> sr;
		try {
			sr = bc.getServiceReferences(CmsSession.class, "(" + CmsSession.SESSION_LOCAL_ID + "=" + localId + ")");
		} catch (InvalidSyntaxException e) {
			throw new SlcException("Cannot get CMS session for id " + localId, e);
		}
		ServiceReference<CmsSession> cmsSessionRef;
		if (sr.size() == 1) {
			cmsSessionRef = sr.iterator().next();
			return (CmsSession) bc.getService(cmsSessionRef);
		} else if (sr.size() == 0) {
			return null;
		} else
			throw new SlcException(sr.size() + " CMS sessions registered for " + localId);

	}

}
