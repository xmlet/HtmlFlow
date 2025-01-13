package htmlflow.test

import htmlflow.HtmlFlow.view
import org.junit.Test
import org.xmlet.htmlapifaster.EnumBorderType
import kotlin.test.assertEquals
import htmlflow.HtmlFlow.doc
import org.xmlet.htmlapifaster.EnumRelType
import htmlflow.*
import org.xmlet.htmlapifaster.*

data class Weather(val country: String, val locations: Iterable<Location>)
data class Location(val city: String, val desc: String, val celsius: Int)

class TestKotlinExtensions {
    @Test
    fun testExtensionDyn() {
        val html = weatherView.render(portugal)
        assertEquals(expectedPortugalWeather, html)
    }

    @Test
    fun testExtensionSimple() {
        val html = weatherView1.render(portugal)
        assertEquals(expectedPortugalWeather, html)
    }

    @Test
    fun `test div extension property`() {

        val expectedHtml = """<!DOCTYPE html>
<html>
	<body>
		<div>
			Hello, HtmlFlow!
		</div>
	</body>
</html>"""
        val builder = StringBuilder()

        doc(builder)
            .html()
            .body()
            .div()
            .text("Hello, HtmlFlow!")
            .`__`() //close div
            .`__`() // close body
            .`__`() // close html

        assertEquals(expectedHtml, builder.toString())
        builder.clear()
        doc(builder)
            .html
            .body
            .div
            .text("Hello, HtmlFlow!")
            .l // close div
            .l // close body
            .l // close html

        assertEquals(expectedHtml, builder.toString())
        builder.clear()
        doc(builder)
            .html {
                body {
                    div {
                        text("Hello, HtmlFlow!")
                    }
                }
            }

        assertEquals(expectedHtml, builder.toString())

        builder.clear()
        doc(builder).
        html {
            body {
                div.text("Hello, HtmlFlow!").l
            }
        }

        assertEquals(expectedHtml, builder.toString())


    }

