package br.com.l2tec.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ServerEndPoint {

	private static Queue<Session> queue = new ConcurrentLinkedQueue<Session>();

	@OnOpen
	public void handleOpen(Session session) {
		System.out.println("Conectado => " + session.getId());
		queue.add(session);
	}

	@OnMessage
	public void handleMessage(String message, Session sender) throws IOException {
		ArrayList<Session> closedSessions = new ArrayList<>();
		String msg = "From " + sender.getId() + ": " + message;
		for (Session session : queue) {
			if (!session.isOpen()){
				System.err.println("Closed session: " + session.getId());
				closedSessions.add(session);
			}else{
				session.getBasicRemote().sendText(msg);
			}
		}
		queue.removeAll(closedSessions);

		//System.out.println("received msg " + message + " from " + session.getId());
		
		//System.out.println(msg);
		//return "";
	}

	@OnClose
	public void habdleClose(Session session) {
		queue.remove(session);
		System.out.println("Desconectado");
	}

	@OnError
	public void habdleError(Throwable t, Session session) {
		queue.remove(session);
		t.printStackTrace();
	}
}
