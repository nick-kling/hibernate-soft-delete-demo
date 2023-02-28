package ga.kling.hibernate.softdelete.demo.repositories

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface SoftDeleteRepository<T: Any, ID: Any>: CrudRepository<T, ID> {
    // Only fetch non-deleted entities
    @Query("select e from #{#entityName} e where e.deleted = false")
    override fun findAll(): List<T>

    // Only fetch entity by ID when it has not been soft-deleted
    @Query("select e from #{#entityName} e where e.deleted = false and e.id = ?1")
    override fun findById(id: ID): Optional<T>

    // Update deleted property instead of deleting
    @Modifying
    @Query("update #{#entityName} e set e.deleted = true where e.id = ?1")
    override fun deleteById(id: ID)

    // Update deleted properties instead of deleting
    @Modifying
    @Query("update #{#entityName} e set e.deleted = true where e.id in ?1")
    override fun deleteAllById(ids: MutableIterable<ID>)
}