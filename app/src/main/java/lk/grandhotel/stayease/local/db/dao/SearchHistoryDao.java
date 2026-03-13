package lk.grandhotel.stayease.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import lk.grandhotel.stayease.local.db.entity.SearchHistoryEntity;

@Dao
public interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistoryEntity entry);

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    List<SearchHistoryEntity> getRecent();

    @Query("DELETE FROM search_history")
    void clearAll();
}