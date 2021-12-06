package com.example.erwancastioni.todo.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.erwancastioni.todo.databinding.FragmentTaskListBinding
import com.example.erwancastioni.todo.form.FormActivity
import com.example.erwancastioni.todo.network.Api
import com.example.erwancastioni.todo.network.TaskListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding

    private val viewModel: TaskListViewModel by viewModels()

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task

        val oldTask = viewModel.taskList.value.firstOrNull { it.id == task?.id }

        if (task != null) {
            if (oldTask != null) {
                viewModel.editTask(task)
            }
            else {
                viewModel.addTask(task)
            }
        }
    }

    private lateinit var adapter: TaskListAdapter

    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.deleteTask(task)
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(activity, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
        }

        override fun onClickShare(task: Task) {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, task.title + " : " + task.description)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //var i = 0
        //var savedTask = savedInstanceState?.getSerializable("task_$i") as? Task

        //if (savedTask != null) {
        //    taskList.clear()
        //}

        //while (savedTask != null) {
        //    taskList.add(savedTask)
        //    i++
        //    savedTask = savedInstanceState?.getSerializable("task_$i") as? Task
        //}

        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskListAdapter(adapterListener)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.taskList.collect { newList ->
                adapter.submitList(newList)
            }
        }

        binding.addTaskBtn.setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        viewModel.loadTasks()
    }

    //override fun onSaveInstanceState(outState: Bundle) {
    //    super.onSaveInstanceState(outState)
    //    for ((i, task) in viewModel.taskList.value.withIndex()) {
    //        outState.putSerializable("task_$i", task)
    //    }
    //}

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            val info = Api.userWebService.getInfo()
            val userInfo = info.body()
            binding.userInfo.text = "${userInfo?.firstName} ${userInfo?.lastName}"
        }
    }
}