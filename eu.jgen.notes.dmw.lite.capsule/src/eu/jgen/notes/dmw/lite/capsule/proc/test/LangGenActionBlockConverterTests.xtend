package eu.jgen.notes.dmw.lite.capsule.proc.test

import com.ca.gen.jmmi.EncyManager
import com.ca.gen.jmmi.MMObj
import com.ca.gen.jmmi.ModelManager
import com.ca.gen.jmmi.objs.Acblkbsd
import com.ca.gen.jmmi.schema.ObjTypeCode
import com.ca.gen.jmmi.schema.PrpTypeCode
import com.google.inject.Inject
import com.google.inject.Provider
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenActionBlockConverter
import eu.jgen.notes.dmw.lite.lang.YWidget
import eu.jgen.notes.dmw.lite.tests.LangInjectorProvider
import eu.jgen.notes.dmw.lite.utility.LangLib
import java.util.ArrayList
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil

@RunWith(XtextRunner)
@InjectWith(LangInjectorProvider)
class LangGenActionBlockConverterTests {
	
	val static MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\"
	
	@Inject LangGenActionBlockConverter converter
	@Inject extension ParseHelper<YWidget>
	@Inject extension ValidationTestHelper
	@Inject extension LangLib
	@Inject Provider<ResourceSet> rsp   
	@Inject extension ConversionUtil    
	
	@Test
	def void testConvertExitState01() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impwd01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var acblkbsd = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON")) as Acblkbsd	
		var ArrayList<Acblkbsd> array = newArrayList()
		array.add(acblkbsd)
		var text = converter.convertExitStatesSingleFile(array, "eu.jgen.notes.samples") 
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	//TODO need more checks on exit states
	
	@Test
	def void testConvertCommand01() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impwd01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var acblkbsd = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON")) as Acblkbsd	
		var ArrayList<Acblkbsd> array = newArrayList()
		array.add(acblkbsd)
		var text = converter.convertCommandsSingleFile(array, "eu.jgen.notes.samples") 
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	//TODO need more checks on commands
	
	@Test
	def void testListOfViews() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impwd02.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
//		var hlent1 = MMObj.getInstance(model,
//			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "PERSON")) as Hlent
		var acblkbsd = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "SOME_ENTITY_LOGIC")) as Acblkbsd	
		var ArrayList<Acblkbsd> array = newArrayList()
//		array.add(hlent1)
		array.add(acblkbsd)
		converter.buildListOfViews(acblkbsd) 
		converter.hlvdfList.forEach[a |
			println(a.convertViewNameAsProperty + " : " + a.convertViewNameAsClass)
		]
     	
//		val dmw = text.loadLibAndParse
//		dmw.assertNoErrors
	}
	
		@Test
	def void testConvertWidget01() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impwd01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
//		var hlent1 = MMObj.getInstance(model,
//			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "PERSON")) as Hlent
		var acblkbsd = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON")) as Acblkbsd	
		var ArrayList<Acblkbsd> array = newArrayList()
//		array.add(hlent1)
		array.add(acblkbsd)
		var text = converter.convertWidgetSingleFile(acblkbsd, "eu.jgen.notes.samples") 
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	def private loadLibAndParse(CharSequence p) {
		val resourceSet = rsp.get
		loadLib(resourceSet)
		p.parse(resourceSet)  
	}

}
