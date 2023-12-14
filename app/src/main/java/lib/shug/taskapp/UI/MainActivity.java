package lib.shug.taskapp.UI;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lib.shug.taskapp.R;
import lib.shug.taskapp.UI.Adapter.TaskAdapter;
import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.DataBase.DataBaseHelper;
import lib.shug.taskapp.Utils.OnDialogCloseListener;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerview;
    private FloatingActionButton addFab;
    private DataBaseHelper dataBaseHelper;
    private List<TaskModel> modelList;
    private TaskAdapter adapter;

    private TextView tvNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initViews
        recyclerview = findViewById(R.id.recyclerview);
        addFab = findViewById(R.id.fab);
        tvNo = findViewById(R.id.tv_no);
        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        modelList = new ArrayList<>();
        adapter = new TaskAdapter();

        recyclerview.setAdapter(adapter);

        loadAdapter();

        addFab.setOnClickListener(v -> AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG));

        //listener
        adapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClick(int id, int bool) {
                dataBaseHelper.updateStatus(id, bool);
                loadAdapter();
            }
            @Override
            public void onItemLongClick(int id) {
                dataBaseHelper.deleteTask(id);
                loadAdapter();
                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        loadAdapter();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadAdapter() {
        modelList = dataBaseHelper.getAllTasks();
        if (modelList.isEmpty()) {
            tvNo.setVisibility(View.VISIBLE);
        } else tvNo.setVisibility(View.GONE);
        recyclerview.setAdapter(adapter);
        Collections.reverse(modelList);
        adapter.setTasks(modelList);
        adapter.notifyDataSetChanged();
    }
}