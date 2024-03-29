package com.example.tasks.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tasks.R
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.model.Task
import com.example.tasks.viewmodel.TaskFormViewModel
import kotlinx.android.synthetic.main.activity_task_form.*
import kotlinx.android.synthetic.main.activity_task_form.button_save
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Calendar

class TaskFormActivity : AppCompatActivity(), View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var mViewModel: TaskFormViewModel
    private val mDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val mListPriorityId: MutableList<Int> = arrayListOf()
    private var mTaskId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_form)

        mViewModel = ViewModelProvider(this).get(TaskFormViewModel::class.java)

        // Inicializa eventos
        listeners()
        observe()
        mViewModel.listPriorities()
        loadDataFromActivity()
    }

    override fun onClick(v: View) {
        val id = v.id
        when(id) {
            R.id.button_save -> {
                handleSave()
            }
            R.id.button_date -> {
                showDatePicker()
            }
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(this, this, year, month, day).show()
    }

    private fun observe() {
        mViewModel.priorities.observe(this, Observer {
            val list: MutableList<String> = arrayListOf()
            for(item in it) {
                list.add(item.description)
                mListPriorityId.add(item.id)
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
            spinner_priority.adapter = adapter
        })
        mViewModel.validation.observe(this, Observer {
            if(it.success()) {
                if(mTaskId == 0) {
                    toast(getString(R.string.task_created))
                } else {
                    toast(getString(R.string.task_updated))
                }
                finish()
            } else {
                toast(it.failure())
            }
        })
        mViewModel.task.observe(this, Observer {
            edit_description.setText(it.description)
            check_complete.isChecked = it.complete
            val date = SimpleDateFormat("yyyy-MM-dd").parse(it.dueDate)
            button_date.setText(mDateFormat.format(date))
            spinner_priority.setSelection(getIndex(it.priorityId))
        })
    }

    private fun toast(str: String) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show()
    }

    private fun getIndex(priorityId: Int): Int {
        var index : Int = 0
        for(i in 0 until mListPriorityId.count()) {
            if(mListPriorityId[i] == priorityId) {
                //INDEX!!
                index = i
            }
        }
        return index
    }

    private fun loadDataFromActivity() {
        val bundle = intent.extras
        if(bundle != null) {
            mTaskId = bundle.getInt(TaskConstants.BUNDLE.TASKID)
            mViewModel.get(mTaskId)
            button_save.text = getString(R.string.update_task)
        }

    }

    private fun handleSave() {
        val task = Task().apply {
            this.id = mTaskId
            this.description = edit_description.text.toString()
            this.dueDate = button_date.text.toString()
            this.complete = check_complete.isChecked
            this.priorityId = mListPriorityId[spinner_priority.selectedItemPosition]
        }

        mViewModel.save(task)
    }

    private fun listeners() {
        button_save.setOnClickListener(this)
        button_date.setOnClickListener(this)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val str = mDateFormat.format(calendar.time)
        button_date.text = str
    }

}
