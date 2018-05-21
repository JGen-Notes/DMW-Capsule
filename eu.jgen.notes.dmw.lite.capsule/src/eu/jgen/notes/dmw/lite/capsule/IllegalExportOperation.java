package eu.jgen.notes.dmw.lite.capsule;

public class IllegalExportOperation extends RuntimeException {

	static final long serialVersionUID =  -1242593497105584673L;
	public IllegalExportOperation() {
	}

	public IllegalExportOperation(String arg0) {
		super(arg0);
	}

	public IllegalExportOperation(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IllegalExportOperation(Throwable arg0) {
		super(arg0);
	}
}