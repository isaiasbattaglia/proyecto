//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/onlineGame");
webSocket.onmessage = function (msg) { update(msg); };
webSocket.onclose = function () { alert("WebSocket connection closed") };
webSocket.onopen = function() {sendUserInfo();};

function Game(user1ID,user2ID,message){
    this.user1ID=user1ID;
    this.user2ID=user2ID;
    this.message=message;
}

var example;
var game;
var currentUserID;
var count=0;

function getUserInfo() {
    $.ajax({
        type: "get",
        url: "/userInfo",
        async: false,
        success : function(userInfo) {
            currentUserID = userInfo.id;
            console.log(currentUserID);
        }
    });
}

function deletee(id) {
    $.ajax({
        type: "get",
        url: "/deleteGame?id="+id+"&uid="+currentUserID,
        async: false,
        success : function() {
            location.reload();
        }
    });
}

function clear(){
  $('#DuelLobby > #table2').hide();
  $('#DuelLobby > #result').hide();
  $('#DuelLobby > #finalResults').hide();
  id("message").innerHTML="";
  id("options").innerHTML="";
  id("question").innerHTML="";
  id("cancelButton").innerHTML="";
}

function showMessage(message){
    clear();
    id("message").innerHTML=message;
}

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

function update(msg){
    var data = JSON.parse(msg.data);
    if(data.msg=="updateOnlineUsers")
        updateChat(data);
    if(data.msg=="updateUsersForDuel")
        updateChat2(data);
    if(data.msg=="leaveTheGame")
      showWinMessage();
    if(data.msg=="UpdateTurn")
        notifyTurn();
    if(data.msg=="wait"){
      id("playOrNot").innerHTML="Play";
      var input = `<button class="btn btn-primary btn-lg round" onclick="cancelReq(${data.rivalID})"> Cancelar </button>`;
      insert("cancelButton",input);
      showMessage("Esperando aceptacion de rival");
    }
    if(data.msg=="acceptReject"){
        id("playOrNot").innerHTML="Play";
        var userID = data.requesterUser;
        var input = `<button class="btn btn-primary btn-lg round"  onclick="requestAccepted(${userID})"> Aceptar </button> <button class="btn btn-primary btn-lg round" onclick="requestRejected(${userID})">Rechazar</button>`;
        showMessage(input);
    }
    if(data.msg=="showQuestion"){
      game=data.gameID;
      question=data.questionID;
      showQuestion(msg);
    }
    if(data.msg=="showResult")
        showResult(data);
    if(data.msg=="showFinalResults")
        showFinalResult(data);
    if(data.msg=="ReqRejected"){
      clear();
      var mesg = `Tu rival rechazo la peticion de duelo.`;
      var input = `<button class="btn btn-primary btn-lg round">Aceptar</button>`;
      var form = `<form action="/fightLobby" method="get">${mesg} ${input}</form>`;
      showMessage(form);
    }
    if(data.msg=="gameReqCanceled"){
     window.location.replace("/fightLobby");
    }
}

function cancelReq(rivalID){
  var jsonObj = {"requestedID":rivalID, "message":"cancelReq"};
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
  window.location.replace("/fightLobby");
}


function requestRejected(rivalID){
  var jsonObj = {"requesterID":rivalID, "requestedID": currentUserID, "message":"ReqRejected"};
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
  window.location.replace("/fightLobby");
}

function showWinMessage(){
  if(id("playOrNot").innerHTML=="Play"){
    clear();
    var mesg = `Tu rival ha abandonado el duelo, ganaste.`;
    var input =`<button class="btn btn-primary btn-lg round">Aceptar</button>`;
    var form = `<form action="/fightLobby" method="get">${mesg} ${input}</form>`;
    showMessage(form);
  }
}

function showQuestion(msg){
  clear();
  var data = JSON.parse(msg.data);
  var span= `<span class="btn-label"><i class="glyphicon glyphicon-chevron-right"></i></span>`;
  var input1=`<input id="answer1" type="hidden" name="q_answer" value="${data.option1}">${data.option1}`;
  var label1=`<label onclick="sendAnswer('${data.option1}')" class="element-animation1 btn btn-lg btn-primary btn-block">${span} ${input1}</label>`;

  var input2=`<input id="answer2" type="hidden" name="q_answer" value="${data.option2}">${data.option2}`;
  var label2=`<label onclick="sendAnswer('${data.option2}')" class="element-animation1 btn btn-lg btn-primary btn-block">${span} ${input2}</label>`;

  var input3=`<input id="answer3" type="hidden" name="q_answer" value="${data.option3}">${data.option3}`;
  var label3=`<label onclick="sendAnswer('${data.option3}')" class="element-animation1 btn btn-lg btn-primary btn-block">${span} ${input3}</label>`;

  var input4=`<input id="answer4" type="hidden" name="q_answer" value="${data.option4}">${data.option4}`;
  var label4=`<label onclick="sendAnswer('${data.option4}')" class="element-animation1 btn btn-lg btn-primary btn-block">${span} ${input4}</label>`;
  id("playOrNot").innerHTML="Play";
  id("question").innerHTML=data.question;
  id("options").innerHTML=label1;
  id("options").innerHTML+=label2;
  id("options").innerHTML+=label3;
  id("options").innerHTML+=label4;
}

