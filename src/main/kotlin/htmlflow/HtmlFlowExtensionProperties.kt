package htmlflow

import kotlin.Unit
import org.xmlet.htmlapifaster.A
import org.xmlet.htmlapifaster.Abbr
import org.xmlet.htmlapifaster.Address
import org.xmlet.htmlapifaster.Area
import org.xmlet.htmlapifaster.Article
import org.xmlet.htmlapifaster.Aside
import org.xmlet.htmlapifaster.Audio
import org.xmlet.htmlapifaster.B
import org.xmlet.htmlapifaster.Base
import org.xmlet.htmlapifaster.Bdi
import org.xmlet.htmlapifaster.Bdo
import org.xmlet.htmlapifaster.Blockquote
import org.xmlet.htmlapifaster.Body
import org.xmlet.htmlapifaster.Br
import org.xmlet.htmlapifaster.Button
import org.xmlet.htmlapifaster.Canvas
import org.xmlet.htmlapifaster.Caption
import org.xmlet.htmlapifaster.Cite
import org.xmlet.htmlapifaster.Code
import org.xmlet.htmlapifaster.Col
import org.xmlet.htmlapifaster.Colgroup
import org.xmlet.htmlapifaster.Data
import org.xmlet.htmlapifaster.Datalist
import org.xmlet.htmlapifaster.Dd
import org.xmlet.htmlapifaster.Del
import org.xmlet.htmlapifaster.Details
import org.xmlet.htmlapifaster.DetailsComplete
import org.xmlet.htmlapifaster.DetailsSummary
import org.xmlet.htmlapifaster.Dfn
import org.xmlet.htmlapifaster.Dialog
import org.xmlet.htmlapifaster.Div
import org.xmlet.htmlapifaster.Dl
import org.xmlet.htmlapifaster.Dt
import org.xmlet.htmlapifaster.Element
import org.xmlet.htmlapifaster.Em
import org.xmlet.htmlapifaster.Embed
import org.xmlet.htmlapifaster.Fieldset
import org.xmlet.htmlapifaster.Figcaption
import org.xmlet.htmlapifaster.Figure
import org.xmlet.htmlapifaster.Footer
import org.xmlet.htmlapifaster.Form
import org.xmlet.htmlapifaster.H1
import org.xmlet.htmlapifaster.H2
import org.xmlet.htmlapifaster.H3
import org.xmlet.htmlapifaster.H4
import org.xmlet.htmlapifaster.H5
import org.xmlet.htmlapifaster.H6
import org.xmlet.htmlapifaster.Head
import org.xmlet.htmlapifaster.Header
import org.xmlet.htmlapifaster.Hr
import org.xmlet.htmlapifaster.I
import org.xmlet.htmlapifaster.Iframe
import org.xmlet.htmlapifaster.Img
import org.xmlet.htmlapifaster.Input
import org.xmlet.htmlapifaster.Ins
import org.xmlet.htmlapifaster.Kbd
import org.xmlet.htmlapifaster.Label
import org.xmlet.htmlapifaster.Legend
import org.xmlet.htmlapifaster.Li
import org.xmlet.htmlapifaster.Link
import org.xmlet.htmlapifaster.Main
import org.xmlet.htmlapifaster.Map
import org.xmlet.htmlapifaster.Mark
import org.xmlet.htmlapifaster.Math
import org.xmlet.htmlapifaster.Meta
import org.xmlet.htmlapifaster.Meter
import org.xmlet.htmlapifaster.Nav
import org.xmlet.htmlapifaster.Noscript
import org.xmlet.htmlapifaster.Object
import org.xmlet.htmlapifaster.Ol
import org.xmlet.htmlapifaster.Optgroup
import org.xmlet.htmlapifaster.Option
import org.xmlet.htmlapifaster.Output
import org.xmlet.htmlapifaster.P
import org.xmlet.htmlapifaster.Param
import org.xmlet.htmlapifaster.Picture
import org.xmlet.htmlapifaster.Pre
import org.xmlet.htmlapifaster.Progress
import org.xmlet.htmlapifaster.Q
import org.xmlet.htmlapifaster.Rb
import org.xmlet.htmlapifaster.Root
import org.xmlet.htmlapifaster.Rp
import org.xmlet.htmlapifaster.Rt
import org.xmlet.htmlapifaster.Rtc
import org.xmlet.htmlapifaster.Ruby
import org.xmlet.htmlapifaster.S
import org.xmlet.htmlapifaster.Samp
import org.xmlet.htmlapifaster.Script
import org.xmlet.htmlapifaster.Section
import org.xmlet.htmlapifaster.Select
import org.xmlet.htmlapifaster.Small
import org.xmlet.htmlapifaster.Source
import org.xmlet.htmlapifaster.Span
import org.xmlet.htmlapifaster.Strong
import org.xmlet.htmlapifaster.Style
import org.xmlet.htmlapifaster.Sub
import org.xmlet.htmlapifaster.Summary
import org.xmlet.htmlapifaster.Sup
import org.xmlet.htmlapifaster.Svg
import org.xmlet.htmlapifaster.Table
import org.xmlet.htmlapifaster.Tbody
import org.xmlet.htmlapifaster.Td
import org.xmlet.htmlapifaster.Template
import org.xmlet.htmlapifaster.Textarea
import org.xmlet.htmlapifaster.Tfoot
import org.xmlet.htmlapifaster.Th
import org.xmlet.htmlapifaster.Thead
import org.xmlet.htmlapifaster.Time
import org.xmlet.htmlapifaster.Title
import org.xmlet.htmlapifaster.Tr
import org.xmlet.htmlapifaster.Track
import org.xmlet.htmlapifaster.U
import org.xmlet.htmlapifaster.Ul
import org.xmlet.htmlapifaster.Var
import org.xmlet.htmlapifaster.Video
import org.xmlet.htmlapifaster.Wbr

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h2: H2<T>
  get() = H2(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h2(block: H2<T>.() -> Unit): T {
  val elem =  H2(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.mark: Mark<T>
  get() = Mark(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.mark(block: Mark<T>.() -> Unit): T {
  val elem =  Mark(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.i: I<T>
  get() = I(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.i(block: I<T>.() -> Unit): T {
  val elem =  I(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.th: Th<T>
  get() = Th(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.th(block: Th<T>.() -> Unit): T {
  val elem =  Th(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.strong: Strong<T>
  get() = Strong(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.strong(block: Strong<T>.() -> Unit): T {
  val elem =  Strong(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.col: Col<T>
  get() = Col(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.col(block: Col<T>.() -> Unit): T {
  val elem =  Col(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.math: Math<T>
  get() = Math(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.math(block: Math<T>.() -> Unit): T {
  val elem =  Math(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.tbody: Tbody<T>
  get() = Tbody(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.tbody(block: Tbody<T>.() -> Unit): T {
  val elem =  Tbody(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.textarea: Textarea<T>
  get() = Textarea(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.textarea(block: Textarea<T>.() -> Unit): T {
  val elem =  Textarea(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.source: Source<T>
  get() = Source(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.source(block: Source<T>.() -> Unit): T {
  val elem =  Source(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.rp: Rp<T>
  get() = Rp(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.rp(block: Rp<T>.() -> Unit): T {
  val elem =  Rp(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.picture: Picture<T>
  get() = Picture(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.picture(block: Picture<T>.() -> Unit): T {
  val elem =  Picture(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.p: P<T>
  get() = P(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.p(block: P<T>.() -> Unit): T {
  val elem =  P(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.dt: Dt<T>
  get() = Dt(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.dt(block: Dt<T>.() -> Unit): T {
  val elem =  Dt(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.label: Label<T>
  get() = Label(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.label(block: Label<T>.() -> Unit): T {
  val elem =  Label(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.embed: Embed<T>
  get() = Embed(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.embed(block: Embed<T>.() -> Unit): T {
  val elem =  Embed(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.rt: Rt<T>
  get() = Rt(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.rt(block: Rt<T>.() -> Unit): T {
  val elem =  Rt(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.address: Address<T>
  get() = Address(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.address(block: Address<T>.() -> Unit): T {
  val elem =  Address(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h4: H4<T>
  get() = H4(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h4(block: H4<T>.() -> Unit): T {
  val elem =  H4(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.`data`: Data<T>
  get() = Data(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.`data`(block: Data<T>.() -> Unit): T {
  val elem =  Data(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.legend: Legend<T>
  get() = Legend(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.legend(block: Legend<T>.() -> Unit): T {
  val elem =  Legend(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.option: Option<T>
  get() = Option(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.option(block: Option<T>.() -> Unit): T {
  val elem =  Option(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.detailssummary: DetailsSummary<T>
  get() = DetailsSummary(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>>
    T.detailssummary(block: DetailsSummary<T>.() -> Unit): T {
  val elem =  DetailsSummary(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.track: Track<T>
  get() = Track(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.track(block: Track<T>.() -> Unit): T {
  val elem =  Track(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.figcaption: Figcaption<T>
  get() = Figcaption(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.figcaption(block: Figcaption<T>.() -> Unit): T {
  val elem =  Figcaption(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.sub: Sub<T>
  get() = Sub(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.sub(block: Sub<T>.() -> Unit): T {
  val elem =  Sub(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.details: Details<T>
  get() = Details(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.details(block: Details<T>.() -> Unit): T {
  val elem =  Details(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.aside: Aside<T>
  get() = Aside(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.aside(block: Aside<T>.() -> Unit): T {
  val elem =  Aside(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.footer: Footer<T>
  get() = Footer(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.footer(block: Footer<T>.() -> Unit): T {
  val elem =  Footer(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h6: H6<T>
  get() = H6(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h6(block: H6<T>.() -> Unit): T {
  val elem =  H6(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.hr: Hr<T>
  get() = Hr(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.hr(block: Hr<T>.() -> Unit): T {
  val elem =  Hr(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.link: Link<T>
  get() = Link(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.link(block: Link<T>.() -> Unit): T {
  val elem =  Link(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.meta: Meta<T>
  get() = Meta(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.meta(block: Meta<T>.() -> Unit): T {
  val elem =  Meta(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.`var`: Var<T>
  get() = Var(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.`var`(block: Var<T>.() -> Unit): T {
  val elem =  Var(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h1: H1<T>
  get() = H1(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h1(block: H1<T>.() -> Unit): T {
  val elem =  H1(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.q: Q<T>
  get() = Q(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.q(block: Q<T>.() -> Unit): T {
  val elem =  Q(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.output: Output<T>
  get() = Output(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.output(block: Output<T>.() -> Unit): T {
  val elem =  Output(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.li: Li<T>
  get() = Li(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.li(block: Li<T>.() -> Unit): T {
  val elem =  Li(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.span: Span<T>
  get() = Span(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.span(block: Span<T>.() -> Unit): T {
  val elem =  Span(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.time: Time<T>
  get() = Time(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.time(block: Time<T>.() -> Unit): T {
  val elem =  Time(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.caption: Caption<T>
  get() = Caption(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.caption(block: Caption<T>.() -> Unit): T {
  val elem =  Caption(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.progress: Progress<T>
  get() = Progress(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.progress(block: Progress<T>.() -> Unit): T {
  val elem =  Progress(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.dl: Dl<T>
  get() = Dl(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.dl(block: Dl<T>.() -> Unit): T {
  val elem =  Dl(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.s: S<T>
  get() = S(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.s(block: S<T>.() -> Unit): T {
  val elem =  S(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h3: H3<T>
  get() = H3(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h3(block: H3<T>.() -> Unit): T {
  val elem =  H3(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.cite: Cite<T>
  get() = Cite(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.cite(block: Cite<T>.() -> Unit): T {
  val elem =  Cite(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.abbr: Abbr<T>
  get() = Abbr(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.abbr(block: Abbr<T>.() -> Unit): T {
  val elem =  Abbr(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.tr: Tr<T>
  get() = Tr(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.tr(block: Tr<T>.() -> Unit): T {
  val elem =  Tr(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.`param`: Param<T>
  get() = Param(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.`param`(block: Param<T>.() -> Unit): T {
  val elem =  Param(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.colgroup: Colgroup<T>
  get() = Colgroup(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.colgroup(block: Colgroup<T>.() -> Unit): T {
  val elem =  Colgroup(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.dfn: Dfn<T>
  get() = Dfn(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.dfn(block: Dfn<T>.() -> Unit): T {
  val elem =  Dfn(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.br: Br<T>
  get() = Br(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.br(block: Br<T>.() -> Unit): T {
  val elem =  Br(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.head: Head<T>
  get() = Head(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.head(block: Head<T>.() -> Unit): T {
  val elem =  Head(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.table: Table<T>
  get() = Table(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.table(block: Table<T>.() -> Unit): T {
  val elem =  Table(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.template: Template<T>
  get() = Template(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.template(block: Template<T>.() -> Unit): T {
  val elem =  Template(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.kbd: Kbd<T>
  get() = Kbd(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.kbd(block: Kbd<T>.() -> Unit): T {
  val elem =  Kbd(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.h5: H5<T>
  get() = H5(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.h5(block: H5<T>.() -> Unit): T {
  val elem =  H5(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.u: U<T>
  get() = U(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.u(block: U<T>.() -> Unit): T {
  val elem =  U(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.noscript: Noscript<T>
  get() = Noscript(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.noscript(block: Noscript<T>.() -> Unit): T {
  val elem =  Noscript(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.root: Root<T>
  get() = Root(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.root(block: Root<T>.() -> Unit): T {
  val elem =  Root(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.div: Div<T>
  get() = Div(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.div(block: Div<T>.() -> Unit): T {
  val elem =  Div(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.select: Select<T>
  get() = Select(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.select(block: Select<T>.() -> Unit): T {
  val elem =  Select(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.`header`: Header<T>
  get() = Header(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.`header`(block: Header<T>.() -> Unit): T {
  val elem =  Header(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.datalist: Datalist<T>
  get() = Datalist(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.datalist(block: Datalist<T>.() -> Unit): T {
  val elem =  Datalist(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.bdi: Bdi<T>
  get() = Bdi(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.bdi(block: Bdi<T>.() -> Unit): T {
  val elem =  Bdi(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.rb: Rb<T>
  get() = Rb(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.rb(block: Rb<T>.() -> Unit): T {
  val elem =  Rb(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.input: Input<T>
  get() = Input(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.input(block: Input<T>.() -> Unit): T {
  val elem =  Input(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.canvas: Canvas<T>
  get() = Canvas(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.canvas(block: Canvas<T>.() -> Unit): T {
  val elem =  Canvas(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.b: B<T>
  get() = B(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.b(block: B<T>.() -> Unit): T {
  val elem =  B(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.ol: Ol<T>
  get() = Ol(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.ol(block: Ol<T>.() -> Unit): T {
  val elem =  Ol(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.code: Code<T>
  get() = Code(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.code(block: Code<T>.() -> Unit): T {
  val elem =  Code(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.dd: Dd<T>
  get() = Dd(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.dd(block: Dd<T>.() -> Unit): T {
  val elem =  Dd(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.title: Title<T>
  get() = Title(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.title(block: Title<T>.() -> Unit): T {
  val elem =  Title(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.thead: Thead<T>
  get() = Thead(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.thead(block: Thead<T>.() -> Unit): T {
  val elem =  Thead(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.em: Em<T>
  get() = Em(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.em(block: Em<T>.() -> Unit): T {
  val elem =  Em(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.fieldset: Fieldset<T>
  get() = Fieldset(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.fieldset(block: Fieldset<T>.() -> Unit): T {
  val elem =  Fieldset(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.area: Area<T>
  get() = Area(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.area(block: Area<T>.() -> Unit): T {
  val elem =  Area(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.sup: Sup<T>
  get() = Sup(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.sup(block: Sup<T>.() -> Unit): T {
  val elem =  Sup(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.style: Style<T>
  get() = Style(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.style(block: Style<T>.() -> Unit): T {
  val elem =  Style(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.nav: Nav<T>
  get() = Nav(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.nav(block: Nav<T>.() -> Unit): T {
  val elem =  Nav(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.ul: Ul<T>
  get() = Ul(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.ul(block: Ul<T>.() -> Unit): T {
  val elem =  Ul(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.tfoot: Tfoot<T>
  get() = Tfoot(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.tfoot(block: Tfoot<T>.() -> Unit): T {
  val elem =  Tfoot(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.bdo: Bdo<T>
  get() = Bdo(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.bdo(block: Bdo<T>.() -> Unit): T {
  val elem =  Bdo(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.script: Script<T>
  get() = Script(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.script(block: Script<T>.() -> Unit): T {
  val elem =  Script(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.section: Section<T>
  get() = Section(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.section(block: Section<T>.() -> Unit): T {
  val elem =  Section(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.meter: Meter<T>
  get() = Meter(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.meter(block: Meter<T>.() -> Unit): T {
  val elem =  Meter(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.small: Small<T>
  get() = Small(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.small(block: Small<T>.() -> Unit): T {
  val elem =  Small(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.form: Form<T>
  get() = Form(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.form(block: Form<T>.() -> Unit): T {
  val elem =  Form(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.base: Base<T>
  get() = Base(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.base(block: Base<T>.() -> Unit): T {
  val elem =  Base(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.blockquote: Blockquote<T>
  get() = Blockquote(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.blockquote(block: Blockquote<T>.() -> Unit): T {
  val elem =  Blockquote(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.audio: Audio<T>
  get() = Audio(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.audio(block: Audio<T>.() -> Unit): T {
  val elem =  Audio(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.a: A<T>
  get() = A(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.a(block: A<T>.() -> Unit): T {
  val elem =  A(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.article: Article<T>
  get() = Article(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.article(block: Article<T>.() -> Unit): T {
  val elem =  Article(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.detailscomplete: DetailsComplete<T>
  get() = DetailsComplete(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>>
    T.detailscomplete(block: DetailsComplete<T>.() -> Unit): T {
  val elem =  DetailsComplete(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.rtc: Rtc<T>
  get() = Rtc(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.rtc(block: Rtc<T>.() -> Unit): T {
  val elem =  Rtc(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.`object`: Object<T>
  get() = Object(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.`object`(block: Object<T>.() -> Unit): T {
  val elem =  Object(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.main: Main<T>
  get() = Main(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.main(block: Main<T>.() -> Unit): T {
  val elem =  Main(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.video: Video<T>
  get() = Video(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.video(block: Video<T>.() -> Unit): T {
  val elem =  Video(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.iframe: Iframe<T>
  get() = Iframe(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.iframe(block: Iframe<T>.() -> Unit): T {
  val elem =  Iframe(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.del: Del<T>
  get() = Del(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.del(block: Del<T>.() -> Unit): T {
  val elem =  Del(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.pre: Pre<T>
  get() = Pre(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.pre(block: Pre<T>.() -> Unit): T {
  val elem =  Pre(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.wbr: Wbr<T>
  get() = Wbr(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.wbr(block: Wbr<T>.() -> Unit): T {
  val elem =  Wbr(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.map: Map<T>
  get() = Map(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.map(block: Map<T>.() -> Unit): T {
  val elem =  Map(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.figure: Figure<T>
  get() = Figure(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.figure(block: Figure<T>.() -> Unit): T {
  val elem =  Figure(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.summary: Summary<T>
  get() = Summary(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.summary(block: Summary<T>.() -> Unit): T {
  val elem =  Summary(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.body: Body<T>
  get() = Body(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.body(block: Body<T>.() -> Unit): T {
  val elem =  Body(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.img: Img<T>
  get() = Img(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.img(block: Img<T>.() -> Unit): T {
  val elem =  Img(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.optgroup: Optgroup<T>
  get() = Optgroup(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.optgroup(block: Optgroup<T>.() -> Unit): T {
  val elem =  Optgroup(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.ins: Ins<T>
  get() = Ins(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.ins(block: Ins<T>.() -> Unit): T {
  val elem =  Ins(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.svg: Svg<T>
  get() = Svg(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.svg(block: Svg<T>.() -> Unit): T {
  val elem =  Svg(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.samp: Samp<T>
  get() = Samp(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.samp(block: Samp<T>.() -> Unit): T {
  val elem =  Samp(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.td: Td<T>
  get() = Td(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.td(block: Td<T>.() -> Unit): T {
  val elem =  Td(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.dialog: Dialog<T>
  get() = Dialog(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.dialog(block: Dialog<T>.() -> Unit): T {
  val elem =  Dialog(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.ruby: Ruby<T>
  get() = Ruby(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.ruby(block: Ruby<T>.() -> Unit): T {
  val elem =  Ruby(this)
  elem.block()
  return elem.l
}

public inline val <T : Element<T,Z>, Z : Element<*,*>> T.button: Button<T>
  get() = Button(this.self())

public fun <T : Element<T,Z>, Z : Element<*,*>> T.button(block: Button<T>.() -> Unit): T {
  val elem =  Button(this)
  elem.block()
  return elem.l
}
