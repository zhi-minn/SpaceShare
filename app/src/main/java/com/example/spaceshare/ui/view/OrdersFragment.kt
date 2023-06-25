//package com.example.spaceshare.ui.view
//
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.viewpager2.adapter.FragmentStateAdapter
//import androidx.viewpager2.widget.ViewPager2
//import com.example.spaceshare.R
//import com.example.spaceshare.ui.orders.AllOrdersFragment
//import com.example.spaceshare.ui.orders.PendingOrdersFragment
//import com.example.spaceshare.ui.orders.SuccessfulOrdersFragment
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayoutMediator
//
//class OrdersFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_reservation, container, false)
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
////        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
////        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
//
//        val fragmentList = arrayListOf<Fragment>(
//            AllOrdersFragment(),
//            SuccessfulOrdersFragment(),
//            PendingOrdersFragment()
//        )
//
//        val adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
//            override fun getItemCount() = fragmentList.size
//            override fun createFragment(position: Int) = fragmentList[position]
//        }
//
////        viewPager.adapter = adapter
////
////        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when(position) {
//                0 -> "All Orders"
//                1 -> "Successful Orders"
//                else -> "Pending Orders"
//            }
//        }.attach()
//    }
//
//}