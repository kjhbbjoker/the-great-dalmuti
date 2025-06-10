package com.example.thegreatdalmuti.lib

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDate
import java.time.LocalDateTime


@NoArgsConstructor
@SuperBuilder
@MappedSuperclass
class BaseEntity {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence")
    @SequenceGenerator(name = "sequence", allocationSize = 1)
    @Id
    val id: Long? = null

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val dateOfCreation: LocalDateTime? = null

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    var dateOfModification: LocalDateTime? = null

    var active = true;

    val entityType: String? = this.javaClass.simpleName
}