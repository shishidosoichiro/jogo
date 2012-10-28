!function (window, $) {

	var JOGO = {
		url: 'http://jogo.herokuapp.com/',
		trackKey: '',
		sitename: window.location.protocol + '//' + window.location.host,
		sessionId: '',
		casename: '',
		setup: function(trackKey, casename){
			$(function(){
				JOGO.trackKey = trackKey;
				JOGO.casename = casename;
				JOGO.visit();
			});
		},
		track: function(eventname, value){
				var url = JOGO.url + 'tracker/?trackKey=$trackKey&sitename=$sitename&sessionId=$sessionId&casename=$casename&eventname=$eventname&value=$value';
				url = url.replace("$trackKey", encodeURI(trackKey));
				url = url.replace("$sitename", encodeURI(sitename));
				url = url.replace("$sessionId", encodeURI(sessionId));
				url = url.replace("$casename", encodeURI(casename));
				url = url.replace("$eventname", encodeURI(eventname));
				if (value) url = url.replace("$value", encodeURI(value));
				if (!value) url = url.replace("$value", '');
				$.ajax({type: "GET", "url": url});
			}
		},
		visit: function(){
			JOGO.track('visit');
		}
	};
	
	window.JOGO = JOGO;

  $(function () {

		JOGO.sessionId = $.cookie('JogoSessionId');
		if (!JOGO.sessionId) {
			JOGO.sessionId = UUID.generate();
			$.cookie('JogoSessionId', JOGO.sessionId);
		}
		
		// if someone plays Soundcloud players, track the event.
		$.getScript("http://w.soundcloud.com/player/api.js", function(){
			$('iframe[src*="w.soundcloud.com"]').each(function(i){
				this.src = this.src;
				var widget = SC.Widget(this);
				widget.bind(SC.Widget.Events.PLAY, function(data){
					widget.getCurrentSound(function (value){
						JOGO.track('Play Soundcloud', value.title);
					});
				});
			});
		});

		// if someone plays Youtube players, track the event.
		JOGO.youtube = function() {
			$('iframe[src*="www.youtube.com"]').each(function(){
				var id = $.url(this.src).segment(2);
				$(this).attr("id", id);
				var player = new YT.Player(id, {
					events: {
						"onStateChange": function(event){
							if(event.data == YT.PlayerState.PLAYING) {
								$.getJSON("http://gdata.youtube.com/feeds/api/videos/" + id + "?alt=json", function(data){
									JOGO.track('Play Youtube', data.entry.title["$t"]);
								});
							}
						}
					}
				});
			});
		}
		$.getScript("http://www.youtube.com/player_api");

		// if someone clicks the links to Amazon, track the event
		$('a[href*="www.amazon.co.jp"]').click(function(){
			var code = $(this).url().segment(3);
			JOGO.track('Go To Amazon', code);
		});
  })

}(window, window.jQuery);

// if someone clicks the links to Amazon, track the event
function onYouTubePlayerAPIReady(){JOGO.youtube();}
