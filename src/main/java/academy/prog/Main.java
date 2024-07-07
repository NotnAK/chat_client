package academy.prog;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter your login: ");
			String login = scanner.nextLine();

			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();


			System.out.println("To send a private message, use the format: @username message.");
			System.out.println("To send a message to everyone, simply type the message below");
			System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) break;
				Message m;
				if(text.startsWith("@")){
					String user = text.substring(1, text.indexOf(" "));
					text = text.substring(text.indexOf(" ") + 1);
					m = new Message(login, text, user);
				}
				else {
					m = new Message(login, text, null);
				}
				int res = m.send(Utils.getURL() + "/add");

				if (res != 200) { // 200 OK
					System.out.println("HTTP error occurred: " + res);
					return;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}
