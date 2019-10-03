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
package org.argeo.slc.client.ui.decorators;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.argeo.slc.SlcException;
import org.argeo.slc.SlcNames;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.SlcImages;
import org.argeo.slc.client.ui.SlcUiConstants;
import org.argeo.slc.client.ui.model.ResultFolder;
import org.argeo.slc.client.ui.model.ResultParent;
import org.argeo.slc.client.ui.model.SingleResultNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

/** Dynamically decorates the results tree. */
public class ResultFailedDecorator extends LabelProvider implements
		ILabelDecorator {

	// FIXME why not use? org.eclipse.jface.viewers.DecoratingLabelProvider

	// private final static Log log = LogFactory
	// .getLog(ResultFailedDecorator.class);

	private final static DateFormat dateFormat = new SimpleDateFormat(
			SlcUiConstants.DEFAULT_DISPLAY_DATE_TIME_FORMAT);

	// hack for SWT resource leak
	// see http://www.eclipse.org/articles/swt-design-2/swt-design-2.html
	// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=181215
	private final Image passedFolder;
	private final Image failedFolder;
	private final Image failedSingleResult;
	private final Image errorSingleResult;
	private final Image passedSingleResult;

	public ResultFailedDecorator() {
		super();
		ImageDescriptor failedDesc = ClientUiPlugin.getDefault().getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
		failedFolder = new DecorationOverlayIcon(SlcImages.FOLDER, failedDesc,
				IDecoration.TOP_LEFT).createImage();
		passedFolder = new DecorationOverlayIcon(SlcImages.FOLDER,
				SlcImages.EXECUTION_PASSED, IDecoration.TOP_LEFT).createImage();
		failedSingleResult = new DecorationOverlayIcon(
				SlcImages.PROCESS_COMPLETED, failedDesc, IDecoration.TOP_LEFT)
				.createImage();
		errorSingleResult = new DecorationOverlayIcon(
				SlcImages.PROCESS_COMPLETED, SlcImages.EXECUTION_ERROR,
				IDecoration.TOP_LEFT).createImage();
		passedSingleResult = new DecorationOverlayIcon(
				SlcImages.PROCESS_COMPLETED, SlcImages.EXECUTION_PASSED,
				IDecoration.TOP_LEFT).createImage();
	}

	// Method to decorate Image
	public Image decorateImage(Image image, Object object) {

		// This method returns an annotated image or null if the
		// image need not be decorated. Returning a null image
		// decorates resource icon with basic decorations provided
		// by Eclipse
		if (object instanceof ResultParent) {
			if (((ResultParent) object).isPassed()) {
				if (object instanceof SingleResultNode)
					return passedSingleResult;
				else if (object instanceof ResultFolder)
					return passedFolder;
				else
					return null;
			} else {
				// ImageDescriptor desc = ClientUiPlugin.getDefault()
				// .getWorkbench().getSharedImages()
				// .getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
				// DecorationOverlayIcon decoratedImage = new
				// DecorationOverlayIcon(
				// image, desc, IDecoration.TOP_LEFT);
				// return decoratedImage.createImage();
				if (object instanceof SingleResultNode) {
					SingleResultNode srn = (SingleResultNode) object;
					boolean isError = false;
					try {
						isError = srn.getNode()
								.getNode(SlcNames.SLC_AGGREGATED_STATUS)
								.hasProperty(SlcNames.SLC_ERROR_MESSAGE);
					} catch (RepositoryException re) {
						// Silent node might not exist
					}
					if (isError)
						return errorSingleResult;
					else
						return failedSingleResult;

				} else
					return failedFolder;
			}
		}
		return null;
	}

	// Method to decorate Text
	public String decorateText(String label, Object object) {
		if (object instanceof SingleResultNode) {
			SingleResultNode srNode = (SingleResultNode) object;
			Node node = srNode.getNode();
			String decoration = null;
			try {
				if (node.hasProperty(SlcNames.SLC_COMPLETED))
					decoration = dateFormat.format(node
							.getProperty(SlcNames.SLC_COMPLETED).getDate()
							.getTime());
			} catch (RepositoryException re) {
				throw new SlcException(
						"Unexpected error defining text decoration for result",
						re);
			}
			return label + " [" + decoration + "]";
		} else
			return null;
	}

	@Override
	public void dispose() {
		failedFolder.dispose();
		failedSingleResult.dispose();
		super.dispose();
	}

}
