package com.off3d.studio.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.off3d.studio.auth.domain.User;
import com.off3d.studio.auth.dto.UserRequestDTO;
import com.off3d.studio.manufacturing.domain.Material;
import com.off3d.studio.manufacturing.domain.Model3D;
import com.off3d.studio.manufacturing.domain.Printer;
import com.off3d.studio.manufacturing.dto.PrintJobRequestDTO;
import com.off3d.studio.sales.domain.Customer;
import com.off3d.studio.sales.domain.Order;
import com.off3d.studio.sales.domain.OrderStatus;
import com.off3d.studio.sales.dto.CustomerRequestDTO;
import com.off3d.studio.sales.dto.OrderRequestDTO;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JsonHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private record OrderData(BigDecimal totalPrice, String status) {}

    public static String getOrderStatusFromJson() {
        Map<String, String> data = loadJsonAsObject("json/order-status.json", Map.class);
        return data.get("status");
    }

    public static Customer getCustomerAsEntity() {
        return loadJsonAsObject("json/customer.json", Customer.class);
    }

    public static Printer getPrinterAsEntity() {
        return loadJsonAsObject("json/printer.json", Printer.class);
    }

    public static Material getMaterialAsEntity() {
        return loadJsonAsObject("json/material.json", Material.class);
    }

    public static Model3D getModel3DAsEntity() {
        return loadJsonAsObject("json/model3d.json", Model3D.class);
    }

    public static PrintJobRequestDTO getPrintJobRequest() {
        return loadJsonAsObject("json/printjob-request.json", PrintJobRequestDTO.class);
    }

    public static CustomerRequestDTO getCustomerRequest() {
        return loadJsonAsObject("json/customer-request.json", CustomerRequestDTO.class);
    }

    public static List<Order> getOrdersAsList() {
        return loadJsonAsList("json/orders.json", Order.class);
    }

    public static OrderRequestDTO getOrderRequest() {
        return loadJsonAsObject("json/order-request.json", OrderRequestDTO.class);
    }

    public static Order getOrderAsEntity() {
        OrderData data = loadJsonAsObject("json/order-entity.json", OrderData.class);

        Order order = new Order();
        order.setTotalPrice(data.totalPrice());
        order.setStatus(OrderStatus.valueOf(data.status()));
        order.setOrderDate(java.time.LocalDateTime.now());
        return order;
    }

    public static UUID getOrderIdToDelete() {
        Map<String, String> data = loadJsonAsObject("json/order-delete.json", Map.class);
        return UUID.fromString(data.get("id"));
    }

    public static User getUserAsEntity() {
        UserRequestDTO dto = loadJsonAsObject("json/user-request.json", UserRequestDTO.class);

        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setRole(dto.role());
        return user;
    }

    private static <T> T loadJsonAsObject(String filePath, Class<T> clazz) {
        try {
            var resource = new ClassPathResource(filePath);
            return objectMapper.readValue(resource.getInputStream(), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo JSON: " + filePath, e);
        }
    }

    private static <T> List<T> loadJsonAsList(String filePath, Class<T> clazz) {
        try {
            var resource = new ClassPathResource(filePath);
            return objectMapper.readValue(resource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo JSON: " + filePath, e);
        }
    }
}