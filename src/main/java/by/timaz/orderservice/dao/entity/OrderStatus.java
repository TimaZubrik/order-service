package by.timaz.orderservice.dao.entity;

public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED,
    RETURN_REQUESTED,
    RETURNED,
    FAILED
}