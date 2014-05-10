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
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.commons.io.FileUtils;

/**
 * Build a UAR from the current project.
 *
 * @author Dr. David H. Akehurst
 * @version 
 * @goal prepare-package
 * @phase prepare-package
 * @requiresProject
 * @threadSafe
 */
public class PreparePackageMojo extends AbstractMojo {

    /**
     * @parameter default-value="${basedir}/src/main/uml"
     * @required
     */
    private File modelDirectory;

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;    
    
    /**
     * @parameter default-value="model"
     * @required
     */
    private String targetModelDirectory;    
    File getTargetModelDirectory() {
    	return new File(this.classesDirectory.toString()+"/"+this.targetModelDirectory);
    }
    
    
    /**
     * The Maven project.
     *
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		try {
			if (this.modelDirectory.exists()) {
				FileUtils.copyDirectory(this.modelDirectory, this.getTargetModelDirectory());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
