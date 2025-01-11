package com.example.bookkeeper

import android.R.id
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class BookAdapter(private val context: Context, private var books: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCover: ImageView = itemView.findViewById(R.id.bookCover)
        val bookTitle: TextView = itemView.findViewById(R.id.bookTitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookAuthor)
        val bookPublishedDate: TextView = itemView.findViewById(R.id.bookPublished)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tem_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.bookTitle.text = book.bookName
        holder.bookAuthor.text = book.author
        holder.bookPublishedDate.text = book.publicationYear.toString()
        // Load the book cover image from the URI string
        loadImageFromUri(book.image, holder.bookCover)

        // Serialize the book object to JSON and pass it to the details activity
        holder.itemView.setOnClickListener {
            val gson = Gson()
            val bookJson = gson.toJson(book)
            val intent = Intent(context, BookDetailsActivity::class.java).apply {
                putExtra("BOOK_JSON", bookJson)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = books.size

    private fun loadImageFromUri(imageData: ByteArray?, imageView: ImageView) {
        // Convert the string back to a Uri
        // val imageUri: Uri = Uri.parse(uriString)
        if (imageData != null) {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            imageView.setImageBitmap(bitmap) // Display the image
        } else {
            //Toast.makeText(this, "No image found for ID: $id", Toast.LENGTH_SHORT).show()
            imageView.setImageResource(R.drawable.cat)
        }
        // Log.d("codenptwotking","this is label for printing === $uriString" + isUriValid(imageUri))
//        try {
//           // imageView.setImageURI(imageUri)
//            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//
//
//            // Set the Bitmap to the ImageView
//            imageView.setImageBitmap(bitmap)
//            // Open an InputStream from the URI and decode it to a Bitmap
////            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
////            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
////            imageView.setImageBitmap(bitmap) // Set the Bitmap to the ImageView
//        } catch (e: Exception) {
//            e.printStackTrace()
//            // Optionally set a placeholder image or handle errors
//            imageView.setImageResource(R.drawable.cat) // Replace with your placeholder image
//        }
    }

    fun updateBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }
}

