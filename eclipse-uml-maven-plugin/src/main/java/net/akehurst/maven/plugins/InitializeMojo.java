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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * This class initializes and validates the setup.
 *
 * @author Dr. David H. Akehurst
 * @goal initialize
 * @phase initialize
 * @description resolves dependencies and adds models to the "platform".
 *
 * @require
 * @requiresDependencyResolution compile
 */
public class InitializeMojo extends AbstractMojo {

    /**
     * The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Resolving Dependencies");

		for (Artifact artifact : this.getDependencies(Artifact.SCOPE_COMPILE)) {
			if (Uar.type.equals(artifact.getType())) {
				getLog().debug("Adding models from: "+artifact.getArtifactId());
				
				
				//new org.eclipse.papyrus.uml.extensionpoints.library.RegisteredLibrary for m2e registration
				//Register uri platform.....artifact.getRepository().pathOf(artifact);
			}
		}

	}

    private List<Artifact> getDependencies( String scope )
    {
        Set<Artifact> artifacts = project.getArtifacts();
        List<Artifact> returnArtifact = new ArrayList<Artifact>();
        for(Artifact a : artifacts) {
            if(scope.equals(a.getScope()))
                returnArtifact.add(a);
        }
        return returnArtifact;
	}	
	
}
