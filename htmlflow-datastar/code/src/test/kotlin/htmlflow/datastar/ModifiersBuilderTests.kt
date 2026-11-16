package htmlflow.datastar

import htmlflow.div
import htmlflow.doc
import htmlflow.html
import jakarta.ws.rs.Path
import kotlinx.serialization.Serializable
import org.xmlet.htmlapifaster.EnumTypeInputType
import org.xmlet.htmlapifaster.EnumTypeScriptType
import org.xmlet.htmlapifaster.body
import org.xmlet.htmlapifaster.button
import org.xmlet.htmlapifaster.details
import org.xmlet.htmlapifaster.head
import org.xmlet.htmlapifaster.input
import org.xmlet.htmlapifaster.option
import org.xmlet.htmlapifaster.p
import org.xmlet.htmlapifaster.script
import org.xmlet.htmlapifaster.select
import org.xmlet.htmlapifaster.span
import org.xmlet.htmlflow.datastar.attributes.dataBind
import org.xmlet.htmlflow.datastar.attributes.dataClass
import org.xmlet.htmlflow.datastar.attributes.dataIgnore
import org.xmlet.htmlflow.datastar.attributes.dataIgnoreMorph
import org.xmlet.htmlflow.datastar.attributes.dataIndicator
import org.xmlet.htmlflow.datastar.attributes.dataOn
import org.xmlet.htmlflow.datastar.attributes.dataOnIntersect
import org.xmlet.htmlflow.datastar.attributes.dataOnInterval
import org.xmlet.htmlflow.datastar.attributes.dataPreserveAttr
import org.xmlet.htmlflow.datastar.attributes.dataRef
import org.xmlet.htmlflow.datastar.attributes.dataSignal
import org.xmlet.htmlflow.datastar.attributes.dataSignals
import org.xmlet.htmlflow.datastar.attributes.dataStyle
import org.xmlet.htmlflow.datastar.attributes.dataText
import org.xmlet.htmlflow.datastar.events.Blur
import org.xmlet.htmlflow.datastar.events.Change
import org.xmlet.htmlflow.datastar.events.Click
import org.xmlet.htmlflow.datastar.events.DblClick
import org.xmlet.htmlflow.datastar.events.Focus
import org.xmlet.htmlflow.datastar.events.FocusIn
import org.xmlet.htmlflow.datastar.events.FocusOut
import org.xmlet.htmlflow.datastar.events.Input
import org.xmlet.htmlflow.datastar.modifiers.DomProperty
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnIntervalModifiers
import org.xmlet.htmlflow.datastar.modifiers.attribute.DataOnModifiers
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ModifiersTests {
    @Test
    fun `CaseModifiers should add correct case modifier`() {
        val expected = expectedHtmlCaseModifier.trimIndent().lines().iterator()
        expectedCaseModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DelayModifier applied to dataOn click event`() {
        val expected = expectedHtmlDelayModifier.trimIndent().lines().iterator()
        expectedDelayModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DurationModifiers should add correct duration modifier`() {
        val expected = expectedHtmlDurationModifier.trimIndent().lines().iterator()
        expectedDurationModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `time modifiers should serialize exact seconds with an s suffix`() {
        val eventModifiers =
            DataOnModifiers().apply {
                delay(90.seconds)
                debounce(1.seconds + 500.milliseconds)
                throttle(2.seconds)
            }

        assertEquals("__delay.90s__debounce.1500ms__throttle.2s", eventModifiers.modifiers)

        val intervalModifiers =
            DataOnIntervalModifiers().apply {
                duration(90.seconds, leading = true)
            }

        assertEquals("__duration.90s.leading", intervalModifiers.modifiers)
    }

    @Test
    fun `ViewTransitionModifiers should add correct viewtransition modifier`() {
        val expected = expectedHtmlViewTransitionModifier.trimIndent().lines().iterator()
        expectedViewTransitionModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `ThrottleModifiers should add correct throttle modifier`() {
        val expected = expectedHtmlThrottleModifier.trimIndent().lines().iterator()
        expectedThrottleModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `TimingEdgeModifiers should add leading and notrailing to debounce`() {
        val expected = expectedHtmlTimingEdgeModifier.trimIndent().lines().iterator()
        expectedTimingEdgeModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `IntersectModifiers should add correct intersect modifiers`() {
        val expected = expectedHtmlIntersectModifier.trimIndent().lines().iterator()
        expectedIntersectModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `IgnoreModifiers should add data-ignore attribute`() {
        val expected = expectedHtmlIgnoreModifier.trimIndent().lines().iterator()
        expectedIgnoreModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `EventModifiers should add all event modifier attributes`() {
        val expected = expectedHtmlEventModifiers.trimIndent().lines().iterator()
        expectedEventModifiers.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `OnceModifier should add __once modifier`() {
        val expected = expectedHtmlOnceModifier.trimIndent().lines().iterator()
        expectedOnceModifier.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `Datastar extensions should render correct attributes`() {
        val expected = expectedHtmlExtensionsOutput.trimIndent().lines().iterator()
        expectedExtensionsOutput.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DataBind prop and event modifiers`() {
        val expected = expectedHtmlDataBindModifiers.trimIndent().lines().iterator()
        expectedDataBindModifiers.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DataStyle should render correctly`() {
        val expected = expectedHtmlDataStyle.trimIndent().lines().iterator()
        expectedDataStyle.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }

    @Test
    fun `DataPreserveAttr and DataRef should render correctly`() {
        val expected = expectedHtmlDataAttributes.trimIndent().lines().iterator()
        expectedDataAttributes.toString().split("\n").forEach { actual ->
            assertEquals(expected.next().trim(), actual.trim())
        }
    }
}

private val expectedDataAttributes =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoDataAttributes")
                            val element = dataRef("div")
                            p {
                                dataText { +"$element" }
                            }
                            details {
                                attrOpen(true)
                                dataPreserveAttr { +"open" }
                            }
                        }
                    }
                }
            }
        }

private val expectedDataStyle =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoDataStyle")
                            span {
                                dataStyle("color") { +"red" }
                                dataStyle("font-size") { +"20px" }
                                text("This text should be red and 20px in size")
                            }
                            div {
                                dataStyle(
                                    "background-color" to "lightblue",
                                    "fontSize" to "12px",
                                )
                            }
                        }
                    }
                }
            }
        }

