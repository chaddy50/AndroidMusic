package com.chaddy50.musicapp.data.scrobbling

data class ScrobbleCall(
    val track: ScrobbleTrackMetadata,
    val listenedAtSeconds: Long,
)

data class NowPlayingCall(
    val track: ScrobbleTrackMetadata,
)

class FakeScrobbleService : IScrobbleService {

    val listenCalls = mutableListOf<ScrobbleCall>()
    val nowPlayingCalls = mutableListOf<NowPlayingCall>()
    var clearNowPlayingCount = 0

    override suspend fun submitListen(
        track: ScrobbleTrackMetadata,
        listenedAtSeconds: Long
    ): Boolean {
        listenCalls.add(ScrobbleCall(track, listenedAtSeconds))
        return true
    }

    override suspend fun submitNowPlaying(
        track: ScrobbleTrackMetadata
    ): Boolean {
        nowPlayingCalls.add(NowPlayingCall(track))
        return true
    }

    override suspend fun clearNowPlaying(): Boolean {
        clearNowPlayingCount++
        return true
    }
}
