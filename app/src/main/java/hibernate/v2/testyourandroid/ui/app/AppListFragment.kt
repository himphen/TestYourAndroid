package hibernate.v2.testyourandroid.ui.app

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hibernate.v2.testyourandroid.R
import hibernate.v2.testyourandroid.databinding.FragmentInfoListviewShimmerBinding
import hibernate.v2.testyourandroid.model.AppItem
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoActivity
import hibernate.v2.testyourandroid.ui.appinfo.AppInfoFragment.Companion.ARG_APP
import hibernate.v2.testyourandroid.ui.base.BaseFragment
import hibernate.v2.testyourandroid.util.Utils.getInstalledPackages
import hibernate.v2.testyourandroid.util.Utils.snackbar
import hibernate.v2.testyourandroid.util.ext.dp2px
import hibernate.v2.testyourandroid.util.ext.isSystemPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Created by himphen on 21/5/16.
 */
class AppListFragment : BaseFragment<FragmentInfoListviewShimmerBinding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentInfoListviewShimmerBinding.inflate(inflater, container, false)

    private lateinit var adapter: AppItemAdapter
    private val appList = ArrayList<AppItem>()
    private var appType = ARG_APP_TYPE_USER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appType = arguments?.getInt(ARG_APP_TYPE, ARG_APP_TYPE_USER) ?: ARG_APP_TYPE_USER
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initAppList()
    }

    private fun initRecyclerView() {
        val viewBinding = viewBinding!!
        adapter = AppItemAdapter(object : AppItemAdapter.ItemClickListener {
            override fun onItemDetailClick(appItem: AppItem) {
                val intent = Intent(context, AppInfoActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable(ARG_APP, appItem)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        })
        val rvlist = viewBinding.rvlist
        rvlist.setAdapter(adapter)
        rvlist.setLayoutManager(LinearLayoutManager(context))
        rvlist.setVeilLayout(R.layout.item_list_info_app, 5)
        rvlist.getRecyclerView().apply {
            clipToPadding = false
            setPaddingRelative(
                paddingStart,
                12f.dp2px(),
                paddingEnd,
                12f.dp2px()
            )
        }
        rvlist.getVeiledRecyclerView().apply {
            clipToPadding = false
            setPaddingRelative(
                paddingStart,
                12f.dp2px(),
                paddingEnd,
                12f.dp2px()
            )
        }
    }

    private fun initAppList() {
        val viewBinding = viewBinding!!
        viewBinding.rvlist.veil()

        lifecycleScope.launch(Dispatchers.IO) {
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

                        val applicationInfo = packageInfo.applicationInfo ?: continue

                        val appItem = AppItem(
                            appName = applicationInfo.loadLabel(packageManager).toString(),
                            packageName = packageInfo.packageName,
                            isSystemApp = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                        )
                        try {
                            appItem.icon = applicationInfo.loadIcon(packageManager)
                        } catch (_: Resources.NotFoundException) {
                        }
                        appList.add(appItem)
                    }

                    appList.sortWith { item1, item2 ->
                        item1.appName.lowercase(Locale.getDefault())
                            .compareTo(item2.appName.lowercase(Locale.getDefault()))
                    }
                }
            } catch (e: Exception) {
                snackbar(view, stringRid = R.string.ui_error)?.show()
            } finally {
                withContext(Dispatchers.Main) {
                    viewBinding.rvlist.unVeil()
                    adapter.submitList(appList)
                }
            }
        }
    }

    companion object {
        const val ARG_APP_TYPE = "appType"
        const val ARG_APP_TYPE_USER = 0
        const val ARG_APP_TYPE_SYSTEM = 1
        const val ARG_APP_TYPE_ALL = 2

        fun newInstance(sensorType: Int) = AppListFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_APP_TYPE, sensorType)
            }
        }
    }
}
