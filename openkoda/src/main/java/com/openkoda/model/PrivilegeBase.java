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

package com.openkoda.model;

import com.openkoda.model.common.LongIdEntity;
import org.springframework.util.Assert;

import static com.openkoda.core.security.HasSecurityRules.BB_CLOSE;
import static com.openkoda.core.security.HasSecurityRules.BB_OPEN;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-31
 */
public interface PrivilegeBase extends LongIdEntity {

    String getLabel();
    PrivilegeGroup getGroup();
    String getCategory();
    String name();
    default boolean isHidden() { return false; };
    default int idOffset() { return 0; };

    default String getDatabaseValue() {
        return BB_OPEN + name() + BB_CLOSE;
    }

    default void checkName(String nameCheck) {
        Assert.isTrue(this.name().equals(nameCheck), "Provided nameCheck have to be exactly the same as enum name.");
    }

    default boolean removable() {
        return false;
    }
    
    default @Override Long getId() {
        return 0l;
    }
}
