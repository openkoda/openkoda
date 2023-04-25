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

package com.openkoda.core.helper;

import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.RequestIdHolder;
import com.openkoda.model.common.ModelConstants;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class SqlCommentStatementInspector
        implements StatementInspector, ReadableCode{

    @Override
    public String inspect(String sql) {

        if (not(sql.contains(ModelConstants.USER_ID_PLACEHOLDER))) {
            return includeRequestId(sql);
        }

        String userId = UserProvider.getUserIdOrNotExistingIdAsString();

        return includeRequestId(sql.replaceAll(ModelConstants.USER_ID_PLACEHOLDER, userId));
    }

//    Includes request id of a current thread to sql statement as a comment at the end
    private String includeRequestId(String sql) {
        return StringUtils.isNotEmpty(RequestIdHolder.getId()) ? String.format("%s /*%s*/", sql, RequestIdHolder.getId()) : sql;
    }
}