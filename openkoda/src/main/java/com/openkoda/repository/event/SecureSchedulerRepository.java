/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

package com.openkoda.repository.event;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.model.event.Scheduler;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;

import static com.openkoda.controller.common.URLConstants.SCHEDULER;

/**
 * A Repository for {@link Scheduler} entity.
 * It basically allows to use JPARepository's methods and to perform searches among Schedulers records in database.
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-20
 */
@Repository
@SearchableRepositoryMetadata(
        entityKey = SCHEDULER,
        descriptionFormula = "(''|| cron_expression || ', Event data = \"' || event_data || '\"')",
        entityClass = Scheduler.class,
        searchIndexFormula = "lower(cron_expression || ' ' || event_data || ' ' ||"
                + "' orgid:' || COALESCE(CAST (organization_id as text), '') || ' ' ||"
                + Scheduler.REFERENCE_FORMULA + ")"
)
public interface SecureSchedulerRepository extends SecureRepository<Scheduler> {


}
