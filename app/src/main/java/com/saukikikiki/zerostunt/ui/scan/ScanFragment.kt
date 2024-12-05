package com.saukikikiki.zerostunt.ui.scan

import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentScanBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.saukikikiki.zerostunt.R
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class ScanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up CameraX
        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.captureButton.setOnClickListener {
            takePhoto()
        }

        // Check permission and initialize CameraX
        checkCameraPermission()
    }

    //  Media Store as menyimpan foto ke galeri di folder Pictures/CameraX-Photos


    private fun takePhoto() {
        //Membuat File untuk menyimpan gambarnya

        val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX-Photos")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(requireContext().contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        //Ambil Gambar
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    Toast.makeText(
                        requireContext(),
                        "Photo saved to gallery: $savedUri", Toast.LENGTH_SHORT
                    ).show()

                    // Analyze image if required
                    analyzeImage(savedUri)
                }

                override fun onError(exception: ImageCaptureException){
                    Log.e("ScanFragment", "Error capturing image: ${exception.message}")
                }
            }
        )
    }

    //fungsi analyze untuk integrasi analisis gambar AI, kalau sudah siap

    private fun analyzeImage(imageUri: Uri?) {
        if (imageUri != null) {
            Log.d("ScanFragment", "Image to be analyzed: $imageUri")
            // Placeholder for AI integration or future image analysis
            // This could involve sending the image to a server or processing it locally
        } else {
            Log.e("ScanFragment", "Image URI is null. Cannot analyze image.")
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Bind use cases to camera
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageCapture
                )

                // Set up PreviewView to show the camera feed
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
            } catch (e: Exception) {
                Log.e("ScanFragment", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        cameraExecutor.shutdown()
    }


    //BAGIAN IZIN AKSES cameraX PADA DEVICE
    //Bila belum diizinkan, maka app minta izin user akses kamera pakai requestPermission
    //Saat diizinkan, inisialisasi KAMERA dilakukan dengan manggil fungsi startCamera()
    private fun checkCameraPermission() {
        val permissions = arrayOf(android.Manifest.permission.CAMERA)

        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, CAMERA_PERMISSION_CODE)
        } else {
            startCamera()
        }
    }

    //Menangani hasil requestPermissions
    //Kode Izin Cocok? kalau cocok diinisialisasi kamera nya
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    //Kode Unik khusus untuk izin kamera
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }


}

