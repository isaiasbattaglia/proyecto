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
var user;
var count=0;

function getUserInfo() {
    $.ajax({
        type: "get",
        url: "/userInfo",
        async: false,
        success : function(userInfo) {
            user = userInfo.id;
            console.log(user);
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

//Update the chat-panel, and the list of connected users
function updateChat(msg) {
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
}

