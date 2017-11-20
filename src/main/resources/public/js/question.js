$(function(){
    var loading = $('#loadbar').hide();
    $(document)
    .ajaxStart(function () {
        loading.show();
    }).ajaxStop(function () {
    	loading.hide();
    });
});	


function sendUserAnswer(option){
  var answer;
  if(option==1)
    answer=id("answer1").value;
  if(option==2)
    answer=id("answer2").value;
  if(option==3)
    answer=id("answer3").value;
  if(option==4)
    answer=id("answer4").value;
  id("answ").value=answer;
  id("answer").submit();
}