package com.devsu.accountservice.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Integration tests for the account, movement, and report HTTP workflow. */
@AutoConfigureMockMvc
@SpringBootTest(
    properties = {
      "spring.datasource.url=jdbc:h2:mem:account_workflow_integration_test;"
          + "MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.rabbitmq.listener.simple.auto-startup=false"
    })
class AccountWorkflowIntegrationTest {

  private final MockMvc mockMvc;

  @Autowired
  AccountWorkflowIntegrationTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void accountMovementAndReportFlowWorksThroughHttpApi() throws Exception {
    createAccount();
    createWithdrawal();
    createDeposit();
    assertCurrentBalance();
    assertInsufficientFunds();
    assertReport();
  }

  private void createAccount() throws Exception {
    mockMvc
        .perform(
            post("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "numeroCuenta": "478758",
                      "tipoCuenta": "Ahorro",
                      "saldoInicial": 2000.00,
                      "estado": true,
                      "clienteId": "CLI-E2E-001"
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.saldoDisponible").value(2000.00));
  }

  private void createWithdrawal() throws Exception {
    mockMvc
        .perform(
            post("/api/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "numeroCuenta": "478758",
                      "fecha": "2026-04-10T09:00:00",
                      "valor": -575.00
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.tipoMovimiento").value("Retiro"))
        .andExpect(jsonPath("$.data.saldo").value(1425.00));
  }

  private void createDeposit() throws Exception {
    mockMvc
        .perform(
            post("/api/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "numeroCuenta": "478758",
                      "fecha": "2026-04-11T09:00:00",
                      "valor": 100.00
                    }
                    """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.tipoMovimiento").value("Deposito"))
        .andExpect(jsonPath("$.data.saldo").value(1525.00));
  }

  private void assertCurrentBalance() throws Exception {
    mockMvc
        .perform(get("/api/cuentas/478758"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.saldoDisponible").value(1525.00));
  }

  private void assertInsufficientFunds() throws Exception {
    mockMvc
        .perform(
            post("/api/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "numeroCuenta": "478758",
                      "fecha": "2026-04-12T09:00:00",
                      "valor": -9999.00
                    }
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value("Saldo no disponible"));
  }

  private void assertReport() throws Exception {
    mockMvc
        .perform(
            get("/api/reportes")
                .param("fecha", "2026-04-01,2026-04-30")
                .param("cliente", "CLI-E2E-001"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].numeroCuenta").value("478758"))
        .andExpect(jsonPath("$.data[0].movimiento").value(-575.00))
        .andExpect(jsonPath("$.data[0].saldoDisponible").value(1425.00))
        .andExpect(jsonPath("$.data[1].movimiento").value(100.00))
        .andExpect(jsonPath("$.data[1].saldoDisponible").value(1525.00));
  }
}
