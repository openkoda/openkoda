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

package com.openkoda.controller;

import com.openkoda.core.job.JobsScheduler;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.repository.SearchableRepositories;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.openkoda.controller.common.URLConstants._HTML;

/**
 * Handles requests for global search. The search is performed among entities implementing interface {@link com.openkoda.model.common.SearchableEntity}
 * which requires implementation of {@link SearchableEntity#getIndexString()}. The implementation is done on the repository level by means of the annotation value
 * {@link SearchableRepositoryMetadata#searchIndexFormula()}.
 * The field {@link SearchableEntity#getIndexString()} for every searchable entity is updated continuously as configured in {@link JobsScheduler#searchIndexUpdaterJob()}.
 * The initial setup of {@link SearchableRepositories#getSearchIndexUpdates()} required for this update is done on the application startup in {@link SearchableRepositories#discoverSearchableRepositories()}
 */
@RestController
@RequestMapping(_HTML)
public class GlobalSearchController extends AbstractGlobalSearchController {

    /** Returns results for global search with {@param search} as the search term
     * @param searchPageable
     * @param search
     * @return
     */
    @GetMapping(value = _SEARCH)
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object getSearchResult(@Qualifier("search") Pageable searchPageable,
                                  @RequestParam(required = false, defaultValue = "", name = "search_search") String search) {
        debug("[getSearchResult] {}", search);
        return findSearchResult(searchPageable, search)
                .mav("search");
    }

}
