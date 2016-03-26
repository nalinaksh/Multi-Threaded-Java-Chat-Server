
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ChatServer
{  public static void main(String[] args ) 
   {  
      ArrayList<ChatHandler> AllHandlers = new ArrayList<ChatHandler>();
		
      try 
      {  ServerSocket s = new ServerSocket(9118);
         
         for (;;)
         {  Socket incoming = s.accept( );
            new ChatHandler(incoming, AllHandlers).start();
         }   
      }
      catch (Exception e) 
      {  System.out.println(e);
      } 
   } 
}

class ChatHandler extends Thread
{  public ChatHandler(Socket i, ArrayList<ChatHandler> h) 
   { 
   		incoming = i;
		handlers = h;
		handlers.add(this);
		try{
			in = new ObjectInputStream(incoming.getInputStream());
			out = new ObjectOutputStream(incoming.getOutputStream());
		}catch(IOException ioe){
				System.out.println("Could not create streams.");
		}
   }


	public synchronized void sendCoordinates(){
	
		ChatHandler left = null;
		for(ChatHandler handler : handlers){

                        if(handler == this)
                        continue;

			ChatMessage2 cm = new ChatMessage2();
			cm.SetCoordinates(myObject.getX(), myObject.getY(), myObject.getPrevX(), myObject.getPrevY());
                        cm.SetDrawing(myObject.isDrawing());
			try{
				handler.out.writeObject(cm);
                        }
			catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
        }

	public synchronized void broadcast(){
	
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
			ChatMessage2 cm = new ChatMessage2();
			cm.setName(uname);
			cm.setMessage(myObject.getMessage());
			try{
				handler.out.writeObject(cm);
                                if ( cm.getMessage().equals("bye")!= true )
                                {
                                  if(handler.appending)
                                  {
				    handler.aout.writeObject(cm);
                                  }
                                  else
                                  {
                                    handler.fout.writeObject(cm);
                                  }
                                }
                                
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
		
		if(myObject.getMessage().equals("bye")){ // my client wants to leave
			done = true;	
			handlers.remove(this);
			System.out.println("Removed handler. Number of handlers: " + handlers.size());
		}
		System.out.println("Number of handlers: " + handlers.size());
   }

	public void adduser() throws IOException, ClassNotFoundException, EOFException, StreamCorruptedException{
           try {
		myObject = (ChatMessage2)in.readObject();
		uname = new String(myObject.getName());
		System.out.println("Newuser: " + uname);


                File f = new File(uname + ".txt");
                if(f.exists()&&  !f.isDirectory())
                {
                   aout = new AppendingObjectOutputStream(new FileOutputStream(uname + ".txt", true));
                   appending = true;
                   System.out.println("Created AppendingObjectOutputStream");
                }
                else
                {
		    fout = new ObjectOutputStream(new FileOutputStream( uname + ".txt"));
                   System.out.println("Created ObjectOutputStream");
		}

                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                String reportDate = df.format(today);
                
                ChatMessage2 cm = new ChatMessage2();
                cm.setMessage(reportDate);
               
                if(this.appending)
                {
                    aout.writeObject(cm);
                }
                else
                {
                   fout.writeObject(cm);
                }
		
                fin = new ObjectInputStream(new FileInputStream( uname + ".txt"));
                
                ChatMessage2 mc;
 
		while(true)
                {
                    mc = (ChatMessage2)fin.readObject();
		    this.out.writeObject(mc);
                    System.out.println("writing history message back to client\n");
		}
             }
             catch(EOFException eofe)
             {
                 System.out.println("EOF Exception occurred...");
                 fin.close();
             }
             catch(StreamCorruptedException sce)
             {
                System.out.println("Stream corrupted exception");
             }
   }

   public synchronized void announce() {
   		
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
		       if(handler == this)
		       	  continue;
			ChatMessage2 cm = new ChatMessage2();
			cm.setName(uname);
			cm.setMessage("newuser");
			try{
				handler.out.writeObject(cm);
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
   }

   public synchronized void senduserlist() {
   
		ChatHandler left = null;
		for(ChatHandler handler : handlers){
		       if(handler == this)
		       	  continue;
			ChatMessage2 cm = new ChatMessage2();
			cm.setName(handler.uname);
			cm.setMessage("olduser");
			try{
				this.out.writeObject(cm);
				System.out.println("Writing to handler outputstream: " + cm.getMessage());
			}catch(IOException ioe){
				//one of the other handlers hung up
				left = handler; // remove that handler from the arraylist
			}
		}
		handlers.remove(left);
   }

   public void run()
   {  
		try{ 
		        adduser();
			announce();
			senduserlist();

			while(!done){
				myObject = (ChatMessage2)in.readObject();
				if(myObject.isDrawing() == false)
                                {
                                    System.out.println("Message read: " + myObject.getMessage());
				    broadcast();
                                }
                                else
                                {
                                    sendCoordinates();
                                }
			}			    
		} catch (IOException e){  
			if(e.getMessage().equals("Connection reset")){
				System.out.println("A client terminated its connection.");
			}else{
				System.out.println("Problem receiving: " + e.getMessage());
			}
		}catch(ClassNotFoundException cnfe){
			System.out.println(cnfe.getMessage());
		}
		finally{
			handlers.remove(this);
		}
   }
   
   ChatMessage2 myObject = null;
   private Socket incoming;

   boolean done = false;
   ArrayList<ChatHandler> handlers;

   ObjectOutputStream out;
   ObjectInputStream in;

   String uname;   

   ObjectInputStream fin;
   ObjectOutputStream fout;
   AppendingObjectOutputStream aout;
   boolean appending = false;

   Integer count;
}

class AppendingObjectOutputStream extends ObjectOutputStream {

  public AppendingObjectOutputStream(OutputStream out) throws IOException{
    super(out);
  }

  @Override
  protected void writeStreamHeader() throws IOException {
    // do not write a header, but reset:
    reset();
  }

}

