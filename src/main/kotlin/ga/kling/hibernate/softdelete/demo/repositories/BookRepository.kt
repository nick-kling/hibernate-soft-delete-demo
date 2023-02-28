package ga.kling.hibernate.softdelete.demo.repositories

import ga.kling.hibernate.softdelete.demo.entities.Book
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: SoftDeleteRepository<Book, Long>