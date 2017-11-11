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
      User currentUser = App.userUsernameMap.get(user);
      App.usersInDuelLobby.remove(user);
      App.leaveTheGame(currentUser);
      App.updateOnlineUsers("updateOnlineUsers");
      App.userUsernameMap.remove(user);
      App.updateOnlineUsers("updateUsersForDuel");
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
            App.updateOnlineUsers("updateOnlineUsers");
        }
        if (description.equals("newGame")) {
            GameService.createGame(obj.getInt("user1ID"),obj.getInt("user2ID"));
            Base.close();
        }
        if(description.equals("GameRequest")){
            User user1 = User.findById(obj.getInt("id"));
            User user2 = User.findById(obj.getInt("rivalID"));
            Session u1 = App.getKeyByValue(App.usersInDuelLobby,user1);
            Session u2 = App.getKeyByValue(App.usersInDuelLobby,user2);
            App.usersInDuelLobby.remove(u1);
            App.usersInDuelLobby.remove(u2);
            Base.close();
            App.sendGameReq(obj.getInt("id"),obj.getInt("rivalID"));
            App.updateOnlineUsers("updateUsersForDuel");
        }
        if(description.equals("ReqAccepted")){
            Game game = GameService.createDuelGame(obj.getInt("requesterID"),obj.getInt("requestedID"));
            Base.close();
            App.updateOnlineUsers("updateUsersForDuel");
            App.sendQuestion(game.getInteger("id"));
        }
        if(description.equals("answered")){
            GameService.setAnswerForUser(obj.getInt("gameID"), obj.getInt("id"),obj.getString("answer"));
            Base.close();
            App.sendResults(obj);
        }
        if(description.equals("newQuestion")){
            Base.close();
            App.sendQuestion(obj.getInt("gameID"));
        }
        if(description.equals("connectInDuelLobby")){
            User u = UserService.getUser(obj.getInt("id"));
            App.usersInDuelLobby.put(user,u);
            App.userUsernameMap.put(user,u);
            Base.close();
            App.updateOnlineUsers("updateUsersForDuel");
            App.updateOnlineUsers("updateOnlineUsers");
        }
        if(description.equals("ReqRejected")){
            Base.close();
            App.ReqRejected(obj.getInt("requesterID"));
        }
        if(description.equals("cancelReq")){
            Base.close();
            App.cancelReq(obj.getInt("requesterID"));
        }

    }
}
