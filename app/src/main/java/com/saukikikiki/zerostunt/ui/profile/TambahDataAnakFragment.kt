package com.saukikikiki.zerostunt.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.saukikikiki.zerostunt.databinding.FragmentTambahDataAnakBinding
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TambahDataAnakFragment : Fragment() {

    private var _binding: FragmentTambahDataAnakBinding? = null
    private val binding get() = _binding!!
    private lateinit var interpreter: Interpreter

    private val meanValues = floatArrayOf(14.90277319f, 2.76429515f, 49.09078904f, 7.61678772f, 69.04057445f)
    private val stdValues = floatArrayOf(8.60481693f, 0.2959524f, 0.43457117f, 1.769163f, 9.49217264f)


    //menghubungkan file XML , inisialisasi binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahDataAnakBinding.inflate(inflater, container, false)
        return binding.root
    }


    //prediksi risiko stunting
    //Berat dan tinggi lahir dinormalisasi menggunakan nilai rata-rata (mean) dan standar deviasi (std), sesuai dengan parameter yang dilatih

    //Data input diubah menjadi format ByteBuffer dengan tipe data float
    //Output buffer juga dideklarasikan untuk menyimpan hasil prediksi dari model
    private fun predictStunting(beratLahir: Float, tinggiLahir: Float): Float {
        // Normalisasi input
        val normalizedBerat = normalize(beratLahir, meanValues[2], stdValues[2])
        val normalizedTinggi = normalize(tinggiLahir, meanValues[3], stdValues[3])

        // Buat input buffer (format sesuai model)
        val inputBuffer = ByteBuffer.allocateDirect(4 * 2).apply {
            order(ByteOrder.nativeOrder())
            putFloat(normalizedBerat)
            putFloat(normalizedTinggi)
        }

        // Output buffer
        val outputBuffer = ByteBuffer.allocateDirect(4).apply {
            order(ByteOrder.nativeOrder())
        }

        // Jalankan prediksi dengan interpreter
        interpreter.run(inputBuffer, outputBuffer)
        outputBuffer.rewind()
        val prediction = outputBuffer.asFloatBuffer().get(0)
        return prediction

        // Ambil hasil prediksi
        return outputBuffer.float
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi model
        interpreter = Interpreter(loadModelFile())

        //Pilihan gender
        binding.ivLakiLaki.setOnClickListener {
            binding.ivLakiLaki.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_boy_icon_active)
            binding.ivPerempuan.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_girl_icon)
        }
        binding.ivPerempuan.setOnClickListener {
            binding.ivPerempuan.setImageResource(com.saukikikiki.zerostunt.R.drawable.ic_girl_active)
            binding.ivLakiLaki.setImageResource(com.saukikikiki.zerostunt.R.drawable.baby_boy_icon)
        }

        //Tombol Simpan Data + Prediksi Stunting
        binding.btnSimpan.setOnClickListener {
            val namaAnak = binding.tilNamaAnak.editText?.text.toString()
            val tanggalLahir = binding.tilTanggalLahir.editText?.text.toString()
            val beratLahir = binding.tilBeratLahir.editText?.text.toString().toFloatOrNull() ?: 0f
            val tinggiLahir = binding.tilTinggiLahir.editText?.text.toString().toFloatOrNull() ?: 0f


            val jenisKelamin = if (binding.ivPerempuan.drawable.constantState == resources.getDrawable(com.saukikikiki.zerostunt.R.drawable.ic_girl_active).constantState) {
                "Perempuan"
            } else {
                "Laki-laki"
            }

            if (namaAnak.isBlank() || tanggalLahir.isBlank()) {
                Toast.makeText(requireContext(), "Harap isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prediksi hasil dengan model
            val prediksiStunting = predictStunting(beratLahir, tinggiLahir)
            val risiko = if (prediksiStunting > 0.5) "Berisiko" else "Tidak Berisiko"

            val action = TambahDataAnakFragmentDirections.actionNavigationTambahDataAnakToNavigationHome(
                namaAnak, tanggalLahir, beratLahir, tinggiLahir, jenisKelamin
            )

            // Tampilkan hasil prediksi
            Toast.makeText(requireContext(), "Hasil: $risiko", Toast.LENGTH_SHORT).show()

            findNavController().navigate(action)
        }
    }

    //memuat file model dari folder assets?
    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = requireContext().assets.openFd("model.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun normalize(value: Float, mean: Float, std: Float): Float {
        return (value - mean) / std
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}