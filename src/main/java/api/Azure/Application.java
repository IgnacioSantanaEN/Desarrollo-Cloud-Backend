package api.Azure;

import java.util.Scanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import api.Azure.service.Sender;

@SpringBootApplication 
public class Application {
    
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

    Sender sender = ctx.getBean(Sender.class);
    
    System.out.println("\n" + "=".repeat(60));
    System.out.println("RabbitMQ - Hello World con Spring Boot");
    System.out.println("=".repeat(60));
    System.out.println("[✓] Aplicación Iniciada correctamente");
    System.out.println("[✓] RabbitMQ conectado en localhost:5672");
    System.out.println("[✓] Cola 'Hello' creada correctamente\n");

    boolean running = true;

    try(Scanner scanner = new Scanner(System.in)) {
        while(running){
            System.out.println("\n--- Menú ---");
            System.out.println("1. Enviar mensaje");
            System.out.println("2. Enviar multiples mensajes");  
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción (1 - 3): ");

            String option = scanner.nextLine().trim();

            switch(option){
                case "1":
                    System.out.print("Ingrese el mensaje a enviar: ");
                    String message = scanner.nextLine().trim();
                    sender.sendMessage(message);
                    break;

                case "2":
                    System.out.println("¿Cuantós mensajes?");
                    try{
                        int count = Integer.parseInt(scanner.nextLine().trim());
                        for (int i = 1; i <= count; i++) {
                            sender.sendMessage("Mensaje " + i + " - Hello RabbitMQ!");
                            Thread.sleep(500); // Pausa de 500ms entre mensajes
                        }
                    } catch(NumberFormatException e){
                        System.out.println("Número inválido. Intente de nuevo.");
                    } catch(InterruptedException e){
                        Thread.currentThread().interrupt();
                    }
                    break;
                case "3":
                    running = false;
                    System.out.println("[✓]Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
                }
            }
        }
    }
}
