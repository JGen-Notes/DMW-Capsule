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

import com.ca.gen.jmmi.Ency
import com.ca.gen.jmmi.EncyManager
import com.ca.gen.jmmi.Model
import com.ca.gen.jmmi.ModelManager
import com.google.common.io.Files
import com.google.inject.Inject
import com.google.inject.Injector
import eu.jgen.notes.dmw.lite.LangStandaloneSetup
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil
import eu.jgen.notes.dmw.lite.capsule.proc.UploadDataModelFragment
import eu.jgen.notes.dmw.lite.lang.YAnnotEntity
import java.io.File
import java.util.List
import java.util.Vector
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.resource.IResourceServiceProvider
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import org.eclipse.xtext.util.CancelIndicator
import org.eclipse.xtext.validation.CheckMode
import eu.jgen.notes.dmw.lite.lang.YAnnotRel

class DMWCapsuleUpload {

	static String MAIN_LIB = "eu/jgen/notes/lib/dmw/lang.dmw";
	static String LOCATION_EMPTY_GEN_MODEL = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\empty.ief\\"

	static String IEF0000 = "ief0000.dat"
	static String IEF0700 = "ief0700.dat"
	static String IEF1200 = "ief1200.dat"
	static String IEF2511 = "ief2511.dat"

	static Injector injector

	var UploadDataModelFragment uploadDataModelFragment;

	private Model model;
	private Ency ency;

	private List<String> dmwFilesList;

	ResourceDescriptionsProvider resourceDescriptionsProvider;

	def static void main(String[] args) {
		System.out.println("DMW Capsule Upload Utility, Version 0.2");
		injector = new LangStandaloneSetup().createInjectorAndDoEMFRegistration();
		var capsuleUpload = new DMWCapsuleUpload()
		if (args.size == 2) {
			capsuleUpload.run(args.get(0), args.get(1))
			return
		}
		println("Usage:  java  -jar capup.jar  modelpath capsulepath ")
	}

	def run(String modelPath, String capsulePath) {
		connectToModel(modelPath)
		if (dmwFilesList === null) {
			dmwFilesList = new Vector<String>()
		}
		findListOfDmwFiles(dmwFilesList, capsulePath + "/src")
		resourceDescriptionsProvider = injector.getInstance(ResourceDescriptionsProvider);
		val resourceSet = injector.getInstance(XtextResourceSet);
		for (item : dmwFilesList) {
			resourceSet.getResource(URI.createFileURI(item), true);
		}
		val url = getClass().getClassLoader().getResource(MAIN_LIB)
		// val stream = url.openStream
		val urlPath = url.path
      	val resource = resourceSet.createResource(URI.createFileURI(urlPath))
//		println(resourceSet.resources.size)

		val validationOKStatus = validateCapluseContents(resourceSet)
		if (validationOKStatus) {
			uploadDataModelFragment = injector.getInstance(UploadDataModelFragment);
			uploadDataModelFragment.connect(model)
			uploadEntityTypeDefinitions(resourceSet)
		    uploadRelationships(resourceSet)
		} else {
			println("Cannot continue until issues resolved.")
		}
		disconnectFromModel();
		println("Upload completed.")
	}

	def private void uploadRelationships(XtextResourceSet resourceSet) {
		resourceSet.resources.forEach [ element |
			element.allContents.forEach [ item |
				if (item instanceof YAnnotRel) {
					val annotRel = item as YAnnotRel
					uploadDataModelFragment.makeRelationships( annotRel)
				}
			]
		]
	} 
	
	def private void uploadEntityTypeDefinitions(XtextResourceSet resourceSet) {
		resourceSet.resources.forEach [ element |
			element.allContents.forEach [ item |
				if (item instanceof YAnnotEntity) {
					val annotEntity = item as YAnnotEntity
					uploadDataModelFragment.makeEntity(annotEntity)
				}
			]
		]
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

	def private void connectToModel(String modelPath) {
		createEmptyModel(modelPath);
		ency = EncyManager.connectLocal(modelPath);
		model = ModelManager.open(ency, ency.getModelIds().get(0));
		System.out.println("Connected to local model: " + model.getName());
	}

	def createEmptyModel(String path) {
		System.out.println("Creating empty model at  " + path);
		copyDatFile(new File(LOCATION_EMPTY_GEN_MODEL + IEF0000), new File(path + "\\" + IEF0000));
		copyDatFile(new File(LOCATION_EMPTY_GEN_MODEL + IEF0700), new File(path + "\\" + IEF0700));
		copyDatFile(new File(LOCATION_EMPTY_GEN_MODEL + IEF1200), new File(path + "\\" + IEF1200));
		copyDatFile(new File(LOCATION_EMPTY_GEN_MODEL + IEF2511), new File(path + "\\" + IEF2511));
		System.out.println("Creating empty model completed.");
	}

	def copyDatFile(File sourceFile, File destinationFile) {
		Files.copy(sourceFile, destinationFile);
	}

	def private void disconnectFromModel() {
		System.out.println("Disconnecting from local model: " + model.getName() + " ....");
		model.save()
		model.close()
		ency.disconnect()
	}

	def private boolean validateCapluseContents(XtextResourceSet resourceSet) {
		var validationOKStatus = true
		var counterFailed = 0;

		val serviceProvider = injector.getInstance(IResourceServiceProvider);
		val resourceValidator = serviceProvider.getResourceValidator();
		val resources = resourceSet.resources
		var counter = resources.size
		println("Validation of the capsule with " + counter + " fragments starting...")
		resources.forEach [ element |
			println("\t" + (element as Resource).getURI.lastSegment)
		]
		for (var i = 0; i < resources.size; i++) {
			val resource = resources.get(i)
			var issues = resourceValidator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
			if (issues.size != 0) {
				issues.forEach [ issue |
					println(
						issue.uriToProblem.lastSegment + ", " + issue.severity + ", \"" + issue.message + "\", line=" +
							issue.lineNumber + ", column=" + issue.column)
				]
				println("Compilation completed with some issue(s) (" + issues.size + ") have been found.")
				validationOKStatus = false
				counterFailed++
			}
		}
		if (validationOKStatus) {
			println("Validation of the capsule with " + counter + " fragment(s) completed.")
		} else {
			println("Validation of the capsule completed with " + counterFailed + " fragments failing.")
		}
		return validationOKStatus
	}

}
