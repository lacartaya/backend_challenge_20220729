package com.celonis.challenge.model;

import com.celonis.challenge.enums.TaskStatusEnum;
import com.celonis.challenge.enums.TaskTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "project_generation_task")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class ProjectGenerationTask {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(length = 36)
    @ToString.Include
    private String id;

    @Column
    @ToString.Include
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @ToString.Include
    private Date creationDate;

    @JsonIgnore
    @Column(length = 2048)
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    @Builder.Default
    @ToString.Include
    private TaskTypeEnum type = TaskTypeEnum.ZIP_GENERATION;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    @Builder.Default
    @ToString.Include
    private TaskStatusEnum status = TaskStatusEnum.PENDING;

    private Integer startValue;
    private Integer targetValue;
    private Integer currentValue;

    @Builder.Default
    private Integer progress = 0;

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) creationDate = new Date();
        if (status == null) status = TaskStatusEnum.PENDING;
        if (progress == null) progress = 0;
        if (type == null) type = TaskTypeEnum.ZIP_GENERATION;
    }
}
