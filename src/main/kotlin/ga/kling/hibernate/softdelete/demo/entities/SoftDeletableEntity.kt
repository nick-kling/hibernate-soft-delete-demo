package ga.kling.hibernate.softdelete.demo.entities

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.io.Serializable

@MappedSuperclass
// Needs to be serializable for JoinFormula to function
open class SoftDeletableEntity: Serializable {
    @Column(name = "deleted")
    var deleted: Boolean = false
}