package org.argeo.slc.lib.vbox;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.core.execution.tasks.SystemCall;

public class VBoxManager {
	private final static Log log = LogFactory.getLog(VBoxManager.class);

	private String machineName;
	private String executable = "VBoxManage";

	private List<VBoxNat> nats = new ArrayList<VBoxNat>();

	public void applyNats() {
		StringBuffer script = new StringBuffer("");
		for (VBoxNat vBoxNat : nats) {
			for (String id : vBoxNat.getMappings().keySet()) {
				VBoxPortMapping mapping = vBoxNat.getMappings().get(id);
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"Protocol", mapping.getProtocol(), script)).run();
				script.append('\n');
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"GuestPort", mapping.getGuest(), script)).run();
				script.append('\n');
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"HostPort", mapping.getHost(), script)).run();
				script.append('\n');
				script.append('\n');
			}
			script.append('\n');
		}

		if (log.isDebugEnabled())
			log.debug("Port setting script:\n" + script);
	}

	protected List<Object> createNatCommand(String id, String device,
			String cfgKey, String value, StringBuffer script) {
		List<Object> cmd = new ArrayList<Object>();
		cmd.add(executable);
		cmd.add("setextradata");
		cmd.add(machineName);
		cmd.add("VBoxInternal/Devices/" + device + "/0/LUN#0/Config/" + id
				+ "/" + cfgKey);
		cmd.add(value);

		for (Object arg : cmd) {
			script.append(arg).append(' ');
		}

		return cmd;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getExecutable() {
		return executable;
	}

	public void setExecutable(String executable) {
		this.executable = executable;
	}

	public List<VBoxNat> getNats() {
		return nats;
	}

	public void setNats(List<VBoxNat> boxNats) {
		nats = boxNats;
	}

}
