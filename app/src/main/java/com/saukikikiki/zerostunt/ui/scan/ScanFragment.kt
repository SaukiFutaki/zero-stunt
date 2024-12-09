package com.saukikikiki.zerostunt.ui.scan

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
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
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.data.api.ScanService
import com.saukikikiki.zerostunt.databinding.FragmentScanBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.text.SimpleDateFormat
import java.util.Locale

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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    //  Media Store as menyimpan foto ke galeri di folder Pictures/CameraX-Photos


    private fun takePhoto() {
        //Membuat File untuk menyimpan gambarnya

        val name = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX-Photos")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireContext().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        //Ambil Gambar
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = outputFileResults.savedUri
                    if (savedUri != null) {
                        uploadImageToApi(savedUri)
                    } else {
                        Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("ScanFragment", "Error capturing image: ${exception.message}")
                }
            }
        )
    }

    //fungsi analyze untuk integrasi analisis gambar AI, kalau sudah siap
    /**
    private fun analyzeImage(imageUri: Uri?) {
    if (imageUri != null) {
    Log.d("ScanFragment", "Image to be analyzed: $imageUri")
    // Placeholder for AI integration or future image analysis
    // This could involve sending the image to a server or processing it locally
    } else {
    Log.e("ScanFragment", "Image URI is null. Cannot analyze image.")
    }
    }
     **/

    private fun uploadImageToApi(imageUri: Uri) {
        val file = File(getPathFromUri(imageUri))
        if (file.exists()) {
            Log.d("ScanFragment", "File exists: ${file.absolutePath}")
        } else {
            Log.e("ScanFragment", "File does not exist!")
        }

        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://34.128.103.130:6000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ScanService::class.java)
        val call = apiService.predict(body)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body()?.string()
                    if (jsonResponse != null) {
                        // Menampilkan hasil sebagai Toast
                        val action = ScanFragmentDirections.actionNavigationScanToNavigationScanResult(jsonResponse)
                        findNavController().navigate(action)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to predict image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
    private fun navigateToResultFragment(jsonResponse: String) {
    val bundle = Bundle()
    bundle.putString("resultJson", jsonResponse)

    val resultFragment = ResultFragment()
    resultFragment.arguments = bundle
    requireActivity().supportFragmentManager.beginTransaction()
    .replace(R.id.fragment_container, resultFragment)
    .addToBackStack(null)
    .commit()
    }
     **/

    private fun getPathFromUri(uri: Uri): String? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
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

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                Toast.makeText(
                    requireContext(),
                    "Camera permission is required",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            Log.d("ScanFragment", "Selected image URI: $selectedImageUri")  // Log URI

            if (selectedImageUri != null) {
                try {
                    uploadImageToApi(selectedImageUri)
                    Toast.makeText(requireContext(), "Image selected: $selectedImageUri", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error getting selected file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //Kode Unik khusus untuk izin kamera
    companion object {
        //Untuk izin akses kamera
        private const val CAMERA_PERMISSION_CODE = 1001
        //Untuk izin akses galeri
        private const val GALLERY_REQUEST_CODE = 1002
    }
}