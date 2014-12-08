/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var ZfreqAnalyzer = {
    contextClass : (window.AudioContext ||
    window.webkitAudioContext ||
    window.mozAudioContext ||
    window.oAudioContext ||
    window.msAudioContext),
    
    checkSuccess : function(){
        if (contextClass) {
        // Web Audio API is available.
            var audioContext = new contextClass();
            console.log("Audio context is available...");
        } else {
            console.log("Web Audio API is not available...");
        }
    }
};
