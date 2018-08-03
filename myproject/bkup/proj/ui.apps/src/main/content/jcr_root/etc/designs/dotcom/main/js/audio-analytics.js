$(document)
		.ready(
				function() {
					var addAudioEventHandler = function (audioTag){
						audioTag.addEventListener('play',
								audioEventHandler, false);
						audioTag.addEventListener('seeked',
								audioEventHandler, false);
						audioTag.addEventListener('seeking',
								audioEventHandler, false);
						audioTag.addEventListener('pause',
								audioEventHandler, false);
						audioTag.addEventListener('ended',
								audioEventHandler, false);
					}

					setTimeout(function() {
                        $("audio").each(function(i,e){addAudioEventHandler(e);})						
					}, 6000);
					
				

					function audioEventHandler(e) {
						var audio = e.target;
						var audioTitle = $(audio.closest("div.suntrust-audio-player-container")).find("h2.analytics-placeholder").text();
						audioTitle = audioTitle.replace(/[^A-Z0-9]+/ig, "");						
						var mediaName = "STcom|ResCntr|"+ audioTitle;
						// var mediaName = "STcom|ResCntr|" +
						// getDynamicMediaVideoTitle();

						s.prop34 = mediaName;
						s.eVar41 = mediaName;
						var mediaLength = audio.duration;
						var mediaPlayerName = "HTML5";

						/* Define audio offset */
						if (audio.currentTime > 0) {
							mediaOffset = Math.floor(audio.currentTime);
						} else {
							mediaOffset = 0;
						}
						;

						/* Call on audio start */
						if (e.type == "play") {
							// alert("play");
							if (mediaOffset == 0) {
								// alert("mediaName : " + mediaName);
								s.Media.open(mediaName, mediaLength,
										mediaPlayerName);
								s.Media.play(mediaName, mediaOffset);
							} else {
								s.Media.play(mediaName, mediaOffset);
							}
							;
						}
						;

						/* Call on scrub start */
						if (e.type == "seeking") {
							s.Media.stop(mediaName, mediaOffset);
						}
						;

						/* Call on scrub stop */
						if (e.type == "seeked") {
							s.Media.play(mediaName, mediaOffset);
						}
						;

						/*Call on pause*/
						if (e.type == "pause") {
							s.Media.stop(mediaName, mediaOffset);
						}
						;

						/*Call on audio complete*/
						if (e.type == "ended") {
							s.Media.stop(mediaName, mediaOffset);
							s.Media.close(mediaName);
							mediaOffset = 0;
						}
						;
					}
					;
				});