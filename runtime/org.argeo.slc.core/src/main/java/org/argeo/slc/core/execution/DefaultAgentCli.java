package org.argeo.slc.core.execution;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.argeo.security.OsAuthenticationToken;
import org.argeo.slc.SlcException;
import org.argeo.slc.execution.SlcAgent;
import org.argeo.slc.execution.SlcAgentCli;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.context.SecurityContextHolder;

public class DefaultAgentCli implements SlcAgentCli {
	private final static String UTF8 = "UTF-8";
	private SlcAgent agent;
	private AuthenticationManager authenticationManager;

	private Long timeout = 24 * 60 * 60 * 1000l;

	public String process(String[] args) {
		OsAuthenticationToken oat = new OsAuthenticationToken();
		Authentication authentication = authenticationManager.authenticate(oat);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		List<URI> uris = asURIs(args);
		String processUuid = agent.process(uris);
		agent.waitFor(processUuid, timeout);
		return processUuid;
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
