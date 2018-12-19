package org.chat.client.gui;

public class Test {

    public static void main(String args[]) {


        String message = "+++";

        for (int j = 0; j < 10; j++) {
            message = message + String.valueOf(j);


            for (int i = 0; i < 10; i++) {
                String name = "Client-Thread-" + String.valueOf(i);
                TestClient client = new TestClient(name, "localhost", "8080");

                client.loginUser();
                client.getMessages();
                client.sendMessage(message);

            }
        }
    }
}