package com.off3d.studio.infra.config;

public class ErrorMessages {
    public static final String PRINTER_NOT_FOUND = "Impressora não encontrada com o ID: ";
    public static final String MATERIAL_NOT_FOUND = "Material não encontrado com o ID: ";
    public static final String MODEL_NOT_FOUND = "Modelo 3D não encontrado com o ID: ";
    public static final String PRINT_JOB_NOT_FOUND = "Trabalho de impressão não encontrado.";
    public static final String ORDER_NOT_FOUND_ID = "Pedido não encontrado com o ID: ";
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado com o ID: ";

    private ErrorMessages() {
        throw new IllegalStateException("Utility class");
    }
}