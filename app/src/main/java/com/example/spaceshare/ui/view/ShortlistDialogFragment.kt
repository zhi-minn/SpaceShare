package com.example.spaceshare.ui.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ListingAdapter
import com.example.spaceshare.adapters.ScrollToTopObserver
import com.example.spaceshare.databinding.DialogShortlistBinding
import com.example.spaceshare.databinding.FragmentSearchBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.ShortlistViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class ShortlistDialogFragment() : DialogFragment() {

    @Inject
    lateinit var shortlistViewModel: ShortlistViewModel

    private lateinit var binding: DialogShortlistBinding
    private lateinit var navController: NavController

    private lateinit var adapter: ListingAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SlideLeftDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_shortlist, container, false)
        binding.noListingView.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureButtons()
        configureRecyclerView()
        configureScrollToTopButton()
        configureListingObservers()
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }
    }

    private fun configureRecyclerView() {
        manager = LinearLayoutManager(requireContext())

        adapter = ListingAdapter(childFragmentManager, object : ListingAdapter.ItemClickListener {
            override fun onItemClick(listing: Listing) {
                val clientListingDialogFragment =
                    ClientListingDialogFragment(listing, searchViewModel = null)
                clientListingDialogFragment.show(
                    Objects.requireNonNull(childFragmentManager),
                    "clientListingDialog"
                )
            }
        })

        adapter.registerAdapterDataObserver(
            ScrollToTopObserver(binding.recyclerView, adapter, manager)
        )

        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = manager
    }

    private fun configureScrollToTopButton() {
        binding.fabScrollToTop.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                binding.recyclerView.quickScrollToTop()
            }
        }
    }

    private suspend fun RecyclerView.quickScrollToTop(
        jumpThreshold: Int = 30,
        speedFactor: Float = 1.5f
    ) {
        val layoutManager = layoutManager as? LinearLayoutManager
            ?: error("Need to be used with a LinearLayoutManager or subclass of it")

        val smoothScroller = object : LinearSmoothScroller(context) {
            init {
                targetPosition = 0
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?) =
                super.calculateSpeedPerPixel(displayMetrics) / speedFactor
        }

        val jumpBeforeScroll = layoutManager.findFirstVisibleItemPosition() > jumpThreshold
        if (jumpBeforeScroll) {
            layoutManager.scrollToPositionWithOffset(jumpThreshold, 0)
            awaitFrame()
        }

        layoutManager.startSmoothScroll(smoothScroller)
    }

    private fun configureListingObservers() {
        shortlistViewModel.shortlistedListings.observe(viewLifecycleOwner) { listings ->
            binding.noListingView.visibility =
                if (listings.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.areEditButtonsGone = true
            adapter.submitList(listings)
        }
    }
}