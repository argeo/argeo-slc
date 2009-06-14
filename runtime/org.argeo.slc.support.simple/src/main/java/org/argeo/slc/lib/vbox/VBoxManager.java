package org.argeo.slc.lib.vbox;

import java.util.ArrayList;
import java.util.List;

import org.argeo.slc.core.execution.tasks.SystemCall;

public class VBoxManager {
	private String machineName;
	private String executable = "VBoxManage";

	private List<VBoxNat> nats = new ArrayList<VBoxNat>();

	public void applyNats() {
		for (VBoxNat vBoxNat : nats)
			for (String id : vBoxNat.getMappings().keySet()) {
				VBoxPortMapping mapping = vBoxNat.getMappings().get(id);
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"Protocol", mapping.getProtocol())).run();
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"GuestPort", mapping.getGuest())).run();
				new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
						"HostPort", mapping.getHost())).run();
			}
	}

	protected List<Object> createNatCommand(String id, String device,
			String cfgKey, String value) {
		List<Object> cmd = new ArrayList<Object>();
		cmd.add(executable);
		cmd.add("setextradata");
		cmd.add(machineName);
		cmd.add("VBoxInternal/Devices/" + device + "/0/LUN#0/Config/" + id
				+ "/" + cfgKey);
		cmd.add(value);
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
