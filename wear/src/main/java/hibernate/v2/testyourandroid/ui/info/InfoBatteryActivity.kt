package hibernate.v2.testyourandroid.ui.info

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.base.BaseActivity

class InfoBatteryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(InfoBatteryFragment())
    }
}