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

package com.openkoda.core.exception;

import com.openkoda.core.flow.HttpStatusException;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.core.tracker.RequestIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


/**
 * Exception resolver used for handling exceptions during web requests.
 * See: {@link com.openkoda.core.configuration.MvcConfig#configureHandlerExceptionResolvers}
 */
public class ErrorLoggingExceptionResolver extends
		SimpleMappingExceptionResolver implements LoggingComponentWithRequestId, ReadableCode {

	private static String userAgentExcludedFromErrorLog = null;

	public ErrorLoggingExceptionResolver(String userAgentExcludedFromErrorLog) {
		setWarnLogCategory(getClass().getName());
		setDefaultErrorView("frontend-resource/error");
		this.userAgentExcludedFromErrorLog = StringUtils.defaultIfBlank(userAgentExcludedFromErrorLog, null);
	}


	/**
	 * This method uses the older API and gets passed the handler (typically the
	 * <tt>@Controller</tt>) that generated the exception.
	 */
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
											  HttpServletResponse response, Object handler, Exception exception) {
		if (shouldErrorLogException(exception, request)) {
			error(exception, "Error message: {}, Status: {}, URI: {}",
					exception == null ? "" : exception.getLocalizedMessage(), response.getStatus(), request.getRequestURI());
		}
		HttpStatus errorStatus = (exception instanceof HttpStatusException) ?
				((HttpStatusException) exception).status :
					((exception instanceof AccessDeniedException) ? UNAUTHORIZED : INTERNAL_SERVER_ERROR);
		String redirect = String.format("redirect:/error?status=%s&requestId=%s", errorStatus.name(), RequestIdHolder.getId());
		return new ModelAndView(redirect);
	}

	/**
	 * Determines if a given exception should be logged with ERROR level
	 */
	private boolean shouldErrorLogException(Exception exception, HttpServletRequest request) {
		boolean exceptionIsNotNull = exception != null;
		String userAgent = request.getHeader("User-Agent");
		boolean isExcludedUserAgent = isExcludedUserAgent(userAgent);
		boolean hasCause = exceptionIsNotNull && exception.getCause() != null;
		boolean isClientAbortException = exceptionIsNotNull && hasCause && (exception instanceof ClientAbortException)
				&& (exception.getCause() instanceof IOException);
		debug("[exceptionToLog] is not null {} class {} cause {} agent [{}]", exceptionIsNotNull,
                exceptionIsNotNull ? exception.getClass().getSimpleName() : "",
                hasCause ? exception.getCause().getClass().getSimpleName() : "", userAgent);
		return not(isExcludedUserAgent || isClientAbortException);
	}

	/**
	 * Determines if given user Agent should be logged as ERROR in case of exception.
	 * Useful in order to exclude log polluting from known troublesome agents.
	 */
	public static boolean isExcludedUserAgent(String userAgent) {
        return StringUtils.contains(userAgent, userAgentExcludedFromErrorLog);
    }

}