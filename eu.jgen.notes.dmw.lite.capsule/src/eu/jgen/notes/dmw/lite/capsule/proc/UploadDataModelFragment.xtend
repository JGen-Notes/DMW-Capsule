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

package eu.jgen.notes.dmw.lite.capsule.proc

import com.ca.gen.jmmi.Ency
import com.ca.gen.jmmi.MMObj
import com.ca.gen.jmmi.Model
import com.ca.gen.jmmi.objs.Attrusr
import com.ca.gen.jmmi.objs.Enty
import com.ca.gen.jmmi.objs.Hlent
import com.ca.gen.jmmi.objs.Relmm
import com.ca.gen.jmmi.schema.ObjTypeCode
import com.ca.gen.jmmi.schema.PrpTypeCode
import com.google.inject.Inject
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil
import eu.jgen.notes.dmw.lite.lang.YAnnotAttr
import eu.jgen.notes.dmw.lite.lang.YAnnotEntity
import eu.jgen.notes.dmw.lite.lang.YAnnotEntityInner
import eu.jgen.notes.dmw.lite.lang.YAnnotRel
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.TerminalRule
import org.eclipse.xtext.nodemodel.impl.HiddenLeafNode
import org.eclipse.xtext.nodemodel.util.NodeModelUtils
import eu.jgen.notes.dmw.lite.capsule.IllegalExportOperation

class UploadDataModelFragment {

	@Inject extension ConversionUtil

	static var Ency ency
	static var Model model

	def void connect(Model newmodel) {
		ency = newmodel.ency
		model = newmodel
	}

	def private String convertCommentIntoDescription(String commentText) {
		if (commentText.trim == "") {
			return ""
		}
		val array = commentText.split("\n")
		val desc = '''
			«FOR line : array»		
				«IF !(line.trim.startsWith("/*") || line.trim.startsWith("*/"))»«line.trim»«ENDIF»
			«ENDFOR»
		'''
		return desc
	}

	/*
	 * Find multi-line comment in front of declaration or annotation associated with the declaration
	 */
	def private String extractMultilineCommentIfAny(EObject object) {
		val leaveNode = NodeModelUtils.getNode(object).leafNodes.findFirst [ node |
			node instanceof HiddenLeafNode && (node.grammarElement as TerminalRule).name == "ML_COMMENT"
		]
		if (leaveNode !== null) {
			return leaveNode.text
		}
		return "hello"
	}

	/*
	 * Create entity type definition
	 */
	def void makeEntity(YAnnotEntity annotEntity) {
		val name = annotEntity.name.createEntityName
		var tdName = annotEntity.name.createTDName
		val mlComment = extractMultilineCommentIfAny(annotEntity)
		val desc = mlComment.convertCommentIntoDescription
		val hlent = doEntity(annotEntity, name, tdName, desc)
		annotEntity.annots.forEach [ element |
			if (element instanceof YAnnotAttr) {
				val annotAttr = element as YAnnotAttr
				doAttribute(hlent, annotAttr)
			}
		]
		println("\tEntity type " + annotEntity.name.createEntityName + " has been created.")
	}

	/*
	 * Create relationships
	 */
	def void makeRelationships(YAnnotRel annotRel) {
		println("\tRelationship " + annotRel.name.createRelationshipName + " is to be created.")
		val destinationEntity = annotRel.target
		val sourceEntity = (annotRel.eContainer as YAnnotEntity)

		var retationshipDestination = annotRel.inverse

//		var retationshipDestination = findDestinationRelationship(destinationEntity, annotRel)
//		if (retationshipDestination === null) {
//				throw new IllegalExportOperation(" something wrong do nothing and return")
//		//	return // something wrong do nothing and return
//		}

		// Find source entity type
		val sourceObjId = model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, sourceEntity.name.createEntityName)
		if (sourceObjId === null) {

			println("\tProblem creating  " + annotRel.name.createRelationshipName)
			throw new IllegalExportOperation("Problem finding  source entity type")
		// return // something wrong do nothing and return
		}
		val sourceHlent = MMObj.getInstance(model, sourceObjId) as Hlent

