package com.example.userapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.userapp.R
import com.example.userapp.databinding.UserActivityDetailBinding
import com.example.userapp.data.entity.User
import com.example.userapp.viewmodel.UserViewModel

class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityDetailBinding
    private val userViewModel: UserViewModel by viewModels()
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("user", User::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("user")
        }

        user?.let { showUser(it) }

        binding.btnEdit.setOnClickListener {
            val dialog = AddUserDialogFragment(user)
            dialog.show(supportFragmentManager, "EditUserDialog")
        }

        binding.btnDelete.setOnClickListener {
            user?.let {
                userViewModel.delete(it)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        user?.let { currentUser ->
            userViewModel.getUser(currentUser.id).observe(this) { updatedUser ->
                updatedUser?.let { showUser(it) }
            }
        }
    }

    private fun showUser(user: User) {
        this.user = user
        // Use data binding to set the user data
        binding.user = user

        // Handle the image separately since it needs custom loading logic
        if (!user.profilePhotoPath.isNullOrEmpty()) {
            Glide.with(this).load(user.profilePhotoPath).into(binding.userImage)
        } else {
            binding.userImage.setImageResource(R.drawable.ic_person)
        }
    }
}
