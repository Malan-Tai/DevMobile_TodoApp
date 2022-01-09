package com.example.erwancastioni.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.databinding.FragmentUserInfoBinding
import com.example.erwancastioni.todo.network.UserInfoViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import java.util.*

class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding

    val mediaStore by lazy { MediaStoreRepository(requireContext()) }
    private lateinit var photoUri: Uri

    private val viewModel: UserInfoViewModel by viewModels()

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) handleImage(uri)
    }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCameraWithPermission()
            else showExplanation()
        }

    // register
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { accepted ->
            if (accepted) handleImage(photoUri)
            else Snackbar.make(binding.takePictureButton, "Échec!", Snackbar.LENGTH_LONG)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.takePictureButton.setOnClickListener {
            launchCameraWithPermission()
        }

        binding.uploadImageButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.confirmButton.setOnClickListener {
            findNavController().navigate(R.id.action_userInfoFragment_to_taskListFragment)
        }

        lifecycleScope.launchWhenStarted {
            photoUri = mediaStore.createMediaUri(
                filename = "new-picture-from-todo-${UUID.randomUUID()}.jpg",
                type = FileType.IMAGE,
                location = SharedPrimary
            ).getOrElse { reason ->
                Log.e("Malan", "Creating Media URI failed: $reason")
                throw Exception()
            }
        }
    }

    private fun launchCameraWithPermission() {
        val camPermission = Manifest.permission.CAMERA
        val permissionStatus = activity?.checkSelfPermission(camPermission)
        val isAlreadyAccepted = permissionStatus == PackageManager.PERMISSION_GRANTED
        val isExplanationNeeded = shouldShowRequestPermissionRationale(camPermission)
        when {
            isAlreadyAccepted -> launchCamera()
            isExplanationNeeded -> showExplanation()
            else -> cameraPermissionLauncher.launch(camPermission)
        }
    }

    private fun showExplanation() {
        // ici on construit une pop-up système (Dialog) pour expliquer la nécessité de la demande de permission
        AlertDialog.Builder(context)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings() }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context?.packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        binding.avatar.load(imageUri)

        lifecycleScope.launch {
            viewModel.editAvatar(requireContext().contentResolver, imageUri)
        }
    }

    private fun launchCamera() {
        cameraLauncher.launch(photoUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.contentResolver?.delete(photoUri, null, null)
    }
}