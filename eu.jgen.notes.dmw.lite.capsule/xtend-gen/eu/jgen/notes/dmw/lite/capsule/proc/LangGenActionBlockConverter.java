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
import com.ca.gen.jmmi.objs.Acblkbsd;
import com.ca.gen.jmmi.objs.Acblkdef;
import com.ca.gen.jmmi.objs.Actif;
import com.ca.gen.jmmi.objs.Actmove;
import com.ca.gen.jmmi.objs.Acton;
import com.ca.gen.jmmi.objs.Actse;
import com.ca.gen.jmmi.objs.Actsub;
import com.ca.gen.jmmi.objs.Command;
import com.ca.gen.jmmi.objs.Dvgent;
import com.ca.gen.jmmi.objs.Dvgin;
import com.ca.gen.jmmi.objs.Dvglcl;
import com.ca.gen.jmmi.objs.Dvgout;
import com.ca.gen.jmmi.objs.Dvset;
import com.ca.gen.jmmi.objs.Entc;
import com.ca.gen.jmmi.objs.Entd;
import com.ca.gen.jmmi.objs.Ents;
import com.ca.gen.jmmi.objs.Entu;
import com.ca.gen.jmmi.objs.Entvw;
import com.ca.gen.jmmi.objs.Enty;
import com.ca.gen.jmmi.objs.Expcmd;
import com.ca.gen.jmmi.objs.Expexus;
import com.ca.gen.jmmi.objs.Expnum;
import com.ca.gen.jmmi.objs.Expoper;
import com.ca.gen.jmmi.objs.Exppar;
import com.ca.gen.jmmi.objs.Exprs;
import com.ca.gen.jmmi.objs.Exptxt;
import com.ca.gen.jmmi.objs.Expvus;
import com.ca.gen.jmmi.objs.Exstate;
import com.ca.gen.jmmi.objs.Grpvw;
import com.ca.gen.jmmi.objs.Hlent;
import com.ca.gen.jmmi.objs.Hlentds;
import com.ca.gen.jmmi.objs.Hlvdf;
import com.ca.gen.jmmi.objs.Lclcm;
import com.ca.gen.jmmi.objs.Lcles;
import com.ca.gen.jmmi.objs.Lclst;
import com.ca.gen.jmmi.objs.Prdas;
import com.ca.gen.jmmi.objs.Prdvw;
import com.ca.gen.jmmi.objs.Suentvw;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import eu.jgen.notes.annot.desc.processor.AnnotationObject;
import eu.jgen.notes.annot.desc.processor.DiagnosticKind;
import eu.jgen.notes.annot.desc.processor.Filer;
import eu.jgen.notes.annot.desc.processor.Messager;
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment;
import eu.jgen.notes.dmw.lite.capsule.ConversionProblem;
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil;
import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;

/**
 * Widget Artifacts Generator
 */
@SuppressWarnings("all")
public class LangGenActionBlockConverter {
  @Inject
  @Extension
  private ConversionUtil _conversionUtil;
  
  public List<Hlvdf> hlvdfList;
  
