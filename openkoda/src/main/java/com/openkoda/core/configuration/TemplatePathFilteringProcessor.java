package com.openkoda.core.configuration;

import static com.openkoda.controller.common.URLConstants.FRONTENDRESOURCEREGEX;
import static com.openkoda.service.export.FolderPathConstants.FRONTEND_RESOURCE_;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.openkoda.controller.common.URLConstants;
import com.openkoda.model.component.FrontendResource;

@Component
public class TemplatePathFilteringProcessor {

    private static final Pattern accessLevelPath = Pattern.compile(FRONTEND_RESOURCE_
            + "(" + Arrays.stream(FrontendResource.AccessLevel.values()).map(al -> al.toString().toLowerCase()).collect(Collectors.joining("|")) + ")/"
            + FRONTENDRESOURCEREGEX + "$");
    
    public static class FilteredTemplatePath {
        private boolean email = false;
        private FrontendResource.AccessLevel accessLevel;

        private String filteredTemplate;
        private String filteredResourceName;
        private String frontendResourceEntryName;

        public boolean isEmail() {
            return email;
        }

        public void setEmail(boolean isEmail) {
            this.email = isEmail;
        }

        public FrontendResource.AccessLevel getAccessLevel() {
            return accessLevel;
        }

        public void setAccessLevel(FrontendResource.AccessLevel accessLevel) {
            this.accessLevel = accessLevel;
        }

        public String getFilteredTemplate() {
            return filteredTemplate;
        }

        public void setFilteredTemplate(String filteredTemplate) {
            this.filteredTemplate = filteredTemplate;
        }

        public String getFilteredResourceName() {
            return filteredResourceName;
        }

        public void setFilteredResourceName(String filteredResourceName) {
            this.filteredResourceName = filteredResourceName;
        }

        public String getFrontendResourceEntryName() {
            return frontendResourceEntryName;
        }

        public void setFrontendResourceEntryName(String frontendResourceEntryName) {
            this.frontendResourceEntryName = frontendResourceEntryName;
        }
    }

    public FilteredTemplatePath processTemplatePath(String template, String resourceName,
            FrontendResource.AccessLevel tenantedResourceAccessLevel) {

        FilteredTemplatePath filteredPath = new FilteredTemplatePath();
        filteredPath.setAccessLevel(tenantedResourceAccessLevel);
        filteredPath.setFilteredTemplate(template);
        filteredPath.setFilteredResourceName(resourceName);

        // resolve template access level
        Matcher m = accessLevelPath.matcher(template);
        if (m.matches()) {
            filteredPath.setAccessLevel(FrontendResource.AccessLevel.valueOf(m.group(1).toUpperCase()));
        }

        int emailPath = -1;
        if (template.endsWith(URLConstants.EMAILRESOURCE_DISCRIMINATOR)) {
            emailPath = template.indexOf("email");
            filteredPath.setEmail(true);
            filteredPath.setFilteredTemplate(template.substring(0, template.length() - 1));
            filteredPath.setFilteredResourceName(resourceName.replace(URLConstants.EMAILRESOURCE_DISCRIMINATOR, ""));
            filteredPath
                    .setFrontendResourceEntryName(StringUtils.substring(template, emailPath, template.length() - 1));
        } else {
            filteredPath.setFrontendResourceEntryName(StringUtils.substringAfterLast(template, "/"));
        }

        return filteredPath;
    }
}
