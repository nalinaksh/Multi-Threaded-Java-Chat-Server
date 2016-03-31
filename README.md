# Java-Chat-Server
Java implementation of a multithreaded chat server with shared whiteboard

File Description:

ChatMessage.java : Specifies the format of chat messages exchanged between Client <-> Server
ChatMessage2.java: Extends ChatMessage class, describes functionality related to shared whiteboard
ChatServer.java  : Implements multi-threaded chat server. Communication between various clients happen via the chat server
Client.java      : Implements the client and the GUI with connet/disconnect buttons, List with active clients, Text Area, Message Area and shared whiteboard
SimplePaint.java : Implements shared whiteboard related functionality

Usage:
- Compile the code: javac *.java
- Start Chat Server from the command line: java ChatServer
- Modify Client.java: In line #84, specify the hostname/IP address of the machine on which  ChatServer is running
- Compile Client.java: javac Client.java 
- Start a client from the command line on different machine: java Client 
- Enter the username in the top left corner textfield of the client chat window and press connect buttion
- Start 2nd/3rd or more Clients, using the above twp steps.
- Exchange messages by typing into the bottom textfield and pressing enter button of keyboard.
- For shared whiteboard, press and drag the mouse in the white area below the text area.  
