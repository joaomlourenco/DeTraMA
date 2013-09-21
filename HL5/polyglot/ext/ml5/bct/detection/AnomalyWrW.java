package polyglot.ext.ml5.bct.detection;

public class AnomalyWrW extends Anomaly {

	public String field1, field2;

	public AnomalyWrW(String field1, String field2) {
		super();
		this.field1 = field1;
		this.field2 = field2;
	}
	
	@Override
	public String toString() {
		return super.toString() + " WrW [field1=" + field1 + ", field2=" + field2 + "]";
	}
}
