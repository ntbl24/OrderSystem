package io.order.backend.shipping;

import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.shipping.application.port.out.ShipmentRepository;
import io.order.backend.shipping.application.usecase.ShipmentService;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentRequest;
import io.order.backend.shipping.infrastructure.web.dto.ShipmentResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {
    @Mock ShipmentRepository shipments;
    @InjectMocks ShipmentService service;

    @Test
    void createShipment_whenClientSuppliesIdAndStatus_shouldGenerateIdentityAndForceCreatedStatus() {
        ShipmentRequest request = request();
        request.setId("client-id");
        request.setStatus("delivered");
        when(shipments.save(any())).thenAnswer(invocation -> {
            Shipment shipment = invocation.getArgument(0);
            assertThat(shipment.getId()).isNull();
            assertThat(shipment.getStatus()).isEqualTo("created");
            shipment.setId("generated");
            return shipment;
        });

        ShipmentResponse response = ShipmentResponse.from(service.createShipment(request.toCommand()));

        assertThat(response.id()).isEqualTo("generated");
        assertThat(response.orderId()).isEqualTo("order");
        assertThat(response.userId()).isEqualTo("user");
        assertThat(response.status()).isEqualTo("created");
        assertThat(response.trackingNumber()).isEqualTo("TRACK");
        assertThat(response.destinationCity()).isEqualTo("Destination");
    }

    @ParameterizedTest
    @CsvSource({"standard,10.00", "express,14.00", "overnight,18.00"})
    void calculateShippingCost_whenServiceAndInsuranceSelected_shouldApplyExistingRatesWithoutSaving(
            String serviceType, String expected) {
        ShipmentRequest request = request();
        request.setServiceType(serviceType);
        request.setWeight(new BigDecimal("2"));
        request.setInsuranceRequired(true);
        request.setDeclaredValue(new BigDecimal("100"));

        Shipment calculated = service.calculateShippingCost(request.toCommand());

        assertThat(calculated.getShippingCost()).isEqualByComparingTo(expected);
        assertThat(calculated.getId()).isNull();
        verifyNoInteractions(shipments);
    }

    @Test
    void calculateShippingCost_whenWeightMissing_shouldKeepLegacyDefaultWeight() {
        ShipmentRequest request = request();
        request.setWeight(null);
        assertThat(service.calculateShippingCost(request.toCommand()).getShippingCost()).isEqualByComparingTo("6.50");
    }

    @Test
    void updateShipment_whenReplacementRequested_shouldKeepStoredIdentity() {
        Shipment stored = new Shipment();
        stored.setId("stored");
        when(shipments.findById("stored")).thenReturn(Optional.of(stored));
        when(shipments.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ShipmentRequest request = request();
        request.setId("untrusted");

        Shipment updated = service.updateShipment("stored", request.toCommand());

        assertThat(updated.getId()).isEqualTo("stored");
        assertThat(updated.getCarrier()).isEqualTo("carrier");
    }

    @Test
    void generateShippingLabel_whenShipmentExists_shouldPersistLabelAndStatus() {
        Shipment stored = new Shipment();
        stored.setId("shipment");
        stored.setTrackingNumber("TRACK");
        when(shipments.findById("shipment")).thenReturn(Optional.of(stored));
        when(shipments.save(stored)).thenReturn(stored);

        Shipment response = service.generateShippingLabel("shipment");

        assertThat(response.getLabelUrl()).isEqualTo("https://shipping-carrier.com/labels/TRACK");
        assertThat(response.getStatus()).isEqualTo("label_generated");
        verify(shipments).save(stored);
    }

    @Test
    void updateTrackingInfo_whenDelivered_shouldRecordTrackingAndDeliveryTime() {
        Shipment stored = new Shipment();
        LocalDateTime before = LocalDateTime.now();
        when(shipments.findById("shipment")).thenReturn(Optional.of(stored));
        when(shipments.save(stored)).thenReturn(stored);

        Shipment updated = service.updateTrackingInfo("shipment", "NEW", "delivered");

        assertThat(updated.getTrackingNumber()).isEqualTo("NEW");
        assertThat(updated.getStatus()).isEqualTo("delivered");
        assertThat(updated.getActualDeliveryDate()).isBetween(before, LocalDateTime.now());
    }

    @Test
    void updateShipmentStatus_whenMissing_shouldFailWithoutSaving() {
        when(shipments.findById("missing")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateShipmentStatus("missing", "delivered"))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(shipments, never()).save(any());
        assertThat(service.getShipmentById("missing")).isEmpty();
    }

    private ShipmentRequest request() {
        ShipmentRequest request = new ShipmentRequest();
        request.setOrderId("order");
        request.setUserId("user");
        request.setTrackingNumber("TRACK");
        request.setCarrier("carrier");
        request.setServiceType("standard");
        request.setWeight(BigDecimal.ONE);
        request.setWeightUnit("kg");
        request.setDestinationCity("Destination");
        return request;
    }
}
