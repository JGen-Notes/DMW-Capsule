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

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.MMObj;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ids.ObjId;
import com.ca.gen.jmmi.objs.Attrusr;
import com.ca.gen.jmmi.objs.Enty;
import com.ca.gen.jmmi.objs.Hlent;
import com.ca.gen.jmmi.objs.Relmm;
import com.ca.gen.jmmi.objs.Subj;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.google.common.base.Objects;
import com.google.inject.Inject;
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil;
import eu.jgen.notes.dmw.lite.capsule.IllegalExportOperation;
import eu.jgen.notes.dmw.lite.lang.YAnnotAttr;
import eu.jgen.notes.dmw.lite.lang.YAnnotEntity;
import eu.jgen.notes.dmw.lite.lang.YAnnotEntityInner;
import eu.jgen.notes.dmw.lite.lang.YAnnotRel;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.impl.HiddenLeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

@SuppressWarnings("all")
public class UploadDataModelFragment {
  @Inject
  @Extension
  private ConversionUtil _conversionUtil;
  
  private static Ency ency;
  
  private static Model model;
  
  public void connect(final Model newmodel) {
    UploadDataModelFragment.ency = newmodel.getEncy();
    UploadDataModelFragment.model = newmodel;
  }
  
  private String convertCommentIntoDescription(final String commentText) {
    String _trim = commentText.trim();
    boolean _equals = Objects.equal(_trim, "");
    if (_equals) {
      return "";
    }
    final String[] array = commentText.split("\n");
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("�FOR line : array�\t\t");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("�IF !(line.trim.startsWith(\"/*\") || line.trim.startsWith(\"*/\"))��line.trim��ENDIF�");
    _builder.newLine();
    _builder.append("�ENDFOR�");
    _builder.newLine();
    final String desc = _builder.toString();
    return desc;
  }
  
  /**
   * Find multi-line comment in front of declaration or annotation associated with the declaration
   */
  private String extractMultilineCommentIfAny(final EObject object) {
    final Function1<ILeafNode, Boolean> _function = (ILeafNode node) -> {
      return Boolean.valueOf(((node instanceof HiddenLeafNode) && Objects.equal(((TerminalRule) node.getGrammarElement()).getName(), "ML_COMMENT")));
    };
    final ILeafNode leaveNode = IterableExtensions.<ILeafNode>findFirst(NodeModelUtils.getNode(object).getLeafNodes(), _function);
    if ((leaveNode != null)) {
      return leaveNode.getText();
    }
    return "hello";
  }
  
  /**
   * Create entity type definition
   */
  public void makeEntity(final YAnnotEntity annotEntity) {
    final String name = this._conversionUtil.createEntityName(annotEntity.getName());
    String tdName = this._conversionUtil.createTDName(annotEntity.getName());
    final String mlComment = this.extractMultilineCommentIfAny(annotEntity);
    final String desc = this.convertCommentIntoDescription(mlComment);
    final Hlent hlent = this.doEntity(annotEntity, name, tdName, desc);
    final Consumer<YAnnotEntityInner> _function = (YAnnotEntityInner element) -> {
      if ((element instanceof YAnnotAttr)) {
        final YAnnotAttr annotAttr = ((YAnnotAttr) element);
        this.doAttribute(hlent, annotAttr);
      }
    };
    annotEntity.getAnnots().forEach(_function);
    String _createEntityName = this._conversionUtil.createEntityName(annotEntity.getName());
    String _plus = ("\tEntity type " + _createEntityName);
    String _plus_1 = (_plus + " has been created.");
    InputOutput.<String>println(_plus_1);
  }
  
