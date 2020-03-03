package hibernate.v2.testyourandroid.ui.test

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.base.BaseActivity

class TestColorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(TestColorFragment())
    }
}