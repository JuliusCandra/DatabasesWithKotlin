package com.dev_candra.mynotesapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.FileObserver.CREATE
import com.dev_candra.mynotesapp.database.DatabaseContract.NoteColums.Companion.TABLE_NAME

internal class DatabaseHelper (context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "dbnoteapp"
        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_NOTE = "CREATE TABLE $TABLE_NAME" +
                "(${DatabaseContract.NoteColums.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${DatabaseContract.NoteColums.TITLE} TEXT NOT NULL," +
                "${DatabaseContract.NoteColums.DESCRIPTION} TEXT NOT NULL," +
                "${DatabaseContract.NoteColums.DATE} TEXT NOT NULL)"

    }


    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(SQL_CREATE_TABLE_NOTE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
       p0?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(p0)
    }

}