private val expectedDataBindModifiers =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("dataBindModifiersDemo")

                            val (signalCar) =
                                dataSignals(
                                    "car" to Car("Tesla Model 3", 2020),
                                )

                            select {
                                dataBind("selectedColor") {
                                    modifiers {
                                        prop(DomProperty.VALUE)
                                        event(Change)
                                    }
                                }

                                option {
                                    attrValue("red")
                                    text("Red")
                                }
                                option {
                                    attrValue("blue")
                                    text("Blue")
                                }
                                option {
                                    attrValue("green")
                                    text("Green")
                                }
                            }

                            p {
                                text("Selected color: ")
                                span {
                                    dataText { +$$"$selectedColor" }
                                }
                            }

                            input {
                                attrType(EnumTypeInputType.TEXT)

                                dataBind("username") {
                                    modifiers {
                                        prop(DomProperty.VALUE)
                                        event(Input)
                                    }
                                }
                            }

                            p {
                                text("Username: ")
                                span {
                                    dataText { +$$"$username" }
                                }
                            }
                            p {
                                text("Favorite htmlflow.datastar.Car: ")
                                span {
                                    dataText { +signalCar.on(Car::carName) }
                                    dataText { +signalCar.on(Car::year) }
                                }
                            }
                            // Just for covering the code percentage of all modifiers in dataBind
                            select {
                                dataBind(signalCar) {
                                    modifiers {
                                        prop(DomProperty.CHECKED)
                                        prop(DomProperty.SELECTED)
                                        prop(DomProperty.DISABLED)
                                        prop(DomProperty.HIDDEN)
                                        prop(DomProperty.READ_ONLY)
                                        prop(DomProperty.REQUIRED)
                                        prop(DomProperty.OPEN)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

@Serializable
class Car(
    val carName: String,
    val year: Int,
)

private val expectedCaseModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoCaseModifier")
                            val userName =
                                dataSignal("Name", "John Doe") {
                                    modifiers {
                                        ifMissing()
                                    }
                                }
                            p { dataText { +userName } }
                        }
                    }
                }
            }
        }

private val expectedDelayModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoDelayModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchData)
                                    modifiers { delay(500.milliseconds) }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

private val expectedDurationModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoDurationModifier")
                            button {
                                dataOnInterval {
                                    get(::fetchData)
                                    modifiers { duration(500.milliseconds, leading = true) }
                                }
                                text("Fetch w/leading")
                            }
                            button {
                                dataOnInterval {
                                    get(::fetchData)
                                    modifiers { duration(500.milliseconds, leading = false) }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

private val expectedViewTransitionModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoViewTransitionModifier")
                            button {
                                dataOn(Click) {
                                    get(::navigatePage)
                                    modifiers { viewTransition() }
                                }
                                text("Navigate")
                            }
                        }
                    }
                }
            }
        }

