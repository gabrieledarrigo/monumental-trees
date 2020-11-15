package darrigo.gabriele.monumental.trees.repository

import darrigo.gabriele.monumental.trees.entity.MonumentalTree
import org.springframework.data.repository.PagingAndSortingRepository

interface MonumentalTreesRepository : PagingAndSortingRepository<MonumentalTree, Int>
