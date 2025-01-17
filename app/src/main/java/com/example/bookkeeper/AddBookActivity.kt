package com.example.bookkeeper

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream


class AddBookActivity : AppCompatActivity() {
    private lateinit var bookRepository: BookRepository
    private lateinit var bookNameEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var summaryEditText: EditText
    private lateinit var publicationYearEditText: EditText
    private lateinit var publishedByEditText: EditText
    private lateinit var genreEditText: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveBookButton: CardView
    private var selectedImageUri: ByteArray? = null
    private var isEditPage: Boolean = false
    private var bookId: Int = 0

    private val PICK_IMAGE_REQUEST = 1


    private val getImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                imageView.setImageURI(uri)
                val inputStream = contentResolver.openInputStream(uri!!)
                val byteArrayOutputStream = ByteArrayOutputStream()

                val buffer = ByteArray(1024)
                var length: Int
                while ((inputStream!!.read(buffer).also { length = it }) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }

                inputStream.close()
                selectedImageUri = byteArrayOutputStream.toByteArray()
                imageView.tag = 1// Set the selected image to the ImageView
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_add_book)
        bookRepository = BookRepository(this)
        bookNameEditText = findViewById(R.id.editBookName)
        authorEditText = findViewById(R.id.editAuthorName)
        summaryEditText = findViewById(R.id.descriptionEditText)
        publicationYearEditText = findViewById(R.id.publicationYearEditText)
        publishedByEditText = findViewById(R.id.publicationByEditText)
        genreEditText = findViewById(R.id.genreEditText)
        imageView = findViewById(R.id.pickImage)
        saveBookButton = findViewById(R.id.AddButton)


        isEditPage = intent.getBooleanExtra("isEditPage", false)

        if (isEditPage) {
            setTextValues();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleBackButtonClick()

        saveBookButton.setOnClickListener {
            if (validateFields()) {
                saveBook()
            }
        }

        imageView.setOnClickListener {
            checkPermission()
            println("image picker clicked")
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        // Validate Book Name
        if (bookNameEditText.text.isNullOrEmpty()) {
            bookNameEditText.error = "Book name is required"
            isValid = false
        }

        // Validate Author Name
        if (authorEditText.text.isNullOrEmpty()) {
            authorEditText.error = "Author name is required"
            isValid = false
        }

        // Validate Summary
        if (summaryEditText.text.isNullOrEmpty()) {
            summaryEditText.error = "Description is required"
            isValid = false
        }

        // Validate Publication Year
        val publicationYear = publicationYearEditText.text.toString()
        if (publicationYear.isEmpty() || !publicationYear.all { it.isDigit() }) {
            publicationYearEditText.error = "Valid publication year is required"
            isValid = false
        }

        // Validate Published By
        if (publishedByEditText.text.isNullOrEmpty()) {
            publishedByEditText.error = "Publisher name is required"
            isValid = false
        }

        // Validate Genre
        if (genreEditText.text.isNullOrEmpty()) {
            genreEditText.error = "Genre is required"
            isValid = false
        }

        // Validate Image (assuming you want to check if an image is picked)
        // If you use a URI or bitmap reference for the image, validate accordingly
        // Here we are just checking if the user has provided an image path or some identifier.
        if (imageView.tag == null) { // Assuming you set a tag when an image is picked
            Toast.makeText(this, "Image is required", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun saveBook() {
       try {
           val bookName = bookNameEditText.text.toString()
           val author = authorEditText.text.toString()
           val summary = summaryEditText.text.toString()
           val publicationYear = publicationYearEditText.text.toString().toIntOrNull() ?: 0
           val publishedBy = publishedByEditText.text.toString()
           val genre = genreEditText.text.toString()
           val image = selectedImageUri // Assuming you set the image path as a tag

           if (isEditPage) {
               var response = bookRepository.updateBook(
                   bookId,
                   bookName,
                   author,
                   summary,
                   publicationYear,
                   publishedBy,
                   genre,
                   image!!
               )
           } else {
               val response = bookRepository.insertBook(
                   bookName,
                   author,
                   summary,
                   publicationYear,
                   publishedBy,
                   genre,
                   image!!
               )
           }
           setResult(200, Intent())
           finish()
       } catch (e: Exception) {
           Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
       }
    }

    private fun openImagePicker() {
        // Create an intent to open the image gallery
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageLauncher.launch(intent) // Launch the image picker using the registered launcher
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermission() {
       try {
           if (ContextCompat.checkSelfPermission(
                   this,
                   android.Manifest.permission.READ_MEDIA_IMAGES
               ) != PackageManager.PERMISSION_GRANTED
           ) {
               ActivityCompat.requestPermissions(
                   this,
                   arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                   PICK_IMAGE_REQUEST
               )
           } else {
               openImagePicker()
           }
       } catch (e: Exception) {
           Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
       }
    }

    private fun setTextValues() {
        try {
            findViewById<TextView>(R.id.addBookTitle).text = "Edit Book Details"
            findViewById<TextView>(R.id.addButtonText).text = "Save Changes"
            bookId = intent.getIntExtra("bookId", 0)

            val books = bookRepository.getAllBooks()
            val bookDetails = books.find { it.id == bookId }

            if (bookDetails != null) {
                bookNameEditText.setText(bookDetails.bookName)
                authorEditText.setText(bookDetails.author)
                summaryEditText.setText(bookDetails.summary)
                publicationYearEditText.setText(bookDetails.publicationYear.toString())
                publishedByEditText.setText(bookDetails.publishedBy)
                genreEditText.setText(bookDetails.genre)

                if (bookDetails.image != null) {
                    val bitmap =
                        BitmapFactory.decodeByteArray(bookDetails.image, 0, bookDetails.image!!.size)
                    imageView.tag = 1// Set the selected image to the ImageView
                    selectedImageUri = bookDetails.image
                    imageView.setImageBitmap(bitmap)
                } else {
                    imageView.setImageResource(R.drawable.cat)
                }
            } else {
                // Handle the case where the book JSON is missing
                Toast.makeText(this, "Failed to load book details.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleBackButtonClick() {
        val backButton = findViewById<ImageView>(R.id.addBookBackBtn)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}