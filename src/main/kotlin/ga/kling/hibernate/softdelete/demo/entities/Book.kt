package ga.kling.hibernate.softdelete.demo.entities

import jakarta.persistence.*
import org.hibernate.annotations.*
import java.time.LocalDate
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    var title: String = "",
    var publishedDate: LocalDate? = null,
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumnsOrFormulas(
        JoinColumnOrFormula(column = JoinColumn(name = "author_id", referencedColumnName = "id")),
        JoinColumnOrFormula(formula = JoinFormula(value = "false", referencedColumnName = "deleted"))
    )
    var author: Author? = null,
    @ManyToMany
    @JoinTable(
        name = "book_categories",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "category_id")]
    )
    @Where(clause = "deleted = false")
    var categories: MutableList<Category> = mutableListOf(),
): SoftDeletableEntity()
