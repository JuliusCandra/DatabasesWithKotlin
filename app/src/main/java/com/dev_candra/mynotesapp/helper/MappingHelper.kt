package com.dev_candra.mynotesapp.helper

import android.database.Cursor
import android.database.DatabaseUtils
import com.dev_candra.mynotesapp.database.DatabaseContract
import com.dev_candra.mynotesapp.entity.Note

object MappingHelper {

    /*
    Pada NoteHelper proses load data dilakukan dengan eksekusi queryAll() menghasilkan objek Cursor, namun pada adapter kita membutuhkan dalam bentuk ArrayList, maka dari itu kita harus mengonversi dari Cursor ke Arraylist, di sinilah fungsi kelas pembantu MappingHelper. MoveToFirst di sini digunakan untuk memindah cursor ke baris pertama sedangkan MoveToNext digunakan untuk memindahkan cursor ke baris selanjutnya.
    Di sini kita ambil datanya satu per satu dan dimasukkan ke dalam ArrayList.
     */

/*
Fungsi apply digunakan untuk menyederhanakan kode yang berulang.
Misalnya notesCursor.geInt cukup ditulis getInt dan notesCursor.getColumnIndexOrThrow cukup ditulis getColumnIndexOrThrow. Untuk lengkapnya dapat anda pelajari di kelas Memulai Pemrograman Dengan Kotlin.
 */

    fun mapCursorToArrayList(notesCursor: Cursor?):ArrayList<Note>{
        val noteList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()){
                val id  = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColums.ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColums.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColums.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColums.DATE))
                noteList.add(Note(
                    id,title,description,date
                ))
            }
        }
        return noteList
    }
}