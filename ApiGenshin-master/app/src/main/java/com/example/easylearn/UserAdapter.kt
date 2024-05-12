package com.example.easylearn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    private lateinit var context: Context
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(id: Int, name: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //val img = itemView.findViewById<ImageView>(R.id.imageView)
        val name = itemView.findViewById<TextView>(R.id.tvName)
        //val email = itemView.findViewById<TextView>(R.id.tvEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.user_items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]

        holder.name.text = user.name

        // Cambiar el color del nombre si el personaje está marcado como favorito
        if (user.isFavorite) {
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.favoriteColor))
        } else {
            holder.name.setTextColor(ContextCompat.getColor(context, R.color.defaultColor))
        }

        // Manejar el clic en el elemento
        holder.itemView.setOnClickListener {
            listener.onItemClick(user.id, user.name)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}