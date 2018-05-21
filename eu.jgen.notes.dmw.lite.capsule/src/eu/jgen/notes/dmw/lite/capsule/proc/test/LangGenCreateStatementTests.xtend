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
import com.ca.gen.jmmi.objs.Hlent
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenDataModelConverter

import org.eclipse.xtext.testing.formatter.FormatterTestHelper

@RunWith(XtextRunner)
@InjectWith(LangInjectorProvider)
class LangGenCreateStatementTests {

	val static MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\"

	@Inject LangGenActionBlockConverter abConverter
	@Inject LangGenDataModelConverter dmConverter
	@Inject extension ParseHelper<YWidget>
	@Inject extension ValidationTestHelper
	@Inject extension LangLib
	@Inject Provider<ResourceSet> rsp

	@Test
	def void testConvertCreateStatement01() {
		var ency = EncyManager.connectLocalForReadOnly(MODELS_PATH + "IMPWD03.ief");
		var model = ModelManager.open(ency, ency.getModelIds().get(0));

		// Create data model.
		val ArrayList<Hlent> list1 = newArrayList()
		list1.add(
			MMObj.getInstance(model,
				model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "SOME_ENTITY_TYPE")) as Hlent);
			val text1 = dmConverter.doConvertEntityType(list1)
			val acblkbsd = MMObj.getInstance(model,
				model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "CREATE_SOME_ENTITY_TYPE")) as Acblkbsd
			val ArrayList<Acblkbsd> list2 = newArrayList()
			list2.add(acblkbsd)
			val text2 = abConverter.doConvertExitStates(list2)
			val text3 = abConverter.doConvertCommands(list2)
			val text4 = abConverter.doConvertWidget(acblkbsd)
			val text = text1 + text2 + text3 + text4
			println(text)
			var mdl = text.loadLibAndParse
			mdl.validate.forEach[println(it)]
			model.close
			ency.disconnect
		}

		def private loadLibAndParse(CharSequence p) {
			val resourceSet = rsp.get
			loadLib(resourceSet)
			p.parse(resourceSet)
		}

	}
	