  public void generate(final Set<AnnotationObject> selection, final ProcessingEnvironment processingEnv, final String srcDirPath, final String packageName) {
    try {
      final List<Acblkbsd> actionBlockList = CollectionLiterals.<Acblkbsd>newArrayList();
      final Consumer<AnnotationObject> _function = (AnnotationObject annotationObject) -> {
        MMObj _genObject = annotationObject.getGenObject();
        if ((_genObject instanceof Acblkbsd)) {
          MMObj _genObject_1 = annotationObject.getGenObject();
          final Acblkbsd acblkbsd = ((Acblkbsd) _genObject_1);
          actionBlockList.add(acblkbsd);
        }
      };
      selection.forEach(_function);
      final String exitStatePath = this.createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"));
      final Writer exitStateWriter = processingEnv.getFiler().openWriter(((exitStatePath + "\\") + "ExitStates.dmw"));
      exitStateWriter.write(this.convertExitStatesSingleFile(actionBlockList, packageName));
      exitStateWriter.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Converting exit states used by action blocks");
      final String commandsPath = this.createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"));
      final Writer commandsWriter = processingEnv.getFiler().openWriter(((commandsPath + "\\") + "Commands.dmw"));
      commandsWriter.write(this.convertCommandsSingleFile(actionBlockList, packageName));
      commandsWriter.close();
      processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Converting commands used by action blocks");
      final Consumer<AnnotationObject> _function_1 = (AnnotationObject annotationObject) -> {
        try {
          MMObj _genObject = annotationObject.getGenObject();
          if ((_genObject instanceof Acblkbsd)) {
            MMObj _genObject_1 = annotationObject.getGenObject();
            final Acblkbsd acblkbsd = ((Acblkbsd) _genObject_1);
            final String actionBlockPath = this.createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"));
            Filer _filer = processingEnv.getFiler();
            String _convertActionBlockName = this._conversionUtil.convertActionBlockName(acblkbsd.getName());
            String _plus = ((actionBlockPath + "\\") + _convertActionBlockName);
            String _plus_1 = (_plus + ".dmw");
            final Writer actionBlockWriter = _filer.openWriter(_plus_1);
            actionBlockWriter.write(this.convertWidgetSingleFile(acblkbsd, packageName));
            actionBlockWriter.close();
            Messager _messager = processingEnv.getMessager();
            String _name = acblkbsd.getName();
            String _plus_2 = ("Converting action block  " + _name);
            _messager.printMessage(DiagnosticKind.INFO, _plus_2);
          }
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      };
      selection.forEach(_function_1);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  /**
   * Converting all exit states used by the selected action blocks.
   */
  public String convertExitStatesSingleFile(final List<Acblkbsd> actions, final String packageName) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package �packageName�;");
    _builder.newLine();
    _builder.append(" ");
    _builder.append("�doConvertExitStates(actions)�");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String doConvertExitStates(final List<Acblkbsd> actions) {
    final List<Exstate> list = CollectionLiterals.<Exstate>newArrayList();
    final Consumer<Acblkbsd> _function = (Acblkbsd acblkbsd) -> {
      List<Expexus> _followUsesexst = acblkbsd.<Expexus>followUsesexst();
      for (final Expexus expexus : _followUsesexst) {
        {
          final String name = expexus.<Exstate>followUsagofe().getName();
          boolean _checkDuplicateExitState = this.checkDuplicateExitState(list, name);
          boolean _not = (!_checkDuplicateExitState);
          if (_not) {
            list.add(expexus.<Exstate>followUsagofe());
          }
        }
      }
    };
    actions.forEach(_function);
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class ExitStates {");
    _builder.newLine();
    _builder.append("�FOR exstate : list�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public var �exstate.name.convertExitStateName� : ExitState");
    _builder.newLine();
    _builder.append("\t   ");
    _builder.append("@action(�IF exstate.terminat.toString == \"M\"�normal �ENDIF��IF exstate.terminat.toString == \"A\"�abort�ENDIF��IF exstate.terminat.toString == \"R\"�rollback�ENDIF�) @msgtype(�IF exstate.msgtype.toString == \"N\"�none�ENDIF��IF exstate.msgtype.toString == \"I\"�info�ENDIF��IF exstate.msgtype.toString == \"W\"�warning�ENDIF��IF exstate.msgtype.toString == \"E\"�error�ENDIF�) @message(\"�exstate.string�\");");
    _builder.newLine();
    _builder.append("�ENDFOR�");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  /**
   * Converting all commands used by the selected action blocks.
   */
  public String convertCommandsSingleFile(final List<Acblkbsd> actions, final String packageName) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package �packageName�;");
    _builder.newLine();
    _builder.append("class Commands {");
    _builder.newLine();
    _builder.newLine();
    _builder.append("�doConvertCommands(actions)�");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String doConvertCommands(final List<Acblkbsd> actions) {
    String _xblockexpression = null;
    {
      final List<Command> list = CollectionLiterals.<Command>newArrayList();
      final Consumer<Acblkbsd> _function = (Acblkbsd acblkbsd) -> {
        List<Expcmd> _followUsescmd = acblkbsd.<Expcmd>followUsescmd();
        for (final Expcmd cmd : _followUsescmd) {
          {
            final String name = cmd.<Command>followUsageof().getName();
            boolean _checkDuplicateCommand = this.checkDuplicateCommand(list, name);
            boolean _not = (!_checkDuplicateCommand);
            if (_not) {
              list.add(cmd.<Command>followUsageof());
            }
          }
        }
      };
      actions.forEach(_function);
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("class Commands {");
      _builder.newLine();
      _builder.append("�FOR command : list�");
      _builder.newLine();
      _builder.append("\t");
      _builder.append("public var �command.name.convertCommandName� : Command;");
      _builder.newLine();
      _builder.append("�ENDFOR�");
      _builder.newLine();
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  public String convertWidgetSingleFile(final Acblkbsd acblkbsd, final String packageName) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("package �packageName�;");
    _builder.newLine();
    _builder.append("import �packageName�.*;");
    _builder.newLine();
    _builder.newLine();
    _builder.append("�doConvertWidget(acblkbsd)�");
    _builder.newLine();
    return _builder.toString();
  }
  
  public String doConvertWidget(final Acblkbsd acblkbsd) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("class �acblkbsd.name.convertActionBlockName� : Widget {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�generateStructuresForEntities(acblkbsd)�");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("var exits : ExitStates;");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("var commands : Commands;");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("public func start() {");
    _builder.newLine();
    _builder.append("\t\t");
    _builder.append("�processStatements(acblkbsd)�");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("}");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String processStatements(final Acblkbsd acblkbsd) {
    StringBuffer buffer = new StringBuffer();
    List<Acton> _followDefndby = acblkbsd.<Acton>followDefndby();
    for (final Acton acton : _followDefndby) {
      buffer.append(this.generateStatement(acton));
    }
    return buffer.toString();
  }
  
  /**
   * Check if exit state is already on the list of exit states used by the group of action blocks.
   */
  private boolean checkDuplicateExitState(final List<Exstate> list, final String name) {
    boolean _isEmpty = list.isEmpty();
    if (_isEmpty) {
      return false;
    }
    for (final Exstate exstate : list) {
      String _name = exstate.getName();
      boolean _equals = Objects.equal(_name, name);
      if (_equals) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Check if command is already on the list of commands used by the group of action blocks.
   */
  private boolean checkDuplicateCommand(final List<Command> list, final String name) {
    boolean _isEmpty = list.isEmpty();
    if (_isEmpty) {
      return false;
    }
    for (final Command command : list) {
      String _name = command.getName();
      boolean _equals = Objects.equal(_name, name);
      if (_equals) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Generate inner classes for structures derived from views on entity types
   */
  private String generateStructuresForEntities(final Acblkdef acblkbsd) {
    this.buildListOfViews(acblkbsd);
    StringBuffer buffer = new StringBuffer();
    for (final Hlvdf hlvw : this.hlvdfList) {
      if ((hlvw instanceof Entvw)) {
        final Entvw entvw = ((Entvw) hlvw);
        Enty _followSees = entvw.<Enty>followSees();
        if ((_followSees instanceof Hlent)) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("class �hlvw.convertViewNameAsClass� : Structure -> �(entvw.followSees as Hlent).name.convertEntityName� {");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("�FOR suentvw : entvw.followDtlbyp�");
          _builder.newLine();
          _builder.append("\t\t");
          _builder.append("�IF suentvw instanceof Prdvw �");
          _builder.newLine();
          _builder.append("\t\t\t");
          _builder.append("�generateStructurePropertyEntity(suentvw as Prdvw,entvw.followSees as Hlent)�");
          _builder.newLine();
          _builder.append("\t\t");
          _builder.append("�ENDIF�");
          _builder.newLine();
          _builder.append("\t");
          _builder.append("�ENDFOR�");
          _builder.newLine();
          _builder.append("}");
          _builder.newLine();
          buffer.append(_builder);
        } else {
          Enty _followSees_1 = entvw.<Enty>followSees();
          if ((_followSees_1 instanceof Hlentds)) {
            StringConcatenation _builder_1 = new StringConcatenation();
            _builder_1.append("class �hlvw.convertViewNameAsClass� : Structure {");
            _builder_1.newLine();
            _builder_1.append("\t");
            _builder_1.append("�FOR suentvw : entvw.followDtlbyp�");
            _builder_1.newLine();
            _builder_1.append("\t\t");
            _builder_1.append("�IF suentvw instanceof Prdvw �");
            _builder_1.newLine();
            _builder_1.append("\t\t\t");
            _builder_1.append("�generateStructurePropertyWorkset(suentvw as Prdvw,entvw.followSees as Hlentds)�");
            _builder_1.newLine();
            _builder_1.append("\t\t");
            _builder_1.append("�ENDIF�");
            _builder_1.newLine();
            _builder_1.append("\t");
            _builder_1.append("�ENDFOR�");
            _builder_1.newLine();
            _builder_1.append("}");
            _builder_1.newLine();
            buffer.append(_builder_1);
          }
        }
      }
    }
    for (final Hlvdf hlvw_1 : this.hlvdfList) {
      if ((hlvw_1 instanceof Entvw)) {
        final Entvw entvw_1 = ((Entvw) hlvw_1);
        Enty _followSees_2 = entvw_1.<Enty>followSees();
        if ((_followSees_2 instanceof Hlent)) {
          StringConcatenation _builder_2 = new StringConcatenation();
          _builder_2.append("public var �hlvw.convertViewNameAsProperty�  : �hlvw.convertViewNameAsClass�;");
          _builder_2.newLine();
          _builder_2.append(" ");
          _builder_2.newLine();
          buffer.append(_builder_2);
        } else {
          Enty _followSees_3 = entvw_1.<Enty>followSees();
          if ((_followSees_3 instanceof Hlentds)) {
            StringConcatenation _builder_3 = new StringConcatenation();
            _builder_3.append("public var �hlvw.convertViewNameAsProperty�  : �hlvw.convertViewNameAsClass�;");
            _builder_3.newLine();
            _builder_3.append(" ");
            _builder_3.newLine();
            buffer.append(_builder_3);
          }
        }
      } else {
        if ((hlvw_1 instanceof Grpvw)) {
          final Grpvw grpvw = ((Grpvw) hlvw_1);
          StringConcatenation _builder_4 = new StringConcatenation();
          _builder_4.append("public var �hlvw.convertViewNameAsProperty� : Array �groupInclude(grpvw)�");
          _builder_4.newLine();
          _builder_4.newLine();
          buffer.append(_builder_4);
        }
      }
    }
    return buffer.toString();
  }
  
  private String groupInclude(final Grpvw grpvw) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("<�FOR hlvdf : grpvw.followGrpfor SEPARATOR \",\"��IF hlvdf instanceof Entvw��(hlvdf as Entvw).convertViewNameAsProperty��ENDIF��ENDFOR�>;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateStructurePropertyEntity(final Prdvw prdvw, final Hlent hlent) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public var �prdvw.followSees.name.convertAttributeName� : �(prdvw.followSees as Attrusr).convertAttribute� -> �hlent.name.convertEntityName�.�prdvw.followSees.name.convertAttributeName�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateStructurePropertyWorkset(final Prdvw prdvw, final Hlentds hlentds) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("public var �prdvw.followSees.name.convertAttributeName� : �(prdvw.followSees as Attrusr).convertAttribute�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  /**
   * Create list of all qualifying views
   */
  public void buildListOfViews(final Acblkdef acblkbsd) {
    this.hlvdfList = CollectionLiterals.<Hlvdf>newArrayList();
    final Consumer<Hlvdf> _function = (Hlvdf hlvdf) -> {
      this.hlvdfList.add(hlvdf);
      this.expandGroupView(hlvdf);
    };
    acblkbsd.<Dvset>followGrpby().<Dvgin>followCntinps().<Hlvdf>followContains().forEach(_function);
    final Consumer<Hlvdf> _function_1 = (Hlvdf hlvdf) -> {
      this.hlvdfList.add(hlvdf);
      this.expandGroupView(hlvdf);
    };
    acblkbsd.<Dvset>followGrpby().<Dvglcl>followCntlcls().<Hlvdf>followContains().forEach(_function_1);
    final Consumer<Hlvdf> _function_2 = (Hlvdf hlvdf) -> {
      this.hlvdfList.add(hlvdf);
      this.expandGroupView(hlvdf);
    };
    acblkbsd.<Dvset>followGrpby().<Dvgout>followCntouts().<Hlvdf>followContains().forEach(_function_2);
    final Consumer<Hlvdf> _function_3 = (Hlvdf hlvdf) -> {
      this.hlvdfList.add(hlvdf);
    };
    acblkbsd.<Dvset>followGrpby().<Dvgent>followCntents().<Hlvdf>followContains().forEach(_function_3);
  }
  
  /**
   * Expand group views. Embedded views are not supported.
   */
  private void expandGroupView(final Hlvdf hlvdf) {
    if ((hlvdf instanceof Grpvw)) {
      final Grpvw grpvw = ((Grpvw) hlvdf);
      final Consumer<Hlvdf> _function = (Hlvdf hlvdfinner) -> {
        if ((hlvdfinner instanceof Grpvw)) {
          throw new ConversionProblem("DMW does not support embedded group views.");
        }
        this.hlvdfList.add(hlvdfinner);
      };
      grpvw.<Hlvdf>followGrpfor().forEach(_function);
    }
  }
  
  /**
   * Create directory structure for the package.
   */
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
  
  private String generateStatement(final Acton acton) {
    ObjTypeCode _objTypeCode = acton.getObjTypeCode();
    if (_objTypeCode != null) {
      switch (_objTypeCode) {
        case LCLCM:
          return this.generateCommandIs(((Lclcm) acton));
        case ENTC:
          return this.generateCreateStatement(((Entc) acton));
        case ENTD:
          return this.generateDeleteStatement(((Entd) acton));
        case ENTS:
          return this.generateReadStatement(((Ents) acton));
        case ENTU:
          return this.generateUpdateStatement(((Entu) acton));
        case ACTSE:
          return this.generateReadEachStatement(((Actse) acton));
        case LCLST:
          return this.generateSetStatement(((Lclst) acton));
        case PRDAS:
          return this.generateSetStatement(((Prdas) acton));
        case ACTMOVE:
          return this.generateMoveStatement(((Actmove) acton));
        case LCLES:
          return this.generateExitStateIsStatement(((Lcles) acton));
        case ACTIF:
          return this.generateIfStatement(((Actif) acton));
        default:
          return ("nothing generated for: " + acton);
      }
    } else {
      return ("nothing generated for: " + acton);
    }
  }
  
  private String generateIfStatement(final Actif actif) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("if (�generateExpression(actif.followDefndby.get(0).followSubjto.followSees.followUses)�) {");
    _builder.newLine();
    _builder.append("\t");
    _builder.newLine();
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateCommandIs(final Lclcm lclcm) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("super.setCommand(self.commands.�(lclcm.followUses.get(0) as Expcmd).followUsageof.name.toLowerCase�);");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateUpdateStatement(final Entu entu) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("update �entu.followActson.convertViewNameAsProperty� -> �entu.followActson.name.toLowerCase��entu.followActson.followSees.name.convertEntityName� {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("�generateSetStatementBlock(entu.followDtlby)�");
    _builder.newLine();
    _builder.append("} success {");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("�generateStatementBlock(entu.followHassuccs.followDefndby)�");
    _builder.newLine();
    _builder.append("}\t\t");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateReadEachStatement(final Actse actse) {
    String _xblockexpression = null;
    {
      final Ents ents = actse.<Ents>followDetlby();
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("�FOR rdvwus : ents.followActsvia BEFORE \"read each \" SEPARATOR \",\"�");
      _builder.newLine();
      _builder.append("�(rdvwus.followActson1 as Entvw).convertViewNameAsProperty� -> �(rdvwus.followActson1 as Entvw).name.toLowerCase��(rdvwus.followActson1 as Entvw).followSees.name.convertEntityName�");
      _builder.newLine();
      _builder.append("     ");
      _builder.append("�ENDFOR� ");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("where �generateExpression(ents.followSubjto.followSees.followUses)�");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("target �actse.followTargets.get(0).followIsfor.convertViewNameAsProperty�");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("success {");
      _builder.newLine();
      _builder.append("     ");
      _builder.append("�generateStatementBlock(actse.followDefndby.get(0).followDefndby)�");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("}");
      _builder.newLine();
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  private String generateReadStatement(final Ents ents) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("�FOR rdvwus : ents.followActsvia BEFORE \"read \" SEPARATOR \",\"�");
    _builder.newLine();
    _builder.append("�(rdvwus.followActson1 as Entvw).convertViewNameAsProperty� -> �(rdvwus.followActson1 as Entvw).name.toLowerCase��(rdvwus.followActson1 as Entvw).followSees.name.convertEntityName�");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("�ENDFOR� ");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("where �generateExpression(ents.followSubjto.followSees.followUses)�");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("success {");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("�generateStatementBlock(ents.followHassuccs.followDefndby)�");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("} not found {");
    _builder.newLine();
    _builder.append("     ");
    _builder.append("�generateStatementBlock(ents.followHasexcp.get(0).followDefndby)�");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("}");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateExitStateIsStatement(final Lcles lcles) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("super.setExitState(self.exits.�(lcles.followUses.get(0) as Expexus).followUsagofe.name.convertExitStateName�);");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateMoveStatement(final Actmove actmove) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("super.moveStruct(self.�actmove.followFrom.convertViewNameAsProperty�,self.�actmove.followTo.convertViewNameAsProperty�);");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateSetStatement(final Lclst lclst) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("self.�generateSetStatementLeft(lclst)� = �generateExpression(lclst.followUses)�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateSetStatement(final Prdas prdas) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("self.�generateSetStatementLeft(prdas)� = �generateExpression(prdas.followUses)�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateSetStatementLeft(final Prdas prdas) {
    Suentvw _followActson = prdas.<Suentvw>followActson();
    if ((_followActson instanceof Prdvw)) {
      Suentvw _followActson_1 = prdas.<Suentvw>followActson();
      return this._conversionUtil.convertPredicateView(((Prdvw) _followActson_1));
    } else {
      return "do not know how to do it yet";
    }
  }
  
  private String generateSetStatementLeft(final Lclst lclst) {
    Suentvw _followActson = lclst.<Suentvw>followActson();
    if ((_followActson instanceof Prdvw)) {
      Suentvw _followActson_1 = lclst.<Suentvw>followActson();
      return this._conversionUtil.convertPredicateView(((Prdvw) _followActson_1));
    } else {
      return "do not know how to do it yet";
    }
  }
  
  private String generateExpression(final List<Exprs> list) {
    final StringBuffer buffer = new StringBuffer();
    final ListIterator<Exprs> expstack = list.listIterator();
    while (expstack.hasNext()) {
      {
        Exprs exprs = expstack.next();
        ObjTypeCode _objTypeCode = exprs.getObjTypeCode();
        if (_objTypeCode != null) {
          switch (_objTypeCode) {
            case EXPNUM:
              buffer.append(((Expnum) exprs).getString());
              break;
            case EXPOPER:
              buffer.append(this._conversionUtil.convertOperationName(((Expoper) exprs)));
              break;
            case EXPVUS:
              Suentvw _followEuses = ((Expvus) exprs).<Suentvw>followEuses();
              String _convertPredicateView = this._conversionUtil.convertPredicateView(((Prdvw) _followEuses));
              String _plus = ("self." + _convertPredicateView);
              buffer.append(_plus);
              break;
            case EXPTXT:
              String _string = ((Exptxt) exprs).getString();
              String _plus_1 = ("\"" + _string);
              String _plus_2 = (_plus_1 + "\"");
              buffer.append(_plus_2);
              break;
            case EXPPAR:
              buffer.append(((Exppar) exprs).getPrnths());
              break;
            case EXPSAU:
              exprs = expstack.next();
              exprs = expstack.next();
              if ((exprs instanceof Expexus)) {
                StringConcatenation _builder = new StringConcatenation();
                _builder.append("super.isExitState(self.exits.�(exprs as Expexus).followUsagofe.name.convertExitStateName�)");
                buffer.append(_builder);
              } else {
                if ((exprs instanceof Expcmd)) {
                  StringConcatenation _builder_1 = new StringConcatenation();
                  _builder_1.append("super.isCommand(self.commands.�(exprs as Expcmd).followUsageof.name.convertCommandName�)");
                  buffer.append(_builder_1);
                } else {
                  buffer.append(("** do not know how to do it: " + exprs));
                }
              }
              break;
            default:
              buffer.append(("** do not know how to do it: " + exprs));
              break;
          }
        } else {
          buffer.append(("** do not know how to do it: " + exprs));
        }
      }
    }
    return buffer.toString();
  }
  
  private String generateDeleteStatement(final Entd entd) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("delete �entd.followActson.convertViewNameAsProperty� -> �entd.followActson.name.toLowerCase��entd.followActson.followSees.name.convertEntityName�;");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateCreateStatement(final Entc entc) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("create �entc.followActson.convertViewNameAsProperty� -> �entc.followActson.name.toLowerCase��entc.followActson.followSees.name.convertEntityName� {");
    _builder.newLine();
    _builder.append("  ");
    _builder.append("�generateSetStatementBlock(entc.followDtlby)�");
    _builder.newLine();
    _builder.append("} success {");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("�generateStatementBlock(entc.followHassuccs.followDefndby)�");
    _builder.newLine();
    _builder.append("} already exist {");
    _builder.newLine();
    _builder.append("   ");
    _builder.append("�generateStatementBlock(entc.followHasexcp.get(0).followDefndby)�");
    _builder.newLine();
    _builder.append("}\t\t");
    _builder.newLine();
    return _builder.toString();
  }
  
  private String generateSetStatementBlock(final List<Actsub> list) {
    final StringBuffer buffer = new StringBuffer();
    for (final Actsub acton : list) {
      buffer.append(this.generateStatement(acton));
    }
    return buffer.toString();
  }
  
  private String generateStatementBlock(final List<Acton> list) {
    final StringBuffer buffer = new StringBuffer();
    for (final Acton acton : list) {
      buffer.append(this.generateStatement(acton));
    }
    return buffer.toString();
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
}
