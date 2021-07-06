package com.backbase.dbs.positivepay.service;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.dbs.positivepay.configuration.PositivePayConfiguration;
import com.backbase.dbs.positivepay.service.model.ArrangementDetails;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtendedSubscriptionValidatorTest {
    private static final String ACCOUNT_NUMBER = "1234509876";

    @Mock
    private PositivePayConfiguration configuration;

    @InjectMocks
    private ExtendedSubscriptionValidator validator;

    @Test
    void filterSubscription() {
        when(configuration.getSubscriptions()).thenReturn(
            Map.of("single-account", "single-account-subscription",
                "achPositivePay", "ach-positive-pay"));

        assertDoesNotThrow(() -> validator.filterSubscription(ArrangementDetails.builder()
            .id("1234")
            .bban(ACCOUNT_NUMBER)
            .identifiers(singletonList("single-account-subscription")).build()));
    }

    @Test
    void filterSubscription_error() {
        when(configuration.getSubscriptions()).thenReturn(
            Map.of("single-account", "single-account-subscription",
                "achPositivePay", "ach-positive-pay"));

        assertThrows(BadRequestException.class, () -> validator.filterSubscription(ArrangementDetails.builder()
            .id("1234")
            .bban("11111")
            .identifiers(singletonList("single-account-subscription")).build()));
    }

}