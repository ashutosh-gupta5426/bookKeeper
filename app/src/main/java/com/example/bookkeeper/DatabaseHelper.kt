package com.example.bookkeeper
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "bookkeeper.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_BOOKS = "books"
        const val COLUMN_ID = "_id"
        const val COLUMN_BOOK_NAME = "book_name"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_SUMMARY = "summary"
        const val COLUMN_PUBLICATION_YEAR = "publication_year"
        const val COLUMN_PUBLISHED_BY = "published_by"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE $TABLE_BOOKS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_BOOK_NAME TEXT NOT NULL, " +
                "$COLUMN_AUTHOR TEXT NOT NULL, " +
                "$COLUMN_SUMMARY TEXT, " +
                "$COLUMN_PUBLICATION_YEAR INTEGER, " +
                "$COLUMN_PUBLISHED_BY TEXT, " +
                "$COLUMN_GENRE TEXT, " +
                "$COLUMN_IMAGE BLOB)")
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }
}
