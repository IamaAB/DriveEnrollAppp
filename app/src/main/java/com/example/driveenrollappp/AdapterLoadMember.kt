package com.example.driveenrollappp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.driveenrollappp.databinding.AllMembersListResBinding
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent

class AdapterLoadMember(var arrayList: ArrayList<AllMember>): RecyclerView.Adapter<AdapterLoadMember.MyViewHolder>() {


    private var onClick: ((String)->Unit)?=null
    fun onClick(onClick: ((String)->Unit )) {
        this.onClick = onClick
    }

    class MyViewHolder(val binding: AllMembersListResBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = AllMembersListResBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }



    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        with(holder){
            with(arrayList[position]){
                binding.txtAdapterName.text = this.firstName + " "+this.lastName
               binding.txtAdapterAge.text="Age : "+this.age
                binding.txtAdapterWeight.text="Car No : "+this.weight
                binding.txtAdapterMobile.text= "Mobile : "+this.mobile
                binding.txtAddress.text= "Address : "+this.address
                binding.txtExpiry.text= "Expiry : "+this.expiryDate


                if(this.image.isNotEmpty()){
                    Glide.with(holder.itemView.context)
                        .load(this.image)
                        .into(binding.imgAdapterPic  )
                }else{
                    if(this.gender=="Male"){
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.boy)
                            .into(binding.imgAdapterPic)
                    }else{
                        Glide.with(holder.itemView.context)
                            .load(R.drawable.girl)
                            .into(binding.imgAdapterPic)
                    }
                }

                binding.layoutMembersList.setOnClickListener {
onClick?.invoke(this.id)
                }
            }
        }


    }
    override fun getItemCount(): Int {
return arrayList.size
    }


    fun updatelist(list :ArrayList<AllMember>){
         arrayList = list
        notifyDataSetChanged()
    }
}