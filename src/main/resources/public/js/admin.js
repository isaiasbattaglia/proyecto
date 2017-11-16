var nickname;
function validateUser() {
  console.log("hola");
  nickname=id("nickname").value;
  console.log("hola paola");
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