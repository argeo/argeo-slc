/*
 * Copyright (C) 2007-2012 Mathieu Baudier
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.argeo.slc.client.ui.ClientUiPlugin;
import org.argeo.slc.client.ui.model.ResultParent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;

public class ResultFailedDecorator extends LabelProvider implements
		ILabelDecorator {

	private final static Log log = LogFactory
			.getLog(ResultFailedDecorator.class);

	public ResultFailedDecorator() {
		super();
	}

	// Method to decorate Image
	public Image decorateImage(Image image, Object object) {

		// This method returns an annotated image or null if the
		// image need not be decorated. Returning a null image
		// decorates resource icon with basic decorations provided
		// by Eclipse
		if (object instanceof ResultParent) {
			log.debug("decorate : " + ((ResultParent) object).getName()
					+ " - passed : " + ((ResultParent) object).isPassed());
			if (!((ResultParent) object).isPassed()) {
				ImageDescriptor desc = ClientUiPlugin.getDefault()
						.getWorkbench().getSharedImages()
						.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
				DecorationOverlayIcon decoratedImage = new DecorationOverlayIcon(
						image, desc, IDecoration.TOP_LEFT);
				return decoratedImage.createImage();
			} else
				return null;
		}
		return null;
	}

	// Method to decorate Text
	public String decorateText(String label, Object object) {
		return null;
	}
}
