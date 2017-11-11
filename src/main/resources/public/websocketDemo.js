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
        url: "/deleteGame?id="+id,
        async: false,
        success : function() {
            location.reload();
        }
    });
}

function clear(){
  id("userlistt").innerHTML="";
  id("message").innerHTML="";
  id("options").innerHTML="";
  id("question").innerHTML="";
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
      var input = `<li><button onclick=cancelReq(${data.rivalID})> Cancelar </button></li>`;
      showMessage("Esperando aceptacion de rival"+input);
    }
    if(data.msg=="acceptReject"){
        id("playOrNot").innerHTML="Play";
        var userID = data.requesterUser;
        var input = `<button onclick=requestAccepted(${userID})> Aceptar </button> <button onclick=requestRejected(${userID})>Rechazar</button>`;
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
      var mesg = `<h1>Tu rival rechazo la peticion de duelo.</h1>`;
      var input = `<li><button>Aceptar</buton></li>`;
      var form = `<form action="/fightLobby" method="get">${mesg} ${input}</form>`;
      showMessage(form);
    }
    if(data.msg=="gameReqCanceled"){
     window.location.replace("/fightLobby");
    }
}

function cancelReq(rivalID){
  var jsonObj = {"requesterID":rivalID, "message":"cancelReq"};
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
    var mesg = `<h1> Tu rival ha abandonado el duelo, ganaste.</h1>`;
    var input = `<li><button>Aceptar</buton></li>`;
    var form = `<form action="/fightLobby" method="get">${mesg} ${input}</form>`;
    showMessage(form);
  }
}

function showQuestion(msg){
  clear();
  id("playOrNot").innerHTML="Play";
  var data = JSON.parse(msg.data);
  id("question").innerHTML=data.question;
  id("options").innerHTML=`<li><button value="${data.option1}" onclick="sendAnswer(this.value)">${data.option1}</buton></li>`;
  id("options").innerHTML+=`<li><button value="${data.option2}" onclick="sendAnswer(this.value)">${data.option2}</buton></li>`;
  id("options").innerHTML+=`<li><button value="${data.option3}" onclick="sendAnswer(this.value)">${data.option3}</buton></li>`;
  id("options").innerHTML+=`<li><button value="${data.option4}" onclick="sendAnswer(this.value)">${data.option4}</buton></li>`;
}

function sendAnswer(answer){
  var jsonObj = {"id": currentUserID, "answer":answer, "message":"answered", "gameID":game, "questionID":question};    
  var jsonString = JSON.stringify(jsonObj);
  webSocket.send(jsonString);
}

function showResult(data){
  if(data.correct)
      showMessage("Respondiste correctamente");
  else
      showMessage("Respondiste incorrectamente");
  setTimeout(function(){requestNewQuestion(data);},3000);
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
    data.userlist.forEach(function (user) {
        if(user.id!=currentUserID){
          var input = `<li>${user.username}<button value=${user.id} id=${count++} onclick="play(this.value)">Play</buton></li>`;
          var form = `<form action="/play" method="get">${input}</form>`;
          insert("userlist", form); 
        }
    });
  }
}

function updateChat2(data) {
    if(id("playOrNot").innerHTML!="Play"){
      if(id("userlistt")!=null){
        id("userlistt").innerHTML = "";
        data.userlist.forEach(function (user) {
          if(user.id!=currentUserID){
            var input = `<li>${user.username}<button value=${user.id} id=${count++} onclick="sendGameRequest(this.value)">Play</buton></li>`;
            //var form = `<form action="/playDuel" method="get">${input}</form>`;
            insert("userlistt", input);
          }
        });
      }
    }
}

function showFinalResult(data){
      var button = `<li><button>Aceptar</buton></li>`;
    if(data.draw){
      var input = `Empataste.<li>Respuestas correctas:${data.correct}</li><li>Respuestas incorrectas:${data.wrong}</li>`;
      var form = `<form action="/fightLobby" method="get">${input} ${button}</form>`;
      showMessage(form);
    }
    else if(data.win){
      var input = `Ganaste.<li>Respuestas correctas:${data.correct}</li><li>Respuestas incorrectas:${data.wrong}</li>`;
      var form = `<form action="/fightLobby" method="get">${input} ${button}</form>`;
      showMessage(form);
    }
    else{
      var input = `Perdiste.<li>Respuestas correctas:${data.correct}</li><li>Respuestas incorrectas:${data.wrong}</li>`;
      var form = `<form action="/fightLobby" method="get">${input} ${button}</form>`;
      showMessage(form);
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