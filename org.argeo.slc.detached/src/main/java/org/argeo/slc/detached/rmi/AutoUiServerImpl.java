package org.argeo.slc.detached.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.TreeMap;

public class AutoUiServerImpl extends UnicastRemoteObject implements
		AutoUiServer, AutoUiContext {
	private Map map = new TreeMap();

	public AutoUiServerImpl() throws RemoteException {
		super();
	}

	public Object executeTask(AutoUiTask task) throws RemoteException {
		try {
			return task.execute(this);
		} catch (Exception e) {
			throw new RemoteException("Coul not execute task.", e);
		}
	}

	public Object getLocalRef(String key) {
		return map.get(key);
	}

	public void setLocalRef(String key, Object ref) {
		map.put(key, ref);
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String name = "AutoUiServer";
			AutoUiServer engine = new AutoUiServerImpl();
			// AutoUiServer stub = (AutoUiServer) UnicastRemoteObject
			// .exportObject(engine, 0);
			// Registry registry = LocateRegistry.getRegistry();
			// registry.rebind(name, stub);
			Naming.rebind(name, engine);
			System.out.println("AutoUiServer bound");

		} catch (Exception e) {
			System.err.println("AutoUiServer exception:");
			e.printStackTrace();
		}

	}
}
