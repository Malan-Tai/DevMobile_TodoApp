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
import coil.load
import coil.transform.CircleCropTransformation
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.databinding.FragmentTaskListBinding
import com.example.erwancastioni.todo.form.FormActivity
import com.example.erwancastioni.todo.network.TaskListViewModel
import com.example.erwancastioni.todo.network.UserInfoViewModel
import com.example.erwancastioni.todo.user.UserInfoActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private lateinit var binding: FragmentTaskListBinding

    private val taskListViewModel: TaskListViewModel by viewModels()
    private val userInfoViewModel: UserInfoViewModel by viewModels()

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as? Task

        val oldTask = taskListViewModel.taskList.value.firstOrNull { it.id == task?.id }

        if (task != null) {
            if (oldTask != null) {
                taskListViewModel.editTask(task)
            }
            else {
                taskListViewModel.addTask(task)
            }
        }
    }

    private lateinit var adapter: TaskListAdapter

    private val adapterListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskListViewModel.deleteTask(task)
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

        binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = TaskListAdapter(adapterListener)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            taskListViewModel.taskList.collect { newList ->
                adapter.submitList(newList)
            }
        }

        lifecycleScope.launch {
            userInfoViewModel.userInfo.collect { newInfo ->
                binding.userInfo.text = "${newInfo.firstName} ${newInfo.lastName}"

                if (newInfo.avatar != null) {
                    binding.avatar.load(newInfo.avatar) {
                        placeholder(R.drawable.ic_launcher_foreground)
                        error(R.drawable.ic_launcher_foreground)
                        transformations(CircleCropTransformation())
                    }
                }
            }
        }

        binding.addTaskBtn.setOnClickListener {
            val intent = Intent(activity, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        binding.avatar.setOnClickListener {
            val intent = Intent(activity, UserInfoActivity::class.java)
            startActivity(intent)
        }

        taskListViewModel.loadTasks()
        userInfoViewModel.loadUserInfo()
    }

    //override fun onSaveInstanceState(outState: Bundle) {
    //    super.onSaveInstanceState(outState)
    //    for ((i, task) in viewModel.taskList.value.withIndex()) {
    //        outState.putSerializable("task_$i", task)
    //    }
    //}

    override fun onResume() {
        super.onResume()

        userInfoViewModel.loadUserInfo()
    }
}