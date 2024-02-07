package htmlflow.test

import htmlflow.*
import org.junit.Test
import org.xmlet.htmlapifaster.EnumBorderType
import kotlin.test.assertEquals

data class Weather(val country: String, val locations: Iterable<Location>)
data class Location(val city: String, val desc: String, val celsius: Int)

class TestKotlinExtensions {
    @Test
    fun testExtensionDyn() {
        val html = weatherView.render(portugal)
        assertEquals(expectedPortugalWeather, html)
    }
}

private val portugal = Weather("Portugal", listOf(
    Location("Porto", "Light rain", 14),
    Location("Lisbon", "Sunny day", 14),
    Location("Sagres", "Sunny day", 18),
//    Location("Horta Azores", "Cloudy", 18),
//    Location("Flores Atores", "Cloudy", 18),
//    Location("Albufeira", "Sunny day", 18),
//    Location("Faro", "Sunny day", 18),
))

private val weatherView = view<Weather> {
    html()
        .head()
        .title().dyn { weather: Weather ->
            +weather.country
        }
        .l // title
        .l // head
        .body()
        .table().attrBorder(EnumBorderType._1)
        .tr()
        .th().add { +"City" }.l
        .th().text("Temperature").l
        .l // tr
        .dyn { weather: Weather ->
            weather.locations.forEach{ loc ->
                tr().add {
                    td().text(loc.city).l
                    td().text(loc.celsius).l
                }
                .l // tr
            }
        }
        .l // table
        .l // body
        .l // html
}

const val expectedPortugalWeather = """<!DOCTYPE html>
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