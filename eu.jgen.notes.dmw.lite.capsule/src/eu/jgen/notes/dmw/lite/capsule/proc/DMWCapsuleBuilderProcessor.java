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
package eu.jgen.notes.dmw.lite.capsule.proc;

import java.io.File;
import java.util.Set;

import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;

import com.google.inject.Injector;

import eu.jgen.notes.annot.desc.processor.AbstractProcessor;
import eu.jgen.notes.annot.desc.processor.AnnotationObject;
import eu.jgen.notes.annot.desc.processor.AnnotationWorker;
import eu.jgen.notes.annot.desc.processor.DiagnosticKind;
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment;
import eu.jgen.notes.annot.desc.processor.ScanEnvironment;
import eu.jgen.notes.annot.desc.processor.SupportedAnnotationTypes;
import eu.jgen.notes.dmw.lite.LangStandaloneSetup;

@SupportedAnnotationTypes(value = { "eu.jgen.notes.dmw.capsule.proc.Extract" })
public class DMWCapsuleBuilderProcessor extends AbstractProcessor {

	private String rootDirPath;
	private String srcDirPath;
	private String metaDirPath;

	private static final String DIRECTORY_META = "\\META-INF";
	private static final String DIRECTORY_SRC = "\\src";
	public static final String OPT_NOTES_DIRECTORY = "directory";
	public static final String OPT_CAGEN_PATH = "cagenpath";    
	public static final String OPT_LOCAL_NAME = "localname";
	public static final String OPT_PACKAGE_NAME = "packagename";
	static Injector injector;

	public DMWCapsuleBuilderProcessor() {
		super();
	}

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		injector = new LangStandaloneSetup().createInjectorAndDoEMFRegistration();
		super.init(processingEnv);
	}

	@Override
	public boolean process(Set<XAnnotation> annotations, ScanEnvironment scanEnv) {
		LangGenDataModelConverter dataModelConverter = injector.getInstance(LangGenDataModelConverter.class);
		LangGenActionBlockConverter actionBlockConverter = injector.getInstance(LangGenActionBlockConverter.class);
		checkInfrastructure();
		Set<AnnotationObject> selection = scanEnv
				.getElementsAnnotatedWith(eu.jgen.notes.dmw.lite.capsule.proc.Extract.class);  
		processingEnv.getMessager().printMessage(DiagnosticKind.WARNING,
				"Only action block sources will be regenerated.");
		dataModelConverter.generate(selection, processingEnv, srcDirPath, processingEnv.getOptions().get(DMWCapsuleBuilderProcessor.OPT_PACKAGE_NAME));
		actionBlockConverter.generate(selection, processingEnv, srcDirPath,processingEnv.getOptions().get(DMWCapsuleBuilderProcessor.OPT_PACKAGE_NAME));
		DMWEclipseInfrastuctureGenerator.generateMetaInfo(processingEnv, metaDirPath);
		String name = processingEnv.getOptions().get(DMWCapsuleBuilderProcessor.OPT_LOCAL_NAME);
		DMWEclipseInfrastuctureGenerator.generateProjectFile(processingEnv, rootDirPath, name);
		DMWEclipseInfrastuctureGenerator.generateClasspathFile(processingEnv, rootDirPath,
				processingEnv.getOptions().getOrDefault(OPT_CAGEN_PATH, "C:/Program Files (x86)/CA/Gen86Free"));
		return true;
	}

	private void checkInfrastructure() {
		emptyDirectories();
		processingEnv.getMessager().printMessage(DiagnosticKind.WARNING,
				"All subdirectories were forced to be emptied.");

		rootDirPath = processingEnv.getOptions().getOrDefault(OPT_NOTES_DIRECTORY, "C:\\TEMP");
		File rootDir = new File(rootDirPath);
		if (!rootDir.exists()) {
			rootDir.mkdirs();
			processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Adding subdirectory. ");
		}
		srcDirPath = rootDirPath + DIRECTORY_SRC;
		File srcDir = new File(srcDirPath);
		if (!srcDir.exists()) {
			srcDir.mkdirs();
			processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Adding source subdirectory. ");
		}
		metaDirPath = rootDirPath + DIRECTORY_META;
		File metaDir = new File(metaDirPath);
		if (!metaDir.exists()) {
			metaDir.mkdirs();
			processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Adding meta-inf subdirectory. ");
		}
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO,
				"Generation " + OPT_NOTES_DIRECTORY + " used: " + rootDirPath);
	}

	private void emptyDirectories() {
		rootDirPath = processingEnv.getOptions().getOrDefault(OPT_NOTES_DIRECTORY, "C:\\TEMP");
		File rootDir = new File(rootDirPath);
		deleteDir(rootDir);
	}

	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				deleteDir(f);
			}
		}
		file.delete();
	}

}
