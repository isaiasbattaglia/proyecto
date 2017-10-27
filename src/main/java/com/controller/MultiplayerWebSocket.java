package trivia;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import org.javalite.activejdbc.Base;

@WebSocket
public class MultiplayerWebSocket {

    private static Map<Session,User> usersOnline = new HashMap<Session,User>();  

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        App.userUsernameMap.put(user, new User());
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
      App.userUsernameMap.remove(user);
      App.updateOnlineUsers();
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
        JSONObject obj = new JSONObject(message);
        String description = new String(obj.getString("message"));
        if(description.equals("connect")){
            User u = UserService.getUser(obj.getInt("id"));
            App.userUsernameMap.put(user,u);
            Base.close();
            App.updateOnlineUsers();
        }
    }
}
