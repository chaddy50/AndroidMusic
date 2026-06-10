package com.chaddy50.musicapp.di

import android.content.Context
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.data.api.audioDb.AudioDbClient
import com.chaddy50.musicapp.data.api.audioDb.AudioDbRepository
import com.chaddy50.musicapp.data.api.audioDb.AudioDbService
import com.chaddy50.musicapp.data.api.audioDb.IAudioDbRepository
import com.chaddy50.musicapp.data.api.openOpus.IOpenOpusRepository
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusClient
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusRepository
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusService
import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.dao.ArtistDao
import com.chaddy50.musicapp.data.dao.ComposerDao
import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.dao.PerformanceDao
import com.chaddy50.musicapp.data.dao.PlaylistDao
import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.scanner.MusicScanner
import com.chaddy50.musicapp.data.util.ArtworkDownloader
import com.chaddy50.musicapp.data.util.IArtworkDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Database & DAOs ---

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase =
        MusicDatabase.getDatabase(context)

    @Provides fun provideTrackDao(db: MusicDatabase): TrackDao = db.trackDao()
    @Provides fun provideAlbumDao(db: MusicDatabase): AlbumDao = db.albumDao()
    @Provides fun provideArtistDao(db: MusicDatabase): ArtistDao = db.artistDao()
    @Provides fun provideAlbumArtistDao(db: MusicDatabase): AlbumArtistDao = db.albumArtistDao()
    @Provides fun provideGenreDao(db: MusicDatabase): GenreDao = db.genreDao()
    @Provides fun providePerformanceDao(db: MusicDatabase): PerformanceDao = db.performanceDao()
    @Provides fun provideComposerDao(db: MusicDatabase): ComposerDao = db.composerDao()
    @Provides fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao()

    // --- Network ---

    @Provides
    @Singleton
    fun provideAudioDbService(): AudioDbService = AudioDbClient.service

    @Provides
    @Singleton
    fun provideOpenOpusService(): OpenOpusService = OpenOpusClient.service

    // --- Utilities ---

    @Provides
    @Singleton
    fun provideArtworkDownloader(@ApplicationContext context: Context): IArtworkDownloader =
        ArtworkDownloader(context)

    // --- Repositories ---

    @Provides
    @Singleton
    fun provideTrackRepository(trackDao: TrackDao): TrackRepository =
        TrackRepository(trackDao)

    @Provides
    @Singleton
    fun provideAlbumRepository(albumDao: AlbumDao): AlbumRepository =
        AlbumRepository(albumDao)

    @Provides
    @Singleton
    fun provideArtistRepository(artistDao: ArtistDao): ArtistRepository =
        ArtistRepository(artistDao)

    @Provides
    @Singleton
    fun provideGenreRepository(genreDao: GenreDao): GenreRepository =
        GenreRepository(genreDao)

    @Provides
    @Singleton
    fun provideAlbumArtistRepository(
        albumArtistDao: AlbumArtistDao,
        genreDao: GenreDao,
        audioDbRepository: IAudioDbRepository,
    ): AlbumArtistRepository =
        AlbumArtistRepository(albumArtistDao, genreDao, audioDbRepository)

    @Provides
    @Singleton
    fun providePerformanceRepository(performanceDao: PerformanceDao): PerformanceRepository =
        PerformanceRepository(performanceDao)

    @Provides
    @Singleton
    fun providePlaylistRepository(playlistDao: PlaylistDao): PlaylistRepository =
        PlaylistRepository(playlistDao)

    @Provides
    @Singleton
    fun provideAudioDbRepository(
        service: AudioDbService,
        artworkDownloader: IArtworkDownloader,
    ): IAudioDbRepository =
        AudioDbRepository(service, artworkDownloader)

    @Provides
    @Singleton
    fun provideOpenOpusRepository(service: OpenOpusService): IOpenOpusRepository =
        OpenOpusRepository(service)

    @Provides
    @Singleton
    fun provideComposerRepository(
        composerDao: ComposerDao,
        openOpusRepository: IOpenOpusRepository,
        artworkDownloader: IArtworkDownloader,
    ): ComposerRepository =
        ComposerRepository(composerDao, openOpusRepository, artworkDownloader)

    // --- Scanner ---

    @Provides
    @Singleton
    fun provideMusicScanner(
        @ApplicationContext context: Context,
        genreRepository: GenreRepository,
        artistRepository: ArtistRepository,
        albumArtistRepository: AlbumArtistRepository,
        albumRepository: AlbumRepository,
        trackRepository: TrackRepository,
        performanceRepository: PerformanceRepository,
    ): MusicScanner =
        MusicScanner(context, genreRepository, artistRepository, albumArtistRepository, albumRepository, trackRepository, performanceRepository)
}
