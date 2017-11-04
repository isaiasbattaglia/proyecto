//Establish the WebSocket connection and set up event handlers
var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/onlineGame");
webSocket.onmessage = function (msg) { updateChat(msg); };
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

//Send a message if it's not empty, then clear the input field
function sendMessage(message) {
    if (message !== "") {
        webSocket.send(message);
        id("message").value = "";
    }
}

function updateChat(msg) {
  var data = JSON.parse(msg.data);
  //id("userlist").innerHTML = "";
  console.log(data.msg=="UpdateTurn");
  console.log(data.userID);
  if(data.msg=="UpdateTurn"){
    if(document.getElementById("gameHome")!=null){
        location.reload();
    }
  }
  else{
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
    var jsonObj = {"id": currentUserID, "message":"connect"};
    var jsonString = JSON.stringify(jsonObj);
    console.log("Into sendUserInfo");
    console.log(jsonString);
    console.log(jsonObj);
    webSocket.send(jsonString); 
}


function deleteGame(id){
    deletee(id);
}



