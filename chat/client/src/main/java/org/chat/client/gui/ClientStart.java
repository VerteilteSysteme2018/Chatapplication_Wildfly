package org.chat.client.gui;

public class ClientStart {

	public static void main (String args[])
	{
		ClientGUI clientGUI = new ClientGUI();
		Thread client = new Thread(clientGUI);
		client.run();
	}
}