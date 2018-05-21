package eu.jgen.notes.dmw.lite.capsule

import com.google.common.base.CaseFormat
import com.ca.gen.jmmi.objs.Hlvdf
import com.ca.gen.jmmi.objs.Grpvw
import com.ca.gen.jmmi.objs.Entvw
import com.ca.gen.jmmi.objs.Attrusr
import com.ca.gen.jmmi.schema.PrpTypeCode
import com.ca.gen.jmmi.objs.Prdvw
import com.ca.gen.jmmi.objs.Expoper

class ConversionUtil {

	public def String convertPredicateView(Prdvw prdvw) {
		'''«prdvw.followDtlofe.convertViewNameAsProperty».«prdvw.followSees.name.convertAttributeName»'''
	}

	public def String convertViewNameAsClass(Hlvdf hlvdf) {
		if (hlvdf instanceof Grpvw) {
			hlvdf.name.doCamelFormat.toFirstUpper
		} else {
			hlvdf.name.doCamelFormat.toFirstUpper + (hlvdf as Entvw).followSees.name.doCamelFormat.toFirstUpper
		}
	}

	public def String convertViewNameAsProperty(Hlvdf hlvdf) {
		if (hlvdf instanceof Grpvw) {
			hlvdf.name.doCamelFormat.toFirstLower
		} else {
			(hlvdf.name.doCamelFormat.toFirstLower + (hlvdf as Entvw).followSees.name.doCamelFormat.toFirstUpper).
				toFirstLower
		}
	}

	public def String convertActionBlockName(String name) {
		return name.doCamelFormat.toFirstUpper
	}

	public def String convertEntityName(String name) {
		return name.doCamelFormat.toFirstUpper
	}

	public def String convertAttributeName(String name) {
		return name.doCamelFormat.toFirstLower
	}

	public def String converRelationshipName(String name) {
		return name.doCamelFormat.toFirstLower
	}

	public def String convertIdentifierName(String name) {
		return name.doCamelFormat.toFirstUpper.toFirstLower
	}

	public def String convertExitStateName(String name) {
		return name.doCamelFormat.toFirstLower
	}

	public def String convertCommandName(String name) {
		return name.toUpperCase
	}

	public def String convertOperationName(Expoper expoper) {
		if (expoper.operatr.toString == "=") {
			return " == "
		} else {
			return " " + expoper.operatr + " "
		}
	}

	public def String doCamelFormat(String string) {
		CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string)
	}

	public def String doGenFormat(String string) {
		CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, string)
	}

	public def createEntityName(String name) {
		name.doGenFormat
	}

	public def createAttributeName(String name) {
		name.doGenFormat
	}

	public def createRelationshipName(String name) {
		name.doGenFormat
	}

	public def createTDName(String name) {
		val tdname = name.doGenFormat
		if (tdname.length > 8) {
			return tdname.substring(0, 7)
		}
		return tdname
	}

	public def String convertAttribute(Attrusr attrusr) {
		if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'T') {
			return "String"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'N') {
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
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'D') {
			return "Date"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'M') {
			return "Time"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'Q') {
			return "Timestamp"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'Z') {
			return "String"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'G') {
			return "String"
		} else if (attrusr.getTextProperty(PrpTypeCode.DOMAN) == 'B') {
			return "Blob"
		} else {
			"unknown"
		}
	}

}
