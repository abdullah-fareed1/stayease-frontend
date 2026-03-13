package lk.grandhotel.stayease.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import lk.grandhotel.stayease.local.db.entity.RoomEntity;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM rooms WHERE cachedAt > :minAge")
    List<RoomEntity> getCachedRooms(long minAge);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RoomEntity> rooms);

    @Query("DELETE FROM rooms")
    void clearAll();
}