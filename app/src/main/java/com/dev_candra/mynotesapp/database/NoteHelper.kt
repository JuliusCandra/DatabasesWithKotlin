package com.dev_candra.mynotesapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns._ID
import com.dev_candra.mynotesapp.database.DatabaseContract.NoteColums.Companion.TABLE_NAME
import java.sql.SQLException

class NoteHelper (context: Context) {
    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private lateinit var databaseHelper: DatabaseHelper
        private var INSTANCE: NoteHelper? = null

        private lateinit var databases: SQLiteDatabase

        /*

        Kelas di bawah menggunakan sebuah pattern yang bernama Singleton Pattern. Dengan singleton sebuah objek hanya bisa memiliki sebuah instance.
        Sehingga tidak terjadi duplikasi instance. Synchronized
        di sini dipakai untuk menghindari duplikasi instance di semua Thread, karena bisa saja kita membuat instance di Thread yang berbeda.

         */
        fun getInstance(context: Context): NoteHelper
         = INSTANCE?: synchronized(this){
            INSTANCE?: NoteHelper(context)
        }
    }

    init {
        databaseHelper = DatabaseHelper(context)
    }

    @Throws(SQLException::class)
    fun open(){
        databases = databaseHelper.writableDatabase
    }

    fun close(){
        databaseHelper.close()

        if (databases.isOpen){
            databases.close()
        }
    }

    fun queryAll(): Cursor{
        return databases.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$_ID ASC",
            null

        )
    }

    fun queryById(id: String): Cursor {
        return databases.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }

    // Proses penambahan data pada NoteHelper dijabarkan dalam bentuk seperti berikut dengan objek Notesebagai parameter inputnya:
    fun insert(values:ContentValues?): Long {
        return databases.insert(DATABASE_TABLE,null,values)
    }
/*
Sementara itu, pembaharuan data dijabarkan dalam bentuk berikut dengan objek Note terbaru (Catatan : _IDsebagai referensinya).
 */
    fun update(id: String, values: ContentValues?): Int{
        return databases.update(DATABASE_TABLE,values,"$_ID = ?", arrayOf(id))
    }

    /*
    Lebih lanjut, proses penghapusan data pada NoteHelper dijabarkan dalam metode deleteById(). Id-nya berasal dari item Note yang dipilih sebagai acuan untuk menghapus data.
     */
    fun deleteById(id : String): Int{
        return databases.delete(DATABASE_TABLE, "$_ID = '$id'" , null)
    }
}