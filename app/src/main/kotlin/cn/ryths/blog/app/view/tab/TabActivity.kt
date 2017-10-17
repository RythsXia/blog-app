package cn.ryths.blog.app.view.tab

import android.app.Fragment
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import cn.ryths.blog.app.R
import cn.ryths.blog.app.databinding.ActivityTabBinding
import cn.ryths.blog.app.view.tab.category.CategoryFragment
import cn.ryths.blog.app.view.tab.index.IndexFragment
import cn.ryths.blog.app.view.tab.setting.SettingFragment


class TabActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTabBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tab)
        binding.viewModel = ViewModel()
    }


    inner class ViewModel : BaseObservable() {
        private var fragmentMap: Map<Int, Fragment> = HashMap()
        /**
         * 标记每个tab按键
         */
        val Tab_Index = 0
        val Tab_Category = 1
        val Tab_Setting = 2
        /**
         * 标记当前激活的tab
         */
        private var activeTab = -1

        @Bindable
        fun getActiveTab(): Int {
            return activeTab
        }

        /**
         * 初始化
         */
        init {
            //默认激活第一个视图
            activeTab = Tab_Index
            val transaction = fragmentManager.beginTransaction()
            var fragment = fragmentMap[Tab_Index]
            if (fragment == null) {
                fragment = IndexFragment()
                fragmentMap += (Tab_Index to fragment)
                transaction.add(R.id.fragment_tab, fragment)
            }
            transaction.show(fragment)
            transaction.commit()
        }

        /**
         * tab点击事件
         */
        fun tabClick(button: View, index: Int) {
            //如果点击的是激活的tab，不做反应
            if (index == activeTab) {
                return
            }
//            button as Button
//            activeBtn.compoundDrawables[1]
//                    .setTint(resources.getColor(R.color.primary_text))
//            button.compoundDrawables[1]
//                    .setTint(resources.getColor(R.color.primary))
//            activeBtn = button
            val transaction = fragmentManager.beginTransaction()
            //隐藏当前激活的fragment
            transaction.hide(fragmentMap[activeTab])
            //设置激活的tab
            activeTab = index
            //从fragmentMap中查找指定的fragment
            var fragment: Fragment? = fragmentMap[index]
            //不存在时就创建，并加入到fragmentMap中
            if (fragment == null) {
                fragment = when (index) {
                    Tab_Index -> {
                        IndexFragment()
                    }
                    Tab_Category -> {
                        CategoryFragment()
                    }
                    else -> {
                        SettingFragment()
                    }
                }
                fragmentMap += (index to fragment)
                transaction.add(R.id.fragment_tab, fragment)
            }
            transaction.show(fragment)
            transaction.commit()
        }
    }

}
