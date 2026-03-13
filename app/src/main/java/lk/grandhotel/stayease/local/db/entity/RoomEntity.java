package lk.grandhotel.stayease.local.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "rooms")
public class RoomEntity {
    @PrimaryKey
    @NonNull
    public String id = "";
    public String title;
    public String description;
    public String type;
    public double pricePerNight;
    public int maxGuests;
    public String amenitiesJson;
    public String imagesJson;
    public boolean isAvailable;
    public double averageRating;
    public int reviewCount;
    public long cachedAt;
}