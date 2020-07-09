package hibernate.v2.testyourandroid.ui.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.pm.PackageInfoCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.helper.UtilHelper.getInstalledPackages
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoActivity
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_listview_scrollbar.*
import java.util.ArrayList
import java.util.Comparator
import java.util.Locale

/**
 * Created by himphen on 21/5/16.
 */
class AppListFragment : BaseFragment() {
    private val appList = ArrayList<AppItem>()
    private var appType = ARG_APP_TYPE_USER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            appType = arguments!!.getInt(ARG_APP_TYPE, ARG_APP_TYPE_USER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_info_listview_scrollbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvlist.layoutManager = LinearLayoutManager(context)
        rvlist.isVerticalScrollBarEnabled = true
        init()
    }

    private fun init() {
        loadAppList()
    }

    @SuppressLint("StaticFieldLeak")
    private fun loadAppList() {
        object : AsyncTask<Void, Void, Void?>() {
            private var dialog: MaterialDialog? = null
            override fun onPreExecute() {
                super.onPreExecute()
                context?.let {
                    // TODO
                    dialog = MaterialDialog(it)
                        .message(R.string.ui_loading)
                        .cancelable(false)
                    dialog?.show()
                }
            }

            override fun doInBackground(vararg voids: Void?): Void? {
                context?.packageManager?.let { packageManager ->
                    val packs = getInstalledPackages(packageManager, PackageManager.GET_PERMISSIONS)
                    for (packageInfo in packs) {
                        if (appType == ARG_APP_TYPE_USER) {
                            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                                continue
                            }
                        } else if (appType == ARG_APP_TYPE_SYSTEM) {
                            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                                continue
                            }
                        }
                        val appItem = AppItem()
                        appItem.appName =
                            packageInfo.applicationInfo.loadLabel(packageManager).toString()
                        appItem.sourceDir = packageInfo.applicationInfo.dataDir
                        appItem.packageName = packageInfo.packageName
                        appItem.versionCode =
                            PackageInfoCompat.getLongVersionCode(packageInfo).toString()
                        appItem.versionName = packageInfo.versionName
                        appItem.firstInstallTime = packageInfo.firstInstallTime
                        try {
                            appItem.icon = packageInfo.applicationInfo.loadIcon(packageManager)
                        } catch (e: Resources.NotFoundException) {
                        }
                        appItem.isSystemApp =
                            packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                        appList.add(appItem)
                    }
                    appList.sortWith(Comparator { item1, item2 ->
                        item1.appName!!.toLowerCase(Locale.getDefault())
                            .compareTo(item2.appName!!.toLowerCase(Locale.getDefault()))
                    })
                }
                return null
            }

            override fun onPostExecute(void: Void?) {
                super.onPostExecute(void)
                dialog?.dismiss()
                rvlist?.adapter =
                    AppItemAdapter(appList, object : AppItemAdapter.ItemClickListener {
                        override fun onItemDetailClick(appItem: AppItem) {
                            val intent = Intent(context, AppInfoActivity::class.java)
                            val bundle = Bundle()
                            bundle.putParcelable("APP", appItem)
                            intent.putExtras(bundle)
                            startActivity(intent)
                        }
                    })
            }
        }.execute()
    }

    companion object {
        const val ARG_APP_TYPE = "appType"
        const val ARG_APP_TYPE_USER = 0
        const val ARG_APP_TYPE_SYSTEM = 1
        const val ARG_APP_TYPE_ALL = 2
        fun newInstance(sensorType: Int): AppListFragment {
            val fragment = AppListFragment()
            val args = Bundle()
            args.putInt(ARG_APP_TYPE, sensorType)
            fragment.arguments = args
            return fragment
        }
    }
}