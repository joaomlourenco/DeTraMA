package polyglot.ext.ml5.bct.detection;

public class AnomalyRwW extends Anomaly {

	public String field;

	public AnomalyRwW(String field) {
		super();
		this.field = field;
	}

	@Override
	public String toString() {
		return super.toString() + " RwW [field=" + field + "]";
	}
	
	
}
