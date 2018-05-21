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

import com.ca.gen.jmmi.MMObj;
import com.ca.gen.jmmi.objs.Attr;
import com.ca.gen.jmmi.objs.Attrusr;
import com.ca.gen.jmmi.objs.Entgrp;
import com.ca.gen.jmmi.objs.Hlent;
import com.ca.gen.jmmi.objs.Ident;
import com.ca.gen.jmmi.objs.Relmm;
import com.ca.gen.jmmi.objs.Subj;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import eu.jgen.notes.annot.desc.processor.AnnotationObject;
import eu.jgen.notes.annot.desc.processor.DiagnosticKind;
import eu.jgen.notes.annot.desc.processor.Filer;
import eu.jgen.notes.annot.desc.processor.Messager;
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment;
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil;
import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Consumer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;

/**
 * Data Model Artifacts Generator
 */
@SuppressWarnings("all")
public class LangGenDataModelConverter {
  @Inject
  @Extension
  private ConversionUtil _conversionUtil;
  
  public void generate(final Set<AnnotationObject> selection, final ProcessingEnvironment processingEnv, final String srcDirPath, final String packageName) {
    final Consumer<AnnotationObject> _function = (AnnotationObject annotationObject) -> {
      MMObj _genObject = annotationObject.getGenObject();
      if ((_genObject instanceof Subj)) {
        final String path = this.createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"));
        MMObj _genObject_1 = annotationObject.getGenObject();
        final Subj subj = ((Subj) _genObject_1);
        final Consumer<Entgrp> _function_1 = (Entgrp element) -> {
          try {
            if ((element instanceof Hlent)) {
              final Hlent hlent = ((Hlent) element);
              Filer _filer = processingEnv.getFiler();
              String _convertEntityName = this._conversionUtil.convertEntityName(hlent.getName());
              String _plus = ((path + "\\") + _convertEntityName);
              String _plus_1 = (_plus + ".dmw");
              final Writer writer = _filer.openWriter(_plus_1);
              final String text = this.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(CollectionLiterals.<Hlent>newArrayList(hlent), Hlent.class)), packageName);
              writer.write(text);
              writer.close();
              Messager _messager = processingEnv.getMessager();
              String _name = hlent.getName();
              String _plus_2 = ("Creating entity type definition for " + _name);
              _messager.printMessage(DiagnosticKind.INFO, _plus_2);
            }
          } catch (Throwable _e) {
            throw Exceptions.sneakyThrow(_e);
          }
        };
        subj.<Entgrp>followDetlby().forEach(_function_1);
      }
    };
    selection.forEach(_function);
  }
  
  private String createPackageFileStructure(final ProcessingEnvironment processingEnv, final String srcDirPath, final String packagepath) {
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
  
  public String convertEntityTypeSingleFile(final Hlent[] list, final String packageName) {
    String _createPackageStatement = this.createPackageStatement(packageName);
    String _doConvertEntityType = this.doConvertEntityType(list);
    return (_createPackageStatement + _doConvertEntityType);
  }
  
  public String doConvertEntityType(final Hlent[] list) {
    String _xblockexpression = null;
    {
      final StringBuffer body = new StringBuffer();
      for (final Hlent hlent : list) {
        body.append(this.convertEntityType(hlent));
      }
      _xblockexpression = body.toString();
    }
    return _xblockexpression;
  }
  
  /**
   * Generate entity type declaration
   */
  public String convertEntityType(final Hlent hlent) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("�importDescription(hlent.getTextProperty(PrpTypeCode.DESC))�");
    _builder.newLine();
    _builder.append("@entity �hlent.name.convertEntityName� {  ");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�FOR attr : hlent.followDscbya�");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("�importDescription(attr.getTextProperty(PrpTypeCode.DESC))� ");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("�importAttribute(attr as Attrusr)�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�ENDFOR�\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�FOR relmm : hlent.followDscbyr�");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("�importRelationship(relmm)�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�ENDFOR�");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�FOR ident : hlent.followIdntby�");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("�importIdentifier(ident)�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�ENDFOR�\t\t\t\t\t\t\t\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    _builder.append("\t\t\t ");
    _builder.newLine();
    final String body = _builder.toString();
    return body;
  }
  
  private String createPackageStatement(final String packageName) {
    String _trim = packageName.trim();
    String _plus = ("package " + _trim);
    return (_plus + ";");
  }
  
  /**
   * Generate comment using DESC associated with the Gen object.
   */
  private String importDescription(final String originalText) {
    String _xblockexpression = null;
    {
      String _trim = originalText.trim();
      boolean _equals = Objects.equal(_trim, "");
      if (_equals) {
        return "";
      }
      final String[] array = originalText.split("\n");
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("/*");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("�FOR line : array�");
      _builder.newLine();
      _builder.append(" \t");
      _builder.append("* � line�");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("�ENDFOR�\t");
      _builder.newLine();
      _builder.append(" ");
      _builder.append("*/");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  /**
   * Generate attribute for entity types and worksets
   */
  private String importAttribute(final Attrusr attrusr) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'T\' �");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: �getTextImpl(attrusr)��makeOptionality(attrusr)� �makeYTextAnnotation(attrusr)�; ");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'N\' �\t ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: �getNumberImpl(attrusr)��makeOptionality(attrusr)� �makeYNumberAnnotation(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'D\' �\t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: Date�makeOptionality(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'M\' �");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: Time�makeOptionality(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'Q\' �\t\t\t\t\t\t\t\t \t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: Timestamp�makeOptionality(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'Z\' �\t ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: �getTextImpl(attrusr)��makeOptionality(attrusr)� �makeYTextAnnotation(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'G\' �\t ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: �getTextImpl(attrusr)��makeOptionality(attrusr)� �makeYTextAnnotation(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�");
    _builder.newLine();
    _builder.append("�IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == \'B\' � \t\t\t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("@attr �attrusr.name.convertAttributeName�: Blob�makeOptionality(attrusr)� �makeYTextAnnotation(attrusr)�;");
    _builder.newLine();
    _builder.append("�ENDIF�\t\t\t");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String getNumberImpl(final Attrusr attrusr) {
    int _decplc = attrusr.getDecplc();
    boolean _equals = (_decplc == 0);
    if (_equals) {
      int _len = attrusr.getLen();
      boolean _lessEqualsThan = (_len <= 4);
      if (_lessEqualsThan) {
        return "Short";
      } else {
        if (((attrusr.getLen() > 4) && (attrusr.getLen() <= 9))) {
          return "Int";
        } else {
          return "Double";
        }
      }
    } else {
      return "Double";
    }
  }
  
  private String getTextImpl(final Attrusr attrusr) {
    String varyingLength = "String";
    char _varlen = attrusr.getVarlen();
    char _charAt = "N".charAt(0);
    boolean _equals = (_varlen == _charAt);
    if (_equals) {
      varyingLength = "String";
    } else {
      char _opt = attrusr.getOpt();
      char _charAt_1 = "Y".charAt(0);
      boolean _equals_1 = (_opt == _charAt_1);
      if (_equals_1) {
        varyingLength = "String";
      }
    }
    return varyingLength;
  }
  
  private String makeYNumberAnnotation(final Attrusr attrusr) {
    int _decplc = attrusr.getDecplc();
    boolean _notEquals = (_decplc != 0);
    if (_notEquals) {
      int _len = attrusr.getLen();
      String _plus = ("@decimal(" + Integer.valueOf(_len));
      String _plus_1 = (_plus + ",");
      int _decplc_1 = attrusr.getDecplc();
      String _plus_2 = (_plus_1 + Integer.valueOf(_decplc_1));
      return (_plus_2 + ")");
    } else {
      int _len_1 = attrusr.getLen();
      String _plus_3 = ("@length(" + Integer.valueOf(_len_1));
      return (_plus_3 + ")");
    }
  }
  
  private String makeOptionality(final Attrusr attrusr) {
    String optional = "?";
    char _opt = attrusr.getOpt();
    char _charAt = "M".charAt(0);
    boolean _equals = (_opt == _charAt);
    if (_equals) {
      optional = "";
    } else {
      char _opt_1 = attrusr.getOpt();
      char _charAt_1 = "O".charAt(0);
      boolean _equals_1 = (_opt_1 == _charAt_1);
      if (_equals_1) {
        optional = "?";
      }
    }
    return optional;
  }
  
  /**
   * Generate @YText annotation
   */
  private String makeYTextAnnotation(final Attrusr attrusr) {
    int _len = attrusr.getLen();
    String _plus = ("@length(" + Integer.valueOf(_len));
    return (_plus + ")");
  }
  
  /**
   * Generate @YSource annotation
   */
  private String makeYRelationshipAnnotation(final Relmm relmm) {
    String type = "associationType=AssociateTypes.Default";
    char _modorref = relmm.getModorref();
    char _charAt = "M".charAt(0);
    boolean _equals = (_modorref == _charAt);
    if (_equals) {
      type = "associationType=AssociateTypes.Modifying";
    } else {
      char _modorref_1 = relmm.getModorref();
      char _charAt_1 = "R".charAt(0);
      boolean _equals_1 = (_modorref_1 == _charAt_1);
      if (_equals_1) {
        type = "associationType=AssociateTypes.Referencing";
      }
    }
    String transferable = "transferable=false";
    char _transf = relmm.getTransf();
    char _charAt_2 = "Y".charAt(0);
    boolean _equals_2 = (_transf == _charAt_2);
    if (_equals_2) {
      transferable = "transferable=true";
    } else {
      char _transf_1 = relmm.getTransf();
      char _charAt_3 = "N".charAt(0);
      boolean _equals_3 = (_transf_1 == _charAt_3);
      if (_equals_3) {
        transferable = "transferable=false";
      }
    }
    String deletionRule = "deletionRule=DeletionRules.Default";
    char _cascade = relmm.getCascade();
    char _charAt_4 = "D".charAt(0);
    boolean _equals_4 = (_cascade == _charAt_4);
    if (_equals_4) {
      deletionRule = "deletionRule=DeletionRules.DeleteEach";
    } else {
      char _cascade_1 = relmm.getCascade();
      char _charAt_5 = "N".charAt(0);
      boolean _equals_5 = (_cascade_1 == _charAt_5);
      if (_equals_5) {
        deletionRule = "deletionRule=DeletionRules.Disassociate";
      } else {
        char _cascade_2 = relmm.getCascade();
        char _charAt_6 = "R".charAt(0);
        boolean _equals_6 = (_cascade_2 == _charAt_6);
        if (_equals_6) {
          deletionRule = "deletionRule=DeletionRules.Disallow";
        } else {
          char _cascade_3 = relmm.getCascade();
          char _charAt_7 = "C".charAt(0);
          boolean _equals_7 = (_cascade_3 == _charAt_7);
          if (_equals_7) {
            deletionRule = "deletionRule=DeletionRules.Default";
          }
        }
      }
    }
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("percentage=�relmm.pctopt�");
    final String percentage = _builder.toString();
    StringConcatenation _builder_1 = new StringConcatenation();
    _builder_1.append("atLeast=�relmm.expmin�");
    final String atLeast = _builder_1.toString();
    StringConcatenation _builder_2 = new StringConcatenation();
    _builder_2.append("onAverage=�relmm.expavg�");
    final String onAverage = _builder_2.toString();
    StringConcatenation _builder_3 = new StringConcatenation();
    _builder_3.append("atMost=�relmm.expmax�");
    final String atMost = _builder_3.toString();
    return (((((((((((((("@YRelationship (" + transferable) + ", ") + type) + ", ") + deletionRule) + ", ") + percentage) + ", ") + atLeast) + ", ") + onAverage) + ", ") + atMost) + ")");
  }
  
  private String importRelationship(final Relmm relmm) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("�importDescription(relmm.getTextProperty(PrpTypeCode.DESC))�");
    _builder.newLine();
    _builder.append("@rel �relmm.name.converRelationshipName� -> �relmm.followInvers.followDscpr.name.convertEntityName��IF relmm.getTextProperty(PrpTypeCode.CARD) == \"M\"�*�ENDIF� <- �relmm.followInvers.followDscpr.name.convertEntityName�.�relmm.followInvers.name.converRelationshipName�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String importIdentifier(final Ident ident) {
    String _xblockexpression = null;
    {
      String name = "id";
      if (((ident.getName() != null) && (ident.getName().length() > 0))) {
        name = this._conversionUtil.convertIdentifierName(ident.getName());
      }
      final Vector<String> list = new Vector<String>();
      List<Attr> _followCntnsa = ident.<Attr>followCntnsa();
      for (final Attr attr : _followCntnsa) {
        list.add(this._conversionUtil.convertIdentifierName(attr.getName()));
      }
      List<Relmm> _followCntnsr = ident.<Relmm>followCntnsr();
      for (final Relmm rel : _followCntnsr) {
        list.add(this._conversionUtil.convertIdentifierName(rel.getName()));
      }
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("@id �name.convertIdentifierName��FOR text : list BEFORE \'(\' SEPARATOR \',\' AFTER \')\'��text��ENDFOR�;");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
}
