package com.example.bookkeeper

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson

class BookDetailsActivity : AppCompatActivity() {
    private lateinit var bookRepository: BookRepository
    private var hasDetailChanges: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bookRepository = BookRepository(this)

        handleBackPress()

        getBookDetails()

        handleEditButtonPress()

        handleDeleteButtonPress()
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == 200) {
            hasDetailChanges = true
            getBookDetails()
        }
    }

    private fun getBookDetails() {
        try {
            // Get the JSON string from the intent
            val bookId = intent.getIntExtra("bookId", 0)

            val books = bookRepository.getAllBooks()

            val bookDetails = books.find { it.id == bookId }

            if (bookDetails != null) {
                // Display book details
                findViewById<TextView>(R.id.bookName).text = bookDetails.bookName
                findViewById<TextView>(R.id.bookAuthor).text = bookDetails.author
                findViewById<TextView>(R.id.bookSummary).text = bookDetails.summary
                findViewById<TextView>(R.id.bookPublicationYear).text =
                    bookDetails.publicationYear.toString()
                findViewById<TextView>(R.id.bookPublishedBy).text = bookDetails.publishedBy
                findViewById<TextView>(R.id.bookGenre).text = bookDetails.genre

                val imageView = findViewById<ImageView>(R.id.bookCover)
                if (bookDetails.image != null) {
                    val bitmap =
                        BitmapFactory.decodeByteArray(bookDetails.image, 0, bookDetails.image!!.size)
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.cat)
                }
            } else {
                // Handle the case where the book JSON is missing
                Toast.makeText(this, "Failed to load book details.", Toast.LENGTH_SHORT).show()
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBackPress() {
        try {
            val backButton = findViewById<ImageView>(R.id.bookDetailsBackBtn)
            backButton.setOnClickListener {
                if (hasDetailChanges) {
                    setResult(200, Intent())
                }
                finish()
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleEditButtonPress() {
        try {
            val editBtn = findViewById<ImageView>(R.id.editDetailsBtn)
            editBtn.setOnClickListener {
                val intent = Intent(this, AddBookActivity::class.java).apply {
                    putExtra("isEditPage", true)
                    putExtra("bookId", intent.getIntExtra("bookId", 0))
                }
                startActivityForResult(intent, 200)
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleDeleteButtonPress() {
        try {
            val deleteButton = findViewById<CardView>(R.id.deleteBookButton)
            deleteButton.setOnClickListener {
                val result = bookRepository.deleteBook(intent.getIntExtra("bookId", 0))
                Toast.makeText(this, "Book deleted Successfully.", Toast.LENGTH_SHORT).show()
                setResult(200)
                finish()
            }
        } catch(e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }
}