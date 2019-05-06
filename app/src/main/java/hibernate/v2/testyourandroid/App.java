package hibernate.v2.testyourandroid;

import android.content.Context;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.Utils;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import androidx.multidex.MultiDexApplication;

/**
 * Created by himphen on 24/5/16.
 */

public class App extends MultiDexApplication {

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		Utils.init(this);
	}
}