function sendAnswer(answer){
  var jsonObj = {"id": currentUserID, "answer":answer, "message":"answered", "gameID":game, "questionID":question};    
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
  waiting();
}

function showResult(data){
  if(data.correct)
      showCorrectWrong(true);
  else
      showCorrectWrong(false);
  setTimeout(function(){requestNewQuestion(data);},3000);
}


function showCorrectWrong(correct){
  clear();
  $('#DuelLobby > #result').show();
  if(correct)
    id("message2").innerHTML="Respondiste correctamente";
  else
    id("message2").innerHTML="Respondiste incorrectamente";
}

function requestNewQuestion(data){
  var jsonObj = {"message":"newQuestion", "gameID":game};    
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);    
}

function requestAccepted(rivalID){
  var jsonObj = {"requesterID":rivalID, "requestedID": currentUserID, "message":"ReqAccepted"};
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
}

function notifyTurn(){
  if(document.getElementById("gameHome")!=null){
    location.reload();
  }
}

function updateChat(data) {
  if(id("userlist")!=null){
    id("userlist").innerHTML = "";
    if(data.userlist.length-1>0){
      $("table").show();
      data.userlist.forEach(function (user) {
          if(user.id!=currentUserID){
            var username = `${user.username}`;
            id("name").innerHTML="";
            insert("name",username)
            var input = `<button class="btn btn-primary btn-lg round" value=${user.id} id=${count++} onclick="play(this.value)">Jugar</button>`;
            var form = `<form action="/play" method="get">${input}</form>`;
            insert("userlist", form); 
          }
      });
    }
    else{
      $("table").hide();
    }
  }
}

function updateChat2(data) {
    if(id("playOrNot").innerHTML!="Play"){
      if(id("userlistt")!=null){
        id("userlistt").innerHTML = "";
        console.log(data.userlist);
        if(data.userlist.length-1>0){
          $('#DuelLobby > #table2').show();
          data.userlist.forEach(function (user) {
            if(user.id!=currentUserID){
              var username = `${user.username}`;
              id("name2").innerHTML=username;
              var input = `<button class="btn btn-primary btn-lg round" value=${user.id} id=${count++} onclick="sendGameRequest(this.value)">Jugar</button>`;
              insert("userlistt", input);
            }
          });
        }
        else
          $('#DuelLobby > #table2').hide();
      }
    }
}

function showFinalResult(data){
    clear();
    $('#DuelLobby > #finalResults').show();
    var button = `<button class="btn btn-primary btn-lg round">Aceptar</button>`;
    var form = `<form action="/fightLobby" method="get">${button}</form>`;
    id("button2").innerHTML=form;
    if(data.draw){
      id("profile-img").src="http://www.triviacrackkingdoms.com/img/rules/king.png";
      id("win").innerHTML="Empataste";
      id("correctAnswers").innerHTML="Respuestas correctas: "+data.correct;
      id("wrongAnswers").innerHTML="Respuestas incorrectas: "+data.wrong;
    }
    else if(data.win){
      id("profile-img").src="http://www.androidappsforpc.org/wp-content/uploads/2015/02/characters-1.png";
      id("win").innerHTML="Ganaste";
      id("correctAnswers").innerHTML="Respuestas correctas: "+data.correct;
      id("wrongAnswers").innerHTML="Respuestas incorrectas: "+data.wrong;
    }
    else{
      id("profile-img").src="http://triviacrackkingdoms.com/img/characters/chara-marks.png";
      id("win").innerHTML="Perdiste";
      id("correctAnswers").innerHTML="Respuestas correctas: "+data.correct;
      id("wrongAnswers").innerHTML="Respuestas incorrectas: "+data.wrong;
    }
}

function waiting(){
  showMessage("Aguardando por el rival . . ."); 
  return false;
}

function nobackbutton(){
  window.location.hash="no-back-button";
  window.location.hash="Again-No-back-button";
  window.onhashchange=function(){window.location.hash="no-back-button";}
}

function sendDuelInfo(){
  var jsonObj = {"id": currentUserID, "message":"connectInDuelLobby"};
  var jsonString = JSON.stringify(jsonObj);
  console.log(jsonString);
  webSocket.send(jsonString);
}

function play(rivalID){
    game = new Game(currentUserID,rivalID,"newGame");
    var jsonString = JSON.stringify(game);
    webSocket.send(jsonString);
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

function sendUserInfo(){
    getUserInfo();
    if(document.getElementById("DuelLobby")==null)
        var jsonObj = {"id": currentUserID, "message":"connect"};
    else
        var jsonObj = {"id": currentUserID, "message":"connectInDuelLobby"};   
    var jsonString = JSON.stringify(jsonObj);
    webSocket.send(jsonString); 
}


function deleteGame(id){
    deletee(id);
}

function sendGameRequest(rivalID){
  id("playOrNot").innerHTML="Play";
  var jsonObj = {"id": currentUserID, "rivalID":rivalID, "message":"GameRequest"};
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
}
