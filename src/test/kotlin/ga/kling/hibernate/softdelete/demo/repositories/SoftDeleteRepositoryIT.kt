package ga.kling.hibernate.softdelete.demo.repositories

import ga.kling.hibernate.softdelete.demo.entities.Author
import ga.kling.hibernate.softdelete.demo.entities.Book
import ga.kling.hibernate.softdelete.demo.entities.Category
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.jdbc.Sql
import java.time.LocalDate

@DataJpaTest
class SoftDeleteRepositoryIT {
    @Autowired
    private lateinit var bookRepository: BookRepository
    @Autowired
    private lateinit var authorRepository: AuthorRepository
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    var savedAuthors: MutableList<Author> = mutableListOf()
    var savedBooks: MutableList<Book> = mutableListOf()
    var savedCategories: MutableList<Category> = mutableListOf()

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Sql("truncate_tables.sql")
    @BeforeEach
    fun setup() {
        val authors = listOf(
            Author(
                firstName = "Dan",
                lastName = "Brown",
                birthDate = LocalDate.of(1964, 6, 22),
            ),
            Author(
                firstName = "J.K.",
                lastName = "Rowling",
                birthDate = LocalDate.of(1965, 7, 31),
            ),
        )
        savedAuthors = authorRepository.saveAll(authors).toMutableList()

        val categories = listOf(
            Category(
                name = "Fantasy",
            ),
            Category(
                name = "Mystery",
            ),
            Category(
                name = "Thriller",
            ),
        )
        savedCategories = categoryRepository.saveAll(categories).toMutableList()

        val books = listOf(
            Book(
                title = "The Da Vinci Code",
                publishedDate = LocalDate.of(2003, 3, 18),
                author = savedAuthors.find { it.lastName == "Brown" },
                categories = savedCategories.filter {
                    it.name in listOf("Mystery", "Thriller")
                }.toMutableList(),
            ),
            Book(
                title = "Harry Potter and the Deathly Hallows",
                publishedDate = LocalDate.of(2007, 7, 21),
                author = savedAuthors.find { it.lastName == "Rowling" },
                categories = savedCategories.filter {
                    it.name in listOf("Fantasy")
                }.toMutableList(),
            ),
            Book(
                title = "Harry Potter and the Half-Blood Prince",
                publishedDate = LocalDate.of(2005, 7, 16),
                author = savedAuthors.find { it.lastName == "Rowling" },
                categories = savedCategories.filter {
                    it.name in listOf("Fantasy")
                }.toMutableList(),
            )
        )
        savedBooks = bookRepository.saveAll(books).toMutableList()
    }
    @Test
    fun `Inserted entities can be soft deleted`() {
        val bookIdToDelete = savedBooks.find { it.title == "Harry Potter and the Half-Blood Prince" }?.id
        bookIdToDelete?.let {
            bookRepository.deleteById(it)
        }
        val categoryIdToDelete = savedCategories.find { it.name == "Mystery" }?.id
        categoryIdToDelete?.let {
            categoryRepository.deleteById(it)
        }

        val authorIdToDelete = savedAuthors.find { it.lastName == "Brown" }?.id
        authorIdToDelete?.let {
            authorRepository.deleteById(it)
        }

        val retrievedBooks = bookRepository.findAll()
        assert(retrievedBooks.size == 2)
        assert(retrievedBooks.any { it.title == "The Da Vinci Code" })
        assert(retrievedBooks.any { it.title == "Harry Potter and the Deathly Hallows" })
        assert(retrievedBooks.none { it.title == "Harry Potter and the Half-Blood Prince" })

        val retrievedAuthors = authorRepository.findAll()
        assert(retrievedAuthors.size == 1)
        assert(retrievedAuthors.any { it.lastName == "Rowling" })
        assert(retrievedAuthors.none { it.lastName == "Brown" })

        val retrievedCategories = categoryRepository.findAll()
        assert(retrievedCategories.size == 2)
        assert(retrievedCategories.any { it.name == "Fantasy" })
        assert(retrievedCategories.any { it.name == "Thriller" })
        assert(retrievedCategories.none { it.name == "Mystery" })
    }

    @Test
    fun `Make sure soft deleted objects are not returned as part of a ManyToOne association`() {
        val authorIdToDelete = savedAuthors.find { it.lastName == "Brown" }?.id
        authorIdToDelete?.let {
            authorRepository.deleteById(it)
        }
        // Make sure that retrieved books are equal to state of DB
        entityManager.flush()
        entityManager.clear()

        val retrievedBooks = bookRepository.findAll()
        assertNull(retrievedBooks.find { it.title == "The Da Vinci Code" }?.author)
        assertNotNull(retrievedBooks.find { it.title == "Harry Potter and the Deathly Hallows" }?.author)
        assertNotNull(retrievedBooks.find { it.title == "Harry Potter and the Half-Blood Prince" }?.author)
    }

    @Test
    fun `Make sure soft deleted objects are not returned as part of a ManyToMany association`() {
        val categoryIdToDelete = savedCategories.find { it.name == "Mystery" }?.id
        categoryIdToDelete?.let {
            categoryRepository.deleteById(it)
        }

        // Make sure that retrieved books are equal to state of DB
        entityManager.flush()
        entityManager.clear()

        val retrievedBooks = bookRepository.findAll()
        val daVinciCodeCategories = retrievedBooks.find { it.title == "The Da Vinci Code" }?.categories
        val deathlyHallowsCategories = retrievedBooks.find { it.title == "Harry Potter and the Deathly Hallows" }?.categories
        val halfBloodPrinceCategories = retrievedBooks.find { it.title == "Harry Potter and the Half-Blood Prince" }?.categories
        assert(daVinciCodeCategories?.size == 1)
        assert(deathlyHallowsCategories?.size == 1)
        assert(halfBloodPrinceCategories?.size == 1)
        assert(daVinciCodeCategories?.any { it.name == "Thriller" } == true)
        assert(daVinciCodeCategories?.none { it.name == "Mystery" } == true)
        assert(deathlyHallowsCategories?.any { it.name == "Fantasy" } == true)
        assert(halfBloodPrinceCategories?.any { it.name == "Fantasy" } == true)
    }
}