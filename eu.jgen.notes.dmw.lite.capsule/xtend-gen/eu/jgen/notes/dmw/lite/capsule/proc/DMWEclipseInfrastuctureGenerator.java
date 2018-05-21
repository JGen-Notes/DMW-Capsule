package eu.jgen.notes.dmw.lite.capsule.proc;

import eu.jgen.notes.annot.desc.processor.DiagnosticKind;
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment;
import eu.jgen.notes.dmw.lite.capsule.proc.DMWCapsuleBuilderProcessor;
import java.io.Writer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class DMWEclipseInfrastuctureGenerator {
  public static void generateMetaInfo(final ProcessingEnvironment processingEnv, final String rootDirPath) {
    try {
      final String name = processingEnv.getOptions().get(DMWCapsuleBuilderProcessor.OPT_LOCAL_NAME);
      final Writer writer = processingEnv.getFiler().openWriter(((rootDirPath + "\\") + "MANIFEST.MF"));
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("Manifest-Version: 1.0");
      _builder.newLine();
      _builder.append("Bundle-ManifestVersion: 2");
      _builder.newLine();
      _builder.append("Bundle-Name: �name�");
      _builder.newLine();
      _builder.append("Bundle-SymbolicName: your.name.capsule");
      _builder.newLine();
      _builder.append("Bundle-Version: 1.0.0.qualifier");
      _builder.newLine();
      _builder.append("Bundle-Vendor: JGen Notes");
      _builder.newLine();
      _builder.append("Require-Bundle: eu.jgen.notes.dmw.lite;bundle-version=\"1.0.0\",");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("org.junit;bundle-version=\"4.12.0\"");
      _builder.newLine();
      _builder.append("Bundle-RequiredExecutionEnvironment: JavaSE-1.8");
      _builder.newLine();
      _builder.newLine();
      _builder.newLine();
      String text = _builder.toString();
      writer.write(text);
      writer.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "MANIFEST.MF file created. ");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static void generateClasspathFile(final ProcessingEnvironment processingEnv, final String rootDirPath, final String genlocation) {
    try {
      final Writer writer = processingEnv.getFiler().openWriter(((rootDirPath + "\\") + ".classpath"));
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      _builder.newLine();
      _builder.append("<classpath>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<classpathentry kind=\"src\" path=\"src\"/>");
      _builder.newLine();
      _builder.append("</classpath>");
      _builder.newLine();
      String text = _builder.toString();
      writer.write(text);
      writer.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Class path file created. ");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static void generateProjectFile(final ProcessingEnvironment processingEnv, final String rootDirPath, final String projectName) {
    try {
      final Writer writer = processingEnv.getFiler().openWriter(((rootDirPath + "\\") + ".project"));
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      _builder.newLine();
      _builder.append("<projectDescription>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<name>�projectName�</name>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<comment></comment>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<projects>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("</projects>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<buildSpec>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<name>org.eclipse.xtext.ui.shared.xtextBuilder</name>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<arguments>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("</arguments>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("</buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<name>org.eclipse.jdt.core.javabuilder</name>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<arguments>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("</arguments>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("</buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<name>org.eclipse.pde.ManifestBuilder</name>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<arguments>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("</arguments>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("</buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<buildCommand>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<name>org.eclipse.pde.SchemaBuilder</name>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("<arguments>");
      _builder.newLine();
      _builder.append("\t\t\t\t");
      _builder.append("</arguments>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("</buildCommand>");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.append("</buildSpec>");
      _builder.newLine();
      _builder.append("\t");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("<natures>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<nature>org.eclipse.jdt.core.javanature</nature>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<nature>org.eclipse.pde.PluginNature</nature>");
      _builder.newLine();
      _builder.append("\t\t\t");
      _builder.append("<nature>org.eclipse.xtext.ui.shared.xtextNature</nature>");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("</natures>");
      _builder.newLine();
      _builder.newLine();
      _builder.append("</projectDescription>");
      _builder.newLine();
      String text = _builder.toString();
      writer.write(text);
      writer.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Project file with Java nature created. ");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
