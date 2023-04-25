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

package com.openkoda.core.audit;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Service
/**
 * <p>IpService class.</p>
 *
 * <p>Extracts Ip address for incoming requests, including proxied traffic.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class IpService implements LoggingComponentWithRequestId {

   /**
    * This method looks for current client ip. Should work with proxies. Please
    * note that it might not work correctly, as it depends on correct behavior
    * of user browser and proxy servers. If current thread is not bound to
    * client request, returns null.
    *
    * @return current client ip address if can obtain it, otherwise null.
    */
   public String getCurrentUserIpAddress() {
      debug( "[getCurrentUserIpAddress]" );
      ServletRequestAttributes currentRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if ( null != currentRequestAttributes ) {
         HttpServletRequest currentRequest = currentRequestAttributes.getRequest();
         return getIpFromRequest( currentRequest );
      }
      return null;
   }

   public boolean checkIPAllowed(String allowedIps, HttpServletRequest request) {
      return checkIPAllowed(allowedIps, getIpFromRequest(request));
   }
   
   private boolean checkIPAllowed(String allowedIps, String requestIP){
      debug("[checkIPAllowed] allowedIps: {} requestIP: {}", allowedIps, requestIP);
      return StringUtils.isBlank(allowedIps) || Arrays.asList(allowedIps.split(",")).contains(requestIP);
   }
   /**
    * This method returns ip address of a client. Works for connections with
    * proxies.
    * 
    * @param request
    * @return ip of client that sent given request
    */
   private String getIpFromRequest(HttpServletRequest request) {
      debug( "[getIpFromRequest] {}" , request );
      return null != getAddressForProxiedRequest( request ) ? getAddressForProxiedRequest( request ) : getAddressForNotProxiedRequest( request );
   }


   /**
    * Extracts client's IP from "X-Forwarded-For" header from the request
    * Useful for requests behind reverse proxy.
    */
   private String getAddressForProxiedRequest(HttpServletRequest request) {
      debug( "[getAddressForProxiedRequest] {}" , request );
      String forwardedForIp = request.getHeader("X-Forwarded-For");
      return  forwardedForIp != null && !forwardedForIp.equals("") && !forwardedForIp.equals("-") ? StringUtils.substringBefore(forwardedForIp, ",") : null;
   }

   /**
    * Extracts client's IP from the request.
    * For application behind reverse proxy it's usually localhost, see {@link #getAddressForProxiedRequest}
    */
   private String getAddressForNotProxiedRequest(HttpServletRequest request) {
      debug( "[getAddressForNotProxiedRequest] {}" , request );
      return request.getRemoteAddr();
   }
}
