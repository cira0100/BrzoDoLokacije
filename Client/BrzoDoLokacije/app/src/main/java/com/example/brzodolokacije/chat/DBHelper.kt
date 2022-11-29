package com.exam


import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.brzodolokacije.Models.ChatPreview
import com.example.brzodolokacije.Models.Message
import java.util.*


class DBHelper :
    SQLiteOpenHelper{

    var db:SQLiteDatabase?=null

    constructor(context: Context, factory: SQLiteDatabase.CursorFactory?):super(context, DATABASE_NAME, factory,3){
        db=readableDatabase
    }


    companion object{
        //database name
        private val DATABASE_NAME = "chatHistory"
        //database tables
        val CONTACTS_TABLE_NAME = "contacts"
        val MESSAGES_TABLE_NAME = "messages"
        private var instance:DBHelper?=null
        fun getInstance(activity: Activity):DBHelper{
            if(instance==null){
                instance= DBHelper(activity,null)
            }
            return instance as DBHelper
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if(!doesTableExist(CONTACTS_TABLE_NAME,db)){
            var sql:String="CREATE TABLE "+ CONTACTS_TABLE_NAME+" (" +
                    "userId " +"TEXT PRIMARY KEY,"+
                    "read " +"INTEGER"+
                    ")"
            db?.execSQL(sql)
        }
        if(!doesTableExist(MESSAGES_TABLE_NAME,db)){
            var sql:String="CREATE TABLE "+ MESSAGES_TABLE_NAME+"(" +
                    "_id "+"TEXT PRIMARY KEY,"+
                    "senderId " +"TEXT,"+
                    "receiverId "+"TEXT,"+
                    "messagge " +"TEXT,"+
                    "timestamp "+"INTEGER"+
                    ")"
            db?.execSQL(sql)
        }
    }

    fun doesTableExist(tableName:String,db: SQLiteDatabase?):Boolean{
        if(db!=null){
            var sqlString:String="select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'"
            var cursor: Cursor=db.rawQuery(sqlString,null)
            if(cursor!=null){
                if(cursor.count>0){
                    return true
                }
                return false
            }
        }
        return false
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME)
        db?.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME)
        onCreate(db)
    }

    fun addMessage(message: Message, sent:Boolean=true){
        onCreate(db)
        if(!message._id.isNullOrEmpty() && message.senderId==message.receiverId){
            Log.d("main", "ne zapisuje se dupla poruka")
        } else {
            if(message._id.isNullOrEmpty()){
                message._id=message.senderId+message.timestamp
            }
            var sql="INSERT INTO "+ MESSAGES_TABLE_NAME+"(_id,senderId,receiverid,messagge,timestamp) VALUES('"+message._id+"','"+
                    message.senderId+"','"+
                    message.receiverId+"','"+
                    message.messagge+ "',"+
                    message.usableTimeStamp.timeInMillis+")"
            db?.execSQL(sql)
            if(sent)
                sql="SELECT * FROM "+ CONTACTS_TABLE_NAME+" WHERE userId='"+message.receiverId+"'"
            else
                sql="SELECT * FROM "+ CONTACTS_TABLE_NAME+" WHERE userId='"+message.senderId+"'"
            var cursor=db?.rawQuery(sql,null)
            if(cursor?.count==0){
                //dodati u kontakte
                var id:String
                id = if(sent) message.receiverId else message.senderId
                var read:Int=if(sent) 1 else 0
                sql="INSERT INTO "+ CONTACTS_TABLE_NAME+"(userId,read) VALUES('"+id+"','"+
                        read+"')"
                db?.execSQL(sql)
            }
        }
    }
    fun getMessages(userId:String, self:Boolean=false): MutableList<Message>? {
        onCreate(db)
        var sql:String
        if(!self)
            sql="SELECT * FROM "+ MESSAGES_TABLE_NAME+" WHERE senderId='"+userId+"' OR receiverId='"+userId+"' ORDER BY timestamp ASC"
        else
            sql="SELECT * FROM "+ MESSAGES_TABLE_NAME+" WHERE senderId='"+userId+"' AND receiverId='"+userId+"' ORDER BY timestamp ASC"
        var cursor=db?.rawQuery(sql,null)
        if(cursor?.count!! >0){
            var messagesList:MutableList<Message> =mutableListOf()
            var idIndex=cursor.getColumnIndexOrThrow("_id")
            var senderIdIndex=cursor.getColumnIndexOrThrow("senderId")
            var receiverIdIndex=cursor.getColumnIndexOrThrow("receiverId")
            var messageIndex=cursor.getColumnIndexOrThrow("messagge")
            var timestampIndex=cursor.getColumnIndexOrThrow("timestamp")
            while(cursor.moveToNext()){
                var cal:Calendar= Calendar.getInstance()
                cal.timeInMillis=cursor.getLong(timestampIndex)
                messagesList.add(
                    Message(
                        cursor.getString(idIndex),
                        cursor.getString(senderIdIndex),
                        cursor.getString(receiverIdIndex),
                        cursor.getString(messageIndex),
                        cal.time,
                        cal
                        )
                    )
                Log.d("main",cal.time.toString())
            }
            return messagesList
        }
        return null
    }

    fun getContacts(): MutableList<ChatPreview>? {
        onCreate(db)
        var sql="SELECT * FROM "+ CONTACTS_TABLE_NAME
        var cursor=db?.rawQuery(sql,null)
        if(cursor?.count!! >0){
            var contactList:MutableList<ChatPreview> =mutableListOf()
            var userIdIndex=cursor.getColumnIndexOrThrow("userId")
            var readIndex=cursor.getColumnIndexOrThrow("read")
            while(cursor.moveToNext()){
                contactList.add(ChatPreview(cursor.getString(userIdIndex),cursor.getInt(readIndex)==1))
            }
            Log.d("main",contactList.size.toString())
            return contactList
        }
        return null
    }

    fun deleteDB() {
        var sql="DROP TABLE IF EXISTS "+ CONTACTS_TABLE_NAME
        db?.execSQL(sql)
        sql="DROP TABLE IF EXISTS "+ MESSAGES_TABLE_NAME
        db?.execSQL(sql)
    }
}