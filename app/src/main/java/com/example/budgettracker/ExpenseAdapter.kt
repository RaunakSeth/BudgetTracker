package com.example.budgettracker

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val expenseList: ArrayList<expenseModel>): RecyclerView.Adapter<ExpenseAdapter.expenseViewHolder>() {


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): expenseViewHolder {
      val itemView=LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_expense,parent,false)
        return expenseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return expenseList.size
    }

    override fun onBindViewHolder(holder: expenseViewHolder, position: Int) {
        var expense:String="₹"+expenseList[position].expense.toString()
        holder.title.setText(expenseList[position].title)
        holder.expense.setText(expense)
        if(expenseList[position].gorl=="Gain")
            holder.expense.setTextColor(Color.parseColor("#0DBF14"))
        if(expenseList[position].gorl=="Loss")
            holder.expense.setTextColor(Color.parseColor("#FF0000"))
    }
    class expenseViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
           val title:TextView=itemView.findViewById(R.id.recyclertitle)
           val expense:TextView=itemView.findViewById(R.id.recyclerexpense)
    }
}