/*
 * Copyright (C) 2010 Mathieu Baudier <mbaudier@argeo.org>
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

package org.argeo.slc.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObjectList implements Serializable{
	private static final long serialVersionUID = -7200599279369084551L;
	private List<Serializable> objects = new ArrayList<Serializable>();

	public ObjectList() {
	}

	public ObjectList(Collection<? extends Serializable> objects) {
		this.objects.addAll(objects);
	}

	@SuppressWarnings(value = { "unchecked" })
	public <T extends Serializable> void fill(List<T> objects) {
		for (Serializable o : this.objects){
			objects.add((T) o);
		}
	}

	public List<Serializable> getObjects() {
		return objects;
	}

	public void setObjects(List<Serializable> objects) {
		this.objects = objects;
	}

}
