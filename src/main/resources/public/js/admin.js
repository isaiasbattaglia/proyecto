var nickname;
var selectedOption;
function validateUser() {
  nickname=id("nickname").value;
  validateUser2(nickname);
}

function validateUser2() {
    $.ajax({
        type: "get",
        url: "/validateUser?username="+nickname,
        async: false,
        success : function(isValid) {
          if(isValid=="true")
              newUser();
            else
              id("h1").innerHTML="El usuario ingresado no es valido.";
        }
    });
}

function newUser() {
    $.ajax({
        type: "post",
        url: "/newUser?username="+nickname,
        async: false,
        success : function(canCreate) {
          if(canCreate=="true")
            id("h1").innerHTML="Se creo el nuevo administrador con exito";
          else
            id("h1").innerHTML="El usuario ingresado ya es administrador";
        }
    });
}

function createQuestion(){
  var category = id("option").value;
  var question = id("question").value;
  var answer1 = id("input1").value;
  var answer2 = id("input2").value;
  var answer3 = id("input3").value;
  var answer4 = id("input4").value;
  var jsonObj;
  console.log("hola!!!");
  console.log(selectedOption);

  if(selectedOption==1)
    jsonObj = {"correct": answer1, "answer2":answer2, "answer3":answer3, "answer4":answer4, "category":category, "question":question};
  if(selectedOption==2)
    jsonObj = {"correct": answer2, "answer2":answer1, "answer3":answer3, "answer4":answer4, "category":category, "question":question};
  if(selectedOption==3)
    jsonObj = {"correct": answer3, "answer2":answer2, "answer3":answer1, "answer4":answer4, "category":category, "question":question};
  if(selectedOption==4)
    jsonObj = {"correct": answer4, "answer2":answer2, "answer3":answer3, "answer4":answer1, "category":category, "question":question};

  var jsonString = JSON.stringify(jsonObj);
  sendQuestion(jsonString);
}

function sendQuestion(json){
  $.ajax({
    type: "post",
    url: "/newQuestion?json="+json,
    async: false,
    success: function(data) {
      id("success").innerHTML="Pregunta creada correctamente";
      clearInput();
    }
  });
}

function saveOption(value){
  selectedOption=value;
}

function clearInput(){
  id("question").value="";
  id("input1").value="";
  id("input2").value="";
  id("input3").value="";
  id("input4").value="";
}