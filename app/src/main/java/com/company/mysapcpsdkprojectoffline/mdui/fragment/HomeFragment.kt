package com.company.mysapcpsdkprojectoffline.mdui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.company.mysapcpsdkprojectoffline.mdui.datamodel.OrderTypeModel
import com.company.mysapcpsdkprojectoffline.mdui.adapter.OrderTypePagerAdapter
import com.company.mysapcpsdkprojectoffline.util.HorizontalMarginItemDecoration
import com.company.mysapcpsdkprojectoffline.R
import com.company.mysapcpsdkprojectoffline.mdui.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setOrderTypePager()

    }

    private fun setOrderTypePager() {

        val orderTypes: MutableList<OrderTypeModel> = arrayListOf()
        orderTypes.add(OrderTypeModel("Sales orders", "345.6"))
        orderTypes.add(OrderTypeModel("Purchase orders", "35.3"))

        val orderTypePagerAdapter = OrderTypePagerAdapter(requireContext(), orderTypes, onItemClick = {
            if (it == 0) {
                (requireActivity() as MainActivity).viewPagerMain.currentItem = 2
            }
        })

        orderTypePager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        orderTypePager.adapter = orderTypePagerAdapter
        orderTypePager.offscreenPageLimit = 1
        val nextItemVisiblePx = resources.getDimension(R.dimen._50sdp)
        val currentItemHorizontalMarginPx =
                resources.getDimension(R.dimen._30sdp)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
        }
        orderTypePager.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(
                requireContext(),
                R.dimen._20sdp,
                R.dimen._70sdp
        )
        orderTypePager.addItemDecoration(itemDecoration)

        pageIndicatorView.count = orderTypes.size

        orderTypePager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                pageIndicatorView.setSelected(position)
//                orderTypePagerAdapter.selectedPosition = position
//                orderTypePagerAdapter.notifyDataSetChanged()
            }
        })
    }
}