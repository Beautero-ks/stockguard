package dev.bops.stockguard.catalog.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.bops.stockguard.catalog.application.dto.CreateProductCommand;
import dev.bops.stockguard.user.application.AuthService;
import dev.bops.stockguard.user.application.dto.AuthResponse;
import dev.bops.stockguard.user.application.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
@Transactional  // Annule les changements après chaque test
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthService authService;

    private String adminToken;
    private String commercialToken;

    @BeforeEach
    void setup() {
        // Création d'un admin
        AuthResponse adminAuth = authService.login(
                new LoginRequest("admin@stockguard.dev", "admin123"));
        adminToken = adminAuth.accessToken();

        // Création d'un commercial
        AuthResponse commercialAuth = authService.login(
                new LoginRequest("commercial@stockguard.dev", "commercial123"));
        commercialToken = commercialAuth.accessToken();
    }

    @Test
    void shouldCreateProduct() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-001", "Résistance 100Ω", "Composant électronique", "Tiroir A1", 10, 100
        );

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.reference").value("ELEC-001"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void shouldRejectDuplicateReference() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-001", "Résistance", null, "Tiroir A1", 10, 100
        );

        // Premier appel : OK
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());

        // Deuxième appel avec la même référence : doit échouer
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindProductById() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-002", "Condensateur", null, "Tiroir B2", 5, 50
        );

        // Création et récupération de l'ID
        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(get("/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("ELEC-002"));
    }

    @Test
    void shouldDeactivateProduct() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-003", "Transistor", null, "Tiroir C3", 20, 200
        );

        String response = mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(delete("/products/{id}/deactivate", id))
                .andExpect(status().isNoContent());

        // Vérifie que le produit est désactivé
        mockMvc.perform(get("/products/{id}/deactivate", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void shouldAllowAdminToCreateProduct() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-001", "Résistance", null, "Tiroir A1", 10, 100);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldRejectCommercialCreatingProduct() throws Exception {
        CreateProductCommand command = new CreateProductCommand(
                "ELEC-002", "Condensateur", null, "Tiroir B2", 5, 50);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + commercialToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isForbidden()); // ou 401 selon config
    }

    @Test
    void shouldAllowCommercialToReadProducts() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + commercialToken))
                .andExpect(status().isOk());

    }
}