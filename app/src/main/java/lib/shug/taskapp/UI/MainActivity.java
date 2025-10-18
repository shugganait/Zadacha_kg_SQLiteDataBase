package lib.shug.taskapp.UI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lib.shug.taskapp.DataBase.DataBaseHelper;
import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.R;
import lib.shug.taskapp.UI.Adapter.TaskAdapter;
import lib.shug.taskapp.Utils.OnDialogCloseListener;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerview;
    private FloatingActionButton addFab;
    private DataBaseHelper dataBaseHelper;
    private List<TaskModel> modelList;
    private TaskAdapter adapter;
    private RadioGroup rgSort;
    private TextView tvNo;

    private enum Filter {ALL, CHECKED, UNCHECKED}

    private Filter currentFilter = Filter.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgSort = findViewById(R.id.rg_sort);
        recyclerview = findViewById(R.id.recyclerview);
        addFab = findViewById(R.id.fab);
        tvNo = findViewById(R.id.tv_no);

        dataBaseHelper = new DataBaseHelper(this);
        modelList = new ArrayList<>();
        adapter = new TaskAdapter();
        recyclerview.setAdapter(adapter);

        adapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClick(TaskModel task, boolean isChecked) {
                dataBaseHelper.updateStatus(task.getId(), isChecked ? 1 : 0);
                loadAdapter();
                applyFilter();
            }

            @Override
            public void onItemLongClick(TaskModel task) {
                dataBaseHelper.deleteTask(task.getId());
                loadAdapter();
                applyFilter();
                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEditClick(TaskModel task) {
                AddNewTask.newInstance(task.getId()).show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        setupListeners();
        loadAdapter();
        applyFilter();
    }

    private void setupListeners() {
        rgSort.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_all) {
                currentFilter = Filter.ALL;
            } else if (checkedId == R.id.rb_checked) {
                currentFilter = Filter.CHECKED;
            } else if (checkedId == R.id.rb_no_checked) {
                currentFilter = Filter.UNCHECKED;
            }
            applyFilter();
        });

        addFab.setOnClickListener(v -> {
            AddNewTask.newInstance(null).show(getSupportFragmentManager(), AddNewTask.TAG);
        });
    }

    private void loadAdapter() {
        modelList = dataBaseHelper.getAllTasks();
    }

    private void applyFilter() {
        List<TaskModel> filtered;
        switch (currentFilter) {
            case CHECKED:
                filtered = modelList.stream().filter(t -> t.getStatus() == 1).collect(Collectors.toList());
                break;
            case UNCHECKED:
                filtered = modelList.stream().filter(t -> t.getStatus() == 0).collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>(modelList);
        }

        tvNo.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.submitList(filtered); // плавная анимация с ListAdapter
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        loadAdapter();
        applyFilter();
    }
}