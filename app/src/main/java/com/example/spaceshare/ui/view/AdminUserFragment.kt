package com.example.spaceshare.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceshare.R
import com.example.spaceshare.SplashActivity
import com.example.spaceshare.adapters.UserAdapter
import com.example.spaceshare.databinding.FragmentAdminUserBinding
import com.example.spaceshare.models.User
import com.example.spaceshare.ui.viewmodel.AdminViewModel
import com.example.spaceshare.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminUserFragment : Fragment(), UserAdapter.ItemClickListener {

    private lateinit var binding: FragmentAdminUserBinding
    @Inject
    lateinit var authViewModel: AuthViewModel
    @Inject
    lateinit var adminViewModel: AdminViewModel

    private lateinit var newArrayList : ArrayList<User>
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_user, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.setHasFixedSize(true)
        newArrayList = arrayListOf<User>()
        binding.adminLogout.setOnClickListener {
            authViewModel.logout()
            val intent = Intent(requireContext(), SplashActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        adminViewModel.getAllUserEntries()
        adminViewModel.userEntriesLiveData.observe(viewLifecycleOwner) { users ->
            newArrayList = users
            binding.recyclerView.adapter = UserAdapter(newArrayList, this)
        }
    }

    override fun onItemClick(position: Int) {
        val args = Bundle()
        args.putString("id", newArrayList[position].id)
        args.putString("firstName", newArrayList[position].firstName)
        args.putString("lastName", newArrayList[position].lastName)
        args.putString("governmentId", newArrayList[position].governmentId)
        navController.navigate(R.id.userEntryFragment, args)
    }
}