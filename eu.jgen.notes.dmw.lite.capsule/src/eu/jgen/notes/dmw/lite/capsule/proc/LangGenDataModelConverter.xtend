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

import com.ca.gen.jmmi.objs.Attrusr
import com.ca.gen.jmmi.objs.Hlent
import com.ca.gen.jmmi.objs.Ident
import com.ca.gen.jmmi.objs.Relmm
import com.ca.gen.jmmi.objs.Subj
import com.ca.gen.jmmi.schema.PrpTypeCode
import com.google.inject.Inject
import eu.jgen.notes.annot.desc.processor.AnnotationObject
import eu.jgen.notes.annot.desc.processor.DiagnosticKind
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil
import java.io.File
import java.util.Set
import java.util.Vector

/**
 *  Data Model Artifacts Generator
 */
class LangGenDataModelConverter {

	@Inject extension ConversionUtil

	public def void generate(Set<AnnotationObject> selection, ProcessingEnvironment processingEnv, String srcDirPath, String packageName) {

		selection.forEach [ annotationObject |
			if (annotationObject.genObject instanceof Subj) {
				val path = createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"))
				val subj = annotationObject.genObject as Subj
				subj.followDetlby.forEach [ element |
					if (element instanceof Hlent) {
						val hlent = element as Hlent
						val writer = processingEnv.getFiler().openWriter(path + "\\" + hlent.name.convertEntityName + ".dmw");
						val text = convertEntityTypeSingleFile(newArrayList(hlent), packageName)
						writer.write(text)
						writer.close()
						processingEnv.getMessager().printMessage(DiagnosticKind.INFO,
							"Creating entity type definition for " + hlent.name);
					}
				]
			}
		]
	}

	 private def String createPackageFileStructure(ProcessingEnvironment processingEnv, String srcDirPath, String packagepath) {
		val path = srcDirPath + "\\" + packagepath
		val packDir = new File(path);
		if (!packDir.exists()) {
			packDir.mkdirs();
			processingEnv.getMessager().printMessage(DiagnosticKind.INFO,
				"Creating  package sub-directories for definitions: " + path);
		}
		return path;
	}

	public def String convertEntityTypeSingleFile(Hlent[] list, String packageName) {
		return createPackageStatement(packageName) + doConvertEntityType(list)
	}

	public def String doConvertEntityType(Hlent[] list) {
		val body = new StringBuffer()
		for (hlent : list) {
		
			body.append(convertEntityType(hlent))
		}
		body.toString
	}

	/*
	 * Generate entity type declaration
	 */
	public def String convertEntityType(Hlent hlent) {
		val body = '''
				
			«importDescription(hlent.getTextProperty(PrpTypeCode.DESC))»
			@entity «hlent.name.convertEntityName» {  
						
				«FOR attr : hlent.followDscbya»
					«importDescription(attr.getTextProperty(PrpTypeCode.DESC))» 
					«importAttribute(attr as Attrusr)»
				«ENDFOR»	
				«FOR relmm : hlent.followDscbyr»
					«importRelationship(relmm)»
				«ENDFOR»
				
				«FOR ident : hlent.followIdntby»
					«importIdentifier(ident)»
				«ENDFOR»								
			}
						 
		'''
		return body
	}

	private def createPackageStatement(String packageName) {
		return "package " + packageName.trim + ";"
	}

	/*
	 * Generate comment using DESC associated with the Gen object.
	 */
	private def String importDescription(String originalText) {
		if (originalText.trim == "") {
			return ""
		}
		val array = originalText.split("\n")
		'''
			/*
			 «FOR line : array»
			 	* « line»
			 «ENDFOR»	
			 */
		'''
	}

	/*  
	 * Generate attribute for entity types and worksets 
	 */
	private def String importAttribute(Attrusr attrusr) {
		'''	
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'T' »
				@attr «attrusr.name.convertAttributeName»: «getTextImpl(attrusr)»«makeOptionality(attrusr)» «makeYTextAnnotation(attrusr)»; 
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'N' »	 
				@attr «attrusr.name.convertAttributeName»: «getNumberImpl(attrusr)»«makeOptionality(attrusr)» «makeYNumberAnnotation(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'D' »		
				@attr «attrusr.name.convertAttributeName»: Date«makeOptionality(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'M' »
				@attr «attrusr.name.convertAttributeName»: Time«makeOptionality(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'Q' »								 		
				@attr «attrusr.name.convertAttributeName»: Timestamp«makeOptionality(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'Z' »	 
				@attr «attrusr.name.convertAttributeName»: «getTextImpl(attrusr)»«makeOptionality(attrusr)» «makeYTextAnnotation(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'G' »	 
				@attr «attrusr.name.convertAttributeName»: «getTextImpl(attrusr)»«makeOptionality(attrusr)» «makeYTextAnnotation(attrusr)»;
			«ENDIF»
			«IF attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'B' » 				
				@attr «attrusr.name.convertAttributeName»: Blob«makeOptionality(attrusr)» «makeYTextAnnotation(attrusr)»;
			«ENDIF»			
		'''
	}

