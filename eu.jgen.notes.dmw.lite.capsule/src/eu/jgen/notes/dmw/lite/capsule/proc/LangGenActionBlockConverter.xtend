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

import com.ca.gen.jmmi.objs.Acblkbsd
import com.ca.gen.jmmi.objs.Acblkdef
import com.ca.gen.jmmi.objs.Actif
import com.ca.gen.jmmi.objs.Actmove
import com.ca.gen.jmmi.objs.Acton
import com.ca.gen.jmmi.objs.Actse
import com.ca.gen.jmmi.objs.Actsub
import com.ca.gen.jmmi.objs.Attrusr
import com.ca.gen.jmmi.objs.Command
import com.ca.gen.jmmi.objs.Entc
import com.ca.gen.jmmi.objs.Entd
import com.ca.gen.jmmi.objs.Ents
import com.ca.gen.jmmi.objs.Entu
import com.ca.gen.jmmi.objs.Entvw
import com.ca.gen.jmmi.objs.Expcmd
import com.ca.gen.jmmi.objs.Expexus
import com.ca.gen.jmmi.objs.Expnum
import com.ca.gen.jmmi.objs.Expoper
import com.ca.gen.jmmi.objs.Exppar
import com.ca.gen.jmmi.objs.Exprs
import com.ca.gen.jmmi.objs.Exptxt
import com.ca.gen.jmmi.objs.Expvus
import com.ca.gen.jmmi.objs.Exstate
import com.ca.gen.jmmi.objs.Grpvw
import com.ca.gen.jmmi.objs.Hlent
import com.ca.gen.jmmi.objs.Hlentds
import com.ca.gen.jmmi.objs.Hlvdf
import com.ca.gen.jmmi.objs.Lclcm
import com.ca.gen.jmmi.objs.Lcles
import com.ca.gen.jmmi.objs.Lclst
import com.ca.gen.jmmi.objs.Prdas
import com.ca.gen.jmmi.objs.Prdvw
import com.ca.gen.jmmi.schema.ObjTypeCode
import com.google.inject.Inject
import eu.jgen.notes.annot.desc.processor.AnnotationObject
import eu.jgen.notes.annot.desc.processor.DiagnosticKind
import eu.jgen.notes.annot.desc.processor.ProcessingEnvironment
import eu.jgen.notes.dmw.lite.capsule.ConversionProblem
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil
import java.io.File
import java.util.List
import java.util.Set

/**
 * Widget Artifacts Generator
 */
class LangGenActionBlockConverter {

	@Inject extension ConversionUtil

	public List<Hlvdf> hlvdfList

	public def void generate(Set<AnnotationObject> selection, ProcessingEnvironment processingEnv, String srcDirPath, String packageName) {

		// Extract list of action blocks
		val List<Acblkbsd> actionBlockList = newArrayList()
		selection.forEach [ annotationObject |
			if (annotationObject.genObject instanceof Acblkbsd) {
				val acblkbsd = annotationObject.genObject as Acblkbsd
				actionBlockList.add(acblkbsd)
			}
		]

		// Extract exit states from the selected action blocks	
		val exitStatePath = createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"))
		val exitStateWriter = processingEnv.getFiler().openWriter(exitStatePath + "\\" + "ExitStates.dmw");
		exitStateWriter.write(convertExitStatesSingleFile(actionBlockList, packageName))
		exitStateWriter.close()
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Converting exit states used by action blocks");

		// Extract commands from the selected action blocks	
		val commandsPath = createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"))
		val commandsWriter = processingEnv.getFiler().openWriter(commandsPath + "\\" + "Commands.dmw");
		commandsWriter.write(convertCommandsSingleFile(actionBlockList, packageName))
		commandsWriter.close()
		processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Converting commands used by action blocks");
		
		selection.forEach [ annotationObject |
			if (annotationObject.genObject instanceof Acblkbsd) {
				val acblkbsd = annotationObject.genObject as Acblkbsd
			    val actionBlockPath = createPackageFileStructure(processingEnv, srcDirPath, packageName.replace(".", "\\"))
			    val actionBlockWriter = processingEnv.getFiler().openWriter(actionBlockPath + "\\" + acblkbsd.name.convertActionBlockName + ".dmw");
			    actionBlockWriter.write(convertWidgetSingleFile(acblkbsd, packageName))
			    actionBlockWriter.close
			    processingEnv.getMessager().printMessage(DiagnosticKind.INFO, "Converting action block  " + acblkbsd.name);
			}
		]
	}