		// Find destination entity type
		val destinationObjId = model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME,
			sourceEntity.name.createEntityName)
		if (destinationObjId === null) {

			println("\tProblem creating  " + annotRel.name.createRelationshipName)
			throw new IllegalExportOperation("Problem finding  destination entity type" +
				destinationEntity.name.createEntityName)
		// return // something wrong do nothing and return
		}
		val destinationHlent = MMObj.getInstance(model, destinationObjId) as Hlent

		// Check maybe relationship having the sane name and target entity  type  name already exists
		for (Relmm relmm : sourceHlent.followDscbyr) {
			if (relmm.name == annotRel.name.createRelationshipName && relmm.followInvers.followDscpr.name ==
				destinationEntity.name.createRelationshipName) {
					println("hahahahah")
				return // relationship already defined - do nothing 
			}
		}
		doRelationship(sourceHlent, destinationHlent, annotRel, retationshipDestination, destinationEntity)
	}

	def static private YAnnotRel findDestinationRelationship(YAnnotEntity targetEntity, YAnnotRel annotRel) {
		for (YAnnotEntityInner feature : targetEntity.annots) {
			if (feature instanceof YAnnotRel) {
				if ((feature as YAnnotRel).name == annotRel.inverse) {
					return feature as YAnnotRel
				}
			}
		}
		return null
	}

	protected def void doRelationship(Hlent sourceEntity, Hlent destinationEntity, YAnnotRel relationshipFrom,
		YAnnotRel relationshipTo, YAnnotEntity targetEntity) {
		model.beginUnitOfWork
		val fromRelmm = MMObj.newInstance(model, ObjTypeCode.RELMM) as Relmm
		val toRelmm = MMObj.newInstance(model, ObjTypeCode.RELMM) as Relmm
		fromRelmm.associateInvers(toRelmm)
		sourceEntity.associateDscbyr(fromRelmm)
		destinationEntity.associateDscbyr(toRelmm)
		fromRelmm.name = relationshipFrom.name.createRelationshipName
		toRelmm.name = relationshipFrom.inverse.name.createRelationshipName

		setRelationshipCardinalityOptionality(relationshipFrom, fromRelmm)
		setRelationshipCardinalityOptionality(relationshipTo, toRelmm)
		model.commitUnitOfWork
	}

	protected def void setRelationshipCardinalityOptionality(YAnnotRel relationshipTo, Relmm toRelmm) {
		if ((relationshipTo.many && relationshipTo.optional ) || (relationshipTo.many && !relationshipTo.optional)) {
			toRelmm.card = 'M'
		} else {
			toRelmm.card = '1'
		}
		// set optionality of the relationship
		if ((relationshipTo.many && !relationshipTo.optional) || (!relationshipTo.many && !relationshipTo.optional)) {
			toRelmm.opt = 'M'
		} else {
			toRelmm.opt = 'O'
		}
	}

	def static private updateRelmmUsingAnnotation(Relmm relmm) {
		relmm.pctopt = 0
		relmm.transf = 'N'
		relmm.expmin = 0
		relmm.expavg = 0
		relmm.expmax = 0
		relmm.modorref = ' '
		relmm.cascade = 'C'
		relmm.modorref = 'M'
		relmm.transf = 'Y'
		relmm.cascade = 'D'
		return relmm
	}

	def private Hlent doEntity(YAnnotEntity entity, String name, String tdName, String desc) {
		model.beginUnitOfWork
		val hlent = MMObj.newInstance(model, ObjTypeCode.HLENT) as Hlent
		model.pcroot.followHassubj.associateDetlby(hlent)
		hlent.name = name
		hlent.dsdname = tdName
		if (desc !== null) {
			hlent.desc = desc
		}
		model.commitUnitOfWork
		return hlent
	}

	/*
	 * Create attribute and associate with the entity.
	 */
	def private void doAttribute(Enty enty, YAnnotAttr annotAttr) {
		switch annotAttr.yclass.name {
			case "String": {
				doAttributeText(enty, annotAttr)
			}
			case "Int": {
				doAttributeNumber(enty, annotAttr)
			}
			case "Short": {
				doAttributeNumber(enty, annotAttr)
			}
			case "Double": {
				doAttributeNumber(enty, annotAttr)
			}
			case "Date": {
				doAttributeDate(enty, annotAttr)
			}
			case "Time": {
				doAttributeTime(enty, annotAttr)
			}
			case "Timestamp": {
				doAttributeTimestamp(enty, annotAttr)
			}
			case "Blob": {
				doAttributeBlob(enty, annotAttr)
			}
			default: {
			}
		}
	}

	/*
	 * Set properties for the text type of attribute.
	 */
	def private doAttributeText(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'T'
		attrusr.casesens = 'Y'
		attrusr.varlen = 'Y'
		attrusr.len = 20
		model.commitUnitOfWork
	}

	/*
	 * Set common for all attribute types  properties
	 */
	protected def void createCommonAttributeProperties(Attrusr attrusr, YAnnotAttr annotAttr) {
		attrusr.desc = extractMultilineCommentIfAny(annotAttr).convertCommentIntoDescription
		attrusr.name = annotAttr.name.createAttributeName
		attrusr.dsdname = annotAttr.name.createTDName
		if (annotAttr.optional == "?") {
			attrusr.opt = 'O'
		} else {
			attrusr.opt = 'M'
		}
	}

	/*
	 * Set properties for the number type of attribute.
	 */
	def private doAttributeNumber(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'N'
		attrusr.len = 9
		attrusr.decplc = 0
		model.commitUnitOfWork
	}

	/*
	 * Set properties for the blob type of attribute.
	 */
	def private doAttributeBlob(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'B'
		attrusr.len = 20
		model.commitUnitOfWork
	}

	/*
	 * Set properties for the date type of attribute.
	 */
	def private doAttributeDate(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'D'
		attrusr.srctype = 'N'
		attrusr.len = 8
		model.commitUnitOfWork
	}

	/*
	 * Set properties for the time type of attribute.
	 */
	def private doAttributeTime(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'M'
		attrusr.srctype = 'N'
		attrusr.len = 6
		model.commitUnitOfWork
	}

	/*
	 * Set properties for the timestamp type of attribute.
	 */
	def private doAttributeTimestamp(Enty enty, YAnnotAttr annotAttr) {
		model.beginUnitOfWork
		val attrusr = MMObj.newInstance(model, ObjTypeCode.ATTRUSR) as Attrusr
		enty.associateDscbya(attrusr)
		createCommonAttributeProperties(attrusr, annotAttr)
		attrusr.doman = 'Q'
		attrusr.srctype = 'N'
		attrusr.len = 20
		model.commitUnitOfWork
	}

	/*
	 * Check if entity already defined in the model
	 */
	def private static boolean isEntityDefined(String name) {
		if (model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, name) !== null) {
			return true
		}
		return false
	}

}
