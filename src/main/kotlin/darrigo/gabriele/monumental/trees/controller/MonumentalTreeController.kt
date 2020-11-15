package darrigo.gabriele.monumental.trees.controller

import darrigo.gabriele.monumental.trees.entity.MonumentalTree
import darrigo.gabriele.monumental.trees.repository.MonumentalTreesRepository
import org.springframework.data.domain.*
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/monumental-trees", produces = [MediaType.APPLICATION_JSON_VALUE])
class MonumentalTreeController(
    private val repository: MonumentalTreesRepository
) {
    @GetMapping
    fun getAll(pageable: Pageable): Page<MonumentalTree> {
        return repository.findAll(pageable)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): MonumentalTree {
        return repository.findById(id)
            .orElseThrow {
                ResponseStatusException(NOT_FOUND, "Monumental tree with id $id cannot be found")
            }
    }

    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@Valid @RequestBody monumentalTree: MonumentalTree): MonumentalTree {
        return repository.save(monumentalTree)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Int, @Valid @RequestBody monumentalTree: MonumentalTree): MonumentalTree {
        return repository.findById(id)
            .orElseThrow {
                ResponseStatusException(NOT_FOUND, "Monumental tree with id $id cannot be found")
            }
            .let {
                repository.save(
                    monumentalTree.also {
                        it.id = id
                    }
                )
            }
    }
}
