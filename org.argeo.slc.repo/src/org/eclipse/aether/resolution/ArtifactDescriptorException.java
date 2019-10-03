/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.resolution;

import org.eclipse.aether.RepositoryException;

/**
 * Thrown in case of an unreadable or unresolvable artifact descriptor.
 */
public class ArtifactDescriptorException
    extends RepositoryException
{

    private final transient ArtifactDescriptorResult result;

    /**
     * Creates a new exception with the specified result.
     * 
     * @param result The descriptor result at the point the exception occurred, may be {@code null}.
     */
    public ArtifactDescriptorException( ArtifactDescriptorResult result )
    {
        super( "Failed to read artifact descriptor"
            + ( result != null ? " for " + result.getRequest().getArtifact() : "" ), getCause( result ) );
        this.result = result;
    }

    /**
     * Creates a new exception with the specified result and detail message.
     * 
     * @param result The descriptor result at the point the exception occurred, may be {@code null}.
     * @param message The detail message, may be {@code null}.
     */
    public ArtifactDescriptorException( ArtifactDescriptorResult result, String message )
    {
        super( message, getCause( result ) );
        this.result = result;
    }

    /**
     * Creates a new exception with the specified result, detail message and cause.
     * 
     * @param result The descriptor result at the point the exception occurred, may be {@code null}.
     * @param message The detail message, may be {@code null}.
     * @param cause The exception that caused this one, may be {@code null}.
     */
    public ArtifactDescriptorException( ArtifactDescriptorResult result, String message, Throwable cause )
    {
        super( message, cause );
        this.result = result;
    }

    /**
     * Gets the descriptor result at the point the exception occurred. Despite being incomplete, callers might want to
     * use this result to fail gracefully and continue their operation with whatever interim data has been gathered.
     * 
     * @return The descriptor result or {@code null} if unknown.
     */
    public ArtifactDescriptorResult getResult()
    {
        return result;
    }

    private static Throwable getCause( ArtifactDescriptorResult result )
    {
        Throwable cause = null;
        if ( result != null && !result.getExceptions().isEmpty() )
        {
            cause = result.getExceptions().get( 0 );
        }
        return cause;
    }

}