	/*
	 * Converting all exit states used by the selected action blocks.
	 */
	def String convertExitStatesSingleFile(List<Acblkbsd> actions, String packageName) {
		return '''
			package «packageName»;
			 «doConvertExitStates(actions)»
		'''
	}

	def String doConvertExitStates(List<Acblkbsd> actions) {
		val List<Exstate> list = newArrayList()
		actions.forEach [ acblkbsd |
			for (expexus : acblkbsd.followUsesexst) {
				val name = expexus.followUsagofe.name
				if (!checkDuplicateExitState(list, name)) {
					list.add(expexus.followUsagofe)
				}
			}
		]
		return '''
			class ExitStates {
			«FOR exstate : list»
				public var «exstate.name.convertExitStateName» : ExitState
				   @action(«IF exstate.terminat.toString == "M"»normal «ENDIF»«IF exstate.terminat.toString == "A"»abort«ENDIF»«IF exstate.terminat.toString == "R"»rollback«ENDIF») @msgtype(«IF exstate.msgtype.toString == "N"»none«ENDIF»«IF exstate.msgtype.toString == "I"»info«ENDIF»«IF exstate.msgtype.toString == "W"»warning«ENDIF»«IF exstate.msgtype.toString == "E"»error«ENDIF») @message("«exstate.string»");
			«ENDFOR»
			}
		'''
	}

	/*
	 * Converting all commands used by the selected action blocks.
	 */
	def String convertCommandsSingleFile(List<Acblkbsd> actions, String packageName) {
		return '''
			package «packageName»;
			class Commands {
			
			«doConvertCommands(actions)»
			}
		'''
	}

	def String doConvertCommands(List<Acblkbsd> actions) {
		val List<Command> list = newArrayList()
		actions.forEach [ acblkbsd |
			for (cmd : acblkbsd.followUsescmd) {
				val name = cmd.followUsageof.name
				if (!checkDuplicateCommand(list, name)) {
					list.add(cmd.followUsageof)
				}
			}
		]
		'''
			class Commands {
			«FOR command : list»
				public var «command.name.convertCommandName» : Command;
			«ENDFOR»
			}
		'''
	}

	def String convertWidgetSingleFile(Acblkbsd acblkbsd, String packageName) {
		'''
			package «packageName»;
			import «packageName».*;
			
			«doConvertWidget(acblkbsd)»
		'''
	}

	def String doConvertWidget(Acblkbsd acblkbsd) {
		'''
			class «acblkbsd.name.convertActionBlockName» : Widget {
				
				«generateStructuresForEntities(acblkbsd)»
				
				var exits : ExitStates;
				
				var commands : Commands;
				
				public func start() {
					«processStatements(acblkbsd)»
				}
			}
		'''
	}

	private def String processStatements(Acblkbsd acblkbsd) {
		var buffer = new StringBuffer()
		for (acton : acblkbsd.followDefndby) {
			buffer.append(generateStatement(acton))
		}
		return buffer.toString
	}

	/*
	 * Check if exit state is already on the list of exit states used by the group of action blocks.
	 */
	private def boolean checkDuplicateExitState(List<Exstate> list, String name) {
		if (list.empty) {
			return false
		}
		for (exstate : list) {
			if (exstate.name == name) {
				return true
			}
		}
		return false
	}

	/*
	 * Check if command is already on the list of commands used by the group of action blocks.
	 */
	private def boolean checkDuplicateCommand(List<Command> list, String name) {
		if (list.empty) {
			return false
		}
		for (command : list) {
			if (command.name == name) {
				return true
			}
		}
		return false
	}

