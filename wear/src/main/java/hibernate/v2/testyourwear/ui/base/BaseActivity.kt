package hibernate.v2.testyourwear.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import hibernate.v2.testyourwear.R

/**
 * Created by himphen on 25/5/16.
 */
abstract class BaseActivity : AppCompatActivity() {

    fun initFragment(fragment: Fragment) {
        setContentView(R.layout.activity_container_no_drawer)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }
}