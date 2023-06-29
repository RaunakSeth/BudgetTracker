package com.example.budgettracker

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class expenseModel(var title:String?=null,
                   var expense:Int?=null,
                   var gorl:String?=null)