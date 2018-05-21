package eu.jgen.notes.dmw.lite.capsule;

public class ConversionProblem extends RuntimeException {

	static final long serialVersionUID =  -1242593497105584673L;
	public ConversionProblem() {
	}

	public ConversionProblem(String arg0) {
		super(arg0);
	}

	public ConversionProblem(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ConversionProblem(Throwable arg0) {
		super(arg0);
	}
}