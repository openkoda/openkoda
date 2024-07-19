package com.openkoda.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GeneralControllerAdvice {

  @ModelAttribute("currentUri")
  String getRequestServletPath(HttpServletRequest request) {
    return request.getServletPath();
  }

  @ModelAttribute("queryString")
  String getRequestQueryString(HttpServletRequest request) {
    return request.getQueryString();
  }
}

