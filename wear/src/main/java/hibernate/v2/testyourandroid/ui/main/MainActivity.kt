package hibernate.v2.testyourandroid.ui.main

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(MainFragment())
    }
}