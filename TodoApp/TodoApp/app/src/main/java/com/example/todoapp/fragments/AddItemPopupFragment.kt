package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddItemPopupBinding
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.helpers.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddItemPopupFragment : DialogFragment() {

    private lateinit var binding: FragmentAddItemPopupBinding
    private lateinit var listener: InterfaceAddNoteBtnClickListener
    private var toDoData: ToDoData? = null

    companion object {
        const val TAG = "DialogFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String) =
            AddItemPopupFragment().apply {
                arguments = Bundle().apply {
                    putString("taskId", taskId)
                    putString("task", task)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddItemPopupBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){
            toDoData = ToDoData(arguments?.getString("taskId").toString(), arguments?.getString("task").toString())
        }
        binding.todoItemText.setText(toDoData?.task)

        binding.addNoteBtn.setOnClickListener{
            val taskText = binding.todoItemText.text.toString()
            if(taskText.isNotEmpty()){
                if(toDoData == null) {
                    listener?.saveTask(taskText, binding.todoItemText)
                }else{
                    toDoData?.task = taskText
                    listener.editTask(toDoData!!, binding.todoItemText)
                }
            }else{
                Toast.makeText(context, "Enter your task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.closeNoteBtn.setOnClickListener{
            dismiss()
        }
    }

    fun setListener(listener: InterfaceAddNoteBtnClickListener){
        this.listener = listener
    }

    interface InterfaceAddNoteBtnClickListener {
        fun saveTask(todo: String, todoItemText: TextInputEditText)
        fun editTask(toDoData: ToDoData, todoItemText: TextInputEditText)
    }


}