package org.argeo.slc.client.ui.dist.commands;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import aQute.lib.osgi.Builder;
import aQute.lib.osgi.Constants;
import aQute.lib.osgi.Jar;

public class BndProcess extends AbstractHandler implements Constants {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Builder b = new Builder();
			b.setProperty(EXPORT_PACKAGE, "org.osgi.framework");
			b.setProperty(PRIVATE_PACKAGE, "*");
			b.addClasspath(new File(
					"/home/mbaudier/.m2/repository/org/eclipse/osgi/org.eclipse.osgi/3.6.1/org.eclipse.osgi-3.6.1.jar"));

			Jar jar = b.build();
			Manifest m = jar.getManifest();
			jar.write(new File(b.getBsn() + ".jar"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static void main(String[] args) {
		try {
			new BndProcess().execute(null);
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
