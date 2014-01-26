package org.argeo.slc.lib.linux;

import java.io.File;

import javax.security.auth.callback.CallbackHandler;

import org.argeo.slc.SlcException;
import org.argeo.slc.core.execution.tasks.SystemCall;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/** Deploy and initialize an LXC container. */
public class DeployLxcContainer implements Runnable {

	private String chroot;

	private Resource hostScript = new ClassPathResource(
			"/org/argeo/slc/lib/linux/lxc-init-host.sh", getClass()
					.getClassLoader());
	private Resource guestScript = new ClassPathResource(
			"/org/argeo/slc/lib/linux/lxc-init-guest.sh", getClass()
					.getClassLoader());;

	private CallbackHandler callbackHandler;

	private Integer ram = 1024;
	private Integer vcpu = 2;

	@Override
	public void run() {
		if (chroot == null || chroot.trim().equals(""))
			throw new SlcException("A chroot directory must be defined");

		File chrootDir = new File(chroot);
		chrootDir.mkdirs();

		ScriptCall hostCall = new ScriptCall(hostScript);
		hostCall.setLogCommand(true);
		hostCall.arg(chroot);
		// hostCall.getEnvironmentVariables().put("CHROOT", chroot);
		hostCall.setSudo("");
		hostCall.setCallbackHandler(callbackHandler);
		hostCall.run();

		ScriptCall guestCall = new ScriptCall(guestScript);
		guestCall.setLogCommand(true);
		guestCall.setSudo("");
		guestCall.setCallbackHandler(callbackHandler);
		guestCall.setChroot(chroot);
		guestCall.run();

		SystemCall virtInstall = new SystemCall(
				"virt-install --connect lxc:/// --name " + chrootDir.getName()
						+ " --ram " + ram + " --vcpu " + vcpu
						+ " --filesystem " + chrootDir.getAbsolutePath()
						+ ",/ --noautoconsole");
		virtInstall.setLogCommand(true);
		virtInstall.setSudo("");
		virtInstall.run();
	}

	public void setChroot(String chroot) {
		this.chroot = chroot;
	}

	public void setHostScript(Resource hostScript) {
		this.hostScript = hostScript;
	}

	public void setGuestScript(Resource guestScript) {
		this.guestScript = guestScript;
	}

	public void setCallbackHandler(CallbackHandler callbackHandler) {
		this.callbackHandler = callbackHandler;
	}

	public void setRam(Integer ram) {
		this.ram = ram;
	}

	public void setVcpu(Integer vcpu) {
		this.vcpu = vcpu;
	}

}
