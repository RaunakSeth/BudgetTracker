package com.example.budgettracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var dbref:DatabaseReference
    private lateinit var expenserecyclerview:RecyclerView
    private lateinit var expenseArrayList: ArrayList<expenseModel>
    private var totexpense:Int=0
    private lateinit var total:TextView
    private lateinit var budget:TextView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbref=FirebaseDatabase.getInstance().getReference("raunak2125csit1074")
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d(TAG, "connected")
                } else {
                    Log.d(TAG, "not connected")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled")
            }
        })
        total=findViewById(R.id.total)
        budget=findViewById(R.id.budget)
        val expense:FloatingActionButton=findViewById(R.id.add)
        total.setOnClickListener(){
            val dialog=Dialog(this,R.style.Dialog)
            dialog.apply { setTitle("Budget Allocation") }.show()
            dialog.setContentView(R.layout.dialog_budget)
            dialog.setCancelable(false)
            val submit:Button =dialog.findViewById(R.id.submit)
            val no:EditText=dialog.findViewById(R.id.amount)
            submit.setOnClickListener(){
                val num: String = no.text.toString()
                if(num!="")
                {
                    dbref.child("totexpense").setValue(num)
                    total.setText("₹$num")
                    budget.setText("₹${num.toInt()-totexpense}")
                    dialog.dismiss()
                }
                else
                {
                    Toast.makeText(applicationContext,"Please enter in text Field",Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
        expense.setOnClickListener()
        {
            Toast.makeText(applicationContext,"By Default Expense is considered if not specified otherwise",Toast.LENGTH_LONG).show()
            val dialog=Dialog(this,R.style.Dialog)
            dialog.apply { setTitle("Add Expense") }.show()
            dialog.setContentView(R.layout.dialog_expense)
            dialog.setCancelable(false)
            val submit:Button =dialog.findViewById(R.id.expensesubmit)
            val no:EditText=dialog.findViewById(R.id.expenseamount)
            val title:EditText=dialog.findViewById(R.id.expensetitle)
            val spin:AutoCompleteTextView=dialog.findViewById(R.id.expensespin)
            val listgorl=ArrayList<String>()
            listgorl.add("Gain")
            listgorl.add("Loss")
            val StringAdapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,listgorl)
            spin.setAdapter(StringAdapter)
            var gorl:String=""
            spin.setOnItemClickListener{
                    adapterView,view,i,l -> gorl=adapterView.getItemAtPosition(i).toString()}
            submit.setOnClickListener(){
                val num: String = no.text.toString()
                val titleexp:String=title.text.toString()

                if(num!="" && titleexp!="" && gorl!="")
                {
                    updateDatabse(num,titleexp,gorl)
                    dialog.dismiss()
                }
                else
                {
                    Toast.makeText(applicationContext,"Please enter text in empty text Field",Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }
        expenserecyclerview=findViewById(R.id.expenserecyclerview)
        expenserecyclerview.layoutManager=LinearLayoutManager(this)
        expenserecyclerview.setHasFixedSize(true)
        expenseArrayList= arrayListOf<expenseModel>()
        getexpenseData()
    }
    fun updateDatabse(num:String, titlexp:String, gorl:String)
    {
         val expID=dbref.push().key!!
         val expense=expenseModel(titlexp,num.toInt(),gorl)
        dbref.child("expenses").child(expID).setValue(expense)
            .addOnSuccessListener{
                Toast.makeText(applicationContext,"Expense Added",Toast.LENGTH_LONG).show()
            }.addOnFailureListener{err->
                Toast.makeText(applicationContext,"Expense not added due to error ${err.message}",Toast.LENGTH_LONG).show()
            }

    }
    fun getexpenseData()
    {
        var ref:DatabaseReference=FirebaseDatabase.getInstance().getReference("raunak2125csit1074").child("totexpense")
        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                        val text = snapshot.getValue().toString()
                        total.setText("₹$text")
                        budget.setText("₹${text.toInt()-totexpense}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
         dbref.child("expenses").addValueEventListener(object: ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {

                 if(snapshot.exists())
                        {
                            expenseArrayList.clear()
                            totexpense=0
                            for(Snapshot in snapshot.children){
                                val expense=Snapshot.getValue(expenseModel::class.java)
                                 expenseArrayList.add(expense!!)
                                if(Snapshot.child("gorl").getValue().toString()=="Gain")
                                    totexpense-=(Snapshot.child("expense").getValue()).toString().toInt()
                                if(Snapshot.child("gorl").getValue().toString()=="Loss")
                                    totexpense+=(Snapshot.child("expense").getValue()).toString().toInt()
                            }
                            expenserecyclerview.adapter=ExpenseAdapter(expenseArrayList)
                            var num:String=total.text.toString()
                            num=num.drop(1)
                            budget.setText("₹${num.toInt()-totexpense}")
                        }
             }

             override fun onCancelled(error: DatabaseError) {

             }

         })
    }
}