package lk.grandhotel.stayease.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;
import lk.grandhotel.stayease.local.db.entity.BookingEntity;

@Dao
public interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BookingEntity booking);

    @Query("SELECT * FROM booking_drafts")
    List<BookingEntity> getAll();

    @Query("DELETE FROM booking_drafts WHERE id = :id")
    void deleteById(String id);

    @Query("DELETE FROM booking_drafts")
    void clearAll();
}