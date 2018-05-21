package eu.jgen.notes.dmw.lite.capsule;

import com.ca.gen.jmmi.objs.Attrusr;
import com.ca.gen.jmmi.objs.Entvw;
import com.ca.gen.jmmi.objs.Enty;
import com.ca.gen.jmmi.objs.Expoper;
import com.ca.gen.jmmi.objs.Grpvw;
import com.ca.gen.jmmi.objs.Hlvdf;
import com.ca.gen.jmmi.objs.Prdvw;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.StringExtensions;

@SuppressWarnings("all")
public class ConversionUtil {
  public String convertPredicateView(final Prdvw prdvw) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("�prdvw.followDtlofe.convertViewNameAsProperty�.�prdvw.followSees.name.convertAttributeName�");
    return _builder.toString();
  }
  
  public String convertViewNameAsClass(final Hlvdf hlvdf) {
    String _xifexpression = null;
    if ((hlvdf instanceof Grpvw)) {
      _xifexpression = StringExtensions.toFirstUpper(this.doCamelFormat(((Grpvw)hlvdf).getName()));
    } else {
      String _firstUpper = StringExtensions.toFirstUpper(this.doCamelFormat(hlvdf.getName()));
      String _firstUpper_1 = StringExtensions.toFirstUpper(this.doCamelFormat(((Entvw) hlvdf).<Enty>followSees().getName()));
      _xifexpression = (_firstUpper + _firstUpper_1);
    }
    return _xifexpression;
  }
  
  public String convertViewNameAsProperty(final Hlvdf hlvdf) {
    String _xifexpression = null;
    if ((hlvdf instanceof Grpvw)) {
      _xifexpression = StringExtensions.toFirstLower(this.doCamelFormat(((Grpvw)hlvdf).getName()));
    } else {
      String _firstLower = StringExtensions.toFirstLower(this.doCamelFormat(hlvdf.getName()));
      String _firstUpper = StringExtensions.toFirstUpper(this.doCamelFormat(((Entvw) hlvdf).<Enty>followSees().getName()));
      _xifexpression = StringExtensions.toFirstLower((_firstLower + _firstUpper));
    }
    return _xifexpression;
  }
  
  public String convertActionBlockName(final String name) {
    return StringExtensions.toFirstUpper(this.doCamelFormat(name));
  }
  
  public String convertEntityName(final String name) {
    return StringExtensions.toFirstUpper(this.doCamelFormat(name));
  }
  
  public String convertAttributeName(final String name) {
    return StringExtensions.toFirstLower(this.doCamelFormat(name));
  }
  
  public String converRelationshipName(final String name) {
    return StringExtensions.toFirstLower(this.doCamelFormat(name));
  }
  
  public String convertIdentifierName(final String name) {
    return StringExtensions.toFirstLower(StringExtensions.toFirstUpper(this.doCamelFormat(name)));
  }
  
  public String convertExitStateName(final String name) {
    return StringExtensions.toFirstLower(this.doCamelFormat(name));
  }
  
  public String convertCommandName(final String name) {
    return name.toUpperCase();
  }
  
  public String convertOperationName(final Expoper expoper) {
    String _string = Character.valueOf(expoper.getOperatr()).toString();
    boolean _equals = Objects.equal(_string, "=");
    if (_equals) {
      return " == ";
    } else {
      char _operatr = expoper.getOperatr();
      String _plus = (" " + Character.valueOf(_operatr));
      return (_plus + " ");
    }
  }
  
  public String doCamelFormat(final String string) {
    return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
  }
  
  public String doGenFormat(final String string) {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, string);
  }
  
  public String createEntityName(final String name) {
    return this.doGenFormat(name);
  }
  
  public String createAttributeName(final String name) {
    return this.doGenFormat(name);
  }
  
  public String createRelationshipName(final String name) {
    return this.doGenFormat(name);
  }
  
  public String createTDName(final String name) {
    final String tdname = this.doGenFormat(name);
    int _length = tdname.length();
    boolean _greaterThan = (_length > 8);
    if (_greaterThan) {
      return tdname.substring(0, 7);
    }
    return tdname;
  }
  
  public String convertAttribute(final Attrusr attrusr) {
    String _xifexpression = null;
    String _textProperty = attrusr.getTextProperty(PrpTypeCode.DOMAN);
    boolean _equals = Objects.equal(_textProperty, "T");
    if (_equals) {
      return "String";
    } else {
      String _xifexpression_1 = null;
      String _textProperty_1 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
      boolean _equals_1 = Objects.equal(_textProperty_1, "N");
      if (_equals_1) {
        int _decplc = attrusr.getDecplc();
        boolean _equals_2 = (_decplc == 0);
        if (_equals_2) {
          int _len = attrusr.getLen();
          boolean _lessEqualsThan = (_len <= 4);
          if (_lessEqualsThan) {
            return "Short";
          } else {
            if (((attrusr.getLen() > 4) && (attrusr.getLen() <= 9))) {
              return "Int";
            } else {
              return "Double";
            }
          }
        } else {
          return "Double";
        }
      } else {
        String _xifexpression_2 = null;
        String _textProperty_2 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
        boolean _equals_3 = Objects.equal(_textProperty_2, "D");
        if (_equals_3) {
          return "Date";
        } else {
          String _xifexpression_3 = null;
          String _textProperty_3 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
          boolean _equals_4 = Objects.equal(_textProperty_3, "M");
          if (_equals_4) {
            return "Time";
          } else {
            String _xifexpression_4 = null;
            String _textProperty_4 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
            boolean _equals_5 = Objects.equal(_textProperty_4, "Q");
            if (_equals_5) {
              return "Timestamp";
            } else {
              String _xifexpression_5 = null;
              String _textProperty_5 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
              boolean _equals_6 = Objects.equal(_textProperty_5, "Z");
              if (_equals_6) {
                return "String";
              } else {
                String _xifexpression_6 = null;
                String _textProperty_6 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
                boolean _equals_7 = Objects.equal(_textProperty_6, "G");
                if (_equals_7) {
                  return "String";
                } else {
                  String _xifexpression_7 = null;
                  String _textProperty_7 = attrusr.getTextProperty(PrpTypeCode.DOMAN);
                  boolean _equals_8 = Objects.equal(_textProperty_7, "B");
                  if (_equals_8) {
                    return "Blob";
                  } else {
                    _xifexpression_7 = "unknown";
                  }
                  _xifexpression_6 = _xifexpression_7;
                }
                _xifexpression_5 = _xifexpression_6;
              }
              _xifexpression_4 = _xifexpression_5;
            }
            _xifexpression_3 = _xifexpression_4;
          }
          _xifexpression_2 = _xifexpression_3;
        }
        _xifexpression_1 = _xifexpression_2;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }
}
