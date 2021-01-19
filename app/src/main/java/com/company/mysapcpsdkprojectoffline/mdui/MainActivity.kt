package com.company.mysapcpsdkprojectoffline.mdui

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.company.mysapcpsdkprojectoffline.mdui.adapter.ViewPagerAdapter
import com.company.mysapcpsdkprojectoffline.util.Util
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.app.SAPWizardApplication.Companion.isNetworkLost
import com.company.mysapcpsdkprojectoffline.mdui.fragment.*
import com.irozon.sneaker.Sneaker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), View.OnClickListener {


    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun main() {

        homeLL.setOnClickListener(this)
        customerLL.setOnClickListener(this)
        salesOrderLL.setOnClickListener(this)
        notificationLL.setOnClickListener(this)
        profileLL.setOnClickListener(this)

        val adapter = ViewPagerAdapter(
                supportFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        adapter.addFrag(HomeFragment(), getString(R.string.home))
        adapter.addFrag(CustomerFragment(), getString(R.string.customer))
        adapter.addFrag(SalesOrderFragment(), getString(R.string.sales_order))
        adapter.addFrag(NotificationFragment(), getString(R.string.notification))
        adapter.addFrag(ProfileFragment(), getString(R.string.profile))

        viewPagerMain.offscreenPageLimit = adapter.count
        viewPagerMain.adapter = adapter

        viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setFragment(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        isNetworkConnected().observe(this, {

            if (!it && !isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_no_internet, sneaker.getView(), false)
                sneaker.sneakCustom(view)
                isNetworkLost = true
            } else if (it && isNetworkLost) {
                val sneaker = Sneaker.with(this).autoHide(true).setDuration(5000)
                val view = LayoutInflater.from(this).inflate(R.layout.topsheet_internet_connected, sneaker.getView(), false)
                sneaker.sneakCustom(view)
                isNetworkLost = false
                Util.scheduleNotification(this)
            }

        })

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.homeLL -> setFragment(0)
            R.id.customerLL -> setFragment(1)
            R.id.salesOrderLL -> setFragment(2)
            R.id.notificationLL -> setFragment(3)
            R.id.profileLL -> setFragment(4)
        }
    }

    private fun setFragment(position: Int) {
        setDefault()
        if (position == 0) {
            menuHomeIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_house))
            menuHomeTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            homeLL.setBackgroundColor(ContextCompat.getColor(this, R.color.navigation_selected_color))
        }
        if (position == 1) {
            menuCustomerIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_gear))
            menuCustomerTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            customerLL.setBackgroundColor(ContextCompat.getColor(this, R.color.navigation_selected_color))
        }
        if (position == 2) {
            menuSalesOrderIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bank))
            menuSalesOrderTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            salesOrderLL.setBackgroundColor(ContextCompat.getColor(this, R.color.navigation_selected_color))
        }
        if (position == 3) {
            menuNotificationIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bell))
            menuNotificationTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            notificationLL.setBackgroundColor(ContextCompat.getColor(this, R.color.navigation_selected_color))
        }
        if (position == 4) {
            menuProfileIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_user))
            menuProfileTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            profileLL.setBackgroundColor(ContextCompat.getColor(this, R.color.navigation_selected_color))
        }
        viewPagerMain.currentItem = position

    }

    private fun setDefault() {
        menuHomeIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_house))
        menuHomeTV.setTextColor(ContextCompat.getColor(this, R.color.black))
        homeLL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        menuCustomerIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_gear))
        menuCustomerTV.setTextColor(ContextCompat.getColor(this, R.color.black))
        customerLL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        menuSalesOrderIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bank))
        menuSalesOrderTV.setTextColor(ContextCompat.getColor(this, R.color.black))
        salesOrderLL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        menuNotificationIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_bell))
        menuNotificationTV.setTextColor(ContextCompat.getColor(this, R.color.black))
        notificationLL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))

        menuProfileIV.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_user))
        menuProfileTV.setTextColor(ContextCompat.getColor(this, R.color.black))
        profileLL.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
    }
}