  /**
   * Create relationships
   */
  public void makeRelationships(final YAnnotRel annotRel) {
    String _createRelationshipName = this._conversionUtil.createRelationshipName(annotRel.getName());
    String _plus = ("\tRelationship " + _createRelationshipName);
    String _plus_1 = (_plus + " is to be created.");
    InputOutput.<String>println(_plus_1);
    final YAnnotEntity destinationEntity = annotRel.getTarget();
    EObject _eContainer = annotRel.eContainer();
    final YAnnotEntity sourceEntity = ((YAnnotEntity) _eContainer);
    YAnnotRel retationshipDestination = annotRel.getInverse();
    final ObjId sourceObjId = UploadDataModelFragment.model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, this._conversionUtil.createEntityName(sourceEntity.getName()));
    if ((sourceObjId == null)) {
      String _createRelationshipName_1 = this._conversionUtil.createRelationshipName(annotRel.getName());
      String _plus_2 = ("\tProblem creating  " + _createRelationshipName_1);
      InputOutput.<String>println(_plus_2);
      throw new IllegalExportOperation("Problem finding  source entity type");
    }
    MMObj _instance = MMObj.getInstance(UploadDataModelFragment.model, sourceObjId);
    final Hlent sourceHlent = ((Hlent) _instance);
    final ObjId destinationObjId = UploadDataModelFragment.model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, 
      this._conversionUtil.createEntityName(sourceEntity.getName()));
    if ((destinationObjId == null)) {
      String _createRelationshipName_2 = this._conversionUtil.createRelationshipName(annotRel.getName());
      String _plus_3 = ("\tProblem creating  " + _createRelationshipName_2);
      InputOutput.<String>println(_plus_3);
      String _createEntityName = this._conversionUtil.createEntityName(destinationEntity.getName());
      String _plus_4 = ("Problem finding  destination entity type" + _createEntityName);
      throw new IllegalExportOperation(_plus_4);
    }
    MMObj _instance_1 = MMObj.getInstance(UploadDataModelFragment.model, destinationObjId);
    final Hlent destinationHlent = ((Hlent) _instance_1);
    List<Relmm> _followDscbyr = sourceHlent.<Relmm>followDscbyr();
    for (final Relmm relmm : _followDscbyr) {
      if ((Objects.equal(relmm.getName(), this._conversionUtil.createRelationshipName(annotRel.getName())) && Objects.equal(relmm.<Relmm>followInvers().<Enty>followDscpr().getName(), 
        this._conversionUtil.createRelationshipName(destinationEntity.getName())))) {
        InputOutput.<String>println("hahahahah");
        return;
      }
    }
    this.doRelationship(sourceHlent, destinationHlent, annotRel, retationshipDestination, destinationEntity);
  }
  
  private static YAnnotRel findDestinationRelationship(final YAnnotEntity targetEntity, final YAnnotRel annotRel) {
    EList<YAnnotEntityInner> _annots = targetEntity.getAnnots();
    for (final YAnnotEntityInner feature : _annots) {
      if ((feature instanceof YAnnotRel)) {
        String _name = ((YAnnotRel) feature).getName();
        YAnnotRel _inverse = annotRel.getInverse();
        boolean _equals = Objects.equal(_name, _inverse);
        if (_equals) {
          return ((YAnnotRel) feature);
        }
      }
    }
    return null;
  }
  
  protected void doRelationship(final Hlent sourceEntity, final Hlent destinationEntity, final YAnnotRel relationshipFrom, final YAnnotRel relationshipTo, final YAnnotEntity targetEntity) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.RELMM);
    final Relmm fromRelmm = ((Relmm) _newInstance);
    MMObj _newInstance_1 = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.RELMM);
    final Relmm toRelmm = ((Relmm) _newInstance_1);
    fromRelmm.associateInvers(toRelmm);
    sourceEntity.associateDscbyr(fromRelmm);
    destinationEntity.associateDscbyr(toRelmm);
    fromRelmm.setName(this._conversionUtil.createRelationshipName(relationshipFrom.getName()));
    toRelmm.setName(this._conversionUtil.createRelationshipName(relationshipFrom.getInverse().getName()));
    this.setRelationshipCardinalityOptionality(relationshipFrom, fromRelmm);
    this.setRelationshipCardinalityOptionality(relationshipTo, toRelmm);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  protected void setRelationshipCardinalityOptionality(final YAnnotRel relationshipTo, final Relmm toRelmm) {
    if (((relationshipTo.isMany() && relationshipTo.isOptional()) || (relationshipTo.isMany() && (!relationshipTo.isOptional())))) {
      toRelmm.setCard('M');
    } else {
      toRelmm.setCard('1');
    }
    if (((relationshipTo.isMany() && (!relationshipTo.isOptional())) || ((!relationshipTo.isMany()) && (!relationshipTo.isOptional())))) {
      toRelmm.setOpt('M');
    } else {
      toRelmm.setOpt('O');
    }
  }
  
  private static Relmm updateRelmmUsingAnnotation(final Relmm relmm) {
    relmm.setPctopt(0);
    relmm.setTransf('N');
    relmm.setExpmin(0);
    relmm.setExpavg(0);
    relmm.setExpmax(0);
    relmm.setModorref(' ');
    relmm.setCascade('C');
    relmm.setModorref('M');
    relmm.setTransf('Y');
    relmm.setCascade('D');
    return relmm;
  }
  
  private Hlent doEntity(final YAnnotEntity entity, final String name, final String tdName, final String desc) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.HLENT);
    final Hlent hlent = ((Hlent) _newInstance);
    UploadDataModelFragment.model.getPcroot().<Subj>followHassubj().associateDetlby(hlent);
    hlent.setName(name);
    hlent.setDsdname(tdName);
    if ((desc != null)) {
      hlent.setDesc(desc);
    }
    UploadDataModelFragment.model.commitUnitOfWork();
    return hlent;
  }
  
  /**
   * Create attribute and associate with the entity.
   */
  private void doAttribute(final Enty enty, final YAnnotAttr annotAttr) {
    String _name = annotAttr.getYclass().getName();
    if (_name != null) {
      switch (_name) {
        case "String":
          this.doAttributeText(enty, annotAttr);
          break;
        case "Int":
          this.doAttributeNumber(enty, annotAttr);
          break;
        case "Short":
          this.doAttributeNumber(enty, annotAttr);
          break;
        case "Double":
          this.doAttributeNumber(enty, annotAttr);
          break;
        case "Date":
          this.doAttributeDate(enty, annotAttr);
          break;
        case "Time":
          this.doAttributeTime(enty, annotAttr);
          break;
        case "Timestamp":
          this.doAttributeTimestamp(enty, annotAttr);
          break;
        case "Blob":
          this.doAttributeBlob(enty, annotAttr);
          break;
        default:
          break;
      }
    } else {
    }
  }
  
  /**
   * Set properties for the text type of attribute.
   */
  private void doAttributeText(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('T');
    attrusr.setCasesens('Y');
    attrusr.setVarlen('Y');
    attrusr.setLen(20);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Set common for all attribute types  properties
   */
  protected void createCommonAttributeProperties(final Attrusr attrusr, final YAnnotAttr annotAttr) {
    attrusr.setDesc(this.convertCommentIntoDescription(this.extractMultilineCommentIfAny(annotAttr)));
    attrusr.setName(this._conversionUtil.createAttributeName(annotAttr.getName()));
    attrusr.setDsdname(this._conversionUtil.createTDName(annotAttr.getName()));
    String _optional = annotAttr.getOptional();
    boolean _equals = Objects.equal(_optional, "?");
    if (_equals) {
      attrusr.setOpt('O');
    } else {
      attrusr.setOpt('M');
    }
  }
  
  /**
   * Set properties for the number type of attribute.
   */
  private void doAttributeNumber(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('N');
    attrusr.setLen(9);
    attrusr.setDecplc(0);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Set properties for the blob type of attribute.
   */
  private void doAttributeBlob(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('B');
    attrusr.setLen(20);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Set properties for the date type of attribute.
   */
  private void doAttributeDate(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('D');
    attrusr.setSrctype('N');
    attrusr.setLen(8);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Set properties for the time type of attribute.
   */
  private void doAttributeTime(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('M');
    attrusr.setSrctype('N');
    attrusr.setLen(6);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Set properties for the timestamp type of attribute.
   */
  private void doAttributeTimestamp(final Enty enty, final YAnnotAttr annotAttr) {
    UploadDataModelFragment.model.beginUnitOfWork();
    MMObj _newInstance = MMObj.newInstance(UploadDataModelFragment.model, ObjTypeCode.ATTRUSR);
    final Attrusr attrusr = ((Attrusr) _newInstance);
    enty.associateDscbya(attrusr);
    this.createCommonAttributeProperties(attrusr, annotAttr);
    attrusr.setDoman('Q');
    attrusr.setSrctype('N');
    attrusr.setLen(20);
    UploadDataModelFragment.model.commitUnitOfWork();
  }
  
  /**
   * Check if entity already defined in the model
   */
  private static boolean isEntityDefined(final String name) {
    ObjId _objIdByName = UploadDataModelFragment.model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, name);
    boolean _tripleNotEquals = (_objIdByName != null);
    if (_tripleNotEquals) {
      return true;
    }
    return false;
  }
}
