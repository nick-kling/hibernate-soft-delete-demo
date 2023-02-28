package ga.kling.hibernate.softdelete.demo.repositories

import ga.kling.hibernate.softdelete.demo.entities.Author
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: SoftDeleteRepository<Author, Long>