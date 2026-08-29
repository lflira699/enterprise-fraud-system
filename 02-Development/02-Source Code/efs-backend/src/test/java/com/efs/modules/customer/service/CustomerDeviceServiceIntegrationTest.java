package com.efs.modules.customer.service;

import com.efs.modules.customer.dto.CustomerDeviceRequest;
import com.efs.modules.customer.dto.CustomerDeviceResponse;
import com.efs.modules.customer.dto.CustomerRequest;
import com.efs.modules.customer.dto.CustomerResponse;
import com.efs.modules.customer.repository.CustomerDeviceRepository;
import com.efs.shared.exception.DuplicateRecordException;
import com.efs.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerDeviceServiceIntegrationTest {

    @Autowired
    private CustomerDeviceServiceInterface customerDeviceService;

    @Autowired
    private CustomerServiceInterface customerService;

    @Autowired
    private CustomerDeviceRepository customerDeviceRepository;

    @Test
    void createDeviceShouldPersistProvidedValues() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceRequest request =
                buildDeviceRequest(uniqueFingerprint());

        CustomerDeviceResponse response =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        request
                );

        assertNotNull(response);
        assertNotNull(response.getDeviceId());

        assertEquals(
                customer.getCustomerId(),
                response.getCustomerId()
        );

        assertEquals(
                request.getDeviceFingerprint(),
                response.getDeviceFingerprint()
        );

        assertEquals(
                "MOBILE",
                response.getDeviceType()
        );

        assertEquals(
                "Android",
                response.getOperatingSystem()
        );

        assertEquals(
                "Chrome",
                response.getBrowser()
        );

        assertEquals(
                "Guatemala",
                response.getCountry()
        );

        assertEquals(
                "Guatemala City",
                response.getCity()
        );

        assertEquals(
                "TRUSTED",
                response.getTrustLevel()
        );

        assertEquals(
                request.getLastSeen(),
                response.getLastSeen()
        );

        assertEquals(
                Boolean.TRUE,
                response.getActive()
        );

        assertTrue(
                customerDeviceRepository.existsById(
                        response.getDeviceId()
                )
        );
    }

    @Test
    void createDeviceShouldDefaultActiveToTrueWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceRequest request =
                buildDeviceRequest(uniqueFingerprint());

        request.setActive(null);

        CustomerDeviceResponse response =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.TRUE,
                response.getActive()
        );
    }

    @Test
    void createDeviceShouldPreserveFalseActiveValue() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceRequest request =
                buildDeviceRequest(uniqueFingerprint());

        request.setActive(Boolean.FALSE);

        CustomerDeviceResponse response =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        request
                );

        assertEquals(
                Boolean.FALSE,
                response.getActive()
        );
    }

    @Test
    void createDeviceShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        CustomerDeviceRequest request =
                buildDeviceRequest(uniqueFingerprint());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDeviceService.createDevice(
                        missingCustomerId,
                        request
                )
        );
    }

    @Test
    void createDeviceShouldRejectDuplicateFingerprintForSameCustomer() {

        CustomerResponse customer = createCustomer();

        String fingerprint = uniqueFingerprint();

        CustomerDeviceRequest firstRequest =
                buildDeviceRequest(fingerprint);

        CustomerDeviceRequest duplicateRequest =
                buildDeviceRequest(fingerprint);

        customerDeviceService.createDevice(
                customer.getCustomerId(),
                firstRequest
        );

        assertThrows(
                DuplicateRecordException.class,
                () -> customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        duplicateRequest
                )
        );
    }

    @Test
    void sameFingerprintShouldBeAllowedForDifferentCustomers() {

        CustomerResponse firstCustomer = createCustomer();
        CustomerResponse secondCustomer = createCustomer();

        String fingerprint = uniqueFingerprint();

        CustomerDeviceResponse firstDevice =
                customerDeviceService.createDevice(
                        firstCustomer.getCustomerId(),
                        buildDeviceRequest(fingerprint)
                );

        CustomerDeviceResponse secondDevice =
                customerDeviceService.createDevice(
                        secondCustomer.getCustomerId(),
                        buildDeviceRequest(fingerprint)
                );

        assertNotNull(firstDevice.getDeviceId());
        assertNotNull(secondDevice.getDeviceId());

        assertNotEquals(
                firstDevice.getDeviceId(),
                secondDevice.getDeviceId()
        );

        assertEquals(
                fingerprint,
                firstDevice.getDeviceFingerprint()
        );

        assertEquals(
                fingerprint,
                secondDevice.getDeviceFingerprint()
        );
    }

    @Test
    void getDeviceByIdShouldReturnExistingDevice() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceResponse created =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        CustomerDeviceResponse found =
                customerDeviceService.getDeviceById(
                        created.getDeviceId()
                );

        assertEquals(
                created.getDeviceId(),
                found.getDeviceId()
        );

        assertEquals(
                customer.getCustomerId(),
                found.getCustomerId()
        );

        assertEquals(
                created.getDeviceFingerprint(),
                found.getDeviceFingerprint()
        );
    }

    @Test
    void getDeviceByIdShouldThrowWhenDeviceDoesNotExist() {

        UUID missingDeviceId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDeviceService.getDeviceById(
                        missingDeviceId
                )
        );
    }

    @Test
    void getDevicesByCustomerIdShouldReturnCustomerDevices() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceResponse first =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        CustomerDeviceResponse second =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        List<CustomerDeviceResponse> devices =
                customerDeviceService
                        .getDevicesByCustomerId(
                                customer.getCustomerId()
                        );

        assertEquals(2, devices.size());

        assertTrue(
                devices.stream()
                        .anyMatch(device ->
                                first.getDeviceId()
                                        .equals(
                                                device.getDeviceId()
                                        )
                        )
        );

        assertTrue(
                devices.stream()
                        .anyMatch(device ->
                                second.getDeviceId()
                                        .equals(
                                                device.getDeviceId()
                                        )
                        )
        );
    }

    @Test
    void getDevicesByCustomerIdShouldThrowWhenCustomerDoesNotExist() {

        UUID missingCustomerId = UUID.randomUUID();

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDeviceService
                        .getDevicesByCustomerId(
                                missingCustomerId
                        )
        );
    }

    @Test
    void updateDeviceShouldPersistUpdatedValues() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceResponse created =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        CustomerDeviceRequest updateRequest =
                buildDeviceRequest(
                        uniqueFingerprint()
                );

        updateRequest.setDeviceType("DESKTOP");
        updateRequest.setOperatingSystem("Windows 11");
        updateRequest.setBrowser("Edge");
        updateRequest.setCountry("United States");
        updateRequest.setCity("Miami");
        updateRequest.setTrustLevel("REVIEW");
        updateRequest.setActive(Boolean.FALSE);

        LocalDateTime updatedLastSeen =
                LocalDateTime.of(
                        2026,
                        8,
                        29,
                        12,
                        30,
                        0
                );

        updateRequest.setLastSeen(updatedLastSeen);

        CustomerDeviceResponse updated =
                customerDeviceService.updateDevice(
                        created.getDeviceId(),
                        updateRequest
                );

        assertEquals(
                created.getDeviceId(),
                updated.getDeviceId()
        );

        assertEquals(
                customer.getCustomerId(),
                updated.getCustomerId()
        );

        assertEquals(
                updateRequest.getDeviceFingerprint(),
                updated.getDeviceFingerprint()
        );

        assertEquals(
                "DESKTOP",
                updated.getDeviceType()
        );

        assertEquals(
                "Windows 11",
                updated.getOperatingSystem()
        );

        assertEquals(
                "Edge",
                updated.getBrowser()
        );

        assertEquals(
                "United States",
                updated.getCountry()
        );

        assertEquals(
                "Miami",
                updated.getCity()
        );

        assertEquals(
                "REVIEW",
                updated.getTrustLevel()
        );

        assertEquals(
                updatedLastSeen,
                updated.getLastSeen()
        );

        assertEquals(
                Boolean.FALSE,
                updated.getActive()
        );
    }

    @Test
    void updateDeviceShouldAllowKeepingSameFingerprint() {

        CustomerResponse customer = createCustomer();

        String fingerprint = uniqueFingerprint();

        CustomerDeviceResponse created =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(fingerprint)
                );

        CustomerDeviceRequest updateRequest =
                buildDeviceRequest(fingerprint);

        updateRequest.setTrustLevel("KNOWN");
        updateRequest.setCity("Quetzaltenango");

        CustomerDeviceResponse updated =
                customerDeviceService.updateDevice(
                        created.getDeviceId(),
                        updateRequest
                );

        assertEquals(
                created.getDeviceId(),
                updated.getDeviceId()
        );

        assertEquals(
                fingerprint,
                updated.getDeviceFingerprint()
        );

        assertEquals(
                "KNOWN",
                updated.getTrustLevel()
        );

        assertEquals(
                "Quetzaltenango",
                updated.getCity()
        );
    }

    @Test
    void updateDeviceShouldRejectFingerprintAlreadyUsedBySameCustomer() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceResponse first =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        CustomerDeviceResponse second =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        buildDeviceRequest(
                                uniqueFingerprint()
                        )
                );

        CustomerDeviceRequest duplicateUpdate =
                buildDeviceRequest(
                        second.getDeviceFingerprint()
                );

        assertThrows(
                DuplicateRecordException.class,
                () -> customerDeviceService.updateDevice(
                        first.getDeviceId(),
                        duplicateUpdate
                )
        );
    }

    @Test
    void updateDeviceShouldDefaultActiveToTrueWhenNull() {

        CustomerResponse customer = createCustomer();

        CustomerDeviceRequest createRequest =
                buildDeviceRequest(
                        uniqueFingerprint()
                );

        createRequest.setActive(Boolean.FALSE);

        CustomerDeviceResponse created =
                customerDeviceService.createDevice(
                        customer.getCustomerId(),
                        createRequest
                );

        assertEquals(
                Boolean.FALSE,
                created.getActive()
        );

        CustomerDeviceRequest updateRequest =
                buildDeviceRequest(
                        created.getDeviceFingerprint()
                );

        updateRequest.setActive(null);

        CustomerDeviceResponse updated =
                customerDeviceService.updateDevice(
                        created.getDeviceId(),
                        updateRequest
                );

        assertEquals(
                Boolean.TRUE,
                updated.getActive()
        );
    }

    @Test
    void updateDeviceShouldThrowWhenDeviceDoesNotExist() {

        UUID missingDeviceId = UUID.randomUUID();

        CustomerDeviceRequest request =
                buildDeviceRequest(
                        uniqueFingerprint()
                );

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerDeviceService.updateDevice(
                        missingDeviceId,
                        request
                )
        );
    }

    private CustomerResponse createCustomer() {

        CustomerRequest request =
                new CustomerRequest();

        request.setCustomerNumber(
                "CUST-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 12)
        );

        request.setCustomerType("INDIVIDUAL");
        request.setFirstName("Integration");
        request.setLastName("Test");

        return customerService.createCustomer(request);
    }

    private CustomerDeviceRequest buildDeviceRequest(
            String fingerprint) {

        CustomerDeviceRequest request =
                new CustomerDeviceRequest();

        request.setDeviceFingerprint(fingerprint);
        request.setDeviceType("MOBILE");
        request.setOperatingSystem("Android");
        request.setBrowser("Chrome");
        request.setCountry("Guatemala");
        request.setCity("Guatemala City");
        request.setTrustLevel("TRUSTED");

        request.setLastSeen(
                LocalDateTime.of(
                        2026,
                        8,
                        29,
                        10,
                        0,
                        0
                )
        );

        request.setActive(Boolean.TRUE);

        return request;
    }

    private String uniqueFingerprint() {

        return "FP-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 20);
    }
}