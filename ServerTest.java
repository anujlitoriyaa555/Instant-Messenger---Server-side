import java.io.*;
import java.awt.*;
import java.net.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JFrame;

class Server extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server(){
		super("Anujs Instant messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
		     new ActionListener(){
				 public void actionPerformed(ActionEvent event){
					 sendMessage(event.getActionCommand());
					 userText.setText("");
				 }
			 }
		);
		
		add(userText , BorderLayout.NORTH);
		chatWindow  = new JTextArea();
		chatWindow.setEditable(false);
		add(new JScrollPane(chatWindow));
		setSize(500,400);
		setVisible(true);
	}
	//set up server
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);
			while(true){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}catch(EOFException eof){
					showMessage("\n server ended connection");
				}finally{
					closeCrap();
				}
			}
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
	}
	
	// wait for connection and display info
	
	private void waitForConnection()throws IOException{
		showMessage("waiting for someone to connect...\n");
		connection = server.accept();
		showMessage("connected to"+connection.getInetAddress().getHostName());
	}
	-
	// setup streams to send and recieve data
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n streams are now set up\n");
	}
	
	// while chatting
	
	private void whileChatting() throws IOException{
		String message = "you are connected";
		sendMessage(message);
		ableToType(true);
		do{
			try{
			message = (String) input.readObject();
			showMessage("\n"+message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n wtf user sent");
			}
		}while(!message.equals("CLIENT - END"));
		
	}
	
	//close streams and socket
	
	private void closeCrap(){
		showMessage("\n closing connection...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
	}
	
	//send a message
	
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
		}catch(IOException ioEx){
			chatWindow.append("\n ERROR: cant send..");
			
		}
	}
	
	// update chat window
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
		   new Runnable(){
			   public void run(){
				   chatWindow.append(text);
			   }
		   }
		);
	}
	
	// able to type
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
		  new Runnable(){
			  public void run(){
				  userText.setEditable(tof);
			  }
		  }
		);
	}
}
class ServerTest{
	public static void main(String args[]){
		
		Server anuj  =  new Server();
		anuj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		anuj.startRunning();
	}
}
	
	
	
	
	
	
	
