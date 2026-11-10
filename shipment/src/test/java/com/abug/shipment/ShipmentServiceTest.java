package com.abug.shipment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.shipment.application.port.out.ShipmentRepository;
import com.abug.shipment.application.usecase.CreateShipmentService;
import com.abug.shipment.application.usecase.ShipmentCommandService;
import com.abug.shipment.application.usecase.ShipmentQueryService;
import com.abug.shipment.application.usecase.UpdateShipmentService;
import com.abug.shipment.domain.Shipment;
import com.abug.shipment.domain.ShipmentId;
import com.abug.shipment.infrastructure.web.dto.ShipmentRequest;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {
    @Mock
    ShipmentRepository shipments;

    @InjectMocks
    ShipmentCommandService commands;

    CreateShipmentService creates;
    UpdateShipmentService updates;
    ShipmentQueryService queries;

    @BeforeEach
    void setUp() {
        creates = new CreateShipmentService(shipments);
        updates = new UpdateShipmentService(shipments);
        queries = new ShipmentQueryService(shipments);
    }

    @Test
    void createShipment_whenRequestHasClientIdAndStatus_generatesIdAndForcesCreatedStatus() {
        ShipmentRequest request = request();
        request.setId("client-id");
        request.setStatus("delivered");
        when(shipments.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Shipment created = creates.createShipment(request.toCommand());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo("created");
        assertThat(created.getOrderId().toString()).isEqualTo(
                com.abug.order.domain.OrderId.of("order").toString());
        assertThat(created.getUserId().toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
        assertThat(created.getTrackingNumber().toString()).isEqualTo("TRACK");
        assertThat(created.getDestinationCity()).isEqualTo("Destination");
        verify(shipments).save(created);
    }

    @Test
    void calculateShippingCost_whenInsuranceSelected_appliesServiceRateWithoutSaving() {
        ShipmentRequest request = request();
        request.setServiceType("express");
        request.setWeight(new BigDecimal("2"));
        request.setInsuranceRequired(true);
        request.setDeclaredValue(new BigDecimal("100"));

        Shipment calculated = commands.calculateShippingCost(request.toCommand());

        assertThat(calculated.getShippingCost()).isEqualByComparingTo("14.00");
        assertThat(calculated.getId()).isNotNull();
        verify(shipments, never()).save(any());
    }

    @Test
    void calculateShippingCost_whenWeightMissing_usesLegacyDefaultWeight() {
        ShipmentRequest request = request();
        request.setWeight(null);

        assertThat(commands.calculateShippingCost(request.toCommand()).getShippingCost())
                .isEqualByComparingTo("6.50");
    }

    @Test
    void updateShipment_whenRequestHasDifferentId_preservesStoredIdentity() {
        Shipment stored = savedShipment();
        when(shipments.findById(stored.getId())).thenReturn(Optional.of(stored));
        ShipmentRequest request = request();
        request.setId("untrusted");
        request.setCarrier("updated carrier");

        Shipment updated = updates.updateShipment(stored.getId(), request.toCommand());

        assertThat(updated.getId()).isEqualTo(stored.getId());
        assertThat(updated.getCarrier()).isEqualTo("updated carrier");
        verify(shipments).save(stored);
    }

    @Test
    void generateShippingLabel_whenShipmentExists_persistsLabelAndStatus() {
        Shipment stored = savedShipment();
        when(shipments.findById(stored.getId())).thenReturn(Optional.of(stored));

        Shipment response = commands.generateShippingLabel(stored.getId());

        assertThat(response.getLabelUrl()).isEqualTo("https://shipping-carrier.com/labels/TRACK");
        assertThat(response.getStatus()).isEqualTo("label_generated");
        verify(shipments).save(stored);
    }

    @Test
    void updateTrackingInfo_whenDelivered_recordsTrackingAndDeliveryTime() {
        Shipment stored = savedShipment();
        when(shipments.findById(stored.getId())).thenReturn(Optional.of(stored));

        Shipment updated = updates.updateTrackingInfo(stored.getId(), "NEW", "delivered");

        assertThat(updated.getTrackingNumber().toString()).isEqualTo("NEW");
        assertThat(updated.getStatus()).isEqualTo("delivered");
        assertThat(updated.getActualDeliveryDate()).isNotNull();
        verify(shipments).save(stored);
    }

    @Test
    void updateShipmentStatus_whenMissing_throwsApplicationErrorWithoutSaving() {
        ShipmentId id = ShipmentId.of("missing");
        when(shipments.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updates.updateShipmentStatus(id, "delivered"))
                .isInstanceOfSatisfying(AppException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRODUCT_NOT_FOUND));
        assertThat(queries.getShipmentById(id)).isEmpty();
        verify(shipments, never()).save(any());
    }

    private ShipmentRequest request() {
        ShipmentRequest request = new ShipmentRequest();
        request.setOrderId("order");
        request.setUserId("00000000-0000-0000-0000-000000000001");
        request.setTrackingNumber("TRACK");
        request.setCarrier("carrier");
        request.setServiceType("standard");
        request.setWeight(BigDecimal.ONE);
        request.setWeightUnit("kg");
        request.setOriginAddress("Origin address");
        request.setOriginCity("Origin");
        request.setOriginState("State");
        request.setOriginCountry("Country");
        request.setOriginPostalCode("00000");
        request.setDestinationAddress("Destination address");
        request.setDestinationCity("Destination");
        request.setDestinationState("State");
        request.setDestinationCountry("Country");
        request.setDestinationPostalCode("11111");
        return request;
    }

    private Shipment savedShipment() {
        when(shipments.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Shipment shipment = creates.createShipment(request().toCommand());
        clearInvocations(shipments);
        return shipment;
    }
}
