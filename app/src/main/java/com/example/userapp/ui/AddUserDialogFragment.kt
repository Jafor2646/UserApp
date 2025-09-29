package com.example.userapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.userapp.R
import com.example.userapp.data.entity.User
import com.example.userapp.databinding.DialogAddUserBinding
import com.example.userapp.utils.FileUtils
import com.example.userapp.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class AddUserDialogFragment(private val user: User? = null) : DialogFragment() {

    private var _binding: DialogAddUserBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private var profilePhotoPath: String? = null
    private val isEditMode = user != null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val file = FileUtils.createImageFile(requireContext())
            profilePhotoPath = file.absolutePath
            FileUtils.saveBitmapToFile(bitmap, file)
            Glide.with(this).load(profilePhotoPath).into(binding.ivProfilePreview)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profilePhotoPath = FileUtils.saveImageFromUri(requireContext(), it)
            Glide.with(this).load(profilePhotoPath).into(binding.ivProfilePreview)
        }
    }

    // Camera permission request
    private val requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            handlePermissionDenied("Camera")
        }
    }

    // Storage/Media permission request for gallery
    private val requestStoragePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            handlePermissionDenied("Gallery")
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        val cameraPermission = Manifest.permission.CAMERA
        when {
            ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                cameraLauncher.launch(null)
            }
            shouldShowRequestPermissionRationale(cameraPermission) -> {
                // Show rationale and request permission
                showPermissionRationale("Camera", "Camera permission is needed to take photos for your profile picture.") {
                    requestCameraPermission.launch(cameraPermission)
                }
            }
            else -> {
                // First time asking or user selected "Don't ask again"
                requestCameraPermission.launch(cameraPermission)
            }
        }
    }

    private fun checkPermissionsAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                galleryLauncher.launch("image/*")
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Show rationale and request permission
                showPermissionRationale("Gallery", "Storage permission is needed to select photos from your gallery.") {
                    requestStoragePermission.launch(permission)
                }
            }
            else -> {
                // First time asking or user selected "Don't ask again"
                requestStoragePermission.launch(permission)
            }
        }
    }

    private fun showPermissionRationale(feature: String, message: String, onPositive: () -> Unit) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("$feature Permission Required")
            .setMessage(message)
            .setPositiveButton("Grant") { _, _ -> onPositive() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun handlePermissionDenied(feature: String) {
        val permission = when (feature) {
            "Camera" -> Manifest.permission.CAMERA
            "Gallery" -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_IMAGES
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            else -> return
        }

        if (!shouldShowRequestPermissionRationale(permission)) {
            // User selected "Don't ask again" or it's permanently denied
            showSettingsDialog(feature)
        } else {
            // User just denied this time
            showPermissionDeniedMessage(feature)
        }
    }

    private fun showPermissionDeniedMessage(feature: String) {
        Snackbar.make(binding.root, "$feature permission is required to use this feature.", Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                when (feature) {
                    "Camera" -> checkPermissionsAndOpenCamera()
                    "Gallery" -> checkPermissionsAndOpenGallery()
                }
            }
            .show()
    }

    private fun showSettingsDialog(feature: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("$feature permission is required but was permanently denied. Please enable it in app settings to use this feature.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireContext().packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddUserBinding.inflate(inflater, container, false)
        binding.isEditMode = isEditMode

        // Load existing user info if edit
        user?.let {
            binding.etName.setText(it.name)
            binding.rgGender.check(
                when (it.gender.lowercase()) {
                    "male" -> R.id.rbMale
                    "female" -> R.id.rbFemale
                    else -> R.id.rbOther
                }
            )
            profilePhotoPath = it.profilePhotoPath
            Glide.with(this).load(profilePhotoPath).into(binding.ivProfilePreview)
        }

        // Division spinner
        val divisions = resources.getStringArray(R.array.divisions)
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, divisions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDivision.adapter = spinnerAdapter
        if (user != null) binding.spinnerDivision.setSelection(divisions.indexOf(user.division))

        // Buttons
        binding.btnCamera.setOnClickListener { checkPermissionsAndOpenCamera() }
        binding.btnGallery.setOnClickListener { checkPermissionsAndOpenGallery() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            if (name.isEmpty()) {
                Snackbar.make(binding.root, "Name cannot be empty", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val gender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "Male"
                R.id.rbFemale -> "Female"
                else -> "Other"
            }
            val division = binding.spinnerDivision.selectedItem.toString()

            if (isEditMode) {
                val updatedUser = user!!.copy(name = name, gender = gender, division = division, profilePhotoPath = profilePhotoPath)
                userViewModel.update(updatedUser)
            } else {
                val newUser = User(name = name, gender = gender, division = division, profilePhotoPath = profilePhotoPath)
                userViewModel.insert(newUser)
            }
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
