package eu.jgen.notes.dmw.lite.capsule.proc.test;

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.EncyManager;
import com.ca.gen.jmmi.MMObj;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ModelManager;
import com.ca.gen.jmmi.objs.Hlent;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenDataModelConverter;
import eu.jgen.notes.dmw.lite.lang.YWidget;
import eu.jgen.notes.dmw.lite.tests.LangInjectorProvider;
import eu.jgen.notes.dmw.lite.utility.LangLib;
import java.util.ArrayList;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.testing.validation.ValidationTestHelper;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(XtextRunner.class)
@InjectWith(LangInjectorProvider.class)
@SuppressWarnings("all")
public class LangGenDataModelConverterTests {
  private final static String MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\";
  
  @Inject
  private LangGenDataModelConverter converter;
  
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
  
  @Test
  public void testEntity01() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenDataModelConverterTests.MODELS_PATH + "impdm01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_ONE"));
      Hlent hlent = ((Hlent) _instance);
      ArrayList<Hlent> array = CollectionLiterals.<Hlent>newArrayList();
      array.add(hlent);
      final ArrayList<Hlent> _converted_array = (ArrayList<Hlent>)array;
      String text = this.converter.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(_converted_array, Hlent.class)), "eu.jgen.notes.samples\n");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testEntity02() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenDataModelConverterTests.MODELS_PATH + "impdm01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_TWO"));
      Hlent hlent = ((Hlent) _instance);
      ArrayList<Hlent> array = CollectionLiterals.<Hlent>newArrayList();
      array.add(hlent);
      final ArrayList<Hlent> _converted_array = (ArrayList<Hlent>)array;
      String text = this.converter.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(_converted_array, Hlent.class)), "eu.jgen.notes.samples\n");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testEntity03() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenDataModelConverterTests.MODELS_PATH + "impdm01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_THREE"));
      Hlent hlent = ((Hlent) _instance);
      ArrayList<Hlent> array = CollectionLiterals.<Hlent>newArrayList();
      array.add(hlent);
      final ArrayList<Hlent> _converted_array = (ArrayList<Hlent>)array;
      String text = this.converter.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(_converted_array, Hlent.class)), "eu.jgen.notes.samples\n");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testEntity04() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenDataModelConverterTests.MODELS_PATH + "impdm01.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "ENTITY_TYPE_FOUR"));
      Hlent hlent = ((Hlent) _instance);
      ArrayList<Hlent> array = CollectionLiterals.<Hlent>newArrayList();
      array.add(hlent);
      final ArrayList<Hlent> _converted_array = (ArrayList<Hlent>)array;
      String text = this.converter.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(_converted_array, Hlent.class)), "eu.jgen.notes.samples\n");
      InputOutput.<String>println(text);
      final YWidget dmw = this.loadLibAndParse(text);
      this._validationTestHelper.assertNoErrors(dmw);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Test
  public void testEntity05() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenDataModelConverterTests.MODELS_PATH + "impdm02.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "HOME_ADDRESS"));
      Hlent hlent1 = ((Hlent) _instance);
      MMObj _instance_1 = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "PERSON"));
      Hlent hlent2 = ((Hlent) _instance_1);
      ArrayList<Hlent> array = CollectionLiterals.<Hlent>newArrayList();
      array.add(hlent1);
      array.add(hlent2);
      final ArrayList<Hlent> _converted_array = (ArrayList<Hlent>)array;
      String text = this.converter.convertEntityTypeSingleFile(((Hlent[])Conversions.unwrapArray(_converted_array, Hlent.class)), "eu.jgen.notes.samples\n");
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
