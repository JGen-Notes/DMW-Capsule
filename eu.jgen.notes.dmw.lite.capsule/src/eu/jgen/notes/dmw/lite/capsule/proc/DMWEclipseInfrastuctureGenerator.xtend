package eu.jgen.notes.dmw.lite.capsule.proc

import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment
import eu.jgen.notes.annot.desc.processor.DiagnosticKind

class DMWEclipseInfrastuctureGenerator {
	
	public static def void generateMetaInfo(ProcessingEnvironment processingEnv, String rootDirPath) {
		val name = processingEnv.getOptions().get(DMWCapsuleBuilderProcessor.OPT_LOCAL_NAME)
		val writer = processingEnv.getFiler().openWriter(rootDirPath + "\\" + "MANIFEST.MF");
		var text = '''
			Manifest-Version: 1.0
			Bundle-ManifestVersion: 2
			Bundle-Name: «name»
			Bundle-SymbolicName: your.name.capsule
			Bundle-Version: 1.0.0.qualifier
			Bundle-Vendor: JGen Notes
			Require-Bundle: eu.jgen.notes.dmw.lite;bundle-version="1.0.0",
			 org.junit;bundle-version="4.12.0"
			Bundle-RequiredExecutionEnvironment: JavaSE-1.8
			
			
		'''
		writer.write(text);
		writer.close();
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "MANIFEST.MF file created. ");
	}
	
	public static def void generateClasspathFile(ProcessingEnvironment processingEnv, String rootDirPath,
		String genlocation) {
		val writer = processingEnv.getFiler().openWriter(rootDirPath + "\\" + ".classpath");
		var text = '''
			<?xml version="1.0" encoding="UTF-8"?>
			<classpath>
				<classpathentry kind="con" path="org.eclipse.jdt.launching.JRE_CONTAINER"/>
				<classpathentry kind="con" path="org.eclipse.pde.core.requiredPlugins"/>
				<classpathentry kind="src" path="src"/>
			</classpath>
		'''
		writer.write(text);
		writer.close();
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Class path file created. ");
	}

	public static def void generateProjectFile(ProcessingEnvironment processingEnv, String rootDirPath,
		String projectName) {
		val writer = processingEnv.getFiler().openWriter(rootDirPath + "\\" + ".project");
		var text = '''
			<?xml version="1.0" encoding="UTF-8"?>
			<projectDescription>
				<name>«projectName»</name>
				<comment></comment>
				<projects>
				</projects>
				<buildSpec>
						<buildCommand>
							<name>org.eclipse.xtext.ui.shared.xtextBuilder</name>
							<arguments>
							</arguments>
						</buildCommand>
						<buildCommand>
							<name>org.eclipse.jdt.core.javabuilder</name>
							<arguments>
							</arguments>
						</buildCommand>
						<buildCommand>
							<name>org.eclipse.pde.ManifestBuilder</name>
							<arguments>
							</arguments>
						</buildCommand>
						<buildCommand>
							<name>org.eclipse.pde.SchemaBuilder</name>
							<arguments>
							</arguments>
						</buildCommand>
					</buildSpec>
				
				<natures>
						<nature>org.eclipse.jdt.core.javanature</nature>
						<nature>org.eclipse.pde.PluginNature</nature>
						<nature>org.eclipse.xtext.ui.shared.xtextNature</nature>
				</natures>
			
			</projectDescription>
		'''
		writer.write(text);
		writer.close();
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Project file with Java nature created. ");
	}
	
} 