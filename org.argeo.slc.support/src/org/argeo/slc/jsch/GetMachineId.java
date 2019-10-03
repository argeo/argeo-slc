package org.argeo.slc.jsch;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;

class GetMachineId {

	public static void main(String[] args) {
		URL url = GetMachineId.class.getResource("jaas.cfg");
		System.setProperty("java.security.auth.login.config", url.toExternalForm());

		String hostname = args[0];
		try {
			LoginContext lc = new LoginContext("SINGLE_USER");
			lc.login();

			SshTarget sshTarget = new SshTarget();
			sshTarget.setHost(hostname);
			RemoteExec remoteExec = new RemoteExec();
			remoteExec.setCommand("cat /etc/machine-id");
			remoteExec.setSshTarget(sshTarget);
			List<String> lines = new ArrayList<>();
			remoteExec.setStdOutLines(lines);

			Subject.doAs(lc.getSubject(), remoteExec.asPrivilegedAction());

			String machineId = lines.get(0);
			System.out.println(hostname + ": " + machineId);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
