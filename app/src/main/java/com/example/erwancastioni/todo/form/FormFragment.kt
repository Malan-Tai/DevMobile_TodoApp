package com.example.erwancastioni.todo.form

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.erwancastioni.todo.databinding.FragmentFormBinding
import com.example.erwancastioni.todo.getNavigationResult
import com.example.erwancastioni.todo.setNavigationResultToPrev
import com.example.erwancastioni.todo.tasklist.Task
import java.util.*

class FormFragment : Fragment() {
    private lateinit var binding: FragmentFormBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val task = getNavigationResult<Task?>("taskToModify")

        if (task != null) {
            binding.editTextTaskTitle.setText(task.title)
            binding.editTextTaskDescription.setText(task.description)
        }
//        else if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain")
//        {
//            binding.editTextTaskDescription.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
//        }

        binding.confirmButton.setOnClickListener {
            val newTask = Task(id = task?.id ?: UUID.randomUUID().toString(), title = binding.editTextTaskTitle.text.toString(), description = binding.editTextTaskDescription.text.toString())
//            intent.putExtra("task", newTask)
//            setResult(RESULT_OK, intent)
//            finish()
            setNavigationResultToPrev<Task>(newTask, "taskToAdd")
            findNavController().popBackStack()
        }
    }
}