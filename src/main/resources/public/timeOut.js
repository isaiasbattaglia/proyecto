
var wa;
var timer=15;
$(document).ready(function(){
  setInterval(function(){ myTimer() }, 1000);
  setTimeout(function(){wrongAnswer();},15000);
});


function myTimer() {
	timer=timer-1;
    document.getElementById("timer").innerHTML = timer;
}

function wrongAnswer(){
	getWrongAnswer();
	document.getElementById("wrongAnswer").submit();
	//location.replace("/answer?answer="+wa);
}

function getWrongAnswer() {
    $.ajax({
        type: "get",
        url: "/getWrongAnswer",
        async: false,
        success : function(answer) {
        	wa=answer;
        }
    });
}


