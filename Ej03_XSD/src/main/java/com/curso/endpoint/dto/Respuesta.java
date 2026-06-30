package com.curso.endpoint.dto;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("respuesta")
public class Respuesta<T> {

    private boolean success;
    private String message;
    private T data;
    private Long timestamp;

    private Respuesta(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public static <T> Respuesta<T> success(T data, String message) {
        return new Respuesta<>(true, message, data);
    }

    public static <T> Respuesta<T> success(T data) {
        return new Respuesta<>(true, "Operación realizada con éxito", data);
    }

    public static <T> Respuesta<T> success(String message) {
        return new Respuesta<>(true, message, null);
    }

    public static <T> Respuesta<T> error(String message) {
        return new Respuesta<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}