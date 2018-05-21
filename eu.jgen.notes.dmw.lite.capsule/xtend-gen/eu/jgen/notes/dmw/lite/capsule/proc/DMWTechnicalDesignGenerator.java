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
 */
package eu.jgen.notes.dmw.lite.capsule.proc;

import com.ca.gen.jmmi.objs.Fielddat;
import com.ca.gen.jmmi.objs.Reciefj;
import com.google.common.base.Objects;
import eu.jgen.notes.annot.desc.processor.AnnotationObject;
import eu.jgen.notes.annot.desc.processor.DiagnosticKind;
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment;
import java.io.File;
import java.io.Writer;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XStringLiteral;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation;
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotationElementValuePair;
import org.eclipse.xtext.xbase.lib.Exceptions;

@SuppressWarnings("all")
public class DMWTechnicalDesignGenerator {
  public static void generate(final Set<AnnotationObject> selection, final ProcessingEnvironment processingEnv, final String srcDirPath) {
    try {
      final String packageName = "td";
      final String path = DMWTechnicalDesignGenerator.createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"));
      final Writer writer = processingEnv.getFiler().openWriter((((path + "\\") + "technical_design") + ".dmw"));
      writer.write(DMWTechnicalDesignGenerator.generateTechnicalDesign(selection, packageName));
      writer.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, 
        "Creating technical design. ");
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public static String generateTechnicalDesign(final Set<AnnotationObject> selection, final String name) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("PACKAGE �name�");
    _builder.newLine();
    _builder.append("TECHNICAL DESIGN {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�FOR reciefj : selection.filter[genObject instanceof Reciefj]��generateTable(reciefj.genObject as Reciefj)��ENDFOR�");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String generateTable(final Reciefj reciefj) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("TABLE �reciefj.name.toLowerCase� IMPLEMENTS �reciefj.followExtfrom.followDefines.head.followImplmnts.name.toLowerCase� {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�FOR fielddat : reciefj.followExtfrom.followContains��generateColumn(fielddat,reciefj.followExtfrom.followDefines.head.followImplmnts.name.toLowerCase)��ENDFOR�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�generatePrimaryKey(reciefj)�");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String generateColumn(final Fielddat fielddat, final String entityName) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("COLUMN �fielddat.name.toLowerCase� IMPLEMENTS �entityName�.�fielddat.followImplmnts.name.toLowerCase� AS �generateColumnType(fielddat)� �IF fielddat.followImplmnts.opt.toString == \"M\"�NOT NULL�ENDIF�");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String generateColumnType(final Fielddat fielddat) {
    String _string = Character.valueOf(fielddat.getFormat()).toString();
    if (_string != null) {
      switch (_string) {
        case "X":
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("CHAR  LENGTH �fielddat.length�");
          return _builder.toString();
        case "D":
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("DATE");
          return _builder_1.toString();
        case "P":
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("DECIMAL");
          return _builder_2.toString();
        case "I":
          StringConcatenation _builder_3 = new StringConcatenation();
          _builder_3.append("INTEGER");
          return _builder_3.toString();
        case "S":
          StringConcatenation _builder_4 = new StringConcatenation();
          _builder_4.append("SMALL");
          return _builder_4.toString();
        case "T":
          StringConcatenation _builder_5 = new StringConcatenation();
          _builder_5.append("TIME");
          return _builder_5.toString();
        case "Q":
          StringConcatenation _builder_6 = new StringConcatenation();
          _builder_6.append("TIMESTAMP");
          return _builder_6.toString();
        case "V":
          StringConcatenation _builder_7 = new StringConcatenation();
          _builder_7.append("VARCHAR LENGTH �fielddat.length�");
          return _builder_7.toString();
        case "U":
          StringConcatenation _builder_8 = new StringConcatenation();
          _builder_8.append("VARCHAR");
          return _builder_8.toString();
        case "G":
          StringConcatenation _builder_9 = new StringConcatenation();
          _builder_9.append("CHAR");
          return _builder_9.toString();
        case "B":
          StringConcatenation _builder_10 = new StringConcatenation();
          _builder_10.append("BLOB");
          return _builder_10.toString();
        default:
          StringConcatenation _builder_11 = new StringConcatenation();
          _builder_11.append("?");
          char _format = fielddat.getFormat();
          return (_builder_11.toString() + Character.valueOf(_format));
      }
    } else {
      StringConcatenation _builder_11 = new StringConcatenation();
      _builder_11.append("?");
      char _format = fielddat.getFormat();
      return (_builder_11.toString() + Character.valueOf(_format));
    }
  }
  
  public static String generatePrimaryKey(final Reciefj reciefj) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("PRIMARY KEY (�FOR fldepud : reciefj.followExtfrom.followRefrdby.head.followUsedby.followDefndby SEPARATOR \",\"��fldepud.followUsageof.name.toLowerCase��ENDFOR�)");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String findPackageName(final XAnnotation annotation) {
    EList<XAnnotationElementValuePair> _elementValuePairs = annotation.getElementValuePairs();
    for (final XAnnotationElementValuePair pair : _elementValuePairs) {
      String _simpleName = pair.getElement().getSimpleName();
      boolean _equals = Objects.equal(_simpleName, "packageBase");
      if (_equals) {
        XExpression _value = pair.getValue();
        return ((XStringLiteral) _value).getValue();
      }
    }
    return "eu.jgen.notes.dmw.sample";
  }
  
  public static String createPackageFileStructure(final ProcessingEnvironment processingEnv, final String srcDirPath, final String packagepath) {
    final String path = ((srcDirPath + "\\") + packagepath);
    final File packDir = new File(path);
    boolean _exists = packDir.exists();
    boolean _not = (!_exists);
    if (_not) {
      packDir.mkdirs();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, 
        ("Creating  package sub-directories for definitions: " + path));
    }
    return path;
  }
  
  private static String createPackageStatement(final String packageName) {
    return (("PACKAGE " + packageName) + "\n");
  }
}
