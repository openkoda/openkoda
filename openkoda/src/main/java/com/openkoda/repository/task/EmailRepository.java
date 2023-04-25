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

import com.openkoda.model.task.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
public interface EmailRepository extends TaskRepository<Email> {

    /**
     * This method requires pessimistic lock to avoid concurrent task execution on two or more nodes
     * In order to query and upgrade Task status in one shot use:
     * emailRepository.findTasksAndSetStateDoing( () -> emailRepository.findByCanBeStartedTrue(pageable) )
     */
    @Lock(PESSIMISTIC_WRITE)
    Page<Email> findByCanBeStartedTrue(Pageable pageable);

    List<Email> findByOrganizationIdAndSubjectContaining(Long orgId, String title);
}