	/*
	 * Generate inner classes for structures derived from views on entity types
	 */
	private def String generateStructuresForEntities(Acblkdef acblkbsd) {
		buildListOfViews(acblkbsd)
		var buffer = new StringBuffer()
		for (hlvw : hlvdfList) {
			if (hlvw instanceof Entvw) {
				val entvw = hlvw as Entvw
				if (entvw.followSees instanceof Hlent) {
					buffer.append( '''
						class «hlvw.convertViewNameAsClass» : Structure -> «(entvw.followSees as Hlent).name.convertEntityName» {
							«FOR suentvw : entvw.followDtlbyp»
								«IF suentvw instanceof Prdvw »
									«generateStructurePropertyEntity(suentvw as Prdvw,entvw.followSees as Hlent)»
								«ENDIF»
							«ENDFOR»
						}
					''')
				} else if (entvw.followSees instanceof Hlentds) {
					buffer.append('''
						class «hlvw.convertViewNameAsClass» : Structure {
							«FOR suentvw : entvw.followDtlbyp»
								«IF suentvw instanceof Prdvw »
									«generateStructurePropertyWorkset(suentvw as Prdvw,entvw.followSees as Hlentds)»
								«ENDIF»
							«ENDFOR»
						}
					''')
				}
			}
		}
		for (hlvw : hlvdfList) {
			if (hlvw instanceof Entvw) {
				val entvw = hlvw as Entvw
				if (entvw.followSees instanceof Hlent) {
					buffer.append( '''						
						public var «hlvw.convertViewNameAsProperty»  : «hlvw.convertViewNameAsClass»;
						 
					''')
				} else if (entvw.followSees instanceof Hlentds) {
					buffer.append('''
						public var «hlvw.convertViewNameAsProperty»  : «hlvw.convertViewNameAsClass»;
						 
					''')
				}
			} else if (hlvw instanceof Grpvw) {
				val grpvw = hlvw as Grpvw
				buffer.append('''
					public var «hlvw.convertViewNameAsProperty» : Array «groupInclude(grpvw)»
					
				''')
			}
		}
		return buffer.toString
	}

	private def String groupInclude(Grpvw grpvw) {
		'''
			<«FOR hlvdf : grpvw.followGrpfor SEPARATOR ","»«IF hlvdf instanceof Entvw»«(hlvdf as Entvw).convertViewNameAsProperty»«ENDIF»«ENDFOR»>;
		'''
	}

	private def String generateStructurePropertyEntity(Prdvw prdvw, Hlent hlent) {
		'''
			public var «prdvw.followSees.name.convertAttributeName» : «(prdvw.followSees as Attrusr).convertAttribute» -> «hlent.name.convertEntityName».«prdvw.followSees.name.convertAttributeName»;
		'''
	}

	private def String generateStructurePropertyWorkset(Prdvw prdvw, Hlentds hlentds) {
		'''
			public var «prdvw.followSees.name.convertAttributeName» : «(prdvw.followSees as Attrusr).convertAttribute»;
		'''
	}

	/*
	 * Create list of all qualifying views
	 */
	public def void buildListOfViews(Acblkdef acblkbsd) {
		hlvdfList = newArrayList()
		acblkbsd.followGrpby.followCntinps.followContains.forEach [ hlvdf |
			hlvdfList.add(hlvdf)
			expandGroupView(hlvdf)
		]
		acblkbsd.followGrpby.followCntlcls.followContains.forEach [ hlvdf |
			hlvdfList.add(hlvdf)
			expandGroupView(hlvdf)
		]
		acblkbsd.followGrpby.followCntouts.followContains.forEach [ hlvdf |
			hlvdfList.add(hlvdf)
			expandGroupView(hlvdf)
		]
		acblkbsd.followGrpby.followCntents.followContains.forEach [ hlvdf |
			hlvdfList.add(hlvdf)
		]
	}

	/*
	 * Expand group views. Embedded views are not supported.
	 */
	private def void expandGroupView(Hlvdf hlvdf) {
		if (hlvdf instanceof Grpvw) {
			val grpvw = hlvdf as Grpvw
			grpvw.followGrpfor.forEach [ hlvdfinner |
				if (hlvdfinner instanceof Grpvw) {
					throw new ConversionProblem("DMW does not support embedded group views.")
				}
				hlvdfList.add(hlvdfinner)
			]
		}
	}

	/*
	 * Create directory structure for the package. 
	 */
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

