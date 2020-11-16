package darrigo.gabriele.monumental.trees.controller

import darrigo.gabriele.monumental.trees.entity.MonumentalTree
import darrigo.gabriele.monumental.trees.repository.MonumentalTreesRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.validation.Valid

@RestController
@RequestMapping("/api/v1/monumental-trees", produces = [MediaType.APPLICATION_JSON_VALUE])
@CrossOrigin("*")
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

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int) {
        return repository.deleteById(id)
    }
}
