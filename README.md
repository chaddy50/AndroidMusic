# What does this app do?
This is a local music player app designed to deal with large, varied music libraries.
It has a specific focus on making classical music easier to organize and access from mobile devices.
# Features
## Classical Focus
Classical music doesn't fit neatly into the usual artist/album/track model, so this app treats it differently:
- **Hierarchical genres** - "Classical" acts as a parent genre with sub-genres like Symphony, Piano Concerto, String Quartet, etc. You can browse straight to a certain type of classical music without wading through unrelated works.

<img src="./screenshots/genre_filter.png" width="300">

- **Performances, not just albums** - The same piece (e.g. Beethoven's Fifth) often has many recordings by various orchestras and conductors. This app treats those as distinct **performances** so you can browse different performances of the same piece rather than having them collapse into a single album entry.

<img src="./screenshots/performances.png" width="300">

- **Catalog Numbers** - Opus numbers, Köchel numbers (K.), Bach-Werke-Verzeichnis (BWV), and others are parsed from track metadata and used for sorting, so works appear in their correct compositional order.

- **Composer metadata** - Composer names are looked up against the **OpenOpus API** to retrieve birth/death dates, musical epoch (Baroque, Classical, Romantic, etc.), and a portrait photo to give more context when browsing through your classical library.

<img src="./screenshots/composer.png" width="300">

## Genre-first Library
This app starts with Genre, then drills down through artists -> albums -> tracks.
This suits large, varied libraries where it can be helpful to narrow your choices down before picking a specific artist or album to listen to.

<img src="./screenshots/library.png" width="300">

## Playlists
Any entity you see in the app - a genre, artist, album, or track - can be long pressed and added to a playlist.
Adding a genre adds every track in that genre, adding an album adds all of its tracks, and so on.
You can create new playlists inline without leaving the screen that you're currently on.
## Now Playing
A mini player sits at the bottom of every screen, and shows the currently playing track.

Expanding it opens a full-screen player that gives you more information about the track, and provides more controls. The full-screen player adjusts it colors based on the artwork of the currently playing track.

<img src="./screenshots/now_playing.png" width="300">

Swiping to the right reveals a queue of the tracks that are currently playing, and you can tap any track to jump directly to it.

<img src="./screenshots/queue.png" width="300">

Now playing state is kept in sync with Android's Media3 session, so player controls also work from the lock screen and notification shade.

# Technical Details
This app was built using [Kotlin](https://kotlinlang.org/) and [Jetpack Compose](https://developer.android.com/compose).

## Libraries Used
[Media3 ExoPlayer](https://developer.android.com/media/media3/exoplayer) for playing music files and handling playback state.
<br>
[Coil](https://github.com/coil-kt/coil) for loading and displaying images.
<br>
[Palette API](https://developer.android.com/develop/ui/views/graphics/palette-colors) for extracting colors from album artwork.
<br>
[Room](https://developer.android.com/training/data-storage/room/) for storing the user's library metadata locally.
<br>
[Retrofit 2](https://github.com/square/retrofit) for making type-safe HTTP API calls.
<br>
[Mediastore](https://developer.android.com/reference/android/provider/MediaStore) for reading local music files.

## External APIs
[Open Opus](https://openopus.org/) for looking up information about classical composers.
<br>
[TheAudioDB](https://www.theaudiodb.com/free_music_api) for looking up information about non-classical artists.

