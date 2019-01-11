package hibernate.v2.testyourandroid;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;

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