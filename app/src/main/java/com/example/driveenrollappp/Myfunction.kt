package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.database.Cursor
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class Myfunction {

    companion object{

        @SuppressLint("SuspiciousIndentation")
        fun getValue(cursor: Cursor, columnName:String):String{
            var value:String = ""

                try {
                var col = cursor.getColumnIndex(columnName)
                    value =cursor.getString(col)
                } catch (e:Exception){
                e.printStackTrace()
                    Log.d("My Function","getValue ${e.printStackTrace()}")
                    value =""
            }
            return value
        }

        fun returnSQLDataFormat(date:String): String {
            try {
                if (date.trim().isNotEmpty()){
                    val dateFormat1 = SimpleDateFormat("dd/MM/yyyy")
                    val firstDate = dateFormat1.parse(date)
                    val dateFormat2 = SimpleDateFormat("yyyy-MM-dd")
                    return dateFormat2.format(firstDate)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return  ""
        }



        fun returnuserDataFormat(date:String): String {
            try {
                if (date.trim().isNotEmpty()){
                    val dateFormat1 = SimpleDateFormat("yyyy-MM-dd")
                    val firstDate = dateFormat1.parse(date)
                    val dateFormat2 = SimpleDateFormat("dd/MM/yyyy")
                    return dateFormat2.format(firstDate)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return  ""
        }

        fun getCurrentDate():String{
            var  txtDate = ""
            try {
              txtDate=  SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }catch (e:Exception){
                e.printStackTrace()
            }
            return txtDate;
        }

    }
}