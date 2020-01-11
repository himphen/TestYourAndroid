package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.fragment.TestColorFragment

class TestColorActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(TestColorFragment())
    }
}