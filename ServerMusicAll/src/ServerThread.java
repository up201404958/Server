import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class represents the thread that deals with the client request
 * In the run method of the Thread client requests are interpreted using a custom communication protocol 
 */
public class ServerThread extends Thread {
	
	/**
	 * Socket attributed to the thread
	 */
	protected Socket ServerClient;
	/**
	 * int Id number
	 */
	protected int clientNo;
	protected Connection myCon;
	/**
	 * Stream used for server output
	 */
	protected ObjectOutputStream out;
	/**
	 * Stream used for messages received
	 */
	protected ObjectInputStream in;
	/**
	 * String used for response 
	 */
	protected String message;
	/**
	 * array used for file transfer
	 */
	protected byte data[];
	
	public ServerThread(Socket skt,int counter) {
		this.ServerClient=skt;
		this.clientNo=counter;
	}
	public ServerThread() {}
	public void run(){
  
		UserHandling user = new UserHandling();
        DatabaseHandling data = new DatabaseHandling();
        PlaylistHandling plst = new PlaylistHandling();
		
		System.out.println("Connection received from " + ServerClient.getInetAddress().getHostName() + "number" + this.clientNo);
    
            try {
				out = new ObjectOutputStream(ServerClient.getOutputStream());
				out.flush();
				in = new ObjectInputStream(ServerClient.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
         
            int check=0;
 
            try{
                message = (String)in.readObject();
                System.out.println("client sent>" + message);
                String[] parts = message.split(" ");
                System.out.println(parts[0]+ parts[1]);
   
                if (parts[0].equals("LOGN")) {
              
                		check=user.checkLogin(parts);
                		 if(check==0)
                     	sendMessage("INVALID LOGIN");
                     else
                     	sendMessage("VALID LOGIN");
                		
                }else if(parts[0].equals("REGS")) {
                		check=user.checkRegister(parts);
                		if(check==0)
                         sendMessage("USERNAME ALREADY EXISTS");
                     else
                         sendMessage("REGISTED");
                }else if(parts[0].equals("CHNGPASS")) {
                		user.changePass(parts);
                	}else if(parts[0].equals("ARTS")) { 
                		ArrayList <String> artists = data.getArtists();
                		sendMessage(artists);
                	}else if(parts[0].equals("ALBS")){
                		ArrayList <String> albums = data.getAlbums();
                		sendMessage(albums);
                	}else if(parts[0].equals("SNGS")) {
                		ArrayList <String> songs = data.getSongs();
                		sendMessage(songs);
                	}else if(parts[0].equals("SNGPLST")) {         		
                		ArrayList <String> playlists=plst.getPlaylist(parts);
                		sendMessage(playlists);
                	}else if(parts[0].equals("CREATE")) {
                		plst.createPlaylist(parts);
                		sendMessage("NEW PLAYLIST");
                }else if(parts[0].equals("PLAYLST")) {
                		ArrayList <String> playlists=plst.getPlaylist(parts);
            			sendMessage(playlists);
                }else if(parts[0].equals("ADDSNG")) {
                		plst.insertSongPlaylist(parts);
                		sendMessage("ADDED SONG");
                }else if(parts[0].equals("GOTOTA")) { 
                		ArrayList <String> album=data.getAlbumSongs(parts);
                		sendMessage(album);
                }else if(parts[0].equals("PLSTSNG")) {
                		ArrayList <String> songs=plst.getPlaylistSongs(parts);
                		sendMessage(songs);
                }else if(parts[0].equals("KPLSTSNG")) {
            			ArrayList <String> songs=plst.orderPlaylistKey(parts);
            			sendMessage(songs);
                }else if(parts[0].equals("BPLSTSNG")) {
            			ArrayList <String> songs=plst.orderPlaylistBPM(parts);
            			sendMessage(songs);
                }else if(parts[0].equals("DWLD")) {
                		sendMusic(parts);
                }else if(parts[0].equals("MYSNGS")){
                		ArrayList <String> songs=user.getUserSongs(parts);
                		sendMessage(songs);
                	}
               
            }
            catch(ClassNotFoundException classnot){
                System.err.println("Data received in unknown format");
            }catch (IOException e) {
				e.printStackTrace();
			}catch (SQLException e) {
				e.printStackTrace();
			}
        }
	

	/**
	 * This method send a specific .wav file requested by the client
	 * @param parts music id
	 * @throws IOException
	 */
	private void sendMusic(String[] parts) throws IOException {
		
		String path = "music/music_" + parts[1]+".wav";
		File file = new File(path);
		data = new byte[2048];
		FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream fileBuffer = new BufferedInputStream(fileStream);
        int count;
        while ((count = fileBuffer.read(data)) > 0) {
            System.out.println("server>" + count);
            out.write(data, 0, count);
            out.flush();
         }
         out.close();
         fileBuffer.close();
         fileStream.close();
	}

    /**
     * This method sends a message to the client reporting success or failure
     * @param msg message to be sent to client
     */
    private void sendMessage(String msg){
        
    		try{
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }
  
	/**
	 * This method sends an ArrayList to the client containing requested information
	 * @param array information to be sent
	 */
	private void sendMessage(ArrayList<String> array) {
		
		try{
            out.writeObject(array);
            out.flush();
            System.out.println("server>" + array);
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
		
	}
}
