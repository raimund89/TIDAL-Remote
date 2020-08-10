# TIDAL-Remote
The official TIDAL app for Android only supports playing music to the Android device itself. While this is of course very nice, many people nowadays have a media center at home that combines all music and video sources to the home speakers and television(s). Some of these systems (like Roon) support TIDAL, but others do not.

This Android app, together with the TIDAL-Streamer application, provides functionality to play TIDAL music and videos on any media center that can run Python 3. Browsing and player control are handled by TIDAL-Remote, while the queue, playback and functions like shuffle and automatic playback are handled by TIDAL-Player.

# Current status
The application is still very much in development. The basic backbone is in place: calls to the TIDAL API endpoints, a Websocket connecting to the TIDAL-Player and a UI that allows searching and exploring the TIDAL database.

# Todo
- [x] Main user interface
- [x] Implementation for API calls
- [ ] Implementation for Websockets
- [ ] Secure saving of login credentials
- [x] Search results
- [ ] Player backend
- [x] Home results
- [x] My Collection results
- [ ] Album, playlist, artist, mix screens
- [ ] Current track info
- [ ] Bottom sheets with options
- [ ] General implementation of TIDAL 'pages'
- [ ] Notification of currently playing
- [ ] Support for more than one player (only 1 at a time can be playing!) using zeroconf/mdns
- [ ] Taking the queue with you when switching players
- [ ] Sharing the player with multiple TIDAL-Remotes (all logged in!)
