function getUrlParameter(name) {
    name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
    var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
    var results = regex.exec(location.search);
    return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
};

$( document ).ready(function() {
  var url = window.location.href;
    var captured = /Error=([^&]+)/.exec(url)[1]; // Value is in [1] ('384' in our case)
    var result = captured ? captured : " ";
    if(result=="PasswordError"){
    	  window.location.replace("#signup");
    	  insert("signupError","Las contraseñas no coinciden vuelva a intentarlo");
    }
    if(result=="Success"){
          window.location.replace("#signin");
          insert("signinError","Cuenta creada con exito.");
    }
    if(result=="userMail"){
          window.location.replace("#signup");
          insert("signupError","Verifique nombre de usuario o email.");
    }
    if(result=="SessionOpen"){
      var captured2 = /username=([^&]+)/.exec(url)[1]; // Value is in [1] ('384' in our case)
      var result2 = captured2 ? captured2 : " ";
      window.location.replace("#signin");
      insert("signinError","Ya has iniciado sesion como: "+result2);
    }
    if(result=="wrongPass"){
        window.location.replace("#signin");
          insert("signinError","Usuario o contraseña incorrecta.");
    }
});

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}
//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}