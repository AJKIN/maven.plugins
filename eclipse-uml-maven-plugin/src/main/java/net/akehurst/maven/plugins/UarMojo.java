/*************************************************************************
* Copyright (c) 2014 - 2014 Dr David H. Akehurst.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* 
*************************************************************************/
package net.akehurst.maven.plugins;

import java.io.File;

/**
 * Build a UAR from the models in the current module.
 *
 * @goal uar
 * @phase package
 * @requiresProject
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class UarMojo
    extends AbstractArchiveMojo
{
    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * @parameter
     */
    private String classifier;

    protected String getClassifier()
    {
        return classifier;
    }

    /**
     * @return type of the generated artifact
     */
    protected String getType()
    {
        return Uar.type;
    }

    /**
     * Return the main classes directory, so it's used as the root of the jar.
     */
    protected File getClassesDirectory()
    {
        return classesDirectory;
    }
}
