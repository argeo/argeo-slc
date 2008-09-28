package org.argeo.slc.autoui.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AutoUiServer extends Remote {
	public Object executeTask(AutoUiTask task) throws RemoteException;
}
