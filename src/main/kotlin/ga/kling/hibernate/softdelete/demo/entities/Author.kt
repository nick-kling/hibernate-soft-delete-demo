package ga.kling.hibernate.softdelete.demo.entities

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "authors")
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var firstName: String = "",
    var lastName: String = "",
    var birthDate: LocalDate? = null,
): SoftDeletableEntity()