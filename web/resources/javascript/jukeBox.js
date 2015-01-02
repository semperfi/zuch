/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


 var TIME_TO_WAIT = 1000/40;  //millisecond

var contextClass = (window.AudioContext ||
window.webkitAudioContext ||
window.mozAudioContext ||
window.oAudioContext ||
window.msAudioContext);
if (contextClass) {
    // Web Audio API is available.
    var audioContext = new contextClass();
    console.log("Audio context is available...");
} else {
    console.log("Web Audio API is not available...");
}

window.requestAnimationFrame = (function(){
    return window.requestAnimationFrame  ||
    window.webkitRequestAnimationFrame ||
    window.mozRequestAnimationFrame  ||
    window.oRequestAnimationFrame  ||
    window.msRequestAnimationFrame  ||
    function(callback){
        window.setTimeout(callback,TIME_TO_WAIT );
    };
})();


function initAudioContext(){
    audio = document.getElementById("zuchAudioPlayer");
    source = audioContext.createMediaElementSource(audio);
    analyser = audioContext.createAnalyser(); 
   // analyser.smoothingTimeConstant = 0;
    source.connect(analyser);
    analyser.connect(audioContext.destination);

}

function onEachStep(){
     var mediaplayer = document.getElementById("zuchAudioPlayer");
     if( !(mediaplayer.paused || mediaplayer.ended) ){
         analyseSound();
         //analyseTimeSound();
     }
}

function setAnalyser(){
      setTimeout(function(){
      window.requestAnimationFrame(setAnalyser);
      onEachStep();
    },TIME_TO_WAIT);
   
}

/*
function initAnalyser(timeToWait){
    setInterval(function() {
      var mediaplayer = document.getElementById("zuchAudioPlayer");
      if( !(mediaplayer.paused || mediaplayer.ended) ){
          analyseSound();
      }
        
    }, timeToWait);  //old value 1000/60
}
*/

function analyseSound(){
    
    var HEIGHT = 60;
    var WIDTH = 330;
    
   // console.log("Analyzing sound...");
    var canvas = document.getElementById('canvas');
    var canvasContext = canvas.getContext('2d');
   
    source.connect(analyser);
    analyser.connect(audioContext.destination);

    
   var freqDomain = new Uint8Array(analyser.frequencyBinCount);
   analyser.getByteFrequencyData(freqDomain);
   //analyser.getByteTimeDomainData(freqDomain);
    
    canvasContext.clearRect(0,0,canvas.width,canvas.height);
    
    for (var i = 0; i < analyser.frequencyBinCount; i++) {
        var value = freqDomain[i];
        var percent = value / 256;
        var height = HEIGHT * percent;
        var offset = HEIGHT - height - 1;
        var barWidth = WIDTH/analyser.frequencyBinCount;
        var hue = i/analyser.frequencyBinCount * 360;
        canvasContext.fillStyle = 'hsl(' + hue + ', 100%, 50%)';
        canvasContext.fillRect(i * barWidth, offset, barWidth , 5); //replae height by 5 to get line effect
        
    }
    
    //initAnalyser(TIME_TO_WAIT);
}


function jukeBoxPlayerEvent(){
    
    console.log("Media player events have been succefully resgistered...");
    
    var mediaplayer = document.getElementById("zuchAudioPlayer");
    
   // mediaplayer.volume = 0.5;
    
    mediaplayer.addEventListener("playing", function() { 
        //document.getElementById("music").play();
        //initAnalyser(TIME_TO_WAIT);
        setAnalyser();
        console.log("mp3 playing...");
        var nextIndex = document.getElementById("nextIndex");
        console.log("next mp3 index: "+ nextIndex.value); 
    
    }, false);
    
    mediaplayer.addEventListener("loadstart", function() { 
        //document.getElementById("music").play();
        console.log("loading start...");
    
    }, false);
    
    mediaplayer.addEventListener("pause", function() { 
        //document.getElementById("music").play();
       // stopTimer();
        console.log("mp3 pause...");
    
    }, false);
    
    mediaplayer.addEventListener("ended", function() { 
        //document.getElementById("music").play();
        console.log("mp3 ended...");
        var nextIndex = document.getElementById("nextIndex");
        console.log("next mp3 index: "+ nextIndex.value); 
        if(nextIndex.value !== "-1"){
            console.log("index is different... "); 
            PF('audioTableWidget').unselectAllRows();
            PF('audioTableWidget').selectRow(+nextIndex.value, false);
        }
        
    
    }, false);
    
    mediaplayer.addEventListener("volumechange", function() { 
        //document.getElementById("music").play();
        console.log("mp3 volumechange...");
    
    }, false);
    
    
  
}

function resetAnalyzer(){
    
    console.log("Reload analyzer...");
    jukeBoxPlayerEvent();
    if(contextClass){
        initAudioContext();
       // initAnalyser(TIME_TO_WAIT);
       setAnalyser();
    }

            
}

//not used in for PF 5.1
function keepPlaying(data){
    
   // var nextIndex = document.getElementById("jukeBoxForm:nextIndex");
    var status = data.status; // Can be "begin", "complete" or "success".

    switch (status) {
        case "begin": // Before the ajax request is sent.
            
            break;

        case "complete": // After the ajax response is arrived.
             
            
            break;

        case "success": // After update of HTML DOM based on ajax response..
            console.log("ajax request success...");
            jukeBoxPlayerEvent();
            if(contextClass){
                initAudioContext();
               // initAnalyser(TIME_TO_WAIT);
               setAnalyser();
            }
            
            break;
    }
    
   
    
}


document.addEventListener("DOMContentLoaded", jukeBoxPlayerEvent, false);
if(contextClass){
    document.addEventListener("DOMContentLoaded", initAudioContext, false);
   // document.addEventListener("DOMContentLoaded", initAnalyser(TIME_TO_WAIT), false);
   document.addEventListener("DOMContentLoaded", setAnalyser(), false);
}






