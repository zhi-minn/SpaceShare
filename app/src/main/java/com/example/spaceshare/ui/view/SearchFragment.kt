package com.example.spaceshare.ui.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ListingAdapter
import com.example.spaceshare.adapters.ScrollToTopObserver
import com.example.spaceshare.databinding.FragmentSearchBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var searchViewModel: SearchViewModel
    private lateinit var adapter: ListingAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        binding.noListingView.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureSearchBar()
        configureFilterButton()
        configureRecyclerView()
        configureScrollToTopButton()
        configureListingObservers()
    }

    private fun configureSearchBar() {
        binding.searchIcon.setOnClickListener {
            openSearchDialog()
        }
        binding.searchTextView.setOnClickListener {
            openSearchDialog()
        }
    }

    private fun openSearchDialog() {
        val searchDialogFragment = SearchDialogFragment()
        searchDialogFragment.show(Objects.requireNonNull(childFragmentManager), "searchDialog")
    }

    private fun configureFilterButton() {
        binding.btnFilter.setOnClickListener {
            val filterDialogFragment = ClientFilterDialogFragment()
            filterDialogFragment.show(Objects.requireNonNull(childFragmentManager), "filterDialog")
        }
    }

    private fun configureRecyclerView() {
        manager = LinearLayoutManager(requireContext())

        adapter = ListingAdapter(childFragmentManager, object : ListingAdapter.ItemClickListener {
            override fun onItemClick(listing: Listing) {
                val clientListingDialogFragment =
                    ClientListingDialogFragment(listing, searchViewModel)
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
        searchViewModel.filteredListings.observe(viewLifecycleOwner) { listings ->
            binding.noListingView.visibility =
                if (listings.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.areEditButtonsGone = true
            adapter.areShortlistButtonsGone = false
            adapter.submitList(listings)
        }
    }
}