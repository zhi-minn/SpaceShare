package com.example.spaceshare.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.spaceshare.R
import com.example.spaceshare.SplashActivity

class DetailedPageFragment : Fragment() {
    private lateinit var DetailedPage: DetailedPageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_detailed_page)

        // Get house from intent extras
//        val intent = Intent(requireContext(), SplashActivity::class.java)
        DetailedPage =  //TODO

        // TODO: Populate views with listing data
    }
}