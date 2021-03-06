package org.argeo.slc.maven;

/*******************************************************************************
 * Copyright (c) 2010 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.apache.maven.wagon.providers.http.LightweightHttpWagon;
import org.apache.maven.wagon.providers.webdav.WebDavWagon;
import org.eclipse.aether.transport.wagon.WagonProvider;

public class ManualWagonProvider implements WagonProvider {

	public Wagon lookup(String roleHint) throws Exception {
		if ("file".equals(roleHint)) {
			return new FileWagon();
		} else if ("http".equals(roleHint)) {
			return new LightweightHttpWagon();
		} else if ("webdav".equals(roleHint)) {
			return new WebDavWagon();
		}
		return null;
	}

	public void release(Wagon wagon) {

	}

}
