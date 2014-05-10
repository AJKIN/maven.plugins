/*************************************************************************
* Copyright (c) 2014 - 2014 Dr David H. Akehurst.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* and is available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* 
*************************************************************************/

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copied and modified by Dr David H. Akehurst
 * Copied because we can't inherit plugins.
 * Modified to enable creation of UAR artifacts
 * 
 */

package net.akehurst.maven.plugins;

import java.io.File;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Base class for creating a archive from project classes.
 *
 * @originalAuthor <a href="evenisse@apache.org">Emmanuel Venisse</a>
 * @author <a>Dr David H. Akehurst</a>
 * @version $Id: AbstractArchiveMojo.java $
 */
public abstract class AbstractArchiveMojo
    extends AbstractMojo
{

    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * List of files to include. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the archive.
     *
     * @parameter
     */
    private String[] includes;

    /**
     * List of files to exclude. Specified as fileset patterns which are relative to the input directory whose contents
     * is being packaged into the archive.
     *
     * @parameter
     */
    private String[] excludes;

    /**
     * Directory containing the generated archive.
     *
     * @parameter default-value="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Name of the generated archive.
     *
     * @parameter alias="aarName" expression="${ar.finalName}" default-value="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The Jar archiver.
     *
     * @component role="org.codehaus.plexus.archiver.Archiver" roleHint="jar"
     */
    private JarArchiver jarArchiver;

    /**
     * The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${session}"
     * @readonly
     * @required
     */
    private MavenSession session;

    /**
     * The archive configuration to use.
     * See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven Archiver Reference</a>.
     *
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Path to the default MANIFEST file to use. It will be used if
     * <code>useDefaultManifestFile</code> is set to <code>true</code>.
     *
     * @parameter default-value="${project.build.outputDirectory}/META-INF/MANIFEST.MF"
     * @required
     * @readonly
     * @since 2.2
     */
    private File defaultManifestFile;

    /**
     * Set this to <code>true</code> to enable the use of the <code>defaultManifestFile</code>.
     *
     * @parameter expression="${ar.useDefaultManifestFile}" default-value="false"
     *
     * @since 2.2
     */
    private boolean useDefaultManifestFile;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether creating the archive should be forced.
     *
     * @parameter expression="${ar.forceCreation}" default-value="false"
     */
    private boolean forceCreation;
	
    /**
     * Skip creating empty archives
     * 
     * @parameter expression="${ar.skipIfEmpty}" default-value="false"
     */
    private boolean skipIfEmpty;

    /**
     * Return the specific output directory to serve as the root for the archive.
     */
    protected abstract File getClassesDirectory();

    protected final MavenProject getProject()
    {
        return project;
    }

    /**
     * Overload this to produce an archive with another classifier, for example a test-ar.
     */
    protected abstract String getClassifier();

    /**
     * Overload this to produce a test-ar, for example.
     */
    protected abstract String getType();

    //modified to non-static so that we can use the overridden 'getType' method instead of hard-coded "jar" extension type
    protected File getArchiveFile( File basedir, String finalName, String classifier )
    {
        if ( classifier == null )
        {
            classifier = "";
        }
        else if ( classifier.trim().length() > 0 && !classifier.startsWith( "-" ) )
        {
            classifier = "-" + classifier;
        }

        return new File( basedir, finalName + classifier + "." + this.getType() );
    }

    /**
     * Default Manifest location. Can point to a non existing file.
     * Cannot return null.
     */
    protected File getDefaultManifestFile()
    {
        return defaultManifestFile;
    }


    /**
     * Generates the archive.
     *
     * @todo Add license files in META-INF directory.
     */
    public File createArchive()
        throws MojoExecutionException
    {
        File arFile = this.getArchiveFile( outputDirectory, finalName, getClassifier() );

        MavenArchiver archiver = new MavenArchiver();

        archiver.setArchiver( jarArchiver );

        archiver.setOutputFile( arFile );

        archive.setForced( forceCreation );

        try
        {
            File contentDirectory = getClassesDirectory();
            if ( !contentDirectory.exists() )
            {
            	//Modified to use overridden getType method
                getLog().warn( this.getType() + " will be empty - no content was marked for inclusion!" );
            }
            else
            {
                archiver.getArchiver().addDirectory( contentDirectory, getIncludes(), getExcludes() );
            }

            File existingManifest = getDefaultManifestFile();

            if ( useDefaultManifestFile && existingManifest.exists() && archive.getManifestFile() == null )
            {
                getLog().info( "Adding existing MANIFEST to archive. Found under: " + existingManifest.getPath() );
                archive.setManifestFile( existingManifest );
            }

            archiver.createArchive( session, project, archive );

            return arFile;
        }
        catch ( Exception e )
        {
            // TODO: improve error handling
        	//Modified to use overridden getType method
            throw new MojoExecutionException( "Error assembling "+this.getType(), e );
        }
    }

    /**
     * Generates the archive.
     *
     * @todo Add license files in META-INF directory.
     */
    public void execute()
        throws MojoExecutionException
    {
        if ( skipIfEmpty && !getClassesDirectory().exists() )
        {
            getLog().info( "Skipping packaging of the "+this.getType() );
        }
        else
        {
            File arFile = createArchive();

            String classifier = getClassifier();
            if ( classifier != null )
            {
                projectHelper.attachArtifact( getProject(), getType(), classifier, arFile );
            }
            else
            {
                getProject().getArtifact().setFile( arFile );
            }
        }
    }

    private String[] getIncludes()
    {
        if ( includes != null && includes.length > 0 )
        {
            return includes;
        }
        return DEFAULT_INCLUDES;
    }

    private String[] getExcludes()
    {
        if ( excludes != null && excludes.length > 0 )
        {
            return excludes;
        }
        return DEFAULT_EXCLUDES;
    }
}
