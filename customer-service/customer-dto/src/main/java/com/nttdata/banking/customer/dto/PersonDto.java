package com.nttdata.banking.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/**
 * DTO for Person data transfer.
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonDto {

    Long personId;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name;

    @NotBlank(message = "Identification is required")
    @Size(max = 20, message = "Identification must not exceed 20 characters")
    String identification;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    String address;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    String phone;
}
