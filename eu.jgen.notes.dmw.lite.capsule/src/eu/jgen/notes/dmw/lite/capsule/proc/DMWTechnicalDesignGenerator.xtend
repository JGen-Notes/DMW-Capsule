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

import com.ca.gen.jmmi.schema.PrpTypeCode
import com.ca.gen.jmmi.objs.Hlent
import com.ca.gen.jmmi.objs.Attrusr
import com.ca.gen.jmmi.objs.Relmm
import com.ca.gen.jmmi.objs.Ident
import com.ca.gen.jmmi.objs.Hlentds
import java.util.Vector
import eu.jgen.notes.annot.desc.processor.AnnotationObject
import java.util.Set
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment
import com.ca.gen.jmmi.objs.Subj
import java.io.File
import eu.jgen.notes.annot.desc.processor.DiagnosticKind
import org.eclipse.xtext.xbase.annotations.xAnnotations.XAnnotation
import org.eclipse.xtext.xbase.XStringLiteral
import com.ca.gen.jmmi.objs.Recdata
import com.ca.gen.jmmi.objs.Reciefj
import com.ca.gen.jmmi.objs.Fielddat

class DMWTechnicalDesignGenerator {

	public static def void generate(Set<AnnotationObject> selection, ProcessingEnvironment processingEnv,
		String srcDirPath) {
				val packageName = "td"
				val path = createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"))
				val writer = processingEnv.getFiler().openWriter(path + "\\" + "technical_design" + ".dmw");
						writer.write(generateTechnicalDesign(selection, packageName))
						writer.close()
						processingEnv.getMessager().printMessage(DiagnosticKind.INFO,
							"Creating technical design. ");
	}
	
	def static String generateTechnicalDesign(Set<AnnotationObject> selection, String name) {
		'''
			PACKAGE �name�
			TECHNICAL DESIGN {
				�FOR reciefj : selection.filter[genObject instanceof Reciefj]��generateTable(reciefj.genObject as Reciefj)��ENDFOR�
			}
		'''
	}    

	def static String generateTable(Reciefj reciefj) {
		'''
			TABLE �reciefj.name.toLowerCase� IMPLEMENTS �reciefj.followExtfrom.followDefines.head.followImplmnts.name.toLowerCase� {
				�FOR fielddat : reciefj.followExtfrom.followContains��generateColumn(fielddat,reciefj.followExtfrom.followDefines.head.followImplmnts.name.toLowerCase)��ENDFOR�
				�generatePrimaryKey(reciefj)�
			}
		'''
	}

	def static String generateColumn(Fielddat fielddat, String entityName) {
		'''
			COLUMN �fielddat.name.toLowerCase� IMPLEMENTS �entityName�.�fielddat.followImplmnts.name.toLowerCase� AS �generateColumnType(fielddat)� �IF fielddat.followImplmnts.opt.toString == "M"�NOT NULL�ENDIF�
		'''
	}
	
		def static String generateColumnType(Fielddat fielddat) {
		 switch (fielddat.format.toString) {
		 	case "X" : {
		 		return '''CHAR  LENGTH �fielddat.length�'''
		 	}
		 	case 'D': {
		 		return '''DATE'''
		 	}
		 	case 'P': {
		 		return '''DECIMAL'''
		 	}
		 	case 'I': {
		 		return '''INTEGER'''
		 	}
		 	case 'S': {
		 		return '''SMALL'''
		 	}
		 	case 'T': {
		 		return '''TIME'''
		 	}
		 	case 'Q': {
		 		return '''TIMESTAMP'''
		 	}
		 	case 'V': {
		 		return '''VARCHAR LENGTH �fielddat.length�'''
		 	}
		 	case 'U': {
		 		return '''VARCHAR'''
		 	}
		 	case 'G': {
		 		return '''CHAR'''
		 	}
		 	case 'B': {
		 		return '''BLOB'''
		 	}
		 	default: {
		 		return '''?''' + fielddat.format
		 	}
		 }
	}

	def static String generatePrimaryKey(Reciefj reciefj) {
		'''
			PRIMARY KEY (�FOR fldepud : reciefj.followExtfrom.followRefrdby.head.followUsedby.followDefndby SEPARATOR ","��fldepud.followUsageof.name.toLowerCase��ENDFOR�)
		'''
	}

	def static String findPackageName(XAnnotation annotation) {
		for (pair : annotation.elementValuePairs) {
			if (pair.element.simpleName == "packageBase") {
				return (pair.value as XStringLiteral).value
			}
		}
		return "eu.jgen.notes.dmw.sample"
	}

	def static String createPackageFileStructure(ProcessingEnvironment processingEnv, String srcDirPath,
		String packagepath) {
		val path = srcDirPath + "\\" + packagepath
		val packDir = new File(path);
		if (!packDir.exists()) {
			packDir.mkdirs();
			processingEnv.getMessager().printMessage(DiagnosticKind.INFO,
				"Creating  package sub-directories for definitions: " + path);
		}
		return path;
	}

	private def static createPackageStatement(String packageName) {
		return "PACKAGE " + packageName + "\n"
	}

}
