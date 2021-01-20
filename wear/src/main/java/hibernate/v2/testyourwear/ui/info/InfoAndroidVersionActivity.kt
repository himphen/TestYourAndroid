package hibernate.v2.testyourwear.ui.info

import android.os.Bundle
import hibernate.v2.testyourwear.ui.base.BaseActivity

class InfoAndroidVersionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(InfoAndroidVersionFragment())
    }
}