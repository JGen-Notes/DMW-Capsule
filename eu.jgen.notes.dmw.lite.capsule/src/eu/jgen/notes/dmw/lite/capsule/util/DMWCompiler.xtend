/**
 * [The "BSD license"]
 * Copyright (c) 2016, JGen Notes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions 
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package eu.jgen.notes.dmw.lite.capsule.util

import com.google.inject.Injector
import java.io.File
import java.util.List
import java.util.Vector
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.resource.IResourceServiceProvider
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtext.validation.CheckMode
import eu.jgen.notes.dmw.lite.LangStandaloneSetup

class DMWCompiler {

	static Injector injector
	
	private List<String> dmwFilesList;
 
	ResourceDescriptionsProvider resourceDescriptionsProvider;

	def static void main(String[] args) {
		System.out.println("DMW Compiler, Version 0.1");
		injector = new LangStandaloneSetup().createInjectorAndDoEMFRegistration();
		var compiler = new DMWCompiler()
		if(args.size == 2) {
					compiler.run(args.get(0), args.get(1))
					return
		}
		println("Usage: java -jar dmw.jar <root path> <file name>" )
	}

	def run(String rootPath, String fragmentFileName) {
		if(dmwFilesList === null) {
			dmwFilesList = new Vector<String>()
		}
		findListOfDmwFiles(dmwFilesList, rootPath)
		resourceDescriptionsProvider = injector.getInstance(ResourceDescriptionsProvider);
		val resourceSet = injector.getInstance(XtextResourceSet);
		for (item : dmwFilesList) {
			resourceSet.getResource(URI.createFileURI(item), true);
		}
		val resource = findMatchingResource(resourceSet, fragmentFileName)
		if(resource === null) {
			println("Specified file with the model fragment not found.")
			return
		}
		val serviceProvider = injector.getInstance(IResourceServiceProvider);
		val resourceValidator = serviceProvider.getResourceValidator();
		var issues = resourceValidator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
		if(issues.size == 0) {
			println("Compilation completed without any issues.")
			return
		}
		issues.forEach [ issue |
			println(
				issue.uriToProblem.lastSegment + ", " + issue.severity + ", \"" + issue.message + "\", line=" +
					issue.lineNumber + ", column=" + issue.column)
		]
			println("Compilation completed but some issues (" + issues.size +") have been found.")
	}

	def private Resource findMatchingResource(ResourceSet resourceSet,  String fragmentFileName) {
		val list = resourceSet.resources
		for (Resource resource : list) {
			if(resource.getURI.lastSegment == fragmentFileName)
			   return resource
		}
		return null		
	}
	
	def private void findListOfDmwFiles(List<String> list, String path) {
		var dir = new File(path)
		for (item : dir.listFiles) {
			if (item.isFile) {
				if (item.name.endsWith(".dmw")) {
					list.add(item.path)
				}
			} else {
				findListOfDmwFiles(list, item.path)
			}
		}
	}

}
