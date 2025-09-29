package com.example.userapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.userapp.adapter.UserAdapter
import com.example.userapp.databinding.ActivityMainBinding
import com.example.userapp.ui.AddUserDialogFragment
import com.example.userapp.ui.UserDetailActivity
import com.example.userapp.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter { user ->
            val intent = Intent(this, UserDetailActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = userAdapter

        userViewModel.allUsers.observe(this) { users ->
            if (users.isEmpty()) {
                binding.tvEmptyState.visibility = android.view.View.VISIBLE
                binding.recyclerView.visibility = android.view.View.GONE
            } else {
                binding.tvEmptyState.visibility = android.view.View.GONE
                binding.recyclerView.visibility = android.view.View.VISIBLE
                userAdapter.updateList(users)
            }
        }

        binding.btnAddUser.setOnClickListener {
            val dialog = AddUserDialogFragment()
            dialog.show(supportFragmentManager, "AddUserDialog")
        }
    }
}
