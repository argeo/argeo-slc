/*
 * Copyright (C) 2007-2012 Argeo GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.argeo.slc.client.ui.dist;

import org.eclipse.swt.graphics.Image;

/** Shared icons. */
public class DistImages {

	public final static Image IMG_ARTIFACT_BASE = DistPlugin
			.getImageDescriptor("icons/artifactBase.gif").createImage();
	public final static Image IMG_ARTIFACT_VERSION_BASE = DistPlugin
			.getImageDescriptor("icons/artifactVersionBase.gif").createImage();
	public final static Image IMG_FILE = DistPlugin.getImageDescriptor(
			"icons/file.gif").createImage();

	/* WORKSPACES */
	public final static Image IMG_WKSP = DistPlugin.getImageDescriptor(
			"icons/distribution_perspective.gif").createImage();

	/* REPOSITORIES */
	public final static Image IMG_REPO = DistPlugin.getImageDescriptor(
			"icons/repo.gif").createImage();
	public final static Image IMG_REPO_READONLY = DistPlugin
			.getImageDescriptor("icons/repoReadOnly.gif").createImage();
	public final static Image IMG_ADD_REPO = DistPlugin.getImageDescriptor(
			"icons/addRepo.gif").createImage();
	public final static Image IMG_REMOVE_REPO = DistPlugin.getImageDescriptor(
			"icons/artifactBase.gif").createImage();
	public final static Image IMG_FETCH_REPO = DistPlugin.getImageDescriptor(
			"icons/fetchRepo.png").createImage();

	/* CHECK BOXES */
	public final static Image CHECKED = DistPlugin.getImageDescriptor(
			"icons/checked.gif").createImage();
	public final static Image UNCHECKED = DistPlugin.getImageDescriptor(
			"icons/unchecked.gif").createImage();
}
