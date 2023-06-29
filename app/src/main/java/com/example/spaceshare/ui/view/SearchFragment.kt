package com.example.spaceshare.ui.view

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
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
        configureRecyclerView()
        configureListingObservers()
    }

    private fun configureSearchBar() {
        binding.searchBarCard.setOnClickListener {
            val searchDialogFragment = SearchDialogFragment(searchViewModel)
            searchDialogFragment.show(Objects.requireNonNull(childFragmentManager), "searchDialog")
        }
    }

    private fun configureRecyclerView() {
        adapter = ListingAdapter(object : ListingAdapter.ItemClickListener {
            override fun onItemClick(listing: Listing) {
                Log.i(TAG, "Implement me here")
            }
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configureListingObservers() {
        searchViewModel.listings.observe(viewLifecycleOwner) { listings ->
            adapter.submitList(listings)
        }
    }
}