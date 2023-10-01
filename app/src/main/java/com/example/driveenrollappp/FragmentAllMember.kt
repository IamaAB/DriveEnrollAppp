package com.example.driveenrollappp

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
import com.example.driveenrollappp.databinding.FragmentAllMemberBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class FragmentAllMember : BaseFragment() {
    private val TAG = "FragmentAllMember"
    var db:DB?=null

    var adapter: AdapterLoadMember?=null
    var arraylist:ArrayList<AllMember> = ArrayList()
    private lateinit var binding:FragmentAllMemberBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity?.title="Dashboard"
        binding = FragmentAllMemberBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db= activity?.let { DB(it) }
        binding.rdGroupMember.setOnCheckedChangeListener { radioGroup, id ->
            when(id){
                R.id.rdActiveMember ->{
                    loadData("A")
                }
                R.id.rdInactiveMember ->{
                    loadData("D")
                }
            }

        }

        binding.imgAddMember.setOnClickListener {
         loadFragment("")
        }
        binding.edtAllMemberSearch.addTextChangedListener(object : TextWatcher {
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
        loadData("A")
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


     private fun loadData(memberStatus: String) {
         lifecycleScope.executeAsyncTask(
             onPreExecute = {
                 showDialog("Processing...")
             },
             doInBackground = {
                 arraylist.clear()
                 val sqlQuery = "SELECT * FROM MEMBER WHERE STATUS = '$memberStatus'"
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
                     binding.recyclerViewMember.visibility = View.VISIBLE
                     binding.txtAllMemberNDF.visibility = View.GONE

                     adapter = AdapterLoadMember(arraylist)
                     binding.recyclerViewMember.layoutManager = LinearLayoutManager(activity)
                     binding.recyclerViewMember.adapter = adapter



                     adapter?.onClick {
                         loadFragment(it)
                     }
                 } else {
                     binding.recyclerViewMember.visibility = View.GONE
                     binding.txtAllMemberNDF.visibility = View.VISIBLE
                 }
                 closeDialog()
             })
     }

     private  fun loadFragment(id:String){
         val fragment = FragmentAddMember()
         val args = Bundle()
         args.putString("ID",id)
         fragment.arguments = args

         val fragmentManager:FragmentManager?=fragmentManager
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