package com.metapro.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    private List<OrderItemDto> items;
    private CustomerDto customer;
    private BuyNowDto buyNowProduct;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String id;
        private ProductDto product;
        private BigDecimal price;
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private String id;
        private String name;
        private BigDecimal price;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyNowDto {
        private ProductDto product;
        private Integer quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDto {
        private String fullName;
        private String phone;
        private String email;
        private String streetAddress;
        private String city;
        private String state;
        private String pincode;
        private String companyName;
        private String gstin;
    }
}
