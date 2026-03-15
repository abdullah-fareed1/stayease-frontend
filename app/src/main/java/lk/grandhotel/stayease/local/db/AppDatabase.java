package lk.grandhotel.stayease.local.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import lk.grandhotel.stayease.local.db.converter.Converters;
import lk.grandhotel.stayease.local.db.dao.BookingDao;
import lk.grandhotel.stayease.local.db.dao.RoomDao;
import lk.grandhotel.stayease.local.db.dao.SearchHistoryDao;
import lk.grandhotel.stayease.local.db.entity.BookingEntity;
import lk.grandhotel.stayease.local.db.entity.RoomEntity;
import lk.grandhotel.stayease.local.db.entity.SearchHistoryEntity;

@Database(
        entities = {RoomEntity.class, BookingEntity.class, SearchHistoryEntity.class},
        version = 2,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoomDao roomDao();
    public abstract BookingDao bookingDao();
    public abstract SearchHistoryDao searchHistoryDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "stayease_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}