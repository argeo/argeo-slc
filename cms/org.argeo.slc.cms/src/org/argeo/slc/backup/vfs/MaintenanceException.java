package org.argeo.slc.backup.vfs;

@Deprecated
class MaintenanceException extends RuntimeException {
	private static final long serialVersionUID = -5770049663929537270L;

	public MaintenanceException(String message, Throwable cause) {
		super(message, cause);
	}

	public MaintenanceException(String message) {
		super(message);
	}

}
