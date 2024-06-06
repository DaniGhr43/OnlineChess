package com.ilm.onlinechess.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class HttpClient {
    public static void main(String[] args) {
        joinGame();
    }

    public static void joinGame() {
        String serverAddress = "13.36.252.117"; // Dirección IP o nombre de host del servidor
        int serverPort = 100; // Puerto del servidor

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Conectado al servidor en " + serverAddress + ":" + serverPort);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream out = socket.getOutputStream();

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            // Leer y enviar el ID de la partida
            System.out.print(in.readLine()); // Leer mensaje del servidor
            String gameId = consoleInput.readLine();
            out.write((gameId + "\n").getBytes());
            out.flush();

            // Hilo para leer mensajes del servidor
            new Thread(() -> {
                String serverResponse;
                try {
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInput;
            while (!(userInput = consoleInput.readLine()).equalsIgnoreCase("EXIT")) {
                out.write((userInput + "\n").getBytes());
                out.flush();
            }

            in.close();
            out.close();
            consoleInput.close();
            socket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


