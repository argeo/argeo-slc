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
package org.eclipse.aether.collection;

import org.eclipse.aether.graph.Dependency;

/**
 * Applies dependency management to the dependencies of a dependency node.
 * <p>
 * <strong>Note:</strong> Implementations must be stateless.
 * <p>
 * <em>Warning:</em> This hook is called from a hot spot and therefore implementations should pay attention to
 * performance. Among others, implementations should provide a semantic {@link Object#equals(Object) equals()} method.
 * 
 * @see org.eclipse.aether.RepositorySystemSession#getDependencyManager()
 * @see org.eclipse.aether.RepositorySystem#collectDependencies(org.eclipse.aether.RepositorySystemSession,
 *      CollectRequest)
 */
public interface DependencyManager
{

    /**
     * Applies dependency management to the specified dependency.
     * 
     * @param dependency The dependency to manage, must not be {@code null}.
     * @return The management update to apply to the dependency or {@code null} if the dependency is not managed at all.
     */
    DependencyManagement manageDependency( Dependency dependency );

    /**
     * Derives a dependency manager for the specified collection context. When calculating the child manager,
     * implementors are strongly advised to simply return the current instance if nothing changed to help save memory.
     * 
     * @param context The dependency collection context, must not be {@code null}.
     * @return The dependency manager for the dependencies of the target node or {@code null} if dependency management
     *         should no longer be applied.
     */
    DependencyManager deriveChildManager( DependencyCollectionContext context );

}
