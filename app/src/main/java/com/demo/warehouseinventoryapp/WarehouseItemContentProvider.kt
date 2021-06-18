package com.demo.warehouseinventoryapp

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class WarehouseItemContentProvider : ContentProvider() {
    private lateinit var db: WarehouseDatabase

    override fun onCreate(): Boolean {
        db = WarehouseDatabase.Companion.getDatabase(context as Context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = TABLE_NAME
        val query = builder.buildQuery(projection, selection,
                null, null, sortOrder, null)
        return db.openHelper.readableDatabase.query(query, selectionArgs)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val rowId = db.openHelper.writableDatabase
                .insert(TABLE_NAME, 0, values)
        return ContentUris.withAppendedId(CONTENT_URI, rowId)
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return db.openHelper.writableDatabase
                .delete(TABLE_NAME, selection, selectionArgs)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return db.openHelper.writableDatabase
                .update(TABLE_NAME, 0, values, selection, selectionArgs)
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    companion object {
        private const val AUTHORITY = "com.demo.warehouseinventoryapp"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

        /* URI Matcher IDs */
        private const val ITEM_ALL_ROWS = 1
        private const val ITEM_SINGLE_ROW = 2

        private fun createUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

            /* Matcher Rules */
            uriMatcher.addURI(AUTHORITY, "$TABLE_NAME#", ITEM_SINGLE_ROW)
            uriMatcher.addURI(AUTHORITY, TABLE_NAME, ITEM_ALL_ROWS)
            return uriMatcher
        }
    }
}
