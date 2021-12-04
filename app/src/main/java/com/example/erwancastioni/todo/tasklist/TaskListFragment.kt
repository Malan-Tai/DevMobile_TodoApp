package com.example.erwancastioni.todo.tasklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erwancastioni.todo.databinding.FragmentTaskListBinding
import java.util.*

class TaskListFragment : Fragment() {

    private lateinit var binding: FragmentTaskListBinding

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val adapter = TaskListAdapter()
        binding.recyclerView.adapter = adapter

        adapter.submitList(taskList.toList())
        adapter.onClickDelete = { task ->
            taskList.remove(task)
            adapter.submitList(taskList.toList())
        }

        binding.addTaskBtn.setOnClickListener {
            val newTask =
                Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            taskList.add(newTask)
            adapter.submitList(taskList.toList())
        }
    }
}