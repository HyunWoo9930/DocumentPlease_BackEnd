package org.example.docuementplease.exceptionHandler;

public class DocumentSaveException extends RuntimeException {
    public DocumentSaveException(String message) {
        super(message);
    }
}
