package hibernate.v2.testyourandroid;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by himphen on 24/5/16.
 */

public class App extends MultiDexApplication {

	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	private Tracker mTracker;

	/**
	 * Gets the default {@link Tracker} for this {@link MultiDexApplication}.
	 *
	 * @return tracker
	 */
	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			mTracker = analytics.newTracker(BuildConfig.GA_TRACKING_ID);
			mTracker.enableAutoActivityTracking(true);
			mTracker.setSessionTimeout(300);
			mTracker.enableExceptionReporting(true);
		}
		return mTracker;
	}

}