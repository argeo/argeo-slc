package org.argeo.slc.core.execution;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.security.OsAuthenticationToken;
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
			log.info(buf);
			return buf.toString();
		} else {
			List<URI> uris = asURIs(args);
			String processUuid = agent.process(uris);
			agent.waitFor(processUuid, timeout);
			return processUuid;
		}
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
						if (currUri != null)
							uris.add(new URI(currUri.toString()));
						currUri = new StringBuilder("flow:");

						String currModule = arg;
						currUri.append('/').append(currModule);
						if (!arg.contains("/")) {
							// flow path not in arg go to next arg
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

						String key;
						if (arg.startsWith("--"))
							key = arg.substring(2);
						else if (arg.startsWith("-"))
							key = arg.substring(1);
						else
							throw new SlcException("Cannot intepret key: "
									+ arg);
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

	protected void help(String[] rawArgs, StringBuilder buf) {
		String[] args = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
		List<URI> uris = asURIs(args);
		uris: for (URI uri : uris) {
			String[] path = uri.getPath().split("/");
			if (path.length < 2) {
				for (ExecutionModuleDescriptor emd : agent
						.listExecutionModuleDescriptors()) {
					buf.append(
							"# Execution Module " + emd.getName() + " v"
									+ emd.getVersion()).append('\n');
					if (emd.getDescription() != null
							&& !emd.getDescription().trim().equals(""))
						buf.append(emd.getDescription()).append('\n');
				}
				continue uris;
			}

			String moduleName = path[1];
			// TODO process version
			String moduleVersion = null;

			ExecutionModuleDescriptor emd = agent.getExecutionModuleDescriptor(
					moduleName, moduleVersion);

			if (path.length >= 2) {
				StringBuilder flow = new StringBuilder();
				for (int i = 2; i < path.length; i++)
					flow.append('/').append(path[i]);
				String flowPath = flow.toString();
				ExecutionFlowDescriptor flowDescriptor = null;
				for (ExecutionFlowDescriptor efd : emd.getExecutionFlows()) {
					if (efd.getName().equals(flowPath)) {
						flowDescriptor = efd;
						break;
					}
				}
				if (flowDescriptor == null)
					throw new SlcException("Flow " + uri + " not found");

				buf.append(
						"# Execution Module " + emd.getName() + " v"
								+ emd.getVersion()).append('\n');
				buf.append(" Flow ").append(flowDescriptor.getName());
				if (flowDescriptor.getDescription() != null
						&& !flowDescriptor.getDescription().trim().equals(""))
					buf.append(" ").append(flowDescriptor.getDescription());
				buf.append('\n');
				ExecutionSpec spec = flowDescriptor.getExecutionSpec();
				for (String attrKey : spec.getAttributes().keySet()) {
					ExecutionSpecAttribute esa = spec.getAttributes().get(
							attrKey);
					buf.append("  --").append(attrKey);
					// TODO check values in query part
					if (esa.getValue() != null)
						buf.append(" ").append(esa.getValue());
					buf.append('\n');
				}
			} else {
				// module only
				buf.append(
						"# Execution Module " + emd.getName() + " v"
								+ emd.getVersion()).append('\n');
				if (emd.getDescription() != null
						&& !emd.getDescription().trim().equals(""))
					buf.append(emd.getDescription()).append('\n');
				for (ExecutionFlowDescriptor efd : emd.getExecutionFlows()) {
					buf.append(" ").append(efd.getName());
					if (efd.getDescription() != null
							&& !efd.getDescription().trim().equals(""))
						buf.append(" ").append(efd.getDescription());
				}
				buf.append('\n');
			}
		}
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
