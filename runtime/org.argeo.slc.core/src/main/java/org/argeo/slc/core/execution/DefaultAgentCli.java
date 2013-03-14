package org.argeo.slc.core.execution;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.security.OsAuthenticationToken;
import org.argeo.slc.BasicNameVersion;
import org.argeo.slc.NameVersion;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.ExecutionFlowDescriptor;
import org.argeo.slc.execution.ExecutionModuleDescriptor;
import org.argeo.slc.execution.ExecutionSpec;
import org.argeo.slc.execution.ExecutionSpecAttribute;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.execution.SlcAgentCli;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Authenticates thread and executes synchronously a command line execution.
 * Reference implementation of args to URIs algorithm.
 */
public class DefaultAgentCli implements SlcAgentCli {
	private final static Log log = LogFactory.getLog(DefaultAgentCli.class);

	private final static String UTF8 = "UTF-8";
	private SlcAgent agent;
	private AuthenticationManager authenticationManager;

	private Long timeout = 24 * 60 * 60 * 1000l;

	public String process(String[] args) {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			OsAuthenticationToken oat = new OsAuthenticationToken();
			Authentication authentication = authenticationManager
					.authenticate(oat);
			SecurityContextHolder.getContext()
					.setAuthentication(authentication);
		}

		if (args.length > 0 && args[0].equals("help")) {
			StringBuilder buf = new StringBuilder();
			help(args, buf);
			log.info("\n" + buf);
			return buf.toString();
		} else {
			List<URI> uris = asURIs(args);
			String processUuid = agent.process(uris);
			agent.waitFor(processUuid, timeout);
			return processUuid;
		}
	}

	protected void help(String[] rawArgs, StringBuilder buf) {
		String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
		if (args.length == 0) {// modules
			for (ExecutionModuleDescriptor emd : agent
					.listExecutionModuleDescriptors()) {
				appendModule(emd, buf);
			}
		} else if (args.length == 1 && !args[0].contains("/")) {// single module
			NameVersion nameVersion = new BasicNameVersion(args[0]);
			ExecutionModuleDescriptor emd = agent.getExecutionModuleDescriptor(
					nameVersion.getName(), nameVersion.getVersion());
			appendModule(emd, buf);

			// flows
			for (ExecutionFlowDescriptor efd : emd.getExecutionFlows()) {
				buf.append(" ").append(efd.getName());
				if (efd.getDescription() != null
						&& !efd.getDescription().trim().equals(""))
					buf.append(" : ").append(" ").append(efd.getDescription());
				buf.append('\n');
			}
			return;
		} else {
			List<URI> uris = asURIs(args);
			for (URI uri : uris) {
				appendUriHelp(uri, buf);
			}
		}
	}

	protected void appendUriHelp(URI uri, StringBuilder buf) {
		String[] path = uri.getPath().split("/");
		NameVersion nameVersion = new BasicNameVersion(path[1]);
		ExecutionModuleDescriptor emd = agent.getExecutionModuleDescriptor(
				nameVersion.getName(), nameVersion.getVersion());

		StringBuilder flow = new StringBuilder();
		for (int i = 2; i < path.length; i++)
			flow.append('/').append(path[i]);
		String flowPath = flow.toString();
		ExecutionFlowDescriptor efd = findExecutionFlowDescriptor(emd, flowPath);
		if (efd == null)
			throw new SlcException("Flow " + uri + " not found");

		appendModule(emd, buf);

		buf.append(" ").append(efd.getName());
		if (efd.getDescription() != null
				&& !efd.getDescription().trim().equals(""))
			buf.append(" : ").append(" ").append(efd.getDescription());
		buf.append('\n');
		Map<String, Object> values = DefaultAgent.getQueryMap(uri.getQuery());
		ExecutionSpec spec = efd.getExecutionSpec();
		for (String attrKey : spec.getAttributes().keySet()) {
			ExecutionSpecAttribute esa = spec.getAttributes().get(attrKey);
			buf.append("  --").append(attrKey);
			if (values.containsKey(attrKey))
				buf.append(" ").append(values.get(attrKey));
			if (esa.getValue() != null)
				buf.append(" (").append(esa.getValue()).append(')');
			buf.append('\n');
		}
	}

	private void appendModule(ExecutionModuleDescriptor emd, StringBuilder buf) {
		buf.append("# ").append(emd.getName());
		if (emd.getDescription() != null
				&& !emd.getDescription().trim().equals(""))
			buf.append(" : ").append(emd.getDescription());
		if (emd.getVersion() != null)
			buf.append(" (v").append(emd.getVersion()).append(")");
		buf.append('\n');
	}

	public static List<URI> asURIs(String[] args) {
		try {
			List<URI> uris = new ArrayList<URI>();
			List<String> leftOvers = new ArrayList<String>();

			Boolean hasArgs = false;
			String currKey = null;
			StringBuilder currUri = null;
			Iterator<String> argIt = Arrays.asList(args).iterator();
			while (argIt.hasNext()) {
				String arg = argIt.next();
				if (!arg.startsWith("-")) {
					if (currKey != null) {// value
						currUri.append(URLEncoder.encode(arg, UTF8));
						currKey = null;
					} else { // module
						if (currUri != null) {
							uris.add(new URI(currUri.toString()));
						}
						currUri = new StringBuilder("flow:");

						String currModule = arg;
						currUri.append('/').append(currModule);
						if (!arg.contains("/")) {
							// flow path not in arg go to next arg
							if (!argIt.hasNext())
								throw new SlcException("No flow found");
							String currFlow = argIt.next();
							if (!currFlow.startsWith("/"))
								currFlow = "/" + currFlow;
							currUri.append(currFlow);
						}
					}
				} else {
					if (currUri == null) {// first args
						leftOvers.add(arg);
					} else {
						String key;
						if (arg.startsWith("--"))
							key = arg.substring(2);
						else if (arg.startsWith("-"))
							key = arg.substring(1);
						else {
							throw new SlcException("Cannot intepret key: "
									+ arg);
						}

						if (!hasArgs) {
							currUri.append('?');
							hasArgs = true;
						} else {
							currUri.append('&');
						}

						// deal with boolean keys
						if (currKey != null) {// value
							currUri.append(URLEncoder.encode("true", UTF8));
							currKey = null;
						}

						currKey = key;
						currUri.append(URLEncoder.encode(key, UTF8))
								.append('=');
					}
				}
			}
			if (currUri != null)
				uris.add(new URI(currUri.toString()));
			return uris;
		} catch (Exception e) {
			throw new SlcException("Cannot convert " + Arrays.toString(args)
					+ " to flow URI", e);
		}
	}

	private ExecutionFlowDescriptor findExecutionFlowDescriptor(
			ExecutionModuleDescriptor emd, String flowPath) {
		ExecutionFlowDescriptor flowDescriptor = null;
		for (ExecutionFlowDescriptor efd : emd.getExecutionFlows()) {
			String name = efd.getName();
			// normalize name as flow path
			if (!name.startsWith("/"))
				name = "/" + name;
			if (name.endsWith("/"))
				name = name.substring(0, name.length() - 1);
			if (name.equals(flowPath)) {
				flowDescriptor = efd;
				break;
			}
		}
		return flowDescriptor;
	}

	public void setAgent(SlcAgent agent) {
		this.agent = agent;
	}

	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

}
