package htmlflow.test

import htmlflow.dyn
import htmlflow.l
import htmlflow.suspending
import htmlflow.viewSuspend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.xmlet.htmlapifaster.EnumBorderType
import kotlin.test.assertEquals

class TestSuspendableViewWeather {
    @Test
    fun testSuspendableViewWeather() = runBlocking {
        val pt = WeatherRx("Portugal", flow {
            emit(Location("Porto", "Light rain", 14))
            emit(Location("Lisbon", "Sunny day", 14))
            emit(Location("Sagres", "Sunny day", 18))
        })
        val html = wxSuspView.render(pt)
        assertEquals(expectedWeather, html)
    }
}

private val wxSuspView = viewSuspend<WeatherRx> {
    html()
        .head()
        .title().dyn { m: WeatherRx ->
            text(m.country)
        }
        .l // title
        .l // head
        .body()
        .table().attrBorder(EnumBorderType._1)
        .tr()
        .th().text("City").l
        .th().text("Temperature").l
        .l // tr
        .suspending { m: WeatherRx -> m.cities.collect {
            tr()
                .td().text(it.city).l
                .td().text(it.celsius).l
            .l // tr
        }}
        .l // table
        .l // body
        .l // html
}

private data class WeatherRx(val country: String, val cities: Flow<Location>)

private const val expectedWeather = """<!DOCTYPE html>
<html>
	<head>
		<title>
			Portugal
		</title>
	</head>
	<body>
		<table border="1">
			<tr>
				<th>
					City
				</th>
				<th>
					Temperature
				</th>
			</tr>
			<tr>
				<td>
					Porto
				</td>
				<td>
					14
				</td>
			</tr>
			<tr>
				<td>
					Lisbon
				</td>
				<td>
					14
				</td>
			</tr>
			<tr>
				<td>
					Sagres
				</td>
				<td>
					18
				</td>
			</tr>
		</table>
	</body>
</html>"""