/*******************************************************************************
 * Copyright (c) 2010, 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.repository;

import java.io.File;

import org.eclipse.aether.RepositorySystemSession;

/**
 * A result from the local repository about the existence of an artifact.
 * 
 * @see LocalRepositoryManager#find(RepositorySystemSession, LocalArtifactRequest)
 */
public final class LocalArtifactResult
{

    private final LocalArtifactRequest request;

    private File file;

    private boolean available;

    private RemoteRepository repository;

    /**
     * Creates a new result for the specified request.
     * 
     * @param request The local artifact request, must not be {@code null}.
     */
    public LocalArtifactResult( LocalArtifactRequest request )
    {
        if ( request == null )
        {
            throw new IllegalArgumentException( "local artifact request has not been specified" );
        }
        this.request = request;
    }

    /**
     * Gets the request corresponding to this result.
     * 
     * @return The corresponding request, never {@code null}.
     */
    public LocalArtifactRequest getRequest()
    {
        return request;
    }

    /**
     * Gets the file to the requested artifact. Note that this file must not be used unless {@link #isAvailable()}
     * returns {@code true}. An artifact file can be found but considered unavailable if the artifact was cached from a
     * remote repository that is not part of the list of remote repositories used for the query.
     * 
     * @return The file to the requested artifact or {@code null} if the artifact does not exist locally.
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Sets the file to requested artifact.
     * 
     * @param file The artifact file, may be {@code null}.
     * @return This result for chaining, never {@code null}.
     */
    public LocalArtifactResult setFile( File file )
    {
        this.file = file;
        return this;
    }

    /**
     * Indicates whether the requested artifact is available for use. As a minimum, the file needs to be physically
     * existent in the local repository to be available. Additionally, a local repository manager can consider the list
     * of supplied remote repositories to determine whether the artifact is logically available and mark an artifact
     * unavailable (despite its physical existence) if it is not known to be hosted by any of the provided repositories.
     * 
     * @return {@code true} if the artifact is available, {@code false} otherwise.
     * @see LocalArtifactRequest#getRepositories()
     */
    public boolean isAvailable()
    {
        return available;
    }

    /**
     * Sets whether the artifact is available.
     * 
     * @param available {@code true} if the artifact is available, {@code false} otherwise.
     * @return This result for chaining, never {@code null}.
     */
    public LocalArtifactResult setAvailable( boolean available )
    {
        this.available = available;
        return this;
    }

    /**
     * Gets the (first) remote repository from which the artifact was cached (if any).
     * 
     * @return The remote repository from which the artifact was originally retrieved or {@code null} if unknown or if
     *         the artifact has been locally installed.
     * @see LocalArtifactRequest#getRepositories()
     */
    public RemoteRepository getRepository()
    {
        return repository;
    }

    /**
     * Sets the (first) remote repository from which the artifact was cached.
     * 
     * @param repository The remote repository from which the artifact was originally retrieved, may be {@code null} if
     *            unknown or if the artifact has been locally installed.
     * @return This result for chaining, never {@code null}.
     */
    public LocalArtifactResult setRepository( RemoteRepository repository )
    {
        this.repository = repository;
        return this;
    }

    @Override
    public String toString()
    {
        return getFile() + " (" + ( isAvailable() ? "available" : "unavailable" ) + ")";
    }

}
