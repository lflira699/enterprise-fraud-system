package com.efs.modules.transaction.port.out;

import java.util.UUID;

public interface CustomerExistencePort {

    boolean exists(UUID customerId);
}