	private def getNumberImpl(Attrusr attrusr) {
		if (attrusr.decplc == 0) {
			if (attrusr.len <= 4) {
				return "Short"
			} else if (attrusr.len > 4 && attrusr.len <= 9) {
				return "Int"
			} else {
				return "Double"
			}
		} else {
			return "Double"
		}
	}

	private def getTextImpl(Attrusr attrusr) {
		// VARLEN
		var varyingLength = "String"
		if (attrusr.varlen == "N".charAt(0)) {
			varyingLength = "String"
		} else if (attrusr.opt == "Y".charAt(0)) {
			varyingLength = "String"
		}
		return varyingLength
	}

	private def String makeYNumberAnnotation(Attrusr attrusr) {
		if (attrusr.decplc != 0) {
			return "@decimal(" + attrusr.len + "," + attrusr.decplc + ")"
		} else {
			return "@length(" + attrusr.len + ")"
		}

	}

	private def String makeOptionality(Attrusr attrusr) {
		var optional = "?"
		if (attrusr.opt == "M".charAt(0)) {
			optional = ""
		} else if (attrusr.opt == "O".charAt(0)) {
			optional = "?"
		}
		return optional
	}

	/*
	 * Generate @YText annotation
	 */
	private def String makeYTextAnnotation(Attrusr attrusr) {
		return "@length(" + attrusr.len + ")"
	}

	/*
	 * Generate @YSource annotation
	 */
	// TODO Some more work on relationship required. (transferable,deletionRule)
	private def String makeYRelationshipAnnotation(Relmm relmm) {
		// MODORREF
		var type = "associationType=AssociateTypes.Default"
		if (relmm.modorref == "M".charAt(0)) {
			type = "associationType=AssociateTypes.Modifying"
		} else if (relmm.modorref == "R".charAt(0)) {
			type = "associationType=AssociateTypes.Referencing"
		}

		// TRANSF
		var transferable = "transferable=false"
		if (relmm.transf == "Y".charAt(0)) {
			transferable = "transferable=true"
		} else if (relmm.transf == "N".charAt(0)) {
			transferable = "transferable=false"
		}

		// CASCADE 
		var deletionRule = "deletionRule=DeletionRules.Default"
		if (relmm.cascade == "D".charAt(0)) {
			deletionRule = "deletionRule=DeletionRules.DeleteEach"
		} else if (relmm.cascade == "N".charAt(0)) {
			deletionRule = "deletionRule=DeletionRules.Disassociate"
		} else if (relmm.cascade == "R".charAt(0)) {
			deletionRule = "deletionRule=DeletionRules.Disallow"
		} else if (relmm.cascade == "C".charAt(0)) {
			deletionRule = "deletionRule=DeletionRules.Default"
		}

		// PCTOPT 
		val percentage = '''percentage=«relmm.pctopt»'''

		// EXPMIN
		val atLeast = '''atLeast=«relmm.expmin»'''

		// EXPAVG 
		val onAverage = '''onAverage=«relmm.expavg»'''

		// (EXPMAX 
		val atMost = '''atMost=«relmm.expmax»'''

		return "@YRelationship (" + transferable + ", " + type + ", " + deletionRule + ", " + percentage + ", " +
			atLeast + ', ' + onAverage + ", " + atMost + ")"
	}

	private def String importRelationship(Relmm relmm) {
		'''
			«importDescription(relmm.getTextProperty(PrpTypeCode.DESC))»
			@rel «relmm.name.converRelationshipName» -> «relmm.followInvers.followDscpr.name.convertEntityName»«IF relmm.getTextProperty(PrpTypeCode.CARD) == "M"»*«ENDIF» <- «relmm.followInvers.followDscpr.name.convertEntityName».«relmm.followInvers.name.converRelationshipName»;
		'''
	}

	private def String importIdentifier(Ident ident) {
		var name = "id"
		if (ident.name !== null && ident.name.length > 0) {
			name = ident.name.convertIdentifierName
		}
		val list = new Vector<String>
		for (attr : ident.followCntnsa) {
			list.add(attr.name.convertIdentifierName)
		}
		for (rel : ident.followCntnsr) {
			list.add(rel.name.convertIdentifierName)
		}
		'''
			@id «name.convertIdentifierName»«FOR text : list BEFORE '(' SEPARATOR ',' AFTER ')'»«text»«ENDFOR»;
		'''
	}

}
