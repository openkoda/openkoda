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

package com.openkoda.core.repository.common;

import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.ModelConstants;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Used to extend JPA repository methods returning void with methods returning
 * anything other than void, so that they can be used in chained lambda expressions
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */@NoRepositoryBean
public interface UnsecuredFunctionalRepositoryWithLongId<T> extends FunctionalRepositoryWithLongId<T>, JpaSpecificationExecutor<T>, ModelConstants, HasSecurityRules {

    default T findOne(Long id){
        return findById(id).orElse(null);
    }
    default boolean deleteOne(Long aLong) {
        deleteById(aLong);
        return true;
    }

    void deleteById(Long aLong);

}
