/*******************************************************************************
 * Copyright (c) 2012 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.metadata;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A skeleton class for metadata.
 */
public abstract class AbstractMetadata
    implements Metadata
{

    private Metadata newInstance( Map<String, String> properties, File file )
    {
        return new DefaultMetadata( getGroupId(), getArtifactId(), getVersion(), getType(), getNature(), file,
                                    properties );
    }

    public Metadata setFile( File file )
    {
        File current = getFile();
        if ( ( current == null ) ? file == null : current.equals( file ) )
        {
            return this;
        }
        return newInstance( getProperties(), file );
    }

    public Metadata setProperties( Map<String, String> properties )
    {
        Map<String, String> current = getProperties();
        if ( current.equals( properties ) || ( properties == null && current.isEmpty() ) )
        {
            return this;
        }
        return newInstance( copyProperties( properties ), getFile() );
    }

    public String getProperty( String key, String defaultValue )
    {
        String value = getProperties().get( key );
        return ( value != null ) ? value : defaultValue;
    }

    /**
     * Copies the specified metadata properties. This utility method should be used when creating new metadata instances
     * with caller-supplied properties.
     * 
     * @param properties The properties to copy, may be {@code null}.
     * @return The copied and read-only properties, never {@code null}.
     */
    protected static Map<String, String> copyProperties( Map<String, String> properties )
    {
        if ( properties != null && !properties.isEmpty() )
        {
            return Collections.unmodifiableMap( new HashMap<String, String>( properties ) );
        }
        else
        {
            return Collections.emptyMap();
        }
    }

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder( 128 );
        if ( getGroupId().length() > 0 )
        {
            buffer.append( getGroupId() );
        }
        if ( getArtifactId().length() > 0 )
        {
            buffer.append( ':' ).append( getArtifactId() );
        }
        if ( getVersion().length() > 0 )
        {
            buffer.append( ':' ).append( getVersion() );
        }
        buffer.append( '/' ).append( getType() );
        return buffer.toString();
    }

    /**
     * Compares this metadata with the specified object.
     * 
     * @param obj The object to compare this metadata against, may be {@code null}.
     * @return {@code true} if and only if the specified object is another {@link Metadata} with equal coordinates,
     *         type, nature, properties and file, {@code false} otherwise.
     */
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }
        else if ( !( obj instanceof Metadata ) )
        {
            return false;
        }

        Metadata that = (Metadata) obj;

        return getArtifactId().equals( that.getArtifactId() ) && getGroupId().equals( that.getGroupId() )
            && getVersion().equals( that.getVersion() ) && getType().equals( that.getType() )
            && getNature().equals( that.getNature() ) && eq( getFile(), that.getFile() )
            && eq( getProperties(), that.getProperties() );
    }

    private static <T> boolean eq( T s1, T s2 )
    {
        return s1 != null ? s1.equals( s2 ) : s2 == null;
    }

    /**
     * Returns a hash code for this metadata.
     * 
     * @return A hash code for the metadata.
     */
    @Override
    public int hashCode()
    {
        int hash = 17;
        hash = hash * 31 + getGroupId().hashCode();
        hash = hash * 31 + getArtifactId().hashCode();
        hash = hash * 31 + getType().hashCode();
        hash = hash * 31 + getNature().hashCode();
        hash = hash * 31 + getVersion().hashCode();
        hash = hash * 31 + hash( getFile() );
        return hash;
    }

    private static int hash( Object obj )
    {
        return ( obj != null ) ? obj.hashCode() : 0;
    }

}
