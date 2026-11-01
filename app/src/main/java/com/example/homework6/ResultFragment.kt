package com.example.homework6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.homework6.databinding.FragmentResultBinding

class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val score = requireArguments().getInt("score")
        val total = requireArguments().getInt("total")

        binding.scoreText.text = getString(
            R.string.quiz_result_score,
            score,
            total
        )

        binding.repeatButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_resultFragment_to_quizFragment
            )
        }

        binding.loginButton.setOnClickListener {
            findNavController().popBackStack(
                R.id.loginFragment,
                false
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}