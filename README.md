# TIDAL-Remote
The official TIDAL app for Android only supports playing music to the Android device itself. While this is of course very nice, many people nowadays have a media center at home that combines all music and video sources to the home speakers and television(s). Some of these systems (like Roon) support TIDAL, but others do not.

This Android app, together with the TIDAL-Streamer application, provides functionality to play TIDAL music and videos on any media center that can run Python 3. Browsing and player control are handled by TIDAL-Remote, while the queue, playback and functions like shuffle and automatic playback are handled by TIDAL-Player.

# Current status
The application is still very much in development. The basic backbone is in place: calls to the TIDAL API endpoints, a Websocket connecting to the TIDAL-Player and a UI that allows searching and exploring the TIDAL database.

# Please read!
This project is not meant to undermine or degrade the TIDAL services in any way. If the official TIDAL app provides all the features you need (browsing the TIDAL database, playing locally or on a Chromecast) please use that! If you have a dedicated music device (like a Raspberry), consider using Roon which is officially supported by TIDAL. TIDAL-Remote is only meant when these solutions are not suitable for you, specifically when the device is not a dedicated *music* device, or when you would also want playback of the music videos TIDAL has to offer. However, be aware that TIDAL-Remote only provides a basic interface, and is missing many of the details available in the official TIDAL app. TIDAL-Remote started as a personal hobby project, and therefore there are no guarantees that it works, keeps working or provides the functionality you would like or the functionalities listed in the documentation.

Use of TIDAL-Remote requires logging in with a username and password (anonymous browsing is NOT supported), and users needs to obtain an API token by themselves. If you have no TIDAL account, please do NOT use this app. (As far as I can test, using the app without an account should be impossible) The app avoids an excessive number of API calls (currently it does way less calls than the official web interface or TIDAL desktop app) and images are cached using Glide. TIDAL-Remote is not able to play tracks on the device itself, but connects to TIDAL-Player via websocket to play tracks using MPD.

Lastly, note that I have reached out to TIDAL to ask if using their APIs is in any way violating their terms and conditions, but apart from the response "Unfortionately, we do not share API" (exact answer) I have received no answer on a follow-up question specifically asking if reverse engineering the API calls is ok.

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
