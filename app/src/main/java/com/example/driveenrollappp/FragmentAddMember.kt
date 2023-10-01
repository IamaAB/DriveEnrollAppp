package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.app.Activity
import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.DatabaseUtils
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.driveenrollappp.databinding.FragmentAddMemberBinding

import java.text.SimpleDateFormat
import java.util.*

class FragmentAddMember : Fragment() {
    private lateinit var binding: FragmentAddMemberBinding
    private var db: DB? = null
    private var twoMonth: String? = null
    private var fourMonth: String? = null
    private var eightMonth: String? = null
    private var twelveMonth: String? = null
    private var fourteenMonth: String? = null
    private var gender ="Male"
    // Declare a constant for image capture request code
    private val IMAGE_CAPTURE_REQUEST_CODE = 100
    private val GALLERY_PERMISSION_REQUEST_CODE = 101
    // Declare a variable to store the image path
    private var imagePath: String? = null
    private var ID=""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = activity?.let { DB(it) }
        activity?.title="Add Members"
ID = arguments!!.getString("ID").toString()
        val cal = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view1, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.edtJoining.setText(sdf.format(cal.time))
            }
        binding.imgPic.setOnClickListener {
            requestGalleryPermission()
        }

        binding.spMembership.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = binding.spMembership.selectedItem.toString().trim()

                if (value == "Select") {
                    binding.edtExpire.setText("")
                    calculateTotal()
                } else {
                    if (binding.edtJoining.text.toString().trim().isNotEmpty()) {
                        calculateExpireDate(getMembershipDuration(value), binding.edtExpire)
                        calculateTotal()
                    } else {
                        showToast("Select Joining Date first")
                        binding.spMembership.setSelection(0)
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.edtdiscount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Do nothing
            }

            override fun afterTextChanged(p0: Editable?) {
                val discount = binding.edtdiscount.text.toString().trim()
                if (discount.isNotEmpty()) {
                    calculateTotal()
                }
            }
        })

            binding.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
                when(id){
                    R.id.rdMale->{
                    gender = "Male"
                    }
                    R.id.rdFeMale->{
                        gender = "Female"
                    }

                }
            }

