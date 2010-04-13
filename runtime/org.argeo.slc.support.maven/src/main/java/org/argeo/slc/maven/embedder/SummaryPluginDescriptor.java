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
