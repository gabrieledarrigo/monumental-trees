package darrigo.gabriele.monumental.trees.controller

import darrigo.gabriele.monumental.trees.entity.MonumentalTree
import darrigo.gabriele.monumental.trees.repository.MonumentalTreesRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
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
@RequestMapping("/api/v1/monumental-trees", produces = [APPLICATION_JSON_VALUE])
@CrossOrigin("*")
class MonumentalTreeController(
    private val repository: MonumentalTreesRepository
) {

    @Operation(summary = "Returns a paginated list of monumental trees")
    @GetMapping
    fun getAll(pageable: Pageable): Page<MonumentalTree> {
        return repository.findAll(pageable)
    }

    @Operation(summary = "Returns a monumental tree by its unique id")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = MonumentalTree::class))
                ]
            ),
            ApiResponse(responseCode = "404", description = "Monumental tree with the specified id cannot be found", content = [Content()])
        ]
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable("id") id: Int): MonumentalTree {
        return repository.findById(id)
            .orElseThrow {
                ResponseStatusException(NOT_FOUND, "Monumental tree with id $id cannot be found")
            }
    }

    @Operation(summary = "Create a new monumental tree")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = MonumentalTree::class))
                ]
            ),
            ApiResponse(responseCode = "400", description = "The monumental tree payload is invalid", content = [Content()])
        ]
    )
    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@Valid @RequestBody monumentalTree: MonumentalTree): MonumentalTree {
        return repository.save(monumentalTree)
    }

    @Operation(summary = "Update an existing monumental tree")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = MonumentalTree::class))
                ]
            ),
            ApiResponse(responseCode = "400", description = "The monumental tree payload is invalid", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Monumental tree with the specified id cannot be found", content = [Content()])
        ]
    )
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

    @Operation(summary = "Delete an existing monumental tree")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", content = [Content()]),
            ApiResponse(responseCode = "404", description = "Monumental tree with the specified id cannot be found", content = [Content()])
        ]
    )
    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int) {
        repository.findById(id)
            .orElseThrow {
                ResponseStatusException(NOT_FOUND, "Monumental tree with id $id cannot be found")
            }
            .let {
                repository.deleteById(id)
            }
    }
}
