package com.example.erwancastioni.todo.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val confirm = findViewById<Button>(R.id.confirm_button)
        val title = findViewById<EditText>(R.id.editTextTaskTitle)
        val desc = findViewById<EditText>(R.id.editTextTaskDescription)

        val task = intent.getSerializableExtra("task") as? Task

        if (task != null) {
            title.setText(task.title)
            desc.setText(task.description)
        }
        else if (intent.action == Intent.ACTION_SEND && intent.type == "text/plain")
        {
            desc.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        }

        confirm.setOnClickListener {
            val newTask = Task(id = task?.id ?: UUID.randomUUID().toString(), title = title.text.toString(), description = desc.text.toString())
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}