package org.argeo.slc.repo.osgi;

import java.io.File;
import java.util.Map;

import org.eclipse.aether.artifact.AbstractArtifact;
import org.eclipse.aether.artifact.Artifact;

/**
 * An artifact whose identity is derived from another artifact. <em>Note:</em> Instances of this class are immutable and
 * the exposed mutators return new objects rather than changing the current instance.
 */
final class SubArtifact
    extends AbstractArtifact
{

    private final Artifact mainArtifact;

    private final String classifier;

    private final String extension;

    private final File file;

    private final Map<String, String> properties;

    /**
     * Creates a new sub artifact. The classifier and extension specified for this artifact may use the asterisk
     * character "*" to refer to the corresponding property of the main artifact. For instance, the classifier
     * "*-sources" can be used to refer to the source attachment of an artifact. Likewise, the extension "*.asc" can be
     * used to refer to the GPG signature of an artifact.
     * 
     * @param mainArtifact The artifact from which to derive the identity, must not be {@code null}.
     * @param classifier The classifier for this artifact, may be {@code null} if none.
     * @param extension The extension for this artifact, may be {@code null} if none.
     */
    public SubArtifact( Artifact mainArtifact, String classifier, String extension )
    {
        this( mainArtifact, classifier, extension, (File) null );
    }

    /**
     * Creates a new sub artifact. The classifier and extension specified for this artifact may use the asterisk
     * character "*" to refer to the corresponding property of the main artifact. For instance, the classifier
     * "*-sources" can be used to refer to the source attachment of an artifact. Likewise, the extension "*.asc" can be
     * used to refer to the GPG signature of an artifact.
     * 
     * @param mainArtifact The artifact from which to derive the identity, must not be {@code null}.
     * @param classifier The classifier for this artifact, may be {@code null} if none.
     * @param extension The extension for this artifact, may be {@code null} if none.
     * @param file The file for this artifact, may be {@code null} if unresolved.
     */
    public SubArtifact( Artifact mainArtifact, String classifier, String extension, File file )
    {
        this( mainArtifact, classifier, extension, null, file );
    }

    /**
     * Creates a new sub artifact. The classifier and extension specified for this artifact may use the asterisk
     * character "*" to refer to the corresponding property of the main artifact. For instance, the classifier
     * "*-sources" can be used to refer to the source attachment of an artifact. Likewise, the extension "*.asc" can be
     * used to refer to the GPG signature of an artifact.
     * 
     * @param mainArtifact The artifact from which to derive the identity, must not be {@code null}.
     * @param classifier The classifier for this artifact, may be {@code null} if none.
     * @param extension The extension for this artifact, may be {@code null} if none.
     * @param properties The properties of the artifact, may be {@code null}.
     */
    public SubArtifact( Artifact mainArtifact, String classifier, String extension, Map<String, String> properties )
    {
        this( mainArtifact, classifier, extension, properties, null );
    }

    /**
     * Creates a new sub artifact. The classifier and extension specified for this artifact may use the asterisk
     * character "*" to refer to the corresponding property of the main artifact. For instance, the classifier
     * "*-sources" can be used to refer to the source attachment of an artifact. Likewise, the extension "*.asc" can be
     * used to refer to the GPG signature of an artifact.
     * 
     * @param mainArtifact The artifact from which to derive the identity, must not be {@code null}.
     * @param classifier The classifier for this artifact, may be {@code null} if none.
     * @param extension The extension for this artifact, may be {@code null} if none.
     * @param properties The properties of the artifact, may be {@code null}.
     * @param file The file for this artifact, may be {@code null} if unresolved.
     */
    public SubArtifact( Artifact mainArtifact, String classifier, String extension, Map<String, String> properties,
                        File file )
    {
        if ( mainArtifact == null )
        {
            throw new IllegalArgumentException( "no artifact specified" );
        }
        this.mainArtifact = mainArtifact;
        this.classifier = classifier;
        this.extension = extension;
        this.file = file;
        this.properties = copyProperties( properties );
    }

    private SubArtifact( Artifact mainArtifact, String classifier, String extension, File file,
                         Map<String, String> properties )
    {
        // NOTE: This constructor assumes immutability of the provided properties, for internal use only
        this.mainArtifact = mainArtifact;
        this.classifier = classifier;
        this.extension = extension;
        this.file = file;
        this.properties = properties;
    }

    public String getGroupId()
    {
        return mainArtifact.getGroupId();
    }

    public String getArtifactId()
    {
        return mainArtifact.getArtifactId();
    }

    public String getVersion()
    {
        return mainArtifact.getVersion();
    }

    public String getBaseVersion()
    {
        return mainArtifact.getBaseVersion();
    }

    public boolean isSnapshot()
    {
        return mainArtifact.isSnapshot();
    }

    public String getClassifier()
    {
        return expand( classifier, mainArtifact.getClassifier() );
    }

    public String getExtension()
    {
        return expand( extension, mainArtifact.getExtension() );
    }

    public File getFile()
    {
        return file;
    }

    public Artifact setFile( File file )
    {
        if ( ( this.file == null ) ? file == null : this.file.equals( file ) )
        {
            return this;
        }
        return new SubArtifact( mainArtifact, classifier, extension, file, properties );
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public Artifact setProperties( Map<String, String> properties )
    {
        if ( this.properties.equals( properties ) || ( properties == null && this.properties.isEmpty() ) )
        {
            return this;
        }
        return new SubArtifact( mainArtifact, classifier, extension, properties, file );
    }

    private static String expand( String pattern, String replacement )
    {
        String result = "";
        if ( pattern != null )
        {
            result = pattern.replace( "*", replacement );

            if ( replacement.length() <= 0 )
            {
                if ( pattern.startsWith( "*" ) )
                {
                    int i = 0;
                    for ( ; i < result.length(); i++ )
                    {
                        char c = result.charAt( i );
                        if ( c != '-' && c != '.' )
                        {
                            break;
                        }
                    }
                    result = result.substring( i );
                }
                if ( pattern.endsWith( "*" ) )
                {
                    int i = result.length() - 1;
                    for ( ; i >= 0; i-- )
                    {
                        char c = result.charAt( i );
                        if ( c != '-' && c != '.' )
                        {
                            break;
                        }
                    }
                    result = result.substring( 0, i + 1 );
                }
            }
        }
        return result;
    }

}