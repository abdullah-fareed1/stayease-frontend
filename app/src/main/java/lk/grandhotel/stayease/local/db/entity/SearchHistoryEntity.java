package lk.grandhotel.stayease.local.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "search_history")
public class SearchHistoryEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String query = "";
    public long timestamp;
}