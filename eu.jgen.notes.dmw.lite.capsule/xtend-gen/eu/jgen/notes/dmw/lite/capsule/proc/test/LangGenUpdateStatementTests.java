package eu.jgen.notes.dmw.lite.capsule.proc.test;

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.EncyManager;
import com.ca.gen.jmmi.MMObj;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ModelManager;
import com.ca.gen.jmmi.objs.Acblkbsd;
import com.ca.gen.jmmi.objs.Hlent;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenActionBlockConverter;
import eu.jgen.notes.dmw.lite.capsule.proc.LangGenDataModelConverter;
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
import org.eclipse.xtext.validation.Issue;
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
public class LangGenUpdateStatementTests {
  private final static String MODELS_PATH = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\";
  
  @Inject
  private LangGenActionBlockConverter abConverter;
  
  @Inject
  private LangGenDataModelConverter dmConverter;
  
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
  public void testConvertCreateStatement01() {
    try {
      Ency ency = EncyManager.connectLocalForReadOnly((LangGenUpdateStatementTests.MODELS_PATH + "IMPWD03.ief"));
      Model model = ModelManager.open(ency, ency.getModelIds().get(0));
      final ArrayList<Hlent> list1 = CollectionLiterals.<Hlent>newArrayList();
      MMObj _instance = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.HLENT, PrpTypeCode.NAME, "SOME_ENTITY_TYPE"));
      list1.add(
        ((Hlent) _instance));
      final String text1 = this.dmConverter.doConvertEntityType(((Hlent[])Conversions.unwrapArray(list1, Hlent.class)));
      MMObj _instance_1 = MMObj.getInstance(model, 
        model.getObjIdByName(ObjTypeCode.ACBLKBSD, PrpTypeCode.NAME, "UPDATE_SOME_ENTITY_TYPE"));
      final Acblkbsd acblkbsd = ((Acblkbsd) _instance_1);
      final ArrayList<Acblkbsd> list2 = CollectionLiterals.<Acblkbsd>newArrayList();
      list2.add(acblkbsd);
      final String text2 = this.abConverter.doConvertExitStates(list2);
      final String text3 = this.abConverter.doConvertCommands(list2);
      final String text4 = this.abConverter.doConvertWidget(acblkbsd);
      final String text = (((text1 + text2) + text3) + text4);
      InputOutput.<String>println(text);
      YWidget mdl = this.loadLibAndParse(text);
      final Consumer<Issue> _function = (Issue it) -> {
        InputOutput.<Issue>println(it);
      };
      this._validationTestHelper.validate(mdl).forEach(_function);
      model.close();
      ency.disconnect();
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
