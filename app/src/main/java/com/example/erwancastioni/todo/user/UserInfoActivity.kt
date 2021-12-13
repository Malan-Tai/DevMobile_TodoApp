package com.example.erwancastioni.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.network.Api
import com.google.android.material.snackbar.Snackbar
import com.google.modernstorage.mediastore.FileType
import com.google.modernstorage.mediastore.MediaStoreRepository
import com.google.modernstorage.mediastore.SharedPrimary
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URI
import java.util.*

class UserInfoActivity : AppCompatActivity() {

    val mediaStore by lazy { MediaStoreRepository(this) }
    private lateinit var photoUri: Uri

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
            else Snackbar.make(findViewById(R.id.takePictureButton), "Échec!", Snackbar.LENGTH_LONG)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val takePictureBtn = findViewById<Button>(R.id.takePictureButton)
        takePictureBtn.setOnClickListener {
            launchCameraWithPermission()
        }

        val uploadImageBtn = findViewById<Button>(R.id.uploadImageButton)
        uploadImageBtn.setOnClickListener {
            galleryLauncher.launch("image/*")
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
        val permissionStatus = checkSelfPermission(camPermission)
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
        AlertDialog.Builder(this)
            .setMessage("🥺 On a besoin de la caméra, vraiment! 👉👈")
            .setPositiveButton("Bon, ok") { _, _ -> launchAppSettings() }
            .setNegativeButton("Nope") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun launchAppSettings() {
        // Cet intent permet d'ouvrir les paramètres de l'app (pour modifier les permissions déjà refusées par ex)
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        // ici pas besoin de vérifier avant car on vise un écran système:
        startActivity(intent)
    }

    private fun handleImage(imageUri: Uri) {
        val image = findViewById<ImageView>(R.id.avatar)
        image.load(imageUri)

        lifecycleScope.launch {
            Api.userWebService.updateAvatar(convert(imageUri))
        }
    }

    private fun launchCamera() {
        cameraLauncher.launch(photoUri)
    }

    private fun convert(uri: Uri): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "temp.jpeg",
            body = this.contentResolver.openInputStream(uri)!!.readBytes().toRequestBody()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.delete(photoUri, null, null)
    }
}