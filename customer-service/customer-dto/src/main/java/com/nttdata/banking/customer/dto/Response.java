package com.nttdata.banking.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

/**
 * Generic response wrapper for API responses.
 *
 * @param <T> the type of data in the response
 */
@Value
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    int statusCode;
    String status;
    String message;
    T data;
    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    public static <T> Response<T> ok(T data) {
        return Response.<T>builder()
                .statusCode(200)
                .status("Ok")
                .message("Operation successful")
                .data(data)
                .build();
    }

    public static <T> Response<T> ok(T data, String message) {
        return Response.<T>builder()
                .statusCode(200)
                .status("Ok")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> created(T data) {
        return Response.<T>builder()
                .statusCode(201)
                .status("Created")
                .message("Resource created successfully")
                .data(data)
                .build();
    }

    public static <T> Response<T> created(T data, String message) {
        return Response.<T>builder()
                .statusCode(201)
                .status("Created")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> updated(T data) {
        return Response.<T>builder()
                .statusCode(200)
                .status("Updated")
                .message("Resource updated successfully")
                .data(data)
                .build();
    }

    public static <T> Response<T> updated(T data, String message) {
        return Response.<T>builder()
                .statusCode(200)
                .status("Updated")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> Response<T> deleted() {
        return Response.<T>builder()
                .statusCode(200)
                .status("Deleted")
                .message("Resource deleted successfully")
                .build();
    }

    public static <T> Response<T> deleted(String message) {
        return Response.<T>builder()
                .statusCode(200)
                .status("Deleted")
                .message(message)
                .build();
    }

    public static <T> Response<T> notFound(String message) {
        return Response.<T>builder()
                .statusCode(404)
                .status("Not found")
                .message(message)
                .build();
    }

    public static <T> Response<T> badRequest(String message) {
        return Response.<T>builder()
                .statusCode(400)
                .status("Bad request")
                .message(message)
                .build();
    }

    public static <T> Response<T> conflict(String message) {
        return Response.<T>builder()
                .statusCode(409)
                .status("Conflict")
                .message(message)
                .build();
    }

    public static <T> Response<T> internalServerError(String message) {
        return Response.<T>builder()
                .statusCode(500)
                .status("Internal server error")
                .message(message)
                .build();
    }
}