    @Test
    fun `test complex HTML page`() {

        val expectedHtml = """<!DOCTYPE html>
<html>
	<head>
		<title>
			My Complex Page
		</title>
		<meta charset="UTF-8">
		<meta name="description" content="This is a complex HTML page.">
		<meta name="keywords" content="HTML,CSS,JavaScript">
		<meta name="author" content="John Doe">
		<link rel="stylesheet" href="styles.css">
		<script src="script.js">
		</script>
	</head>
	<body>
		<header>
			<h1>
				Welcome to My Complex Page
			</h1>
			<nav>
				<ul>
					<li>
						<a href="#section1">
							Section 1
						</a>
					</li>
					<li>
						<a href="#section2">
							Section 2
						</a>
					</li>
					<li>
						<a href="#section3">
							Section 3
						</a>
					</li>
				</ul>
			</nav>
		</header>
		<main>
			<section id="section1">
				<h2>
					Section 1
				</h2>
				<p>
					This is the first section.
				</p>
				<img src="image1.jpg" alt="Image 1">
			</section>
			<section id="section2">
				<h2>
					Section 2
				</h2>
				<p>
					This is the second section.
				</p>
				<img src="image2.jpg" alt="Image 2">
			</section>
			<section id="section3">
				<h2>
					Section 3
				</h2>
				<p>
					This is the third section.
				</p>
				<img src="image3.jpg" alt="Image 3">
			</section>
		</main>
		<footer>
			<p>
				Copyright © 2024
			</p>
		</footer>
	</body>
</html>
        """.trimIndent()

        val builder = StringBuilder()

        doc(builder)
            .html()
            .head()
            .title()
            .raw("My Complex Page")
            .`__`() //title
            .meta().attrCharset("UTF-8")
            .`__`() //meta
            .meta().attrName("description").attrContent("This is a complex HTML page.")
            .`__`() //meta
            .meta().attrName("keywords").attrContent("HTML,CSS,JavaScript")
            .`__`() //meta
            .meta().attrName("author").attrContent("John Doe")
            .`__`() //meta
            .link().attrRel(EnumRelType.STYLESHEET).attrHref("styles.css")
            .`__`() //link
            .script().attrSrc("script.js")
            .`__`() //script
            .`__`() //head
            .body()
            .header()
            .h1()
            .raw("Welcome to My Complex Page")
            .`__`() //h1
            .nav()
            .ul()
            .li()
            .a().attrHref("#section1")
            .raw("Section 1")
            .`__`() //a
            .`__`() //li
            .li()
            .a().attrHref("#section2")
            .raw("Section 2")
            .`__`() //a
            .`__`() //li
            .li()
            .a().attrHref("#section3")
            .raw("Section 3")
            .`__`() //a
            .`__`() //li
            .`__`() //ul
            .`__`() //nav
            .`__`() //header
            .main()
            .section().attrId("section1")
            .h2()
            .raw("Section 1")
            .`__`() //h2
            .p()
            .raw("This is the first section.")
            .`__`() //p
            .img().attrSrc("image1.jpg").attrAlt("Image 1")
            .`__`() //img
            .`__`() //section
            .section().attrId("section2")
            .h2()
            .raw("Section 2")
            .`__`() //h2
            .p()
            .raw("This is the second section.")
            .`__`() //p
            .img().attrSrc("image2.jpg").attrAlt("Image 2")
            .`__`() //img
            .`__`() //section
            .section().attrId("section3")
            .h2()
            .raw("Section 3")
            .`__`() //h2
            .p()
            .raw("This is the third section.")
            .`__`() //p
            .img().attrSrc("image3.jpg").attrAlt("Image 3")
            .`__`() //img
            .`__`() //section
            .`__`() //main
            .footer()
            .p()
            .raw("Copyright © 2024")
            .`__`() //p
            .`__`() //footer
            .`__`() //body
            .`__`() //html

        assertEquals(expectedHtml, builder.toString())
        builder.clear()

        doc(builder)
            .html
            .head
            .title
            .raw("My Complex Page")
            .l //title
            .meta.attrCharset("UTF-8")
            .l //meta
            .meta.attrName("description").attrContent("This is a complex HTML page.")
            .l //meta
            .meta.attrName("keywords").attrContent("HTML,CSS,JavaScript")
            .l //meta
            .meta.attrName("author").attrContent("John Doe")
            .l //meta
            .link.attrRel(EnumRelType.STYLESHEET).attrHref("styles.css")
            .l //link
            .script.attrSrc("script.js")
            .l //script
            .l //head
            .body
            .header
            .h1
            .raw("Welcome to My Complex Page")
            .l //h1
            .nav
            .ul
            .li
            .a.attrHref("#section1")
            .raw("Section 1")
            .l //a
            .l //li
            .li
            .a.attrHref("#section2")
            .raw("Section 2")
            .l //a
            .l //li
            .li
            .a.attrHref("#section3")
            .raw("Section 3")
            .l //a
            .l //li
            .l //ul
            .l //nav
            .l //header
            .main
            .section.attrId("section1")
            .h2
            .raw("Section 1")
            .l //h2
            .p
            .raw("This is the first section.")
            .l //p
            .img.attrSrc("image1.jpg").attrAlt("Image 1")
            .l //img
            .l //section
            .section.attrId("section2")
            .h2
            .raw("Section 2")
            .l //h2
            .p
            .raw("This is the second section.")
            .l //p
            .img.attrSrc("image2.jpg").attrAlt("Image 2")
            .l //img
            .l //section
            .section.attrId("section3")
            .h2
            .raw("Section 3")
            .l //h2
            .p
            .raw("This is the third section.")
            .l //p
            .img.attrSrc("image3.jpg").attrAlt("Image 3")
            .l //img
            .l //section
            .l //main
            .footer
            .p
            .raw("Copyright © 2024")
            .l //p
            .l //footer
            .l //body
            .l //html

        assertEquals(expectedHtml, builder.toString())
        builder.clear()

        doc(builder)
            .html {
                head {
                    title {
                        text("My Complex Page")
                    }
                    meta {
                        attrCharset("UTF-8")
                    }
                    meta {
                        attrName("description")
                        attrContent("This is a complex HTML page.")
                    }
                    meta {
                        attrName("keywords")
                        attrContent("HTML,CSS,JavaScript")
                    }
                    meta {
                        attrName("author")
                        attrContent("John Doe")
                    }
                    link {
                        attrRel(EnumRelType.STYLESHEET)
                        attrHref("styles.css")
                    }
                    script {
                        attrSrc("script.js")
                    }
                }
                body {
                    header {
                        h1 {
                            text("Welcome to My Complex Page")
                        }
                        nav {
                            ul {
                                li.a.attrHref("#section1").text("Section 1").l.l
                                li.a.attrHref("#section2").text("Section 2").l.l
                                li.a.attrHref("#section3").text("Section 3").l.l
                            }
                        }
                    }
                    main {
                        section {
                            attrId("section1")
                            h2.text("Section 1").l
                            p.text("This is the first section.").l
                            img.attrSrc("image1.jpg").attrAlt("Image 1").l
                        }
                        section {
                            attrId("section2")
                            h2.text("Section 2").l
                            p.text("This is the second section.").l
                            img.attrSrc("image2.jpg").attrAlt("Image 2").l
                        }
                        section {
                            attrId("section3")
                            h2.text("Section 3").l
                            p.text("This is the third section.").l
                            img.attrSrc("image3.jpg").attrAlt("Image 3").l
                        }
                    }
                    footer {
                        p {
                            text("Copyright © 2024")
                        }
                    }
                }
            }

        assertEquals(expectedHtml, builder.toString())
        builder.clear()
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
    it.html
        .head
        .title.dyn { weather: Weather ->
            +weather.country
        }
        .l // title
        .l // head
        .body {
            table.attrBorder(EnumBorderType._1)
                .tr {
                    th {
                        text("City")
                    }
                    th {
                        text("Temperature")
                    }
                } // tr
                .dyn { weather: Weather ->
                    weather.locations.forEach { loc ->
                        tr {
                            td.text(loc.city).l
                            td.text(loc.celsius).l
                        }
                    }
                }
                .l // table
        } // body
        .l // html
}

private val weatherView1 = view<Weather> {
    it.html
        .head
        .title.dyn { weather: Weather ->
            +weather.country
        }
        .l // title
        .l // head
        .body {
            table.attrBorder(EnumBorderType._1)
                .tr
                .th.text("City").l
                .th.text("Temperature").l
                .l // tr
                .dyn { weather: Weather ->
                    weather.locations.forEach { loc ->
                        tr {
                            td.text(loc.city).l
                            td.text(loc.celsius).l
                        }
                    }
                }
                .l // table
        } // body
        .l // html
}

private val expectedPortugalWeather = """<!DOCTYPE html>
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
