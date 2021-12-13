package com.example.erwancastioni.todo.user

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import coil.load
import com.example.erwancastioni.todo.R
import java.io.File

class UserInfoActivity : AppCompatActivity() {

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { accepted ->
            if (accepted) launchCameraWithPermission()
            else showExplanation()
        }

    // register
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        val tmpFile = File.createTempFile("avatar", "jpeg")
        tmpFile.outputStream().use {
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        handleImage(tmpFile.toUri())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        val takePictureBtn = findViewById<Button>(R.id.takePictureButton)
        takePictureBtn.setOnClickListener {
            launchCameraWithPermission()
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
    }

    private fun launchCamera() {
        cameraLauncher.launch(null)
    }
}