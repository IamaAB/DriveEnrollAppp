package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.driveenrollappp.databinding.ActivityLoginBinding
import com.example.driveenrollappp.databinding.ActivityMainBinding
import com.example.driveenrollappp.databinding.ForgetPasswordDialogBinding

class LoginActivity : AppCompatActivity() {
    var db: DB? = null
    var session: SessionManager? = null
    var editUsername : EditText ?=null
    var editPassword : EditText ?=null

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = DB(this)
        session = SessionManager(this)
        editUsername = binding.loginUsername
        editPassword = binding.loginNumberpassword

    binding.btnLogin.setOnClickListener {
     if (validateLogin())
     {
         getLogin()
     }

    }
        binding.txtForgotPassword.setOnClickListener {

showDialog()
        }

    }


    private fun getLogin(){
try {
val sqlQuery = " SELECT * FROM ADMIN WHERE USER_NAME ='" +editUsername?.text.toString().trim()+"' AND PASSWORD = '"+editPassword?.text.toString().trim()+"' AND ID = '1'"

       db?.fireQuery(sqlQuery)?.use {
    if (it.count > 0) {
        session?.setLogin(true)
        Toast.makeText(this,getString(R.string.succesfully_login), Toast.LENGTH_LONG).show()
        val intent = Intent (this,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
           else{
        session?.setLogin(false)
        Toast.makeText(this,getString(R.string.loginfailed), Toast.LENGTH_LONG).show()

    }
    }

}catch (e:Exception)
{
    e.printStackTrace()
}
    }

    private fun validateLogin():Boolean{
         if (editUsername?.text.toString().trim().isEmpty())
 {
        Toast.makeText(this, "Enter User Name ",Toast.LENGTH_LONG).show()
     return false
         }else if (editPassword?.text.toString().trim().isEmpty())
 {
     Toast.makeText(this, "Enter Password ",Toast.LENGTH_LONG).show()
     return false
 }

return true
    }
private fun showDialog(){

val binding2 = ForgetPasswordDialogBinding.inflate(LayoutInflater.from(this))
    val dialog = Dialog(this,R.style.AlertDialogCustom)
    dialog.setContentView(binding2.root)
    dialog.setCancelable(false)
    dialog.show()

    binding2.btnForgetSubmit.setOnClickListener {
        if (binding2.edtForgetMobile.text.toString().trim().isNotEmpty()){
            checkData(binding2.edtForgetMobile.text.toString().trim(),binding2.txtYourPassword)

        }else{
            Toast.makeText(this,"Enter Mobile No.",Toast.LENGTH_LONG).show()
        }

    }
    binding2.imgBackButton.setOnClickListener {
        dialog.dismiss()
    }

}

    @SuppressLint("SetTextI18n")
    private fun checkData(mobile:String, txtShowPassword:TextView){
        try {
            val sqlQuery = "Select * FROM ADMIN  WHERE MOBILE = '$mobile'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count > 0) {
                    val password = Myfunction.getValue(it,"PASSWORD")
                    txtShowPassword.visibility = View.VISIBLE
                    txtShowPassword.text= "Your Password is : $password"

                }else{
                    Toast.makeText(this,"InCorrect Mobile Number ",Toast.LENGTH_LONG).show()
                    txtShowPassword.visibility = View.GONE
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }

    }


}
