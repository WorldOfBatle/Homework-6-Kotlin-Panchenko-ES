package com.example.homework6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.homework6.databinding.FragmentBookBinding

class BookFragment : Fragment() {

    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    private var bookCardText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookCardText = savedInstanceState?.getString(KEY_BOOK_CARD)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.showBookButton.setOnClickListener {
            showBookCard()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        displayBookCard()
    }

    private fun showBookCard() {
        val title = binding.bookTitleEditText.text.toString().trim()
        val author = binding.bookAuthorEditText.text.toString().trim()
        val ratingText = binding.bookRatingEditText.text.toString()
            .trim()
            .replace(',', '.')
        val rating = ratingText.toDoubleOrNull()

        binding.bookTitleLayout.error = null
        binding.bookAuthorLayout.error = null
        binding.bookRatingLayout.error = null

        bookCardText = null
        displayBookCard()

        var hasErrors = false

        if (title.isBlank()) {
            binding.bookTitleLayout.error = getString(R.string.error_book_title)
            hasErrors = true
        }

        if (author.isBlank()) {
            binding.bookAuthorLayout.error = getString(R.string.error_book_author)
            hasErrors = true
        }

        if (rating == null || !rating.isFinite() || rating !in 0.0..5.0) {
            binding.bookRatingLayout.error = getString(R.string.error_book_rating)
            hasErrors = true
        }

        if (hasErrors) {
            return
        }

        bookCardText = getString(
            R.string.book_card_result,
            title,
            author,
            rating.toString()
        )

        displayBookCard()
    }

    private fun displayBookCard() {
        binding.bookResultTextView.text = bookCardText.orEmpty()
        binding.bookResultTextView.visibility =
            if (bookCardText != null) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_BOOK_CARD, bookCardText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_BOOK_CARD = "book_card"
    }
}