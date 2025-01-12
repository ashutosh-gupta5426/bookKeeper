package com.example.bookkeeper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class BookRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // Create
    fun insertBook(
        bookName: String,
        author: String,
        summary: String,
        publicationYear: Int,
        publishedBy: String,
        genre: String,
        image: ByteArray
    ): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_BOOK_NAME, bookName)
            put(DatabaseHelper.COLUMN_AUTHOR, author)
            put(DatabaseHelper.COLUMN_SUMMARY, summary)
            put(DatabaseHelper.COLUMN_PUBLICATION_YEAR, publicationYear)
            put(DatabaseHelper.COLUMN_PUBLISHED_BY, publishedBy)
            put(DatabaseHelper.COLUMN_GENRE, genre)
            put(DatabaseHelper.COLUMN_IMAGE, image)
        }

        val id = db.insert(DatabaseHelper.TABLE_BOOKS, null, values)
        db.close()
        return id
    }

    // Read
    @SuppressLint("Range")
    fun getAllBooks(): List<Book> {
        val bookList = mutableListOf<Book>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor =
            db.query(DatabaseHelper.TABLE_BOOKS, null, null, null, null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val book = Book().apply {
                    id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID))
                    bookName =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_BOOK_NAME))
                    author = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AUTHOR))
                    summary = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SUMMARY))
                    publicationYear =
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PUBLICATION_YEAR))
                    publishedBy =
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PUBLISHED_BY))
                    genre = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_GENRE))
                    image = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE))
                }
                bookList.add(book)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return bookList
    }

    // Update
    fun updateBook(
        id: Int,
        bookName: String,
        author: String,
        summary: String,
        publicationYear: Int,
        publishedBy: String,
        genre: String,
        image: ByteArray
    ): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_BOOK_NAME, bookName)
            put(DatabaseHelper.COLUMN_AUTHOR, author)
            put(DatabaseHelper.COLUMN_SUMMARY, summary)
            put(DatabaseHelper.COLUMN_PUBLICATION_YEAR, publicationYear)
            put(DatabaseHelper.COLUMN_PUBLISHED_BY, publishedBy)
            put(DatabaseHelper.COLUMN_GENRE, genre)
            put(DatabaseHelper.COLUMN_IMAGE, image)
        }

        val rowsAffected = db.update(
            DatabaseHelper.TABLE_BOOKS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected
    }

    // Delete
    fun deleteBook(id: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_BOOKS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
    }
}