binding.btnAddMembersave.setOnClickListener {
 if (validate()){
saveData()
 }

}

        binding.imgPicDate.setOnClickListener {
            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        getFee()

binding.btnActiveInactive.setOnClickListener {
    try {
        if(getStatus()=="A"){
            val sqlQuery = "UPDATE MEMBER SET STATUS='D' WHERE ID='$ID'"
            db?.fireQuery(sqlQuery)
            showToast("Member is Inactive now")
        }else{
            val sqlQuery = "UPDATE MEMBER SET STATUS='A' WHERE ID='$ID'"
            db?.fireQuery(sqlQuery)
            showToast("Member is Active now")
        }


    }catch (e:Exception){
        e.printStackTrace()
    }


}
        if (ID.trim().isNotEmpty()){
            if (getStatus()=="A"){
                binding.btnActiveInactive.text="Inactive"
                binding.btnActiveInactive.visibility=View.VISIBLE
            }else{
                binding.btnActiveInactive.text="Active"
                binding.btnActiveInactive.visibility=View.VISIBLE
            }
            loadData()
        }else{
            binding.btnActiveInactive.visibility=View.GONE
        }


    }

    private fun getStatus():String{
        var status =""
        try{
            val sqlQuery = "SELECT STATUS FROM MEMBER WHERE ID ='$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if (it.count>0){
                    status= Myfunction.getValue(it,"STATUS")
                }
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return status
    }


    private fun getFee() {
        try {
                    val sqlQuery = "SELECT * FROM FEE WHERE ID = '1'"
    db?.fireQuery(sqlQuery).use { cursor ->
                if (cursor != null) {
                    if (cursor.count > 0) {
                        twoMonth = cursor?.let { Myfunction.getValue(it, "TWO_WEEK") }
                        fourMonth = cursor?.let { Myfunction.getValue(it, "FOUR_WEEK") }
                        eightMonth = cursor?.let { Myfunction.getValue(it, "EIGHT_WEEK") }
                        twelveMonth = cursor?.let { Myfunction.getValue(it, "TWELVE_WEEK") }
                        fourteenMonth = cursor?.let { Myfunction.getValue(it, "FOURTEEN_WEEK") }
                    }
                }
            }

            calculateTotal() // Call calculateTotal() here to update the total amount
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotal() {
        val month = binding.spMembership.selectedItem.toString().trim()
        val discount = binding.edtdiscount.text.toString().trim()

        val selectedMembershipDuration = getMembershipDuration(month)
        val membershipFee = getMembershipFee(selectedMembershipDuration)

        if (membershipFee != null) {
            val discountAmount = (membershipFee * (discount.toDoubleOrNull() ?: 0.0)) / 100
            val total = membershipFee - discountAmount
            binding.edtamount.setText(String.format("%.2f",total))
        } else {
            binding.edtamount.setText("0.00")
        }
    }

    private fun getMembershipDuration(month: String): Int {
        return when (month) {
            "2 month" -> 2
            "4 month" -> 4
            "8 month" -> 8
            "12 month" -> 12
            "14 month" -> 14
            else -> 0
        }
    }

    private fun getMembershipFee(month: Int): Double? {
        return when (month) {
            2 -> twoMonth?.toDoubleOrNull()
            4 -> fourMonth?.toDoubleOrNull()
            8 -> eightMonth?.toDoubleOrNull()
            12 -> twelveMonth?.toDoubleOrNull()
            14 -> fourteenMonth?.toDoubleOrNull()
            else -> null
        }
    }




    @SuppressLint("SimpleDateFormat")
    private fun calculateExpireDate(month: Int, edtExpiry: EditText) {
        val dtStart = binding.edtJoining.text.toString().trim()
        if (dtStart.isNotEmpty()) {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date1 = format.parse(dtStart)

            val cal = Calendar.getInstance()
            if (date1 != null) {
                cal.time = date1
            }
            cal.add(Calendar.MONTH, month)

            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            edtExpiry.setText(sdf.format(cal.time))
        } else {
            showToast("Select Joining Date first")
        }
    }

    private fun showToast(value: String) {
        Toast.makeText(activity, value, Toast.LENGTH_LONG).show()
    }

    private  fun validate():Boolean{
        if (binding.edtFirstName.text.toString().trim().isEmpty()){
          showToast("Enter first name")
            return false
        } else if (binding.edtLastName.text.toString().trim().isEmpty()){
            showToast("Enter last name")
            return false
        }else if (binding.edtAge.text.toString().trim().isEmpty()){
            showToast("Enter age")
            return false
        }else if (binding.edtMobile.text.toString().trim().isEmpty()){
            showToast("Enter mobile no ")
            return false
        }else if (binding.edtJoining.text.toString().trim().isEmpty()) {
            showToast("Select Joining Date")
            return false
        }else if (binding.edtExpire.text.toString().trim().isEmpty()) {
            showToast("Select Membership Period ")
            return false
        }
        return true
    }


    private fun saveData() {
        try {
            var myIncrementId= ""
            if(ID.trim().isEmpty()){
                myIncrementId =getIncremented()
            }else{
                myIncrementId=ID
            }

            val sqlQuery = "INSERT OR REPLACE INTO MEMBER (ID, FIRST_NAME, LAST_NAME, GENDER, AGE, WEIGHT, MOBILE, ADDRESS, DATE_OF_JOINING, MEMBERSHIP, EXPIRE_ON, DISCOUNT, TOTAL, IMAGE_PATH, STATUS) VALUES " +
                    "('${myIncrementId}', ${DatabaseUtils.sqlEscapeString(binding.edtFirstName.text.toString().trim())}, " +
                    "${DatabaseUtils.sqlEscapeString(binding.edtLastName.text.toString().trim())}, '$gender', " +
                    "'${binding.edtAge.text.toString().trim()}', ${binding.edtweight.text.toString().trim()}, " +
                    "${binding.edtMobile.text.toString().trim()}, ${DatabaseUtils.sqlEscapeString(binding.edtAddress.text.toString().trim())}, " +
                    "'${Myfunction.returnSQLDataFormat(binding.edtJoining.text.toString().trim())}', '${binding.spMembership.selectedItem.toString().trim()}', " +
                    "'${Myfunction.returnSQLDataFormat(binding.edtExpire.text.toString().trim())}', '${binding.edtdiscount.text.toString().trim()}', " +
                    "'${binding.edtamount.text.toString().trim()}', ${imagePath?.let { "'$it'" } ?: "NULL"}, 'A')"

            db?.executeQuery(sqlQuery)
            showToast("Data Saved Successfully")

      if (ID.trim().isEmpty()){
          clearData()
      }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getIncremented():String{
        var incrementId = ""
        try {
var sqlQuery = "SELECT IFNULL(MAX(ID)+1,'1') AS ID FROM MEMBER"
            db?.fireQuery(sqlQuery)?.use {
                if(it.count>0){
                    incrementId = Myfunction.getValue(it,"ID")
                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        return incrementId
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // Process the selected image URI, save to database, etc.
            imagePath = selectedImageUri.toString()
            binding.imgPic.setImageURI(selectedImageUri)
        }
    }


    // Function to request gallery permission
    private fun requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                GALLERY_PERMISSION_REQUEST_CODE
            )
        }
    }

    // Function to open gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false) // Set to true if you want to allow multiple image selection
        }
        startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    showToast("Gallery permission denied")
                }
            }

        }
    }


    private fun clearData(){
        binding.edtFirstName.setText("")
        binding.edtLastName.setText("")
        binding.edtAge.setText("")
        binding.edtweight.setText("")
        binding.edtMobile.setText("")
        binding.edtAddress.setText("")
        binding.edtJoining.setText("")
        binding.edtExpire.setText("")
        binding.edtdiscount.setText("")
        binding.edtamount.setText("")
        imagePath=""

        Glide.with(this)
            .load(R.drawable.boy)
            .into(binding.imgPic)


    }

    @SuppressLint("SimpleDateFormat")
    private fun loadData(){
        try{

val sqlQuery = "SELECT * FROM MEMBER WHERE ID = '$ID'"
            db?.fireQuery(sqlQuery)?.use {
                if(it.count>0){

                val    firstName = Myfunction.getValue(it, "FIRST_NAME")
                    val  lastName = Myfunction.getValue(it, "LAST_NAME")
                    val  age = Myfunction.getValue(it, "AGE")
                    val    gender = Myfunction.getValue(it, "GENDER")
                    val  weight = Myfunction.getValue(it, "WEIGHT")
                    val  mobileNo = Myfunction.getValue(it, "MOBILE")
                    val    address = Myfunction.getValue(it, "ADDRESS")
                    val   dateofJoining = Myfunction.getValue(it, "DATE_OF_JOINING")
                 val membership = Myfunction.getValue(it,"MEMBERSHIP")
                    val   expiryDate = Myfunction.getValue(it, "EXPIRE_ON")
                    val   discount = Myfunction.getValue(it, "DISCOUNT")
                    val   total = Myfunction.getValue(it, "TOTAL")
                    imagePath=Myfunction.getValue(it,"IMAGE_PATH")


                    binding.edtFirstName.setText(firstName)
                    binding.edtLastName.setText(lastName)
                    binding.edtAge.setText(age)
                    binding.edtweight.setText(weight)
                    binding.edtMobile.setText(mobileNo)
                    binding.edtAddress.setText(address)
                    binding.edtJoining.setText(Myfunction.returnuserDataFormat(dateofJoining))

                    if(imagePath!!.isNotEmpty()){
                        Glide.with(this)
                            .load(this.imagePath)
                            .into(binding.imgPic )
                    }else{
                        if(gender=="Male"){
                            Glide.with(this)
                                .load(R.drawable.boy)
                                .into(binding.imgPic)
                        }else{
                            Glide.with(this)
                                .load(R.drawable.girl)
                                .into(binding.imgPic)
                        }
                    }

                    if (membership.trim().isNotEmpty()){
                        when(membership){
                            "2 month"->{
                              binding.spMembership.setSelection(1)
                            }
                            "4 month"->{
                                binding.spMembership.setSelection(2)
                            }
                            "8 month"->{
                                binding.spMembership.setSelection(3)
                            }
                            "12 month"->{
                                binding.spMembership.setSelection(4)
                            }
                            "14 month"->{
                                binding.spMembership.setSelection(5)
                        }else->{
                            binding.spMembership.setSelection(0)
                        }

                        }
                    }
                    if(gender =="Male"){
                        binding.radioGroup.check(R.id.rdMale)
                    }else{
                        binding.radioGroup.check(R.id.rdFeMale)
                    }

                    binding.edtExpire.setText(Myfunction.returnuserDataFormat(expiryDate))
                    binding.edtamount.setText(total)
                    binding.edtdiscount.setText(discount)


                }
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}

