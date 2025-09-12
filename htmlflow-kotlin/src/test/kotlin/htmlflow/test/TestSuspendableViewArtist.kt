package htmlflow.test

import htmlflow.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.Test
import reactor.core.publisher.Mono
import java.lang.System.lineSeparator
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals

class TestSuspendableViewArtist {
    @Test
    fun testSuspendableViewArtist() = runBlocking {
        val bowie = Artist(
            "David Bowie",
            MusicBrainz(artist = "David Bowie", 1960, "New York", listOf("Pop Rock")),
            SpotifyArtist("David Bowie",  16428657, listOf("Heroes", "Starman", "Rebel Rebel", "Space Oddity", "Let's dance"), listOf()),
        )
        val bowieAsync = ArtistAsync(
            System.currentTimeMillis(),
            bowie.name,
            bowie.monoMusicBrainz(),
            bowie.monoSpotify(),
        )
        val html = htmlFlowArtistSuspendingView.render(bowieAsync)
            .split(lineSeparator())
            .iterator()
        expectedArtistHtml
            .split(lineSeparator())
            .forEach { assertEquals(it, html.next()) }

    }
}

private val htmlFlowArtistSuspendingView = viewSuspend<ArtistAsync> {
    html()
        .body()
        .h3().dyn { m: ArtistAsync -> text(m.name) }
        .l // h3
        .h3().text("MusicBrainz info:").l
        .ul()
        .suspending { m: ArtistAsync ->
            val mb = m.musicBrainz.await()
            li().raw("Founded: ${mb.year}").l
            li().raw("From: ${mb.from}").l
            li().raw("Genre: ${mb.genres}").l
        }
        .l // ul
        .p().b().text("Spotify popular tracks:").l
        .await { m: ArtistAsync, resume -> m.spotify.thenAccept {
            +it.popularSongs.joinToString(", ")
            resume()
        }}
        .l // p
        .l // body
        .l // html
}

private class ArtistAsync(
    val startTime: Long,
    val name: String,
    val musicBrainz: CompletableFuture<MusicBrainz>,
    val spotify: CompletableFuture<SpotifyArtist>,
)

private class MusicBrainz(
    val artist: String,
    val year: Int,
    val from: String,
    genresList: List<String>)
{
    val genres = genresList.joinToString(", ")
}

private class SpotifyArtist(
    val artist: String,
    val monthlyListeners: Int,
    val popularSongs: List<String>,
    val albums: List<String>)


private data class Artist(
    val name: String,
    private val musicBrainz: MusicBrainz,
    private val spotify: SpotifyArtist,
) {
    companion object {
        var timeout: Long = 100
    }

    fun monoMusicBrainz() = Mono
        .fromSupplier { musicBrainz }
        .delayElement(Duration.of(timeout, ChronoUnit.MILLIS))
        .toFuture()


    fun monoSpotify() = Mono
        .fromSupplier { spotify }
        .delayElement(Duration.of(timeout + 1000, ChronoUnit.MILLIS))
        .toFuture()
}

private const val expectedArtistHtml = """<!DOCTYPE html>
<html>
	<body>
		<h3>
			David Bowie
		</h3>
		<h3>
			MusicBrainz info:
		</h3>
		<ul>
			<li>
				Founded: 1960
			</li>
			<li>
				From: New York
			</li>
			<li>
				Genre: Pop Rock
			</li>
		</ul>
		<p>
			<b>
				Spotify popular tracks:
			</b>
			Heroes, Starman, Rebel Rebel, Space Oddity, Let's dance
		</p>
	</body>
</html>"""