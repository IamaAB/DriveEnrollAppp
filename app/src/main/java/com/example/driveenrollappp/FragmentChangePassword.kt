package com.example.driveenrollappp

import android.database.DatabaseUtils
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.driveenrollappp.databinding.FragmentChangePasswordBinding
import com.example.driveenrollappp.databinding.FragmentFeePendingBinding

class FragmentChangePassword : Fragment() {

private lateinit var binding:FragmentChangePasswordBinding
    private var db:DB?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentChangePasswordBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title="Change Password"
db= activity?.let { DB(it) }

        filloldMobile()
        binding.btnchangepassword.setOnClickListener {
            try {
                if (binding.edtnewpassword.text.toString().trim().isNotEmpty()) {
                    if (binding.edtnewpassword.text.toString()
                            .trim() == binding.edtconfirmpassword.text.toString().trim()
                    ) {
                        val sqlQuery = "UPDATE ADMIN SET PASSWORD=" + DatabaseUtils.sqlEscapeString(
                            binding.edtnewpassword.text.toString().trim()
                        ) + ""


                        db?.executeQuery(sqlQuery)
                        showToast("Password Changed Successfully")
                    } else {
                        showToast("Password Not matched")
                    }

                }else{
                    showToast("Enter New Password")
                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }


        binding.btnchagemobilenumber.setOnClickListener {
            try {
if(binding.edtnewNumber.text.toString().trim().isNotEmpty()){
    val sqlQuery = "UPDATE ADMIN SET MOBILE = '"+binding.edtnewNumber.text.toString().trim()+"'"
db?.executeQuery(sqlQuery)
    showToast("Mobile number changed successfully")
}else{
    showToast("Enter New Mobile Number")
}

            }catch (e:Exception){
                e.printStackTrace()
            }
        }

    }


    private fun filloldMobile(){
        try {
val sqlQuery = "SELECT MOBILE FROM ADMIN"
            db?.fireQuery(sqlQuery)?.use {
               val mobile = Myfunction.getValue(it,"MOBILE")
                if (mobile.trim().isNotEmpty()){
                    binding.edtoldnumber.setText(mobile)
                }
            }


        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }
}