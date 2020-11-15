package darrigo.gabriele.monumental.trees.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.OffsetDateTime
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Size

@Entity
@Table(name = "monumental_tree")
class MonumentalTree(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int? = null,

        @Enumerated(EnumType.STRING)
        var status: Status,

        @Column(name = "point_id", unique = true)
        @field:Size(max = 512)
        var pointId: String,

        @Enumerated(EnumType.STRING)
        var typology: Typology,

        @field:Size(max = 512)
        var region: String,

        @field:Size(max = 512)
        var province: String,

        @field:Size(max = 1024)
        var locality: String,

        @field:Size(max = 1024)
        var place: String,

        var latitude: Double,

        var longitude: Double,

        var altitude: Double,

        @field:Size(max = 1024)
        var genre: String,

        @Column(name = "scientific_name")
        @field:Size(max = 1024)
        var scientificName: String,

        @Column(name = "common_name")
        @field:Size(max = 1024)
        var commonName: String,

        @Enumerated(EnumType.STRING)
        var context: Context,

        @Column(name = "age_criteria")
        var ageCriteria: Boolean = false,

        @Column(name = "circumference_criteria")
        var circumferenceCriteria: Boolean = false,

        @Column(name = "height_criteria")
        var heightCriteria: Boolean = false,

        @Column(name = "crown_criteria")
        var crownCriteria: Boolean = false,

        @Column(name = "shape_criteria")
        var shapeCriteria: Boolean = false,

        @Column(name = "ecological_criteria")
        var ecologicalCriteria: Boolean = false,

        @Column(name = "botanic_criteria")
        var botanicCriteria: Boolean = false,

        @Column(name = "architecture_criteria")
        var architectureCriteria: Boolean = false,

        @Column(name = "landscape_criteria")
        var landscapeCriteria: Boolean = false,

        @Column(name = "historical_criteria")
        var historicalCriteria: Boolean = false,

        var height: Double? = null,

        var circumference: Double? = null,

        @Column(name = "average_group_height")
        var averageGroupHeight: Double? = null,

        @Column(name = "max_group_height")
        var maxGroupHeight: Double? = null,

        @Column(name = "average_group_circumference")
        var averageGroupCircumference: Double? = null,

        @Column(name = "max_group_circumference")
        var maxGroupCircumference: Double? = null,

        @field:Size(max = 1024)
        var decree: String? = null,

        @Column(name = "additional_decree")
        @field:Size(max = 1024)
        var additionalDecree: String? = null,

        @CreatedDate
        @Column(name = "created_at")
        var createdAt: OffsetDateTime = OffsetDateTime.now(),

        @LastModifiedDate
        @Column(name = "updated_at")
        var updatedAt: OffsetDateTime = OffsetDateTime.now()
)


enum class Status {
    ISCRITTO_IN_ELENCO,
    RIMOSSO_PER_ABBATTIMENTO,
    RIMOSSO_PER_MORTE_NATURALE,
    RIMOSSO_PER_PERDITA_REQUISITI,
}

enum class Typology {
    ALBERO_SINGOLO,
    GRUPPO,
    GRUPPO_PLURISPEFICIFO,
    FILARE_SINGOLO,
    FILARE_DOPPIO,
    VIALE_ALBERATO,
}

enum class Context {
    URBANO,
    EXTRA_URBANO,
}