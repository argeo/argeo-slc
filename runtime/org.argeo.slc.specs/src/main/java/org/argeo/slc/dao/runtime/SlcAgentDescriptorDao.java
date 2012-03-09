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
package org.argeo.slc.dao.runtime;

import java.util.List;

import org.argeo.slc.runtime.SlcAgentDescriptor;

/**
 * @deprecated will be removed in SLC 2.0, use
 *             {@link org.argeo.slc.dao.SlcAgentDescriptorDao} instead.
 */
public interface SlcAgentDescriptorDao {
	public void create(SlcAgentDescriptor slcAgentDescriptor);

	public void delete(SlcAgentDescriptor slcAgentDescriptor);

	public void delete(String agentId);

	public List<SlcAgentDescriptor> listSlcAgentDescriptors();

	public SlcAgentDescriptor getAgentDescriptor(String agentId);
}