private val expectedThrottleModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoThrottleModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchData)
                                    modifiers {
                                        throttle(500.milliseconds)
                                    }
                                }
                                text("Fetch")
                            }
                        }
                    }
                }
            }
        }

private val expectedTimingEdgeModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoTimingEdgeModifier")
                            button {
                                dataOn(Click) {
                                    get(::fetchLeading)
                                    modifiers { debounce(500.milliseconds) { leading() } }
                                }
                                text("Leading")
                            }
                            button {
                                dataOn(Click) {
                                    get(::fetchNoTrailing)
                                    modifiers { debounce(500.milliseconds) { noTrailing() } }
                                }
                                text("No Trailing")
                            }
                        }
                    }
                }
            }
        }

private val expectedIntersectModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoIntersectModifier")
                            div {
                                dataOnIntersect {
                                    get(::fetchHalf)
                                    modifiers { half() }
                                }
                            }
                            div {
                                dataOnIntersect {
                                    get(::fetchFull)
                                    modifiers { full() }
                                }
                            }
                            div {
                                dataOnIntersect {
                                    get(::fetchFull)
                                    modifiers { exit() }
                                }
                            }
                        }
                    }
                }
            }
        }

private val expectedIgnoreModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoIgnoreModifier")
                            div {
                                dataIgnore {
                                    modifiers { self() }
                                }
                                p { text("This content is ignored by Datastar") }
                            }
                            div {
                                p { text("This content is not ignored by Datastar") }
                            }
                        }
                    }
                }
            }
        }

private val expectedEventModifiers =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoEventModifiers")
                            div {
                                dataOn(Click) {
                                    get(::fetchData)
                                    modifiers {
                                        passive()
                                        capture()
                                        window()
                                        outside()
                                        prevent()
                                        stop()
                                        document()
                                    }
                                }
                                text("Click")
                            }
                        }
                    }
                }
            }
        }

private val expectedOnceModifier =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        button {
                            dataOn(Click) {
                                get(::fetchData)
                                modifiers { once() }
                            }
                            text("Click")
                        }
                    }
                }
            }
        }

private val expectedExtensionsOutput =
    StringBuilder()
        .apply {
            doc {
                html {
                    head {
                        script {
                            attrType(EnumTypeScriptType.MODULE)
                            attrSrc("https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js")
                        }
                    }
                    body {
                        div {
                            attrId("demoDataClass")
                            dataClass("myClass") {
                                +$$"$isVisible"
                            }
                        }
                        div {
                            attrId("demoIgnoreMorph")
                            dataIgnoreMorph()
                            text("This element is skipped during morphing")
                        }
                        div {
                            attrId("demoDataBind")
                            val selectedColor = dataSignal("selectedColor", "red")
                            select {
                                dataBind(selectedColor.name)
                                option {
                                    attrValue("red")
                                    text("Red")
                                }
                                option {
                                    attrValue("blue")
                                    text("Blue")
                                }
                            }
                            select {
                                dataBind("size")
                                option {
                                    attrValue("sm")
                                    text("Small")
                                }
                                option {
                                    attrValue("lg")
                                    text("Large")
                                }
                            }
                        }
                        div {
                            attrId("demoDataIndicator")
                            button {
                                dataOn(DblClick) {
                                    get(::fetchData)
                                }
                                dataIndicator("isFetching")
                                text("Fetch")
                            }
                            div {
                                attrId("demoDataOn")
                                dataOn(Blur) {
                                    get(::fetchData)
                                }
                                dataOn(Focus) {
                                    get(::fetchData)
                                }
                                dataOn(FocusIn) {
                                    get(::fetchData)
                                }
                                dataOn(FocusOut) {
                                    get(::fetchData)
                                }
                            }
                        }
                    }
                }
            }
        }

