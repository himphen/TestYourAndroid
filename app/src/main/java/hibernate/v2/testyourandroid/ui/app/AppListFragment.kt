package hibernate.v2.testyourandroid.ui.app

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoActivity
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.getInstalledPackages
import hibernate.v2.testyourandroid.util.Utils.snackbar
import hibernate.v2.testyourandroid.util.ext.isSystemPackage
import kotlinx.android.synthetic.main.fragment_info_listview_shimmer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import java.util.Locale

/**
 * Created by himphen on 21/5/16.
 */
class AppListFragment : BaseFragment(R.layout.fragment_info_listview_shimmer) {
    private lateinit var adapter: AppItemAdapter
    private val appList = ArrayList<AppItem>()
    private var appType = ARG_APP_TYPE_USER

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { arguments ->
            appType = arguments.getInt(ARG_APP_TYPE, ARG_APP_TYPE_USER)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initAppList()
    }

    private fun initRecyclerView() {
        adapter = AppItemAdapter(appList, object : AppItemAdapter.ItemClickListener {
            override fun onItemDetailClick(appItem: AppItem) {
                val intent = Intent(context, AppInfoActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable(ARG_APP, appItem)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        rvlist.setAdapter(adapter)
        rvlist.setLayoutManager(LinearLayoutManager(context))
        rvlist.setVeilLayout(R.layout.list_item_info_app, 5)
    }

    private fun initAppList() {
        rvlist.veil()
        appList.clear()

        scope.launch {
            try {
                context?.packageManager?.let { packageManager ->
                    val packs =
                        getInstalledPackages(packageManager, PackageManager.GET_PERMISSIONS)

                    for (packageInfo in packs) {
                        if (appType == ARG_APP_TYPE_USER) {
                            if (packageInfo.isSystemPackage()) {
                                continue
                            }
                        } else if (appType == ARG_APP_TYPE_SYSTEM) {
                            if (!packageInfo.isSystemPackage()) {
                                continue
                            }
                        }
                        val appItem = AppItem(
                            appName = packageInfo.applicationInfo.loadLabel(packageManager)
                                .toString(),
                            packageName = packageInfo.packageName,
                            isSystemApp = (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        )
                        try {
                            appItem.icon = packageInfo.applicationInfo.loadIcon(packageManager)
                        } catch (e: Resources.NotFoundException) {
                        }
                        appList.add(appItem)
                    }

                    appList.sortWith { item1, item2 ->
                        item1.appName.toLowerCase(Locale.getDefault())
                            .compareTo(item2.appName.toLowerCase(Locale.getDefault()))
                    }
                }
            } catch (e: Exception) {
                snackbar(view, stringRid = R.string.ui_error)?.show()
            } finally {
                withContext(Dispatchers.Main) {
                    rvlist.unVeil()
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.coroutineContext.cancel()
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