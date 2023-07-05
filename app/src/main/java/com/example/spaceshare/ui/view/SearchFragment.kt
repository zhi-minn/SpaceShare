package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ListingAdapter
import com.example.spaceshare.databinding.FragmentSearchBinding
import com.example.spaceshare.models.Listing
import com.example.spaceshare.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureSearchBar()
        configureFilterButton()
        configureRecyclerView()
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
        val searchDialogFragment = SearchDialogFragment(searchViewModel)
        searchDialogFragment.show(Objects.requireNonNull(childFragmentManager), "searchDialog")
    }

    private fun configureFilterButton() {
        binding.btnFilter.setOnClickListener {
            val filterDialogFragment = ClientFilterDialogFragment(searchViewModel)
            filterDialogFragment.show(Objects.requireNonNull(childFragmentManager), "filterDialog")
        }
    }

    private fun configureRecyclerView() {
        adapter = ListingAdapter(childFragmentManager, object : ListingAdapter.ItemClickListener {
            override fun onItemClick(listing: Listing) {
                val clientListingDialogFragment = ClientListingDialogFragment(listing)
                clientListingDialogFragment.show(Objects.requireNonNull(childFragmentManager),
                    "clientListingDialog")
            }
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configureListingObservers() {
        searchViewModel.listings.observe(viewLifecycleOwner) { listings ->
            binding.noListingView.visibility = if (listings.isNotEmpty()) View.GONE else View.VISIBLE
            adapter.areEditButtonsGone = true
            adapter.submitList(listings)
        }
    }
}