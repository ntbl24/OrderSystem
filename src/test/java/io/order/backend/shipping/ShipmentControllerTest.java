package io.order.backend.shipping;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.application.port.in.CreateShipmentUseCase;
import io.order.backend.shipping.application.port.in.ShipmentCommandUseCase;
import io.order.backend.shipping.application.port.in.ShipmentQueryUseCase;
import io.order.backend.shipping.application.port.in.UpdateShipmentUseCase;
import io.order.backend.shipping.domain.Shipment;
import io.order.backend.shipping.domain.ShipmentId;
import io.order.backend.shipping.domain.TrackingNumber;
import io.order.backend.shipping.infrastructure.web.controller.ShipmentController;
import io.order.backend.user.domain.UserId;

@ExtendWith(MockitoExtension.class)
class ShipmentControllerTest {
    @Mock
    ShipmentCommandUseCase commands;
    @Mock
    CreateShipmentUseCase creates;
    @Mock
    ShipmentQueryUseCase queries;
    @Mock
    UpdateShipmentUseCase updates;

    MockMvc mvc;
    Shipment shipment;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(
                new ShipmentController(commands, creates, queries, updates)).build();
        shipment = Shipment.create(OrderId.of("order"), UserId.of("00000000-0000-0000-0000-000000000001"),
                TrackingNumber.of("TRACK"),
                "carrier", "standard", BigDecimal.ONE, "kg", new BigDecimal("6.50"), "created",
                null, null, "Origin address", "Origin", "State", "Country", "00000",
                "Destination address", "Destination", "State", "Country", "11111",
                null, null, null, false, false, null, null, null, null);
    }

    @Test
    void createShipment_whenRequestValid_returnsShipmentAndMapsCommand() throws Exception {
        when(creates.createShipment(any())).thenReturn(shipment);

        mvc.perform(post("/api/v1/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(shipment.getId().toString()))
                .andExpect(jsonPath("$.data.orderId").value(OrderId.of("order").toString()))
                .andExpect(jsonPath("$.data.destinationCity").value("Destination"));

        verify(creates).createShipment(argThat(command ->
                command.orderId().equals(OrderId.of("order"))
                        && command.userId().equals(UserId.of("00000000-0000-0000-0000-000000000001"))
                        && command.carrier().equals("carrier")));
    }

    @Test
    void getShipmentById_whenFound_mapsResponseAndTypedIdentifier() throws Exception {
        when(queries.getShipmentById(shipment.getId())).thenReturn(Optional.of(shipment));

        mvc.perform(get("/api/v1/shipments/{id}", shipment.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(shipment.getId().toString()))
                .andExpect(jsonPath("$.data.trackingNumber").value("TRACK"));

        verify(queries).getShipmentById(shipment.getId());
    }

    @Test
    void getShipmentById_whenMissing_returnsNotFound() throws Exception {
        when(queries.getShipmentById(ShipmentId.of("missing"))).thenReturn(Optional.empty());

        mvc.perform(get("/api/v1/shipments/missing"))
                .andExpect(status().isNotFound());
    }

    private String validRequest() {
        return """
                {
                  "orderId":"order",
                  "userId":"00000000-0000-0000-0000-000000000001",
                  "trackingNumber":"TRACK",
                  "carrier":"carrier",
                  "serviceType":"standard",
                  "weight":1,
                  "weightUnit":"kg",
                  "originAddress":"Origin address",
                  "originCity":"Origin",
                  "originState":"State",
                  "originCountry":"Country",
                  "originPostalCode":"00000",
                  "destinationAddress":"Destination address",
                  "destinationCity":"Destination",
                  "destinationState":"State",
                  "destinationCountry":"Country",
                  "destinationPostalCode":"11111"
                }
                """;
    }
}
