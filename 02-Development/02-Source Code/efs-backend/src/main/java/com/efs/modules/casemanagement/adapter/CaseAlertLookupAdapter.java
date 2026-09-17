package com.efs.modules.casemanagement.adapter;

import com.efs.modules.alert.port.out.CaseAlertLookupPort;
import com.efs.modules.casemanagement.repository.CaseAlertRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CaseAlertLookupAdapter
        implements CaseAlertLookupPort {

    private final CaseAlertRepository caseAlertRepository;

    public CaseAlertLookupAdapter(
            CaseAlertRepository caseAlertRepository) {

        this.caseAlertRepository =
                caseAlertRepository;
    }

    @Override
    public List<UUID> getSourceAlertIdsByCaseId(
            UUID caseId) {

        return caseAlertRepository
                .findByCaseIdOrderByGeneratedAtDesc(
                        caseId
                )
                .stream()
                .map(caseAlert ->
                        caseAlert.getSourceAlertId()
                )
                .filter(sourceAlertId ->
                        sourceAlertId != null
                )
                .toList();
    }
}