package com.logopeda.document.dto;

/** Bytes plus metadata returned when downloading a document. */
public record DocumentContent(
        String fileName,
        String contentType,
        byte[] content) {
}
