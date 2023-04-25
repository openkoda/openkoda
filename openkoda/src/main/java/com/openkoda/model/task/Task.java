/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.Organization;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

/**
 *
 * <p>Stores the information required by the job to execute.</p>
 * See also {@link com.openkoda.core.job}
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Task extends TimestampedEntity implements AuditableEntityOrganizationRelated {

    public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    private static final int MAX_ATTEMPTS_DEFAULT = 5;

    public enum TaskState {
        NEW, DOING, DONE, FAILED, FAILED_PERMANENTLY
    }
    private int attempts;

    @Column(name = "start_after")
    private LocalDateTime startAfter;

    @Enumerated(STRING)
    private TaskState state;

    @Formula("(current_timestamp > start_after AND (state = 'NEW' OR state = 'FAILED'))")
    private boolean canBeStarted;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;
    @Column(nullable = true, name = ORGANIZATION_ID)
    private Long organizationId;

    @Formula(REFERENCE_FORMULA)
    private String referenceString;

    /**
     * <p>Constructor for Task.</p>
     */
    public Task() {
        attempts = 0;
        state = TaskState.NEW;
        startAfter = LocalDateTime.now();
    }

    /**
     * <p>Constructor for Task.</p>
     *
     * @param startAfter a {@link java.time.LocalDateTime} object.
     */
    public Task(LocalDateTime startAfter) {
        attempts = 0;
        state = TaskState.NEW;
        this.startAfter = startAfter;
    }

    /**
     * <p>start.</p>
     */
    public void start() {
        attempts++;
        state = TaskState.DOING;
    }

    /**
     * <p>fail.</p>
     */
    public void fail() {
        if(attempts >= getMaxAttempts()) {
            state = TaskState.FAILED_PERMANENTLY;
        } else {
            state = TaskState.FAILED;
        }
    }

    /**
     * <p>complete.</p>
     */
    public void complete() {
        state = TaskState.DONE;
    }

    /**
     * <p>getMaxAttempts.</p>
     *
     * @return a int.
     */
    public int getMaxAttempts() {
        return MAX_ATTEMPTS_DEFAULT;
    }

    /** {@inheritDoc} */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>attempts</code>.</p>
     *
     * @return a int.
     */
    public int getAttempts() {
        return attempts;
    }

    /**
     * <p>Getter for the field <code>state</code>.</p>
     *
     * @return a {@link com.openkoda.model.task.Task.TaskState} object.
     */
    public TaskState getState() {
        return state;
    }

    /**
     * <p>isCanBeStarted.</p>
     *
     * @return a boolean.
     */
    public boolean isCanBeStarted() {
        return canBeStarted;
    }

    /**
     * <p>Getter for the field <code>startAfter</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object.
     */
    public LocalDateTime getStartAfter() {
        return startAfter;
    }

    /**
     * <p>Setter for the field <code>startAfter</code>.</p>
     *
     * @param startAfter a {@link java.time.LocalDateTime} object.
     */
    public void setStartAfter(LocalDateTime startAfter) {
        this.startAfter = startAfter;
    }

    public void increaseAttempt() {
        this.attempts++;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Override
    public String getReferenceString() {
        return referenceString;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Organization getOrganization() {
        return organization;
    }
}
