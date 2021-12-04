package com.example.erwancastioni.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erwancastioni.todo.databinding.FragmentTaskListBinding
import com.example.erwancastioni.todo.form.FormActivity
import java.util.*

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding

    private val taskList = mutableListOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task

        val oldTask = taskList.firstOrNull { it.id == task?.id }
        if (oldTask != null) taskList.remove(oldTask)

        if (task != null) {
            taskList.add(task)
            adapter.submitList(taskList.toList())
        }
    }

    lateinit var adapter: TaskListAdapter

    val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList.remove(task)
            adapter.submitList(taskList.toList())
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
        }
    }

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
        adapter = TaskListAdapter(adapterListener)
        binding.recyclerView.adapter = adapter

        adapter.submitList(taskList.toList())

        binding.addTaskBtn.setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }
    }
}