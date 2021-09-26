package com.everis.listadecontatos.helpers

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.everis.listadecontatos.feature.listacontatos.model.ContatosVO

class HelperDB(
    context: Context?
) : SQLiteOpenHelper(context, NAME_BANK, null, CURRENT_VERSION) {

    companion object {
        private val NAME_BANK = "contato.db"
        private val CURRENT_VERSION = 1
    }

    val TABLE_NAME = "contato"
    val COLUMNS_ID = "id"
    val COLUMNS_NAME = "name"
    val COLUMNS_PHONE = "phone"
    val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS $TABLE_NAME(" +
            "$COLUMNS_ID INTEGER NOT NULL," +
            "$COLUMNS_NAME TEXT NOT NULL," +
            "$COLUMNS_PHONE TEXT NOT NULL," +
            "PRIMARY KEY ($COLUMNS_ID AUTOINCREMENT)" +
            ")"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int,  newVersion: Int) {
        if(oldVersion != newVersion) {
            //update da sua table ou criar novas tabelas
            db?.execSQL(DROP_TABLE)
        }
        onCreate(db)
    }

    fun contactsSearch(search: String, isSearchForId: Boolean = false) : List<ContatosVO> {
        val db = readableDatabase ?: return mutableListOf()
        var lista = mutableListOf<ContatosVO>()
        var where: String? = null
        var args: Array<String> = arrayOf()
        if(isSearchForId){
            where = "$COLUMNS_ID = ?"
            args = arrayOf("$search")
        } else {
            where = "$COLUMNS_NAME LIKE ?"
            args = arrayOf("%$search%")
        }
        var cursor = db.query(TABLE_NAME, null, where, args,null,null,null)
        if (cursor == null) {
            db.close()
            return mutableListOf()
        }
        while(cursor.moveToNext()) {
            var contact = ContatosVO(
                cursor.getInt(cursor.getColumnIndex(COLUMNS_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_PHONE))
            )
            lista.add(contact)
        }
        db.close()
        return lista
    }

    fun saveContact(contact: ContatosVO) {
        val db = writableDatabase ?: return
        val sql = "INSERT INTO  $TABLE_NAME ($COLUMNS_NAME, $COLUMNS_PHONE) VALUES (?,?)"
        var array = arrayOf(contact.nome, contact.telefone)
        db.execSQL(sql, array)
        db.close()
    }
    fun deleteContact(id: Int) {
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $TABLE_NAME WHERE $COLUMNS_ID = ?"
        val arg = arrayOf("$id")
        db.execSQL(sql,arg)
        db.close()
    }
    fun updateContato(contato: ContatosVO) {
        val db = writableDatabase ?: return
        val content = ContentValues()
        content.put(COLUMNS_NAME, contato.nome)
        content.put(COLUMNS_PHONE, contato.telefone)
        val where = "id = ?"
        var arg = arrayOf("${contato.id}")
        db.update(TABLE_NAME, content, where, arg)
        db.close()
    }
}