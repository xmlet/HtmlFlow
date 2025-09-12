package htmlflow.viewloader

interface ViewModel

data class UserVm(val name: String) : ViewModel

open class BaseVm(open val baseContent: String) : ViewModel

class DerivedVm(override val baseContent: String) : BaseVm(baseContent)

interface ProfileLike : ViewModel {
    val name: String
}

data class PublicProfile(override val name: String, val bio: String) : ProfileLike

@Suppress("unused")
data class UnrelatedVm(val data: String) : ViewModel {
    override fun toString(): String = data
}

interface SecondaryInterface : ViewModel {
    val secondary: String
}

class MultiInterfaceVm(override val name: String, override val secondary: String) :
    ProfileLike, SecondaryInterface
