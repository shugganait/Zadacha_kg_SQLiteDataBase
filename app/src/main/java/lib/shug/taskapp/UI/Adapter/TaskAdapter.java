package lib.shug.taskapp.UI.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.R;

public class TaskAdapter extends ListAdapter<TaskModel, TaskAdapter.ViewHolder> {

    public interface OnTaskClickListener {
        void onItemClick(TaskModel task, boolean isChecked);
        void onItemLongClick(TaskModel task);
    }

    private OnTaskClickListener listener;

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<TaskModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<TaskModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaskModel oldItem, @NonNull TaskModel newItem) {
                    return oldItem.getTask().equals(newItem.getTask())
                            && oldItem.getDescription().equals(newItem.getDescription())
                            && oldItem.getStatus() == newItem.getStatus();
                }
            };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskModel task = getItem(position);
        holder.tvTitle.setText(task.getTask().isEmpty() ? "Без заголовка" : task.getTask());
        holder.tvDesc.setVisibility(task.getDescription().isEmpty() ? View.GONE : View.VISIBLE);
        holder.tvDesc.setText(task.getDescription());
        holder.checkBox.setOnCheckedChangeListener(null); // Сброс, чтобы избежать вызова при перепривязке
        holder.checkBox.setChecked(task.getStatus() != 0);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) listener.onItemClick(task, isChecked);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onItemLongClick(task);
            return true;
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvDesc, tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}