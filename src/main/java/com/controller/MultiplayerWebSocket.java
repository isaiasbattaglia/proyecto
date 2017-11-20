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
        MultiplayerHelper.userUsernameMap.put(user, new User());
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
      User currentUser = MultiplayerHelper.userUsernameMap.get(user);
      MultiplayerHelper.usersInDuelLobby.remove(user);
      MultiplayerHelper.leaveTheGame(currentUser);
      MultiplayerHelper.updateOnlineUsers("updateUsersForDuel");
      MultiplayerHelper.userUsernameMap.remove(user);
      MultiplayerHelper.updateOnlineUsers("updateOnlineUsers");
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/trivia", "root", "root");
        JSONObject obj = new JSONObject(message);
        String description = new String(obj.getString("message"));
        if(description.equals("connect")){
            User u = UserService.getUser(obj.getInt("id"));
            MultiplayerHelper.userUsernameMap.put(user,u);
            Base.close();
            MultiplayerHelper.updateOnlineUsers("updateOnlineUsers");
        }
        if (description.equals("newGame")) {
            GameService.createGame(obj.getInt("user1ID"),obj.getInt("user2ID"));
            Base.close();
        }
        if(description.equals("GameRequest")){
            User user1 = User.findById(obj.getInt("id"));
            User user2 = User.findById(obj.getInt("rivalID"));
            Session u1 = MultiplayerHelper.getKeyByValue(MultiplayerHelper.usersInDuelLobby,user1);
            Session u2 = MultiplayerHelper.getKeyByValue(MultiplayerHelper.usersInDuelLobby,user2);
            MultiplayerHelper.usersInDuelLobby.remove(u1);
            MultiplayerHelper.usersInDuelLobby.remove(u2);
            Base.close();
            MultiplayerHelper.sendGameReq(obj.getInt("id"),obj.getInt("rivalID"));
            MultiplayerHelper.updateOnlineUsers("updateUsersForDuel");
        }
        if(description.equals("ReqAccepted")){
            Game game = GameService.createDuelGame(obj.getInt("requesterID"),obj.getInt("requestedID"));
            Base.close();
            MultiplayerHelper.updateOnlineUsers("updateUsersForDuel");
            MultiplayerHelper.sendQuestion(game.getInteger("id"));
        }
        if(description.equals("answered")){
            GameService.setAnswerForUser(obj.getInt("gameID"), obj.getInt("id"),obj.getString("answer"));
            Base.close();
            MultiplayerHelper.sendResults(obj);
        }
        if(description.equals("newQuestion")){
            Base.close();
            MultiplayerHelper.sendQuestion(obj.getInt("gameID"));
        }
        if(description.equals("connectInDuelLobby")){
            User u = UserService.getUser(obj.getInt("id"));
            MultiplayerHelper.usersInDuelLobby.put(user,u);
            MultiplayerHelper.userUsernameMap.put(user,u);
            Base.close();
            MultiplayerHelper.updateOnlineUsers("updateUsersForDuel");
            MultiplayerHelper.updateOnlineUsers("updateOnlineUsers");
        }
        if(description.equals("ReqRejected")){
            Base.close();
            MultiplayerHelper.ReqRejected(obj.getInt("requesterID"));
        }
        if(description.equals("cancelReq")){
            Base.close();
            MultiplayerHelper.cancelReq(obj.getInt("requestedID"));
        }

    }
}
