package com.example.volunteermanagementapp


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class VolunteerAdapter(private val volunteerList: ArrayList<Volunteer>):
    RecyclerView.Adapter<VolunteerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_volunteer,
            parent,false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VolunteerAdapter.MyViewHolder, position: Int) {

        val volunteer : Volunteer = volunteerList[position]
        holder.volunteer_available_date.text = volunteer.volunteer_available_date
        holder.volunteer_available_from.text = volunteer.volunteer_available_from
        holder.volunteer_available_till.text = volunteer.volunteer_available_till
        holder.volunteer_email.text = volunteer.volunteer_email
        holder.volunteer_name.text = volunteer.volunteer_name
        holder.volunteer_phone.text = volunteer.volunteer_phone


    }

    override fun getItemCount(): Int {
        return volunteerList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){


        val volunteer_available_date : TextView = itemView.findViewById(R.id.availableDate)
        val volunteer_available_from : TextView = itemView.findViewById(R.id.availableStart)
        val volunteer_available_till : TextView = itemView.findViewById(R.id.availableEnd)
        val volunteer_email : TextView = itemView.findViewById(R.id.volunteerEmail)
        val volunteer_name : TextView = itemView.findViewById(R.id.volunteerName)
        val volunteer_phone : TextView = itemView.findViewById(R.id.volunteerNumber)





    }
}