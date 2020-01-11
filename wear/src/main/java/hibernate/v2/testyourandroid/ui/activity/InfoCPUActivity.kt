package hibernate.v2.testyourandroid.ui.activity

import android.os.Bundle
import hibernate.v2.testyourandroid.ui.fragment.InfoCPUFragment

class InfoCPUActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFragment(InfoCPUFragment())
    }
}