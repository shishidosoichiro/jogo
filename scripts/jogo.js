!function (window, $) {

	function getUrl() {
		var path = 'scripts/jogo.js';
		var src = '';
		var elements = document.getElementsByTagName ('script');
		for ( var i = 0; i < elements.length; i++ ) {
			var element = elements[i];
			if (!element.src) continue;
			if (!element.src.match(new RegExp(path))) continue;
			src = element.src;
			break;
		}
		if (!src) throw path + ' is not found.';
		return src.replace(path, '');
	}

	var JOGO = {
		url: getUrl(),
		trackKey: '',
		sitename: window.location.protocol + '//' + window.location.host,
		sessionId: '',
		casename: '',
		setup: function(trackKey, casename){
			this.trackKey = trackKey;
			this.casename = casename;
		},
		track: function(eventname, value){
			var url = JOGO.url + 'track?trackKey=$trackKey&sitename=$sitename&sessionId=$sessionId&casename=$casename&eventname=$eventname&value=$value';
			url = url.replace("$trackKey", encodeURI(this.trackKey));
			url = url.replace("$sitename", encodeURI(this.sitename));
			url = url.replace("$sessionId", encodeURI(this.sessionId));
			url = url.replace("$casename", encodeURI(this.casename));
			url = url.replace("$eventname", encodeURI(eventname));
			if (value) url = url.replace("$value", encodeURI(value));
			if (!value) url = url.replace("$value", '');
			$.ajax({type: "GET", "url": url});
		},
		visit: function(){
			this.track('Visit', window.location.pathname);
		}
	};
	
	window.JOGO = JOGO;
	var Deferred = (function(){
		var State = function(name){
			this.name = name;
			this.fired = false;
			this.procs = new Array();
		};
		State.prototype = {
			go: function(proc) {
				this.procs.push(proc)
				if ( this.fired ) proc(this.firedArguments);
			}
			,fire: function() {this.fireWith()}
			,fireWith: function() {
				this.fired = true;
				this.firedArguments = arguments;
				for (var i in this.procs ) {
					this.procs[i](arguments);
				}
			}
		};
		var Promise = function(dfd){
			this.dfd = dfd;
		};
		Promise.prototype = {
			done: function(proc) {
				this.dfd.done(proc);
				return this;
			}
			,fail: function(proc) {
				this.dfd.fail(proc);
				return this;
			}
			,then: function(done, fail) {
				this.done(done);
				this.fail(fail);
				return this;
			}
		};
		var Class = function(){
			this.resolved = new State("resolved");
			this.rejected = new State("rejected");
			this.pending = new State("pending");
			this.prms = new Promise(this);
			this.st = this.pending;
		};
		Class.prototype = {
			state: function() {
				return this.st.name;
			}
			,resolve: function() {this.resolveWith();}
			,resolveWith: function() {
				if ( this.isResolved() ) return;
				this.st = this.resolved;
				this.st.fire(arguments);
			}
			,isResolved: function() {return this.st == this.resolved;}
			,reject: function() {this.rejectWith();}
			,rejectWith: function() {
				if ( this.isRejected() ) return;
				this.st = this.rejected;
				this.st.fire(arguments);
			}
			,isRejected: function() {return this.st == this.rejected;}
			,done: function(proc) {
				this.resolved.go(proc);
				return this;
			}
			,fail: function(proc) {
				this.rejected.go(proc);
				return this;
			}
			,then: function(done, fail) {
				this.done(done);
				this.fail(fail);
				return this;
			}
			,promise: function() {
				return this.prms;
			}
		};
		return Class;
	})();
	
	var Loader = (function(){
		var Class = function(){
			this.dfds = {};
			this.dfd = new Deferred();
		};
		Class.prototype = {
			load: function(path){
				if (this.dfds[path]) return this;
				
				var dfd = new Deferred();
				this.dfds[path] = dfd;
				var script = document.createElement('script');
				script.src = path;
				script.type = 'text/javascript';
				var self = this;
				script.onload = function(){
					dfd.resolve();
					self.verify();
				};
				
				var s = document.getElementsByTagName('script')[0];
				s.parentNode.insertBefore(script, s);
				return this;
			}
			,done: function(callback) {this.dfd.done(callback);}
			,fail: function(callback) {this.dfd.fail(callback);}
			,verify: function() {
				for ( var i in this.dfds ) {
					if ( ! this.dfds[i].isResolved() ) return;
				}
				for ( var i in this.dfds ) {
					if ( this.dfds[i].isRejected() ) {
						this.dfd.reject();
						return;
					}
				}
				this.dfd.resolve();
			}
		};
		return Class;
	})();

	new Loader()
	.load(JOGO.url + 'scripts/jquery.js')
	.load(JOGO.url + 'scripts/purl.js')
	.load(JOGO.url + 'scripts/uuid.js')
	.load(JOGO.url + 'scripts/cookies.js')
	.done(function(){
	  $(function () {
			JOGO.sessionId = Cookies.get('JogoSessionId');
			if (!JOGO.sessionId) {
				JOGO.sessionId = UUID.generate();
				Cookies.set('JogoSessionId', JOGO.sessionId);
			}
			JOGO.visit();
			
			// if someone plays Soundcloud players, track the event.
			$.getScript("http://w.soundcloud.com/player/api.js", function(){
				$('iframe[src*="w.soundcloud.com"]').each(function(i){
					this.src = this.src;
					var widget = SC.Widget(this);
					widget.bind(SC.Widget.Events.PLAY, function(data){
						widget.getCurrentSound(function (value){
							JOGO.track('Play Soundcloud', value.title);
							if (track) track('Play Soundcloud', value.title);
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
										if (track) track('Play Youtube', data.entry.title["$t"]);
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
				if (track) track('Go To Amazon', code);
			});
		});
	});

}(window, window.jQuery);

// if someone clicks the links to Amazon, track the event
function onYouTubePlayerAPIReady(){JOGO.youtube();}
