package darrigo.gabriele.monumental.trees.controller

import darrigo.gabriele.monumental.trees.entity.MonumentalTree
import darrigo.gabriele.monumental.trees.repository.MonumentalTreesRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.data.domain.*

@RestController
@RequestMapping("/monumental-trees")
class MonumentalTreeController(
        private val repository: MonumentalTreesRepository
) {
    @GetMapping
    fun getAll(pageable: Pageable): Page<MonumentalTree> {
        return repository.findAll(pageable)
    }
}