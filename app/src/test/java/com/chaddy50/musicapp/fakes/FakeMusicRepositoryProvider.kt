package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.MusicRepositoryProvider
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.Dispatchers

class FakeMusicRepositoryProvider(
    private val genreDao: FakeGenreDao = FakeGenreDao(),
    private val trackDao: FakeTrackDao = FakeTrackDao(),
    private val albumDao: FakeAlbumDao = FakeAlbumDao(),
    private val albumArtistDao: FakeAlbumArtistDao = FakeAlbumArtistDao(),
    private val performanceDao: FakePerformanceDao = FakePerformanceDao(),
    private val playlistDao: FakePlaylistDao = FakePlaylistDao(),
) : MusicRepositoryProvider {
    override val trackRepository = TrackRepository(trackDao)
    override val albumRepository = AlbumRepository(albumDao)
    override val genreRepository = GenreRepository(genreDao)
    override val albumArtistRepository = AlbumArtistRepository(
        albumArtistDao, FakeAudioDbRepository()
    )
    override val performanceRepository = PerformanceRepository(performanceDao)
    override val playlistRepository = PlaylistRepository(playlistDao)
    override val filesDirPath = "/fake/files"
}
