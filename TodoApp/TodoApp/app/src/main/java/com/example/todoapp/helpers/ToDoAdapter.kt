package com.example.todoapp.helpers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.TodoItemBinding
import com.example.todoapp.fragments.AddItemPopupFragment.InterfaceAddNoteBtnClickListener

class ToDoAdapter (private val list:MutableList<ToDoData>):
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    private var listener: InterfaceToDoAdapterClick? = null


    inner class ToDoViewHolder(val binding: TodoItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {

        val binding = TodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task
                binding.deleteTask.setOnClickListener{
                    listener?.onDeleteTaskBtnClicked(this)
                }
                binding.editTask.setOnClickListener{
                    listener?.onEditTaskBtnClicked(this)
                }
            }
        }
    }

    fun setListener(listener: InterfaceToDoAdapterClick){
        this.listener = listener
    }

    interface InterfaceToDoAdapterClick{
        fun onDeleteTaskBtnClicked(toDoData: ToDoData)
        fun onEditTaskBtnClicked(toDoData: ToDoData)
    }

}