// Event listener for DOM
//document.addEventListener("DOMContentLoaded", theDOMHasLoaded, false);
$(document).ready(function(){

var isAIE = navigator.appName == 'Microsoft Internet Explorer'
	|| /Edge\/\d./i.test(navigator.userAgent) || !!(navigator.userAgent.match(/Trident/) || navigator.userAgent
			.match(/rv:11/))
	|| (typeof $.browser !== "undefined" && $.browser.msie == 1);


// array for AudioObjects
var audioList = [];
// components and the index for their AudioObject
var componentDict = {};
// store AudioObject that is currently playing
var playingAudio = null;
// store playhead id if one is being dragged
var onplayhead = null;

/* AudioObject Constructor */
function AudioObject(audio, duration) {
    this.audio = audio;
    this.id = audio.id;
    this.duration = duration;
}

/* bindAudioPlayer
 * Store audioplayer components in correct AudioObject
 * num identifes correct audioplayer
 */
AudioObject.prototype.bindAudioPlayer = function (num) {
    this.audioplayer = document.getElementById("audioplayer-" + num);
    this.playbutton = document.getElementById("playbutton-" + num);
    this.timeline = document.getElementById("timeline-" + num);
    this.playhead = document.getElementById("playhead-" + num);
    this.timelineWidth = this.timeline.offsetWidth - this.playhead.offsetWidth;
	this.total_Time = document.getElementById("total-time-" + num);
	this.current_Time = document.getElementById("current-time-" + num);
	this.progress = document.getElementById("audio-progress-" + num); //Progress
}

/* addEventListeners() */
AudioObject.prototype.addEventListeners = function () {

	this.audio.addEventListener("loadedmetadata", AudioObject.prototype.loadedData,false);
    this.audio.addEventListener("timeupdate", AudioObject.prototype.timeUpdate, false);
    this.audio.addEventListener("durationchange", AudioObject.prototype.durationChange, false);
    //this.audio.addEventListener("canplaythrough", AudioObject.prototype.playAble, false);
    this.playbutton.addEventListener("click", AudioObject.prototype.pressPlay, false);
	//if (!isAIE) {
        this.timeline.addEventListener("click", AudioObject.prototype.timelineClick, false);
        // Makes playhead draggable 
        this.playhead.addEventListener('mousedown', AudioObject.prototype.mouseDown, false);    
        window.addEventListener('mouseup', mouseUp, false);
    //}
}

/* populateAudioList */
function populateAudioList() {
    var audioElements = document.getElementsByClassName("music");
    for (i = 0; i < audioElements.length; i++) {
        audioList.push(
            new AudioObject(audioElements[i], 0)
        );
        audioList[i].bindAudioPlayer(i);
        audioList[i].addEventListeners();
    }	
}

/* populateComponentDictionary() 
 * {key=element id : value=index of audioList} */
function populateComponentDictionary() {
    for (i = 0; i < audioList.length; i++) {
        componentDict[audioList[i].audio.id] = i;
        componentDict[audioList[i].playbutton.id] = i;
        componentDict[audioList[i].timeline.id] = i;
        componentDict[audioList[i].playhead.id] = i;
		componentDict[audioList[i].total_Time.id] = i;
		componentDict[audioList[i].current_Time.id] = i;
    }	
}

///////////////////////////////////////////////
// Update Audio Player
///////////////////////////////////////////////

/* durationChange
 * set duration for AudioObject */
AudioObject.prototype.durationChange = function () {
    var ao = audioList[getAudioListIndex(this.id)];
     if(isFinite(this.duration))
    {
    	ao.duration = this.duration;
		//ao.total_Time.textContent = formatTime(ao.duration);
    }
    else {
		ao.duration = $(this).prev('.aem-audio-duration').html();

    }
}

AudioObject.prototype.loadedData = function () {
    var ao = audioList[getAudioListIndex(this.id)];
    ao.duration = this.duration;
    if(isFinite(ao.duration))
    {
        ao.total_Time.textContent = formatTime(ao.duration);
        console.log('True duration='+ao.duration);
    }
    else
    {
        console.log('duration='+ao.duration);
        //ao.duration = $(this).parents().find('.aem-audio-duration').html();
    }
}
AudioObject.prototype.playAble = function () {
    var ao = audioList[getAudioListIndex(this.id)];
	ao.playbutton.style.visibility='visible';
}

/* pressPlay() 
 * call play() for correct AudioObject
 */

AudioObject.prototype.pressPlay = function () {
    var index = getAudioListIndex(this.id);
    audioList[index].play();
}

/* play() 
 * play or pause selected audio, if there is a song playing pause it
 */
AudioObject.prototype.play = function () {
	populateAudioList();
    if (this == playingAudio) {
        playingAudio = null;
        this.audio.pause();
        changeClass(this.playbutton, "playbutton play");
    }
    // else check if playing audio exists and pause it, then start this
    else {
        if (playingAudio != null) {
            playingAudio.audio.pause();
            changeClass(playingAudio.playbutton, "playbutton play");
        }
        this.audio.play();
        playingAudio = this;
        changeClass(this.playbutton, "playbutton pause");
    }
}

/* timelineClick()
 * get timeline's AudioObject
 */
AudioObject.prototype.timelineClick = function (event) {
    var ao = audioList[getAudioListIndex(this.id)];
    if(isFinite(ao.audio.duration))
    {
    	ao.audio.currentTime = ao.audio.duration * clickPercent(event, ao.timeline, ao.timelineWidth);
    }
    else {
        var aem_duration = $(this).attr('duration');
		console.log("Clickao.audio.duration=="+aem_duration);
        ao.audio.currentTime = aem_duration * clickPercent(event, ao.timeline, ao.timelineWidth);
    }

}

/* mouseDown */
AudioObject.prototype.mouseDown = function (event) {
    onplayhead = this.id;
    var ao = audioList[getAudioListIndex(this.id)];
    window.addEventListener('mousemove', AudioObject.prototype.moveplayhead, true);
    ao.audio.removeEventListener('timeupdate', AudioObject.prototype.timeUpdate, false);
}

/* mouseUp EventListener
 * getting input from all mouse clicks */
function mouseUp(e) {
    if (onplayhead != null) {
        var ao = audioList[getAudioListIndex(onplayhead)];
        window.removeEventListener('mousemove', AudioObject.prototype.moveplayhead, true);
        // change current time
        if(isFinite(ao.audio.duration))
        {
            ao.audio.currentTime = ao.audio.duration * clickPercent(e, ao.timeline, ao.timelineWidth);
        }    
        else {
            console.log('playhead='+onplayhead);
        	var movehead = document.getElementById(onplayhead);
            var aem_duration = movehead.parentElement.getAttribute('duration');
    		console.log("Infinity Mouse Move=="+aem_duration);
            ao.audio.currentTime = aem_duration * clickPercent(e, ao.timeline, ao.timelineWidth);
        }
        ao.audio.addEventListener('timeupdate', AudioObject.prototype.timeUpdate, false);
    }
    onplayhead = null;
}

/* mousemove EventListener
 * Moves playhead as user drags */
AudioObject.prototype.moveplayhead = function (e) {
    var ao = audioList[getAudioListIndex(onplayhead)];
    var newMargLeft = e.clientX - getPosition(ao.timeline);

  if (newMargLeft >= 0 && newMargLeft <= ao.timelineWidth) {
        document.getElementById(onplayhead).style.marginLeft = newMargLeft + "px";
    }
    if (newMargLeft < 0) {
        playhead.style.marginLeft = "0px";
    }
    if (newMargLeft > ao.timelineWidth) {
        playhead.style.marginLeft = ao.timelineWidth + "px";
    }
}

/* timeUpdate 
 * Synchronizes playhead position with current point in audio 
 * this is the html audio element
 */
AudioObject.prototype.timeUpdate = function () {
    // audio element's AudioObject
    var ao = audioList[getAudioListIndex(this.id)];
    if(!isFinite(ao.duration))
    {
    	ao.duration = $(this).prev('.aem-audio-duration').html();

    }
    //console.log('timeUpdate=' +ao.duration);
    var playPercent = ao.timelineWidth * (ao.audio.currentTime / ao.duration);
    ao.playhead.style.marginLeft = playPercent + "px";
	ao.current_Time.textContent = formatTime(ao.audio.currentTime);
    //ao.total_Time.textContent = formatTime(ao.duration);
	ao.progress.style.width = playPercent + "px";
    // If song is over
    if (ao.audio.currentTime == ao.duration) {
        changeClass(ao.playbutton, "playbutton play");
        ao.audio.currentTime = 0;
        ao.audio.pause();
        playingAudio = null;
    }
}

///////////////////////////////////////////////
// Utility Methods
///////////////////////////////////////////////

/* changeClass 
 * overwrites element's class names */
function changeClass(element, newClasses) {
    element.className = newClasses;
}

/* getAudioListIndex
 * Given an element's id, find the index in audioList for the correct AudioObject */
function getAudioListIndex(id) {
    return componentDict[id];
}

/* clickPercent()
 * returns click as decimal (.77) of the total timelineWidth */
function clickPercent(event, timeline, timelineWidth) {
   return (event.clientX - getPosition(timeline)) / timelineWidth;
}

// getPosition
// Returns elements left position relative to top-left of viewport
function getPosition(el) {
    return el.getBoundingClientRect().left;
}

function formatTime(time) {
  var min = Math.floor(time / 60);
  var sec = Math.floor(time % 60);
  return min + ':' + ((sec<10) ? ('0' + sec) : sec);
}

///////////////////////////////////////////////
// GENERATE HTML FOR AUDIO ELEMENTS AND PLAYERS
///////////////////////////////////////////////


/* createAudioPlayers
 * create audio players for each file in files */
function createAudioPlayers() {
    $('.music').each(function(f){
        var audio_duration = $(this).prev('.aem-audio-duration').html();
		this.id="audio-"+f;
        var playerString = "<div id=\"audioplayer-" + f + "\" class=\"audioplayer\"><button id=\"playbutton-" + f + "\" class=\"play playbutton\"></button><div id=\"timeline-" + f + "\" class=\"timeline\" duration="+audio_duration+"><div id=\"audio-progress-" + f + "\" class=\"audio-progress\"></div><div id=\"playhead-" + f + "\" class=\"playhead\"></div></div><div class=\"time-wrapper\"><span id=\"current-time-" + f + "\" class=\"current-time\">0:00</span><span class=\"time-slash\">/</span><span id=\"total-time-" + f + "\" class=\"total-time\">0:00</span></div></div>";
        $(this).after(playerString);
    });
}


/* theDOMHasLoaded()
 * Execute when DOM is loaded */
//function theDOMHasLoaded(event) {

    // Generate HTML for audio elements and audio players
    //createAudioElements();
    createAudioPlayers();

    // Populate Audio List

    populateAudioList();
    populateComponentDictionary();

$(window).resize(function(){
    populateAudioList();
})
function reloadAudio(){
	$('.music').each(function(){
        $(this).trigger('load');
        var totalduration = this.duration;
    	/*if(isFinite(totalduration))
    	{
			//$(this).next().find('.total-time').html(formatTime(sec));
        }
        else 
        {
			reloadAudio();
        }*/


    });
}
    if (isAIE) {
        setTimeout(function(){
            $('.music').each(function(){
                var ttime= $(this).next().find('.total-time').html();
                var sec;
                console.log("ttime"+ttime);
                //$(this).next().find('.total-time').html(formatTime(this.duration));
                if(ttime == '0:00')
                {

                    var aem_time= $(this).prev('.aem-audio-duration').html();
                    $(this).next().find('.total-time').html(formatTime(aem_time));
                    console.log('aem duration=='+aem_time);
                    reloadAudio();

                }        
            });
        },800);
    }

});