import com.stepango.forma.owner.Team
import com.stepango.forma.owner.Person

object Users {
    val stepango = Person(
        fullName = "Stepan Goncharov",
        userName = "stepango",
        email = "step.89.g@gmail.com"
    )

    val michaem = Person(
        fullName = "Michael Emelyanov",
        userName = "michaem",
        email = "michaem@gmail.com"
    )
}

object Teams {
    val core = Team(
        name ="Core",
        emailAlias = "android-core@forma.com",
        leads = arrayOf(
            Users.stepango,
            Users.michaem
        )
    )
}