	private def String generateStatement(Acton acton) {
		switch (acton.objTypeCode) {
			case ObjTypeCode.LCLCM: {
				return generateCommandIs(acton as Lclcm)
			}
			case ObjTypeCode.ENTC: {
				return generateCreateStatement(acton as Entc)
			}
			
			case ObjTypeCode.ENTD: {
				return generateDeleteStatement(acton as Entd)
			}
			case ObjTypeCode.ENTS: {
				return generateReadStatement(acton as Ents)
			}
			case ObjTypeCode.ENTU: {
				return generateUpdateStatement(acton as Entu)
			}
			case ObjTypeCode.ACTSE: {
				return generateReadEachStatement(acton as Actse)
			}
			case ObjTypeCode.LCLST: {
				return generateSetStatement(acton as Lclst)
			}
			case ObjTypeCode.PRDAS: {
				return generateSetStatement(acton as Prdas)
			}
			case ObjTypeCode.ACTMOVE: {
				return generateMoveStatement(acton as Actmove)
			}
			case ObjTypeCode.LCLES: {
				return generateExitStateIsStatement(acton as Lcles)
			}
			case ObjTypeCode.ACTIF: {
				return generateIfStatement(acton as Actif)
			}
			default: {
				return "nothing generated for: " + acton
			}
		}
	}

	private def String generateIfStatement(Actif actif) {
		'''
			if («generateExpression(actif.followDefndby.get(0).followSubjto.followSees.followUses)») {
				
			}
		'''
	}

	private def String generateCommandIs(Lclcm lclcm) {
		'''
			super.setCommand(self.commands.«(lclcm.followUses.get(0) as Expcmd).followUsageof.name.toLowerCase»);
		'''
	}

	private def String generateUpdateStatement(Entu entu) {
		return 		'''
			update «entu.followActson.convertViewNameAsProperty» -> «entu.followActson.name.toLowerCase»«entu.followActson.followSees.name.convertEntityName» {
			  «generateSetStatementBlock(entu.followDtlby)»
			} success {
			   «generateStatementBlock(entu.followHassuccs.followDefndby)»
			}		
		'''
	}

	private def String generateReadEachStatement(Actse actse) {
		val ents = actse.followDetlby
		'''		 
			 «FOR rdvwus : ents.followActsvia BEFORE "read each " SEPARATOR ","»
			 «(rdvwus.followActson1 as Entvw).convertViewNameAsProperty» -> «(rdvwus.followActson1 as Entvw).name.toLowerCase»«(rdvwus.followActson1 as Entvw).followSees.name.convertEntityName»
			      «ENDFOR» 
			   where «generateExpression(ents.followSubjto.followSees.followUses)»
			   target «actse.followTargets.get(0).followIsfor.convertViewNameAsProperty»
			   success {
			      «generateStatementBlock(actse.followDefndby.get(0).followDefndby)»
			   }
		'''
	}   

	private def String generateReadStatement(Ents ents) {
		'''		 
			 «FOR rdvwus : ents.followActsvia BEFORE "read " SEPARATOR ","»
			 «(rdvwus.followActson1 as Entvw).convertViewNameAsProperty» -> «(rdvwus.followActson1 as Entvw).name.toLowerCase»«(rdvwus.followActson1 as Entvw).followSees.name.convertEntityName»
			      «ENDFOR» 
			   where «generateExpression(ents.followSubjto.followSees.followUses)»
			   success {
			      «generateStatementBlock(ents.followHassuccs.followDefndby)»
			   } not found {
			      «generateStatementBlock(ents.followHasexcp.get(0).followDefndby)»
			   }
		'''
	}

	private def String generateExitStateIsStatement(Lcles lcles) {
		'''
			super.setExitState(self.exits.«(lcles.followUses.get(0) as Expexus).followUsagofe.name.convertExitStateName»);
		'''
	}

