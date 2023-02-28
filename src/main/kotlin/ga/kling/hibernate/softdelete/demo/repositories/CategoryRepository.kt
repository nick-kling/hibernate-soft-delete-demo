package ga.kling.hibernate.softdelete.demo.repositories

import ga.kling.hibernate.softdelete.demo.entities.Category
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: SoftDeleteRepository<Category, Long>