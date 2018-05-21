package eu.jgen.notes.dmw.lite.capsule.proc.test

import eu.jgen.notes.dmw.lite.utility.LangLib
import com.ca.gen.jmmi.EncyManager
import com.ca.gen.jmmi.MMObj
import com.ca.gen.jmmi.ModelManager
import com.ca.gen.jmmi.objs.Hlent
import com.ca.gen.jmmi.schema.ObjTypeCode
import com.ca.gen.jmmi.schema.PrpTypeCode
import com.google.inject.Inject
import eu.jgen.notes.dmw.lite.lang.YWidget
import eu.jgen.notes.dmw.lite.tests.LangInjectorProvider
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.eclipse.emf.ecore.resource.ResourceSet
import com.google.inject.Provider
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenDataModelConverter

@RunWith(XtextRunner)
@InjectWith(LangInjectorProvider)
class LangGenDataModelConverterTests {
	
	val static MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\"

	@Inject LangGenDataModelConverter converter
	@Inject extension ParseHelper<YWidget>
	@Inject extension ValidationTestHelper
	@Inject extension LangLib
	@Inject Provider<ResourceSet> rsp
       
	@Test
	def void testEntity01() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impdm01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var hlent = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_ONE")) as Hlent
		var array = newArrayList()
		array.add(hlent)
		var text = converter.convertEntityTypeSingleFile(array, "eu.jgen.notes.samples\n")
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	@Test
	def void testEntity02() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impdm01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var hlent = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_TWO")) as Hlent
		var array = newArrayList()
		array.add(hlent)
		var text = converter.convertEntityTypeSingleFile(array, "eu.jgen.notes.samples\n")
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	@Test
	def void testEntity03() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impdm01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var hlent = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_THREE")) as Hlent
		var array = newArrayList()
		array.add(hlent)
		var text = converter.convertEntityTypeSingleFile(array, "eu.jgen.notes.samples\n")
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	@Test
	def void testEntity04() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impdm01.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var hlent = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_FOUR")) as Hlent
		var array = newArrayList()
		array.add(hlent)
		var text = converter.convertEntityTypeSingleFile(array, "eu.jgen.notes.samples\n")
     	println(text)
		val dmw = text.loadLibAndParse
		dmw.assertNoErrors
	}
	
	@Test
	def void testEntity05() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "impdm02.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));
		var hlent1 = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "HOME_ADDRESS")) as Hlent
		var hlent2 = MMObj.getInstance(model,
			model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "PERSON")) as Hlent	
		var array = newArrayList()
		array.add(hlent1)
		array.add(hlent2)
		var text = converter.convertEntityTypeSingleFile(array, "eu.jgen.notes.samples\n")
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
