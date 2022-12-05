package com.example.brzodolokacije.Models

import java.util.*

data class MessageReceive(
    var senderId:String,
    var messagge:String,
    var timestamp:Date
)

data class MessageSend(
    var receiverId:String,
    var messagge:String
)

data class Message(
    var _id:String,
    var senderId: String,
    var receiverId: String,
    var messagge: String,
    var timestamp: Date,
    var usableTimeStamp:Calendar
)

data class ChatPreview(
    var userId:String,
    var read:Boolean,
    var username:String
)