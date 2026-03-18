package com.off3d.studio.infra.config;

public class ErrorMessages {
    // Mensagens do Módulo Sales (Pedidos e Clientes)
    public static final String CUSTOMER_NOT_FOUND = "Cliente não encontrado com o ID: ";
    public static final String ORDER_NOT_FOUND_ID = "Pedido não encontrado com id: ";
    public static final String ORDER_NOT_FOUND = "Pedido não encontrado";

    // Mensagens do Módulo Manufacturing (Impressoras e Modelos)
    public static final String PRINT_JOB_NOT_FOUND = "Trabalho de impressão não encontrado";
    public static final String PRINTER_NOT_FOUND = "Impressora não encontrada";
    public static final String MODEL_NOT_FOUND = "Modelo 3D não encontrado";

    // Construtor privado para evitar instanciação, já que é uma classe de constantes
    private ErrorMessages() {
        throw new IllegalStateException("Utility class");
    }
}