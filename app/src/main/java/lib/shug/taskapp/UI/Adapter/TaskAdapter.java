package lib.shug.taskapp.UI.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lib.shug.taskapp.DataBase.Model.TaskModel;
import lib.shug.taskapp.R;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private List<TaskModel> modelList;

    public interface OnTaskClickListener {
        void onItemClick(int id, int bool);

        void onItemLongClick(int id);
    }

    private OnTaskClickListener listener;

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public TaskAdapter() {
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TaskModel item = modelList.get(position);
        if (item.getTask().equals("")) {
            holder.tvTitle.setText("Без заголовки");
        } else {
            holder.tvTitle.setText(item.getTask());
        }
        holder.checkBox.setChecked(toBoolean(item.getStatus()));
        if (item.getDescription().equals("")) {
            holder.tvDesc.setVisibility(View.GONE);
        } else {
            holder.tvDesc.setText(item.getDescription());
        }
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                listener.onItemClick(item.getId(), 1);
            } else
                listener.onItemClick(item.getId(), 0);
        });
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item.getId());
            return true;
        });
    }

    public boolean toBoolean(int num) {
        return num != 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTasks(List<TaskModel> mList) {
        this.modelList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvDesc;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }
    }
}
