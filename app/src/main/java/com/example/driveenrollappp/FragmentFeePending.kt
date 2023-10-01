package com.example.driveenrollappp

import android.os.Binder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.driveenrollappp.databinding.FragmentFeePendingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class FragmentFeePending : BaseFragment() {

 private lateinit var binding: FragmentFeePendingBinding
    var db:DB?=null
    var adapter: AdapterLoadMember?=null
    var arraylist:ArrayList<AllMember> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = FragmentFeePendingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title="Pending Fee"
        db = activity?.let { DB(it) }

        binding.edtPendingSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                myFilter(p0.toString())
            }

        })

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
    private fun loadData() {
        lifecycleScope.executeAsyncTask(
            onPreExecute = {
                showDialog("Processing...")
            },
            doInBackground = {
                arraylist.clear()
                val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS = 'A' AND EXPIRE_ON<='"+Myfunction.getCurrentDate()+"'"
                val cursor = db?.fireQuery(sqlQuery)

                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        val list = AllMember(
                            id = Myfunction.getValue(cursor, "ID"),
                            firstName = Myfunction.getValue(cursor, "FIRST_NAME"),
                            lastName = Myfunction.getValue(cursor, "LAST_NAME"),
                            age = Myfunction.getValue(cursor, "AGE"),
                            gender = Myfunction.getValue(cursor, "GENDER"),
                            weight = Myfunction.getValue(cursor, "WEIGHT"),
                            mobile = Myfunction.getValue(cursor, "MOBILE"),
                            address = Myfunction.getValue(cursor, "ADDRESS"),
                            image = Myfunction.getValue(cursor, "IMAGE_PATH"),
                            dateofJoining = Myfunction.returnuserDataFormat(Myfunction.getValue(cursor, "DATE_OF_JOINING")),
                            expiryDate = Myfunction.returnuserDataFormat(Myfunction.getValue(cursor, "EXPIRE_ON"))
                        )
                        arraylist.add(list)
                    } while (cursor.moveToNext())
                }

                cursor?.close()
            },
            onPostExecute = {
                if (arraylist.size > 0) {
                    binding.recyclerViewPending.visibility = View.VISIBLE
                    binding.txtPendingNDF.visibility = View.GONE

                    adapter = AdapterLoadMember(arraylist)
                    binding.recyclerViewPending.layoutManager = LinearLayoutManager(activity)
                    binding.recyclerViewPending.adapter = adapter



                    adapter?.onClick {
                        loadFragment(it)
                    }
                } else {
                    binding.recyclerViewPending.visibility = View.GONE
                    binding.txtPendingNDF.visibility = View.VISIBLE
                }
                closeDialog()
            })
    }
    fun <R> CoroutineScope.executeAsyncTask(
        onPreExecute: () -> Unit,
        doInBackground: suspend () -> R?,
        onPostExecute: (R?) -> Unit
    ) = launch {
        onPreExecute()
        val result = withContext(Dispatchers.IO) {
            doInBackground()
        }
        onPostExecute(result)
    }
    private  fun loadFragment(id:String){
        val fragment = FragmentAddMember()
        val args = Bundle()
        args.putString("ID",id)
        fragment.arguments = args

        val fragmentManager: FragmentManager?=fragmentManager
        fragmentManager!!.beginTransaction().replace(R.id.frame_container,fragment,"Fragment").commit()

    }

    private fun myFilter(searchValue:String){
        val temp:ArrayList<AllMember> = ArrayList()
        if(arraylist.size>0){
            for (list in arraylist){
                if(list.firstName.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT)) ||
                    list.lastName.toLowerCase(Locale.ROOT).contains(searchValue.toLowerCase(Locale.ROOT)))
                {
                    temp.add(list)
                }
            }
        }
        adapter?.updatelist(temp)
    }


}