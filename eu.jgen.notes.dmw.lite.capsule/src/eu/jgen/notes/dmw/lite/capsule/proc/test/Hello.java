package eu.jgen.notes.dmw.lite.capsule.proc.test;

import static org.junit.Assert.*;

import org.junit.Test;

import eu.jgen.notes.dmw.lite.capsule.ConversionUtil;

public class Hello {

	@Test
	public void test() {
		 String text = "ExitStates";
		 String a1 = new ConversionUtil().doGenFormat(text);
		 String a2 =  new ConversionUtil().createEntityName(a1);
		System.out.println(new ConversionUtil().doGenFormat(text));
	}

}
