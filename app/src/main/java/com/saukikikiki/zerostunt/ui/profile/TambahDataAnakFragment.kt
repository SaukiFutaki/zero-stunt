package com.saukikikiki.zerostunt.ui.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.saukikikiki.zerostunt.R


import com.saukikikiki.zerostunt.data.room.AppDatabase
import com.saukikikiki.zerostunt.data.room.ChildDataDao
import com.saukikikiki.zerostunt.data.room.ChildEntity
import com.saukikikiki.zerostunt.databinding.FragmentTambahDataAnakBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TambahDataAnakFragment : Fragment() {

    private var _binding: FragmentTambahDataAnakBinding? = null
    private val binding get() = _binding!!

    private lateinit var interpreter: Interpreter
    private lateinit var appDatabase: AppDatabase
    private lateinit var childDataDao: ChildDataDao

    // Normalization parameters
    private val meanValues = floatArrayOf(0.5f, 12f, 3f, 50f, 10f, 75f)
    private val stdValues = floatArrayOf(0.5f, 6f, 0.5f, 5f, 2f, 10f)
    private var isLakiLakiSelected: Boolean = false
    private var isPerempuanSelected: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahDataAnakBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load TFLite model
        interpreter = Interpreter(loadModelFile())

        appDatabase = Room.databaseBuilder(
            requireContext(),
            AppDatabase::class.java, "child-database"
        ).build()
        childDataDao = appDatabase.ChildDao()

        binding.ivLakiLaki.setOnClickListener {
            isLakiLakiSelected = !isLakiLakiSelected
            isPerempuanSelected = false

            if (isLakiLakiSelected) {
                binding.ivLakiLaki.scaleX = 1.2f
                binding.ivLakiLaki.scaleY = 1.2f
                binding.ivPerempuan.scaleX = 1f
                binding.ivPerempuan.scaleY = 1f
                binding.ivPerempuan.setImageResource(R.drawable.baby_girl_icon)

            } else {
                binding.ivLakiLaki.scaleX = 1f
                binding.ivLakiLaki.scaleY = 1f
                binding.ivLakiLaki.setImageResource(R.drawable.baby_boy_icon)
                Toast.makeText(
                    requireContext(),
                    "Laki-Laki",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.ivPerempuan.setOnClickListener {
            isPerempuanSelected = !isPerempuanSelected
            isLakiLakiSelected = false

            if (isPerempuanSelected) {
                binding.ivPerempuan.scaleX = 1.2f
                binding.ivPerempuan.scaleY = 1.2f
                binding.ivLakiLaki.scaleX = 1f
                binding.ivLakiLaki.scaleY = 1f
                binding.ivLakiLaki.setImageResource(R.drawable.baby_boy_icon)
            } else {
                binding.ivPerempuan.scaleX = 1f
                binding.ivPerempuan.scaleY = 1f
                binding.ivPerempuan.setImageResource(R.drawable.baby_girl_icon)
            }
        }

        binding.btnSimpan.setOnClickListener {
            val gender = when {
                isLakiLakiSelected -> 1f
                isPerempuanSelected -> 0f
                else -> {
                    return@setOnClickListener
                }
            }
            val namaAnak = binding.etNamaAnak.text.toString()
            val age = binding.etUmur.text.toString().toFloatOrNull() ?: 0f
            val birthWeight = binding.etBeratLahir.text.toString().toFloatOrNull() ?: 0f
            val birthLength = binding.etPanjangLahir.text.toString().toFloatOrNull() ?: 0f
            val bodyWeight = binding.etBeratBadan.text.toString().toFloatOrNull() ?: 0f
            val bodyLength = binding.etPanjangBadan.text.toString().toFloatOrNull() ?: 0f

            if (age == 0f || birthWeight == 0f || birthLength == 0f || bodyWeight == 0f || bodyLength == 0f) {
                Toast.makeText(
                    requireContext(),
                    "Harap isi semua field dengan benar",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val result =
                predictStunting(gender, age, birthWeight, birthLength, bodyWeight, bodyLength)
            // Save result to SharedPreferences
            val sharedPref =
                requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("statusStunting", result)
                putString("namaAnak", namaAnak)
                putFloat("gender", gender)
                putFloat("age", age)
                putFloat("birthWeight", birthWeight)
                putFloat("birthLength", birthLength)
                putFloat("bodyWeight", bodyWeight)
                putFloat("bodyLength", bodyLength)
                apply()
            }

            val childData = ChildEntity(
                name = namaAnak,
                gender = gender,
                age = age,
                birthWeight = birthWeight,
                birthLength = birthLength,
                bodyWeight = bodyWeight,
                bodyLength = bodyLength,
                stuntingStatus = result
            )
            GlobalScope.launch {
                childDataDao.insert(childData)
            }
            Log.d("TambahDataAnakFragment", "Hasil prediksi: $childData")

            Toast.makeText(requireContext(), "Hasil prediksi: $result", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_navigation_tambah_data_anak_to_navigation_home)
        }
    }

    private fun predictStunting(
        gender: Float,
        age: Float,
        birthWeight: Float,
        birthLength: Float,
        bodyWeight: Float,
        bodyLength: Float
    ): String {
        val inputBuffer = ByteBuffer.allocateDirect(6 * 4).order(ByteOrder.nativeOrder())
        inputBuffer.putFloat(normalize(gender, meanValues[0], stdValues[0]))
        inputBuffer.putFloat(normalize(age, meanValues[1], stdValues[1]))
        inputBuffer.putFloat(normalize(birthWeight, meanValues[2], stdValues[2]))
        inputBuffer.putFloat(normalize(birthLength, meanValues[3], stdValues[3]))
        inputBuffer.putFloat(normalize(bodyWeight, meanValues[4], stdValues[4]))
        inputBuffer.putFloat(normalize(bodyLength, meanValues[5], stdValues[5]))
        inputBuffer.rewind()

        val outputBuffer = ByteBuffer.allocateDirect(1 * 4).order(ByteOrder.nativeOrder())
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()

        val prediction = outputBuffer.float
        return if (prediction > 0.7) "Stunting" else "Tidak Stunting"
    }

    private fun normalize(value: Float, mean: Float, std: Float): Float {
        return (value - mean) / std
    }

    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = requireActivity().assets.openFd("stunting_model (1).tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        interpreter.close()
    }
}