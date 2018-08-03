/*Play One Video custom code Start*/

var track_video=true;

function playOnevideo(){
	var videos = document.querySelectorAll('video');
	for(var i=0; i<videos.length; i++)
    {
	   videos[i].addEventListener('play', function(){pauseAll(this)}, true);
    }

	function pauseAll(elem){
		for(var i=0; i<videos.length; i++){
			if(videos[i] == elem) continue;
			if(videos[i].played.length > 0 && !videos[i].paused){
			  videos[i].pause();
			}
		}
	}
    track_video=false;
}

/*Play One Video custom code End*/