package org.argeo.slc.backup.vfs;

import org.apache.commons.vfs2.FileObject;

/** Backup utilities */
public class BackupUtils {
	/** Close a file object quietly even if it is null or throws an exception. */
	public static void closeFOQuietly(FileObject fo) {
		if (fo != null) {
			try {
				fo.close();
			} catch (Exception e) {
				// silent
			}
		}
	}
	
	/** Prevents instantiation */
	private BackupUtils() {
	}
}
