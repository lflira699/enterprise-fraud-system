package com.efs.modules.alert.port.out;

import java.util.List;
import java.util.UUID;

public interface CaseAlertLookupPort {

    List<UUID> getSourceAlertIdsByCaseId(
            UUID caseId
    );
}