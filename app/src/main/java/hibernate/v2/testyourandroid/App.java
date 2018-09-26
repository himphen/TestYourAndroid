package hibernate.v2.testyourandroid;

import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate;
import com.blankj.utilcode.util.Utils;

/**
 * Created by himphen on 24/5/16.
 */

public class App extends MultiDexApplication {

	LocalizationApplicationDelegate localizationDelegate = new LocalizationApplicationDelegate(this);

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(localizationDelegate.attachBaseContext(base));
		MultiDex.install(this);
		Utils.init(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		localizationDelegate.onConfigurationChanged(this);
	}

	@Override
	public Context getApplicationContext() {
		return localizationDelegate.getApplicationContext(super.getApplicationContext());
	}
}