private val expectedHtmlCaseModifier =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoCaseModifier" data-signals__ifmissing="{Name: 'John Doe'}">
                    <p data-text="$Name">
                    </p>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlDelayModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoDelayModifier">
                    <button data-on:click__delay.500ms="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlDurationModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoDurationModifier">
                    <button data-on-interval__duration.500ms.leading="@get('/delay-modifier/fetch')">
                        Fetch w/leading
                    </button>
                    <button data-on-interval__duration.500ms="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlTimingEdgeModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoTimingEdgeModifier">
                    <button data-on:click__debounce.500ms.leading="@get('/timing-edge/leading')">
                        Leading
                    </button>
                    <button data-on:click__debounce.500ms.notrailing="@get('/timing-edge/no-trailing')">
                        No Trailing
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlViewTransitionModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoViewTransitionModifier">
                    <button data-on:click__viewtransition="@get('/view-transition/navigate')">
                        Navigate
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlThrottleModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoThrottleModifier">
                    <button data-on:click__throttle.500ms="@get('/delay-modifier/fetch')">
                        Fetch
                    </button>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlIntersectModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoIntersectModifier">
                    <div data-on-intersect__half="@get('/intersect/half')">
                    </div>
                    <div data-on-intersect__full="@get('/intersect/full')">
                    </div>
                    <div data-on-intersect__exit="@get('/intersect/full')">
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlIgnoreModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoIgnoreModifier">
                    <div data-ignore__self="">
                        <p>
                            This content is ignored by Datastar
                        </p>
                    </div>
                    <div>
                        <p>
                            This content is not ignored by Datastar
                        </p>
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlEventModifiers =
    """        
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoEventModifiers">
                    <div data-on:click__passive__capture__window__outside__prevent__stop__document="@get('/delay-modifier/fetch')">
                        Click
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlOnceModifier =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <button data-on:click__once="@get('/delay-modifier/fetch')">
                    Click
                </button>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlExtensionsOutput =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoDataClass" data-class:myClass="$isVisible">
                </div>
                <div id="demoIgnoreMorph" data-ignore-morph="">
                    This element is skipped during morphing
                </div>
                <div id="demoDataBind" data-signals="{selectedColor: 'red'}">
                    <select data-bind="selectedColor">
                        <option value="red">
                            Red
                        </option>
                        <option value="blue">
                            Blue
                        </option>
                    </select>
                    <select data-bind="size">
                        <option value="sm">
                            Small
                        </option>
                        <option value="lg">
                            Large
                        </option>
                    </select>
                </div>
                <div id="demoDataIndicator">
                    <button data-on:dblclick="@get('/delay-modifier/fetch')" data-indicator="isFetching">
                        Fetch
                    </button>   
                    <div id="demoDataOn" data-on:blur="@get('/delay-modifier/fetch')" data-on:focus="@get('/delay-modifier/fetch')" data-on:focusin="@get('/delay-modifier/fetch')" data-on:focusout="@get('/delay-modifier/fetch')">
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlDataBindModifiers =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="dataBindModifiersDemo" data-signals="{car: {carName: 'Tesla Model 3', year: 2020}}">
                    <select data-bind__prop.value__event.change="selectedColor">
                        <option value="red">
                            Red
                        </option>
                        <option value="blue">
                            Blue
                        </option>
                        <option value="green">
                            Green
                        </option>
                    </select>
                    <p>
                        Selected color:
                        <span data-text="$selectedColor">
                        </span>
                    </p>
                    <input type="text" data-bind__prop.value__event.input="username">
                    <p>
                        Username:
                        <span data-text="$username">
                        </span>
                    </p>
                    <p>
                        Favorite htmlflow.datastar.Car:
                        <span data-text="$car.carName" data-text="$car.year">
                        </span>
                    </p>
                    <select data-bind__prop.checked__prop.selected__prop.disabled__prop.hidden__prop.readOnly__prop.required__prop.open="car">
                    </select>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlDataStyle =
    """
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoDataStyle">
                    <span data-style:color="red" data-style:font-size="20px">
                        This text should be red and 20px in size
                    </span>
                    <div data-style="{'background-color': lightblue, fontSize: 12px}">
                    </div>
                </div>
            </body>
        </html>
    """.trimMargin()

private val expectedHtmlDataAttributes =
    $$"""
        <!DOCTYPE html>
        <html>
            <head>
                <script type="module" src="https://cdn.jsdelivr.net/gh/starfederation/datastar@v1.0.1/bundles/datastar.js">
                </script>
            </head>
            <body>
                <div id="demoDataAttributes" data-ref="div">
                    <p data-text="$div">
                    </p>
                    <details open="open" data-preserve-attr="open">
                    </details>
                </div>
            </body>
        </html>
    """.trimMargin()

@Path("/delay-modifier/fetch")
private fun fetchData() {}

@Path("/view-transition/navigate")
private fun navigatePage() {}

@Path("/timing-edge/leading")
private fun fetchLeading() {}

@Path("/timing-edge/no-trailing")
private fun fetchNoTrailing() {}

@Path("/intersect/half")
private fun fetchHalf() {}

@Path("/intersect/full")
private fun fetchFull() {}
