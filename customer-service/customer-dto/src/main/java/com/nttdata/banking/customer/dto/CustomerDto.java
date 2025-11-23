package com.nttdata.banking.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for Customer data transfer.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {

    Long customerId;

    @Valid
    @NotNull(message = "Person data is required")
    PersonDto person;

    @NotBlank(message = "Password is required")
    String password;

    Boolean status;
}
