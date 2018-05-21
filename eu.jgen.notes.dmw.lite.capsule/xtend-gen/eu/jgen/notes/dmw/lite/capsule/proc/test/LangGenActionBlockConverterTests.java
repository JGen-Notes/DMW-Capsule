package eu.jgen.notes.dmw.lite.capsule.proc.test;

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.EncyManager;
import com.ca.gen.jmmi.MMObj;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ModelManager;
import com.ca.gen.jmmi.objs.Acblkbsd;
import com.ca.gen.jmmi.objs.Hlvdf;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.jgen.notes.dmw.lite.capsule.ConversionUtil;
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenActionBlockConverter;
import eu.jgen.notes.dmw.lite.lang.YWidget;
import eu.jgen.notes.dmw.lite.tests.LangInjectorProvider;
import eu.jgen.notes.dmw.lite.utility.LangLib;
import java.util.ArrayList;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(LangInjectorProvider.class)
@SuppressWarnings("all")
public class LangGenActionBlockConverterTests {
  private final static String MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\";
  
  @Inject
  private LangGenActionBlockConverter converter;
  
  @Inject
  @Extension
  private ParseHelper<YWidget> _parseHelper;
  
  @Inject
  @Extension
  private ValidationTestHelper _validationTestHelper;
  
  @Inject
  @Extension
  private LangLib _langLib;
  
  @Inject
  private Provider<ResourceSet> rsp;
  
  @Inject
  @Extension
  private ConversionUtil _conversionUtil;
  
  @Test
  public void testConvertExitState01() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenActionBlockConverterTests.MODELS_PATH + "impwd01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON"));
      Acblkbsd acblkbsd = ((Acblkbsd) _instance);
      ArrayList<Acblkbsd> array = CollectionLiterals.<Acblkbsd>newArrayList();
      array.add(acblkbsd);
      String text = this.converter.convertExitStatesSingleFile(array, "eu.jgen.notes.samples");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testConvertCommand01() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenActionBlockConverterTests.MODELS_PATH + "impwd01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON"));
      Acblkbsd acblkbsd = ((Acblkbsd) _instance);
      ArrayList<Acblkbsd> array = CollectionLiterals.<Acblkbsd>newArrayList();
      array.add(acblkbsd);
      String text = this.converter.convertCommandsSingleFile(array, "eu.jgen.notes.samples");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testListOfViews() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenActionBlockConverterTests.MODELS_PATH + "impwd02.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "SOME_ENTITY_LOGIC"));
      Acblkbsd acblkbsd = ((Acblkbsd) _instance);
      ArrayList<Acblkbsd> array = CollectionLiterals.<Acblkbsd>newArrayList();
      array.add(acblkbsd);
      this.converter.buildListOfViews(acblkbsd);
      final Consumer<Hlvdf> _function = (Hlvdf a) -> {
        String _convertViewNameAsProperty = this._conversionUtil.convertViewNameAsProperty(a);
        String _plus = (_convertViewNameAsProperty + " : ");
        String _convertViewNameAsClass = this._conversionUtil.convertViewNameAsClass(a);
        String _plus_1 = (_plus + _convertViewNameAsClass);
        InputOutput.<String>println(_plus_1);
      };
      this.converter.hlvdfList.forEach(_function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testConvertWidget01() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenActionBlockConverterTests.MODELS_PATH + "impwd01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "PROCESS_PERSON"));
      Acblkbsd acblkbsd = ((Acblkbsd) _instance);
      ArrayList<Acblkbsd> array = CollectionLiterals.<Acblkbsd>newArrayList();
      array.add(acblkbsd);
      String text = this.converter.convertWidgetSingleFile(acblkbsd, "eu.jgen.notes.samples");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private YWidget loadLibAndParse(final CharSequence p) {
    try {
      YWidget _xblockexpression = null;
      {
        final ResourceSet resourceSet = this.rsp.get();
        this._langLib.loadLib(resourceSet);
        _xblockexpression = this._parseHelper.parse(p, resourceSet);
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
