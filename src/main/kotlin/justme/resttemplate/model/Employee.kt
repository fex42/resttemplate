package justme.resttemplate.model

data class Employee(
    var id: Long = 0,
    var firstName: String?,
    var lastName: String?,
    var yearlyIncome: Long = 0
) {
    constructor() : this(0, null, null, 0)
}