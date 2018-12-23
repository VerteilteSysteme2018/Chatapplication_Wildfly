package edu.hm.dako.chat.client_jms.gui;

public class ClientRunner {

	public static void main (String args[])
	{
		ClientGUI clientGUI = new ClientGUI();
		Thread client = new Thread(clientGUI);
		client.run();
	}
}