package hibernate.v2.testyourandroid;

/**
 * Created by himphen on 24/5/16.
 */
public enum Environment {
	CONFIG(true);

	private final boolean debug;

	Environment(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}
}