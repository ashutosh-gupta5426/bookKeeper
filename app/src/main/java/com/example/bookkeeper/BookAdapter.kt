package com.example.bookkeeper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.InputStream

class BookAdapter(private val context: Context, private val books: List<Book>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

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
        holder.bookPublishedDate.text  = book.publicationYear.toString()
        // Load the book cover image from the URI string
        loadImageFromUri(book.image, holder.bookCover)
    }

    override fun getItemCount() = books.size

    private fun loadImageFromUri(uriString: String, imageView: ImageView) {
        // Convert the string back to a Uri
        val imageUri: Uri = Uri.parse(uriString)
        println("this is label for printing === $uriString")
        try {
            // Open an InputStream from the URI and decode it to a Bitmap
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap) // Set the Bitmap to the ImageView
        } catch (e: Exception) {
            e.printStackTrace()
            // Optionally set a placeholder image or handle errors
            imageView.setImageResource(R.drawable.cat) // Replace with your placeholder image
        }
    }
}

