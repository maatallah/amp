package com.automusicplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MusicDao {
    @Query("SELECT * FROM tracks WHERE title LIKE '%' || :query || '%' ORDER BY title ASC LIMIT 50")
    suspend fun searchTracks(query: String): List<TrackEntity>

    @Query("SELECT * FROM tracks ORDER BY date_added DESC")
    fun getAllTracks(): List<TrackEntity>

    @Query("UPDATE tracks SET play_count = play_count + 1, last_played = :timestamp WHERE id = :trackId")
    fun updateTrackTelemetry(trackId: Long, timestamp: Long)

    @Query("SELECT * FROM tracks WHERE play_count > :minPlays ORDER BY play_count DESC LIMIT 50")
    suspend fun getMostPlayedTracks(minPlays: Int = 5): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE last_played >= :timestampThreshold ORDER BY last_played DESC LIMIT 50")
    suspend fun getRecentlyPlayedTracks(timestampThreshold: Long): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE genre_id = :genreId ORDER BY title ASC")
    suspend fun getTracksByGenre(genreId: Long): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE artist_id = :artistId ORDER BY title ASC")
    suspend fun getTracksByArtist(artistId: Long): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: ArtistEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: GenreEntity): Long
}
