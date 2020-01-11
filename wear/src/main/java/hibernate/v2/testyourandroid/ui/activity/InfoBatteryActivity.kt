package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.fragment.InfoBatteryFragment

class InfoBatteryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(InfoBatteryFragment())
    }
}