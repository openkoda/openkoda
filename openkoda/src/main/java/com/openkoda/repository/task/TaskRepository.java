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

package com.openkoda.repository.task;

import com.openkoda.core.repository.common.FunctionalRepositoryWithLongId;
import com.openkoda.model.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * Abstract repository for Task-based repositories
 */
@NoRepositoryBean
public interface TaskRepository<T extends Task> extends FunctionalRepositoryWithLongId<T> {

    Pageable OLDEST_1 = PageRequest.of(0, 1, Sort.Direction.ASC, "startAfter");
    Pageable OLDEST_10 = PageRequest.of(0, 10, Sort.Direction.ASC, "startAfter");
    Pageable OLDEST_100 = PageRequest.of(0, 100, Sort.Direction.ASC, "startAfter");

    @Modifying
    @Query(value = "UPDATE Task t SET t.state = :state, t.updatedOn = CURRENT_TIMESTAMP where t in :tasks")
    int setDoingState(@Param("tasks") List<Task> tasks, @Param("state") Task.TaskState taskState);

    /**
     * This method allows to find tasks and immediately set their status to DOING
     * This allows to avoid situations where two jobs run the same tasks.
     * In order to work properly, the query should be a method annotated with
     * @Lock(PESSIMISTIC_WRITE)
     */
    @Transactional(propagation = REQUIRES_NEW)
    default Page<T> findTasksAndSetStateDoing(Supplier<Page<T>> query) {
        Page<T> result = query.get();
        if (result.getNumberOfElements() > 0) {
            setDoingState((List<Task>) result.getContent(), Task.TaskState.DOING);
        }
        return result;
    }

}
