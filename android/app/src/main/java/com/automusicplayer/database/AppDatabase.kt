package com.automusicplayer.database

import android.content.Context
import androidx.room.*

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String
)

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "art_path") val artPath: String?,
    @ColumnInfo(name = "year") val year: Int?
)

@Entity(tableName = "genres")
data class GenreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "ignored") val ignored: Boolean = false
)

@Entity(
    tableName = "tracks",
    indices = [
        Index(value = ["artist_id", "album_id"], name = "idx_tracks_artist_album"),
        Index(value = ["title"], name = "idx_tracks_title")
    ],
    foreignKeys = [
        ForeignKey(entity = AlbumEntity::class, parentColumns = ["id"], childColumns = ["album_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = ArtistEntity::class, parentColumns = ["id"], childColumns = ["artist_id"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = GenreEntity::class, parentColumns = ["id"], childColumns = ["genre_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "file_path") val filePath: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "bitrate") val bitrate: Int?,
    @ColumnInfo(name = "file_size") val fileSize: Long,
    @ColumnInfo(name = "date_added") val dateAdded: Long,
    @ColumnInfo(name = "last_played") val lastPlayed: Long?,
    @ColumnInfo(name = "play_count") val playCount: Int = 0,
    @ColumnInfo(name = "album_id") val albumId: Long?,
    @ColumnInfo(name = "artist_id") val artistId: Long?,
    @ColumnInfo(name = "genre_id") val genreId: Long?
)

@Entity(
    tableName = "custom_tag_map",
    foreignKeys = [
        ForeignKey(entity = TrackEntity::class, parentColumns = ["id"], childColumns = ["track_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class CustomTagMapEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "track_id") val trackId: Long,
    @ColumnInfo(name = "tag_key") val tagKey: String,
    @ColumnInfo(name = "tag_value") val tagValue: String
)

@Dao
interface MusicDao {
    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' ORDER BY title ASC LIMIT 50")
    suspend fun searchTracks(query: String): List<TrackEntity>

    @Query("SELECT * FROM tracks ORDER BY date_added DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long
}

@Database(entities = [ArtistEntity::class, AlbumEntity::class, GenreEntity::class, TrackEntity::class, CustomTagMapEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auto_music_player.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
