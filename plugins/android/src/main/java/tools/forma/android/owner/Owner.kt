package tools.forma.android.owner

sealed class Owner(val name: String, val email: String, val tags: Map<String, String>)

class Person(
    fullName: String,
    val userName: String,
    email: String,
    tags: Map<String, String> = emptyMap()
) : Owner(fullName, email, tags)

class Team(
    name: String,
    emailAlias: String,
    val leads: Array<Person> = emptyArray(),
    tags: Map<String, String> = emptyMap()
) : Owner(name, emailAlias, tags)

object NoOwner : Owner("No owner", "", emptyMap())