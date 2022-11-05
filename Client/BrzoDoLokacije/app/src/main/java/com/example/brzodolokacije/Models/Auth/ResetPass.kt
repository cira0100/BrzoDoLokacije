package com.example.brzodolokacije.Models.Auth

data class ResetPass(var email:String,var kod:String,var newpass:String)
data class JustMail(var email:String)