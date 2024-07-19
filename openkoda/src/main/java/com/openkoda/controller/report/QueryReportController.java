package com.openkoda.controller.report;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.file.File;
import com.openkoda.model.report.QueryReport;
import com.openkoda.repository.NativeQueries;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.openkoda.controller.common.URLConstants.*;

@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _QUERY_REPORT, _HTML + _QUERY_REPORT})
public class QueryReportController extends AbstractController implements HasSecurityRules {

    @Inject
    NativeQueries nativeQueries;

    @PostMapping({_NEW_SETTINGS, _ID_SETTINGS})
    @PreAuthorize(CHECK_CAN_CREATE_REPORTS)
    @ResponseBody
    public Object saveReport(@PathVariable(name=ID, required = false) Long existingReportId,
                             @PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
                             @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveReport]");

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(QUERY_REPORT);
        if (!hasGlobalOrOrgPrivilege(conf.getPostNewPrivilege(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init()
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().findOne(existingReportId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, a.result != null ? a.result : conf.createNewEntity(organizationId)))
                .thenSet(organizationRelatedEntity, a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .thenSet(reportId, a -> a.model.get(organizationRelatedEntity).getId())
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.model.get(organizationRelatedEntity)))
                .execute()
                .mav(a -> a.get(reportId), a -> br.getFieldError().getField());
    }

    @PostMapping(_QUERY)
    @PreAuthorize(CHECK_CAN_CREATE_REPORTS)
    public Object runQuery(@PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
                           @RequestParam("query") String query,
                           @RequestParam(value = "reportId", required = false) Long reportId,
                           @RequestParam(value = "resultView", defaultValue = "report-data-table") String resultView) {
        debug("[runQuery]");

        List<LinkedHashMap<String, Object>> queryResult = new ArrayList<>();
        String errorLog = null;
        try {
            queryResult = nativeQueries.runReadOnly(query);
        } catch (InvalidDataAccessResourceUsageException | JpaSystemException | GenericJDBCException e) {
            error("[runQuery]", e);
            errorLog = String.format("%s\n%s", e.getCause().getMessage(), e.getCause().getCause().getMessage());
        }

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(QUERY_REPORT);
        boolean canSaveReport = hasGlobalOrOrgPrivilege(conf.getGetSettingsPrivilege(), organizationId);

        List<LinkedHashMap<String, Object>> finalQueryResult = queryResult;
        String finalErrorLog = errorLog;
        return Flow.init(PageAttributes.query, query)
                .thenSet(PageAttributes.reportId, a -> reportId)
                .thenSet(genericReportViewLinkedHashMap, a -> finalQueryResult)
                .thenSet(error, a -> finalErrorLog)
                .then(a -> (QueryReport) conf.getSecureRepository().findOne(reportId))
                .then(a -> a.result != null ? a.result : new QueryReport(query))
                .thenSet(conf.getFormAttribute(), a -> canSaveReport ? conf.createNewForm(organizationId, a.result) : null)
                .execute()
                .mav(resultView);

    }

    @PostMapping(_QUERY + _CSV)
    @PreAuthorize(CHECK_CAN_READ_REPORTS)
    public void runQueryToCsv(@PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
                              @RequestParam(value = "reportId", required = false) Long reportId,
                              @RequestParam("query") String query,
                              HttpServletResponse response) throws SQLException, IOException {
        debug("[runQueryToCsv]");

        List<LinkedHashMap<String, Object>> queryResult = new ArrayList<>();
        try {
            queryResult = nativeQueries.runReadOnly(query);
        } catch (InvalidDataAccessResourceUsageException | JpaSystemException | GenericJDBCException e) {
            error("[runQueryToCsv]", e);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        List<LinkedHashMap<String, Object>> result = queryResult;

        File report = Flow.init()
                .then(a -> repositories.secure.queryReport.findOne(reportId))
                .thenSet(fileName, a -> a.result != null ? String.format("%s_%s.csv", a.result.getFileName(), dtf.format(LocalDateTime.now())) : String.format("report_%s.csv", dtf.format(LocalDateTime.now())))
                .thenSet(genericReportViewLinkedHashMap, a -> result)
                .thenSet(genericTableViewList, a -> !a.model.get(genericReportViewLinkedHashMap).isEmpty() ?
                        a.model.get(genericReportViewLinkedHashMap).stream().map(stringObjectMap -> stringObjectMap.values().stream().toList()).collect(Collectors.toList())
                        : new ArrayList<>())
                .thenSet(genericTableHeaders, a -> !a.model.get(genericReportViewLinkedHashMap).isEmpty() ? a.model.get(genericReportViewLinkedHashMap).get(0).keySet().toArray(String[]::new) : new String[]{})
                .thenSet(file, a -> {
                    try {
                        return services.csv.createCSV(a.model.get(fileName), a.model.get(genericTableViewList), a.model.get(genericTableHeaders));
                    } catch (IOException | SQLException e) {
                        error("[runQueryToCsv]", e);
                        return null;
                    }
                })
                .execute()
                .get(file);
        services.file.getFileContentAndPrepareResponse(report, true, false, response);
    }

    @GetMapping(_ID)
    @PreAuthorize(CHECK_CAN_READ_REPORTS)
    public Object getReport(@PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
                            @PathVariable(name=ID) Long reportId,
                            @RequestParam(value = "resultView", defaultValue = "report-data-table") String resultView) {
        debug("[getReport]");
        QueryReport queryReportById = repositories.secure.queryReport.findOne(reportId);
        if(queryReportById != null) {
            return runQuery(organizationId, queryReportById.getQuery(), reportId, resultView);
        } else {
            return ResponseEntity.notFound();
        }
    }
}
