package lib.shug.taskapp.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import lib.shug.taskapp.DataBase.DataBaseHelper;
import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.R;
import lib.shug.taskapp.Utils.OnDialogCloseListener;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "AddNewTask";

    private EditText etTask;
    private EditText etDesc;
    private Button btnSave;

    private DataBaseHelper dataBaseHelper;

    public static AddNewTask newInstance() {
        return new AddNewTask();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_newtask, container, false);

        // Регулируем размер
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().setOnShowListener(dialog -> {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).setPeekHeight(requireContext().getResources().getDisplayMetrics().heightPixels / 2);
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTask = view.findViewById(R.id.et_title);
        btnSave = view.findViewById(R.id.btn_save);
        etDesc = view.findViewById(R.id.et_desc);

        dataBaseHelper = new DataBaseHelper(getActivity());

        btnSave.setOnClickListener(v -> {
            if (!etTask.getText().toString().equals("") || !etDesc.getText().toString().equals("")) {
                String title = etTask.getText().toString();
                String desc = etDesc.getText().toString();

                TaskModel item = new TaskModel();
                item.setTask(title);
                item.setDescription(desc);
                item.setStatus(0);
                dataBaseHelper.insertTask(item);
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Нельзя сохранить пустую задачу", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListener) {
            ((OnDialogCloseListener) activity).onDialogClose(dialog);
        }
    }
}