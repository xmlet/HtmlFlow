package htmlflow.viewloader

data class SimpleTestViewModel(val content: String) : ViewModel

data class AsyncTestViewModel(val content: String) : ViewModel

data class SimpleTestViewModel2(val content: String) : ViewModel

data class SimpleTestViewModel3(val content: String) : ViewModel

data class SimpleTestViewModel4(val content: String) : ViewModel

data class AsyncTestViewModel2(val content: String) : ViewModel

data class ComplexTestViewModel(
    val title: String,
    val items: List<String>,
    val isActive: Boolean,
) : ViewModel

open class BaseTestViewModel(open val baseContent: String) : ViewModel

data class InheritedTestViewModel(
    override val baseContent: String,
    val derivedContent: String,
) : BaseTestViewModel(baseContent)

interface TestViewModelInterface {
    val interfaceContent: String
}

data class InterfaceTestViewModel(
    override val interfaceContent: String,
    val additionalContent: String,
) : TestViewModelInterface, ViewModel

data class BuilderTestViewModel(val content: String) : ViewModel

data class BuilderTestViewModel2(val content: String) : ViewModel

data class BuilderTestViewModel3(val content: String) : ViewModel
