package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.driveenrollappp.databinding.FragmentAddUppdateFeeBinding

class FragmentAddUppdateFee : Fragment() {
   private lateinit var binding: FragmentAddUppdateFeeBinding
    var db:DB?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddUppdateFeeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        activity?.title="Add/Update Fee"
        binding.btnMemberShip.setOnClickListener {
            if (validate()) {
                saveData()
            }
        }

        fillData()
    }

    private fun validate():Boolean{
        if(binding.edttwoweek.text.toString().trim().isEmpty())
        {
           showToast("Enter Two Week Fees")
            return false
        }
        else if(binding.edtfourweek.text.toString().trim().isEmpty())
        {
            showToast("Enter Four Week Fees")
            return false
        }
        else if(binding.edteightweeks.text.toString().trim().isEmpty())
        {
            showToast("Enter Eight Week Fees")
            return false
        }
        else if(binding.edttwelveweeks.text.toString().trim().isEmpty())
        {
            showToast("Enter Twelve Week Fees")
            return false
        }
        else if(binding.edtfourteenweeks.text.toString().trim().isEmpty())
        {
            showToast("Enter Fourteen Week Fees")
            return false
        }


        return true
    }

    @SuppressLint("SuspiciousIndentation")
    private fun saveData(){
     try {
         val sqlQuery = "INSERT OR REPLACE INTO FEE(ID,TWO_WEEK,FOUR_WEEK,EIGHT_WEEK,TWELVE_WEEK ,FOURTEEN_WEEK)VALUES" +
                 "('1','"+binding.edttwoweek.text.toString().trim()+"','"+binding.edtfourweek.text.toString().trim()+"',"+
                 "'"+binding.edteightweeks.text.toString().trim()+"','"+binding.edttwelveweeks.text.toString().trim()+"',"+
                 "'"+binding.edtfourteenweeks.text.toString().trim()+"')"
            db?.executeQuery(sqlQuery)
         showToast("Data Saved Successfully")

     }   catch (e:Exception){
         e.printStackTrace()
     }


    }

    private fun fillData(){
        try {
            val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
            db?.fireQuery(sqlQuery)?.use {

                if (it.count>0) {
                    it.moveToFirst()
                    binding.edttwoweek.setText(Myfunction.getValue(it, "TWO_WEEK"))
                    
                    binding.edtfourweek.setText(Myfunction.getValue(it, "FOUR_WEEK"))
                    binding.edteightweeks.setText(Myfunction.getValue(it, "EIGHT_WEEK"))
                    binding.edttwelveweeks.setText(Myfunction.getValue(it, "TWELVE_WEEK"))
                    binding.edtfourteenweeks.setText(Myfunction.getValue(it, "FOURTEEN_WEEK"))
                }
            }

        } catch (e:Exception){
            e.printStackTrace()
        }
    }


 private fun showToast(value: String){
     Toast.makeText(activity,value,Toast.LENGTH_LONG).show()
 }

}