package lib.shug.taskapp.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lib.shug.taskapp.DataBase.Model.TaskModel;

public class DataBaseHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    private static final String DATABASE_NAME = "TODO_DATABASE";
    private static final String TABLE_NAME = "TODO_TABLE";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TASK";
    private static final String COL_3 = "DESCRIPTION";
    private static final String COL_4 = "STATUS";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, TASK TEXT, DESCRIPTION TEXT, STATUS INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void insertTask(TaskModel model) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, model.getTask());
        values.put(COL_3, model.getDescription());
        values.put(COL_4, 0);
        db.insert(TABLE_NAME, null, values);
    }

    public void updateStatus(int id, int status) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_4, status);
        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void updateTask(int id, String newTask, String newDescription) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, newTask);
        values.put(COL_3, newDescription);

        db.update(TABLE_NAME, values, "ID=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{String.valueOf(id)});
    }

    public List<TaskModel> getAllTasks() {
        db = this.getWritableDatabase();
        Cursor cursor = null;
        List<TaskModel> modelList = new ArrayList<>();

        db.beginTransaction();
        try {
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        TaskModel task = new TaskModel();
                        task.setId(cursor.getInt(cursor.getColumnIndex(COL_1)));
                        task.setTask(cursor.getString(cursor.getColumnIndex(COL_2)));
                        task.setDescription(cursor.getString(cursor.getColumnIndex(COL_3)));
                        task.setStatus(cursor.getInt(cursor.getColumnIndex(COL_4)));
                        modelList.add(task);
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            if (cursor != null) {
                cursor.close();
            }
        }
        return modelList;
    }

    public List<TaskModel> searchTasks(String query) {
        db = this.getReadableDatabase();
        List<TaskModel> modelList = new ArrayList<>();

        // Проверяем, что строка не null и не пустая
        if (query == null || query.trim().isEmpty()) {
            return getAllTasks(); // возвращаем все, если ничего не введено
        }

        Cursor cursor = null;
        try {
            // LIKE с подстановкой %
            cursor = db.query(
                    TABLE_NAME,
                    null,
                    COL_2 + " LIKE ?",
                    new String[]{"%" + query.trim() + "%"},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TaskModel task = new TaskModel();
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_1)));
                    task.setTask(cursor.getString(cursor.getColumnIndexOrThrow(COL_2)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COL_3)));
                    task.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(COL_4)));
                    modelList.add(task);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e("DataBaseHelper", "Ошибка при поиске: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }

        return modelList;
    }
}