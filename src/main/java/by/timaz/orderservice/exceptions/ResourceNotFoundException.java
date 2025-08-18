package by.timaz.orderservice.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String param, String paramValue) {
        super(resource+" with "+param+" = "+paramValue+" not found");
    }

}
