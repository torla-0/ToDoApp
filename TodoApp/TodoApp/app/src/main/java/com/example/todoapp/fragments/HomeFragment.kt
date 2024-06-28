package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddItemPopupBinding
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.databinding.FragmentSignInBinding
import com.example.todoapp.databinding.FragmentSignUpBinding
import com.example.todoapp.fragments.AddItemPopupFragment.InterfaceAddNoteBtnClickListener
import com.example.todoapp.helpers.ToDoAdapter
import com.example.todoapp.helpers.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddItemPopupFragment.InterfaceAddNoteBtnClickListener,
    ToDoAdapter.InterfaceToDoAdapterClick {
    private lateinit var auth: FirebaseAuth
    private lateinit var navControl: NavController
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var addItemPopupFragment: AddItemPopupFragment? = null
    private lateinit var adapter: ToDoAdapter
    private lateinit var list:MutableList<ToDoData>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)
        getDataFromFirebase()
        binding.addBtn.setOnClickListener{
            if(addItemPopupFragment != null){
                childFragmentManager.beginTransaction().remove(addItemPopupFragment!!).commit()
            }
            addItemPopupFragment = AddItemPopupFragment()
            addItemPopupFragment!!.setListener(this)
            addItemPopupFragment!!.show(childFragmentManager, "AddItemPopupFragment")
        }

    }

    private fun init(view:View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://todoapp-b3d84-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Tasks").child(auth.currentUser?.uid.toString())

        binding.taskListRecyclerView.setHasFixedSize(true)
        binding.taskListRecyclerView.layoutManager = LinearLayoutManager(context)
        list = mutableListOf()
        adapter = ToDoAdapter(list)
        adapter.setListener(this)
        binding.taskListRecyclerView.adapter = adapter
    }

    override fun saveTask(todo: String, todoItemText: TextInputEditText) {
        database.push().setValue(todo).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Task added", Toast.LENGTH_SHORT).show()
                todoItemText.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            addItemPopupFragment!!.dismiss()
        }
    }

    override fun editTask(toDoData: ToDoData, todoItemText: TextInputEditText) {
        val map = HashMap<String, Any>()
        map[toDoData.taskId]  = toDoData.task
        database.updateChildren(map).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            addItemPopupFragment!!.dismiss()
        }
    }

    private fun getDataFromFirebase(){
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for(taskSnapshot in snapshot.children){
                    val todoTask = taskSnapshot.key?.let{
                        ToDoData(it, taskSnapshot.value.toString())
                    }
                    if(todoTask != null){
                        list.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onDeleteTaskBtnClicked(toDoData: ToDoData) {
        database.child(toDoData.taskId).removeValue().addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(toDoData: ToDoData) {

        if(addItemPopupFragment != null)
            childFragmentManager.beginTransaction().remove(addItemPopupFragment!!).commit()

        addItemPopupFragment = AddItemPopupFragment.newInstance(toDoData.taskId, toDoData.task)
        addItemPopupFragment!!.setListener(this)
        addItemPopupFragment!!.show(childFragmentManager, AddItemPopupFragment.TAG)
    }

}