	private def String generateMoveStatement(Actmove actmove) {
		'''
			super.moveStruct(self.«actmove.followFrom.convertViewNameAsProperty»,self.«actmove.followTo.convertViewNameAsProperty»);
		'''
	}

//	private def String generateViewName(Entvw entvw) {
//		if (entvw.followSees instanceof Hlent) {
//			val ent = entvw.followSees as Hlent
//			return entvw.name.toLowerCase + "." + ent.name.toLowerCase
//		} else if (entvw.followSees instanceof Hlentds) {
//			val wrk = entvw.followSees as Hlentds
//			return entvw.name.toLowerCase + "." + wrk.name.toLowerCase
//		}
//		"cannot build view name"
//	}

	private def String generateSetStatement(Lclst lclst) {
		'''
			self.«generateSetStatementLeft(lclst)» = «generateExpression(lclst.followUses)»;
		'''
	}
	
	private def String generateSetStatement(Prdas prdas) {
		'''
			self.«generateSetStatementLeft(prdas)» = «generateExpression(prdas.followUses)»;
		'''
	}

	private def String generateSetStatementLeft(Prdas prdas) {
		if (prdas.followActson instanceof Prdvw) {
			return (prdas.followActson as Prdvw).convertPredicateView
		} else {
			return "do not know how to do it yet"
		}
	}
	
	private def String generateSetStatementLeft(Lclst lclst) {
		if (lclst.followActson instanceof Prdvw) {
			return (lclst.followActson as Prdvw).convertPredicateView
		} else {
			return "do not know how to do it yet"
		}
	}

	private def String generateExpression(List<Exprs> list) {
		val buffer = new StringBuffer()
		val expstack = list.listIterator
		while (expstack.hasNext) {
			var exprs = expstack.next
			switch (exprs.objTypeCode) {
				case ObjTypeCode.EXPNUM: {
					buffer.append((exprs as Expnum).string)
				}
				case ObjTypeCode.EXPOPER: {
					buffer.append((exprs as Expoper).convertOperationName)
				}
				case ObjTypeCode.EXPVUS: {
					buffer.append("self." + ((exprs as Expvus).followEuses as Prdvw).convertPredicateView)
				}
				case ObjTypeCode.EXPTXT: {
					buffer.append("\"" + (exprs as Exptxt).string + "\"")
				}
				case ObjTypeCode.EXPPAR: {
					buffer.append((exprs as Exppar).prnths)
				}
				case ObjTypeCode.EXPSAU: {
					exprs = expstack.next
					exprs = expstack.next
					if (exprs instanceof Expexus) {
						buffer.
							append('''super.isExitState(self.exits.«(exprs as Expexus).followUsagofe.name.convertExitStateName»)''')
					} else if (exprs instanceof Expcmd) {
						buffer.
							append('''super.isCommand(self.commands.«(exprs as Expcmd).followUsageof.name.convertCommandName»)''')
					} else {
						buffer.append("** do not know how to do it: " + exprs)
					}
				}
				default: {
					buffer.append("** do not know how to do it: " + exprs)
				}
			}
		}
		return buffer.toString
	}

	private def String generateDeleteStatement(Entd entd) {
		'''
			delete «entd.followActson.convertViewNameAsProperty» -> «entd.followActson.name.toLowerCase»«entd.followActson.followSees.name.convertEntityName»;
		'''
	}

	private def String generateCreateStatement(Entc entc) {
		'''
			create «entc.followActson.convertViewNameAsProperty» -> «entc.followActson.name.toLowerCase»«entc.followActson.followSees.name.convertEntityName» {
			  «generateSetStatementBlock(entc.followDtlby)»
			} success {
			   «generateStatementBlock(entc.followHassuccs.followDefndby)»
			} already exist {
			   «generateStatementBlock(entc.followHasexcp.get(0).followDefndby)»
			}		
		'''
	}

	private def String generateSetStatementBlock(List<Actsub> list) {
		val buffer = new StringBuffer()
		for (acton : list) {
			buffer.append(generateStatement(acton))
		}
		return buffer.toString
	}

	private def String generateStatementBlock(List<Acton> list) {
		val buffer = new StringBuffer()
		for (acton : list) {
			buffer.append(generateStatement(acton))
		}
		return buffer.toString
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
				 ï¿½FOR line : arrayï¿½
				 	* ï¿½ lineï¿½
				 ï¿½ENDFORï¿½	
				 */
			'''
		}

	}
	