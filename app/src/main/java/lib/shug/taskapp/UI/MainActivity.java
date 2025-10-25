package lib.shug.taskapp.UI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lib.shug.taskapp.DataBase.DataBaseHelper;
import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.R;
import lib.shug.taskapp.UI.Adapter.TaskAdapter;
import lib.shug.taskapp.Utils.OnDialogCloseListener;
import lib.shug.taskapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private ActivityMainBinding binding;
    private DataBaseHelper dataBaseHelper;
    private List<TaskModel> modelList;
    private TaskAdapter adapter;

    private enum Filter {ALL, CHECKED, UNCHECKED}

    private Filter currentFilter = Filter.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataBaseHelper = new DataBaseHelper(this);
        modelList = new ArrayList<>();
        adapter = new TaskAdapter();
        binding.recyclerview.setAdapter(adapter);

        adapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onItemClick(TaskModel task, boolean isChecked) {
                dataBaseHelper.updateStatus(task.getId(), isChecked ? 1 : 0);
                loadAdapter();
                applyFilterAndSearch(binding.etSearch.getText().toString());
            }

            @Override
            public void onItemLongClick(TaskModel task) {
                dataBaseHelper.deleteTask(task.getId());
                loadAdapter();
                applyFilterAndSearch(binding.etSearch.getText().toString());
                Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemEditClick(TaskModel task) {
                AddNewTask.newInstance(task.getId()).show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        setupListeners();
        loadAdapter();
        applyFilterAndSearch(binding.etSearch.getText().toString());
    }

    private void setupListeners() {
        binding.rgSort.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_all) {
                currentFilter = Filter.ALL;
            } else if (checkedId == R.id.rb_checked) {
                currentFilter = Filter.CHECKED;
            } else if (checkedId == R.id.rb_no_checked) {
                currentFilter = Filter.UNCHECKED;
            }
            applyFilterAndSearch(binding.etSearch.getText().toString());
        });

        binding.fab.setOnClickListener(v -> {
            AddNewTask.newInstance(null).show(getSupportFragmentManager(), AddNewTask.TAG);
        });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadAdapter() {
        modelList = dataBaseHelper.getAllTasks();
    }

//    private void applyFilter() {
//        List<TaskModel> filtered;
//        switch (currentFilter) {
//            case CHECKED:
//                filtered = modelList.stream().filter(t -> t.getStatus() == 1).collect(Collectors.toList());
//                break;
//            case UNCHECKED:
//                filtered = modelList.stream().filter(t -> t.getStatus() == 0).collect(Collectors.toList());
//                break;
//            default:
//                filtered = new ArrayList<>(modelList);
//        }
//
//        binding.tvNo.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
//        adapter.submitList(filtered);
//    }

    private void applyFilterAndSearch(String query) {
        List<TaskModel> baseList;

        try {
            // Если пользователь что-то ищет — получаем отфильтрованный список
            if (query != null && !query.trim().isEmpty()) {
                baseList = dataBaseHelper.searchTasks(query);
            } else {
                baseList = dataBaseHelper.getAllTasks();
            }

            // Применяем фильтр по статусу
            List<TaskModel> filtered;
            switch (currentFilter) {
                case CHECKED:
                    filtered = baseList.stream()
                            .filter(t -> t.getStatus() == 1)
                            .collect(Collectors.toList());
                    break;
                case UNCHECKED:
                    filtered = baseList.stream()
                            .filter(t -> t.getStatus() == 0)
                            .collect(Collectors.toList());
                    break;
                default:
                    filtered = new ArrayList<>(baseList);
            }

            binding.tvNo.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.submitList(filtered);

        } catch (Exception e) {
            Log.e("MainActivity", "Ошибка при применении фильтра или поиска: " + e.getMessage());
            Toast.makeText(this, "Произошла ошибка при поиске", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        loadAdapter();
        applyFilterAndSearch(binding.etSearch.getText().toString());
    }
}