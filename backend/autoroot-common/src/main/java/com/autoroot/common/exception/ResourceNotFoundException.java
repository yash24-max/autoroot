package com.autoroot.common.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends AutoRootException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier), 
              "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceType, String identifier, Throwable cause) {
        super(String.format("%s not found with identifier: %s", resourceType, identifier), 
              "RESOURCE_NOT_FOUND", cause);
    }
}