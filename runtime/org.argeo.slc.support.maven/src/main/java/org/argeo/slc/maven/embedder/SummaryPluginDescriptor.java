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

package org.argeo.slc.maven.embedder;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: SummaryPluginDescriptor.java 292750 2005-09-30 14:33:10Z jdcasey $
 */
public class SummaryPluginDescriptor
{
    private String groupId;

    private String artifactId;

    private String name;

    public SummaryPluginDescriptor( String groupId, String artifactId, String name )
    {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.name = name;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getName()
    {
        return name;
    }
}
