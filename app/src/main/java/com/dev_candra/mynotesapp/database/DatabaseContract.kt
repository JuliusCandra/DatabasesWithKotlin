package com.dev_candra.mynotesapp.database

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class NoteColums: BaseColumns{
        companion object{
            const val TABLE_NAME = "note"
            const val ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"
        }
    }
}