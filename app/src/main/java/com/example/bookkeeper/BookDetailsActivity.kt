package com.example.bookkeeper

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson

class BookDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the JSON string from the intent
        val bookJson = intent.getStringExtra("BOOK_JSON")

        if (bookJson != null) {
            // Deserialize the JSON string back into a Book object
            val gson = Gson()
            val book = gson.fromJson(bookJson, Book::class.java)

            // Display book details
            findViewById<TextView>(R.id.bookName).text = book.bookName
            findViewById<TextView>(R.id.bookAuthor).text = book.author
            findViewById<TextView>(R.id.bookSummary).text = book.summary
            findViewById<TextView>(R.id.bookPublicationYear).text = book.publicationYear.toString()
            findViewById<TextView>(R.id.bookPublishedBy).text = book.publishedBy
            findViewById<TextView>(R.id.bookGenre).text = book.genre

            val imageView = findViewById<ImageView>(R.id.bookCover)
            if (book.image != null) {
                val bitmap = BitmapFactory.decodeByteArray(book.image, 0, book.image!!.size)
                imageView.setImageBitmap(bitmap)
            } else {
                imageView.setImageResource(R.drawable.cat)
            }
        } else {
            // Handle the case where the book JSON is missing
            Toast.makeText(this, "Failed to load book details.", Toast.LENGTH_SHORT).show()
        }
    }
}