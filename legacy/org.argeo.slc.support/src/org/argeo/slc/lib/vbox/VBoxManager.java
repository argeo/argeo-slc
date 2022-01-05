package org.argeo.slc.lib.vbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.argeo.api.cms.CmsLog;
import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.core.io.Resource;

public class VBoxManager {
	private final static CmsLog log = CmsLog.getLog(VBoxManager.class);

	private VBoxMachine vm;
	private String executable = "VBoxManage";

	private List<VBoxNat> nats = new ArrayList<VBoxNat>();

	public void importOvf(Resource ovfDefinition) {
		try {
			List<Object> cmd = new ArrayList<Object>();
			cmd.add(executable);
			cmd.add("import");
			cmd.add(ovfDefinition.getFile().getCanonicalPath());
			cmd.add("--vsys 0 --vmname <name>");
			cmd.add("0");
			cmd.add("--vmname");
			cmd.add(vm.getName());
			new SystemCall(cmd).run();
		} catch (IOException e) {
			throw new SlcException("Cannot import OVF appliance "
					+ ovfDefinition, e);
		}
	}

	public void startVm() {
		startVm("gui");
	}

	public void startVmHeadless() {
		startVm("vrdp");
	}

	public void startVm(String type) {
		List<Object> cmd = new ArrayList<Object>();
		cmd.add(executable);
		cmd.add("startvm");
		cmd.add(vm.getName());
		cmd.add("--type");
		cmd.add(type);
		new SystemCall(cmd).run();
	}

	public void applyNats() {
		StringBuffer script = new StringBuffer("");
		for (VBoxNat vBoxNat : nats) {
			for (String id : vBoxNat.getMappings().keySet()) {
				VBoxPortMapping mapping = vBoxNat.getMappings().get(id);

				// Try to delete rule first
				try {
					StringBuffer delCmd = new StringBuffer(
							"VBoxManage modifyvm");
					delCmd.append(" \"").append(vm.getName()).append("\"");
					delCmd.append(" --natpf").append(vBoxNat.getDevice())
							.append(" ");
					delCmd.append(" delete ");
					delCmd.append("\"").append(id).append("\"");
					new SystemCall(delCmd.toString()).run();
					script.append(delCmd).append("\n");
				} catch (Exception e) {
					// silent
				}

				StringBuffer cmd = new StringBuffer("VBoxManage modifyvm");
				cmd.append(" \"").append(vm.getName()).append("\"");
				cmd.append(" --natpf").append(vBoxNat.getDevice()).append(" ");
				cmd.append("\"");
				cmd.append(id).append(",");
				cmd.append(mapping.getProtocol()).append(",");
				cmd.append(",");
				cmd.append(mapping.getHostPort()).append(",");
				cmd.append(vBoxNat.getGuestIp()).append(",");
				cmd.append(mapping.getGuestPort());
				cmd.append("\"");

				new SystemCall(cmd.toString()).run();
				script.append(cmd).append("\n");

				// Older VirtualBox
				// new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
				// "Protocol", mapping.getProtocol(), script)).run();
				// script.append('\n');
				// new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
				// "GuestPort", mapping.getGuest(), script)).run();
				// script.append('\n');
				// new SystemCall(createNatCommand(id, vBoxNat.getDevice(),
				// "HostPort", mapping.getHost(), script)).run();
				// script.append('\n');
				// script.append('\n');
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
		cmd.add(vm.getName());
		cmd.add("VBoxInternal/Devices/" + device + "/0/LUN#0/Config/" + id
				+ "/" + cfgKey);
		cmd.add(value);

		for (Object arg : cmd) {
			script.append(arg).append(' ');
		}

		return cmd;
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

	public void setVm(VBoxMachine vm) {
		this.vm = vm;
	}

}
