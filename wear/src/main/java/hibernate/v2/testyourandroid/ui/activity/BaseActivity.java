package hibernate.v2.testyourandroid.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by himphen on 25/5/16.
 */
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

	protected Activity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}
}