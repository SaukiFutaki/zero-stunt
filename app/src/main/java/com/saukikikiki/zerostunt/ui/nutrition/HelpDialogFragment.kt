package com.saukikikiki.zerostunt.ui.nutrition

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.DialogFragment
import com.saukikikiki.zerostunt.R
import com.saukikikiki.zerostunt.databinding.FragmentHelpDialogBinding
import com.saukikikiki.zerostunt.databinding.FragmentNutritionBinding


class HelpDialogFragment : DialogFragment() {
    private  var _binding: FragmentHelpDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHelpDialogBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dismiss()
        }
//
//        view.translationY = view.height.toFloat()

//        view.animate()
//            .translationY(0f)
//            .setDuration(300L)
//            .setInterpolator(DecelerateInterpolator())
//            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}