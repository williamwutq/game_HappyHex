# HappyHex  
A hexagonal Block Blast (e.g. Hex FRVR) implementation using java, build with custom graphics and advanced data processing.  

This is a very happy and fun game, with some Easter Eggs, for everyone :)

<b>Author:</b> William Wu  
<b>Languages:</b> Java ([Graphics](#Graphics-(GUI))), Python (used for autoplay and [ML](#develop--machine-learning))  
<b>Last edited:</b> 07/07/2025  
<b>Latest release:</b> [1.3.4](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.3.4)

> [!IMPORTANT]
> This project need the following [dependencies](#Dependencies) to run:
> 1. `javax.json` package ([Source](https://docs.oracle.com/javaee/7/api/javax/json/package-summary.html)). 
> 2. Implementation of `javax.json`, such as JSR-000374 Java API ([Download](https://download.oracle.com/otndocs/jcp/json_p-1_1-final-spec/index.html)). 
> 3. (*For tests*) `org.junit.jupiter.api` package ([Source](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)).<br/>
>    Or: delete the `tests` package in source code<br/>
>    See [Tests](#Tests) for more

> [!NOTE]
> The project may contain bugs:  
> - Report bugs by create an [issue](https://github.com/williamwutq/game_HappyHex/issues).  
> - The most recent release have ***zero*** detected bugs.  

---

## About
This is just a cute project of mine. From early on after learning java in high school and helped the teacher taught its graphics library `javax.swing` I
felt compelled to actually program a functional game. Combined with my earlier ideas of designing a hexagonal grid coordinate system, the rough picture of
what would be future `HappyHex` formed in my mind. In my spare time, I gradually developed this project through following my [timeline](#Future-Timeline)
and added increasingly attractive features. From version 0.4 to 1.3, it has grown from just a game page with buggy information display that had to be
launched through terminal commands and contained no color indication to a comprehensive and enjoyable game that features settings, themes, and animation.
Game logging and viewing, and even resume game functionalities are slowly build up in [Pull Requests](https://github.com/williamwutq/game_HappyHex/pulls).
It has become that one project which I could not stop thinking about. Step by step improvements will be made in different aspects and merged together for
revisions after revisions. In the end, I would incorporate intelligent autoplay systems, unlockable achievements, and even fancier graphics.

## License
Distributed under the MIT License

[License Link](https://github.com/williamwutq/game_HappyHex/blob/main/LICENSE)

## Usage
How to download the game and play, or, if you want, compile it yourself.  
- Method 1: Run the [`.jar`](https://github.com/williamwutq/game_HappyHex/blob/main/HappyHex_jar%20Version%201.2.3.zip) file  
   1. Find the Latest Release (See the [top](#HappyHex) of this document).  
   2. Download the asset from the release.
   3. Find `HappyHex_jar Version x.x.x.zip` zip file.  
   4. Use a tool to decompress the zip file, which will create a folder named `HappyHex_jar`.  
   5. Make sure that the folder contains:  
      a. The main jar file, `game_HappyHex.jar`.  
      b. The JSON utilities, `javax.json-1.1.4.jar` and `javax.json-api-1.1.jar`.  
      c. The `data` folder.  
   6. Double click or use a tool to run `game_HappyHex.jar`.  
- Method 2: Download code from GitHub.com  
   1. Find the Latest Release (See the [top](#HappyHex) of this document).  
   2. Download the asset from the release.
   3. Download [dependencies](#Dependencies). You don't need `javax.swing` if it is already installed.
   4. Set the downloaded dependencies as project dependencies or libraries.
   5. Remove `HappyHex_jar Version x.x.x.zip` zip file, which contain a compiled jar.
   6. If tests are not needed, remove the `tests` package.
   7. Compile the program.
   8. Run main method in `Main` class in the main directory, usually named `game_HappyHex`.

Usage of the Game Viewer please see its own [README](viewer/README.md). Generally, users may run the [JAR File](viewer/GameViewer.jar).

### Dependencies
- [javax.swing](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html)
- [javax.json](https://docs.oracle.com/javaee/7/api/javax/json/package-summary.html)
- [javax.json api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)
- [org.junit.jupiter.api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)

## Game Play
### Launcher Navigation
The launcher is the window that opens after the application is opened. The game is not directly played, but rather can be [started](#Start-Game) with a
start button. The [settings](#Settings-Page) and color [theme](#Themes-Page) can be changed through the launcher, which enhances the game experience for
the player. In addition, [Easter Eggs](#Easter-Eggs) can also be activated with the launcher.  

The launcher use an adjustable window, and all components contained in the window are dynamically resizable. That said, there is a minimum size limitation
of the game window, because otherwise the game cannot show all of its content. The limitation is currently `400 * 400` pixels.

The launcher has the following pages:  
#### Main Page
The main page displays the game name, version, credits, game hints, and provides buttons for navigating to other pages.
- <b>Game Name</b>  

  The game name, or title, appears on the topmost of the page. The title contain the characters "⬢HAPPY⬢⬢HEX⬢" with 12 different colors.
  These 12 colors will be the exact same color as the [colors](#Normal-Color-Theme) used for piece and block generation.  
  The sequencing of the colors is randomized every time the page is refreshed, which can be done by relaunching the application or switch to another
  page and back.  

- <b>Game Version</b>  

  The game version is displayed underneath the game title with an italic font. The version should say "Version x.x.x". This version corresponds to all other
  game versions presented in the game logs, messages, data recordings generated by this specific instance of the game HappyHex.  

- <b>Game Hints</b>  

  The game hints are hint messages that are displayed in the main page under the game version and game title. These messages either hint at a specific game
  mechanics, serve as tips for navigating the launcher or obtaining a high score, provide a humorous evironment by providing insights as me the developer, 
  or unveil secrets in the game such as the [Easter Eggs](#Easter-Eggs).  
  They are mostly gray in most themes, but their colors may change.  
  The hint message will change everytime the main page is refreshed, which can be done by relaunching the application or switch to another page and back.  
  An example message is: "Try hover over blocks", which hints that hovering over blocks in the game field when a piece is selected reveals
  the potential placements of pieces in the game.  

- <b>Buttons</b>  

  The game provides clickable buttons for navigating to other pages. All buttons are resizable and are of the same height.  
  
  1. Login Button  
     The login button has the word "LOG IN" written on it. It is intended to provide users for redirection to the [Login Page](#Login-Page).  
     When the game launcher is first launched, the player will not be automatically logged it. To log in, click on the button.  
  2. Settings Button  
     The settings button has the word "SETTING" written on it. It is intended to provide users for redirection to the [Settings Page](#Settings-Page).  
     Under the default setting, the game is playable, but the settings page offer player more options in game difficulty, board size, and whether to
     restart an unfinished game if such games exist.
  3. Themes Button  
     The settings button has the word "THEMES" written on it. It is intended to provide users for redirection to the [Themes Page](#Themes-Page).  
     The default [color theme](#Theme,-Color-and-Font) of the game is [Normal Theme](#Normal-Color-Theme), but the themes page enables players to 
     switch to other themes instead if they prefer to do so.  
  4. Start Button  
     The start button has the word "START" written on it. When clicked, it starts a new game based on the settings provided,
     modifiable via [Settings Page](#Settings-Page). If the player is logged in and an unfinished game exist, and the restart game setting is set to
     be `ON`, the unfinished game will continue.  
     The game play part is considered to be outside launcher. For detailed game rules, see [Game Rules](#Game-Rules).  

- <b>Game Credits</b>  

  On the button of the page there will be a section with the game credits to its [developers](#Contribution), which is currently just me.  
  To the left of the section, the words "A W.W Game" can be seen, with the two Ws shown in a slightly larger font with a different color.  
  To the right of the section, a copy right can be seen, attributing the game copyright to William Wu. See [License](#License) for details.  

#### Login Page
The login page enables the user to log in with their username. While no password is required, a unique username is recommended.
User information is logged in `logs.json`, which stores past game records, including recent and highest scores and turns.  

The page contains the following elements:  

- <b>Game Title</b>  
  
  This is the exact same element as that in [Main Page](#Main-Page), with the only difference being the elimination of the space above this title.
  The element contains the game title, appears on the topmost of the page. The title contain the characters "⬢HAPPY⬢⬢HEX⬢" with 12 different colors,
  which will be the exact same color as the [colors](#Normal-Color-Theme) used for piece and block generation. 
  The sequencing of the colors is randomized every time the page is refreshed.

- <b>Username Requirements</b>  
  
  The username requirements displays in monospaced font about the requirements for allowed usernames. The requirements read as follows:  

  ```  
  1. Must be between 3 - 24 characters long, inclusive.  
  2. Only contain 1-9, A-Z, a-z, dash, underline or space.  
  3. Must contain at least one letter.  
  4. Special Symbols such as #%$ are not allowed.  
  5. Cannot start or end with dash, underline or spaces.  
  6. Cannot be one of the keywords used by the game system.  
  ```  

  The game keywords can be found in the [Prohibited-Usernames](#Prohibited-Usernames) section of this document, but note that [Easter Eggs](#Easter-Eggs)
  may be connected to some of the keywords that is "prohibited". Generally speaking, however, keywords are considered to be invalid.

- <b>Username Entering Field</b>  
  
  This is an interactive field for the player to enter their username.  

  The length and composition of the username must follow the username rules described above of the username to be considered valid. This field provides
  keyboard interaction, as the players may use their keyboard to enter their desired usernames.  
  
  When no player is logged in, this field will display a prompt "ENTER THE USERNAME HERE!". Deleting this prompt to enter the username.
  On the other hand, when a player is logged in, this field will display the username of the logged in player in another color, as defined by the current
  theme. To log out, the player may enter "Out", "Logout", or "Log out".

  Whenever an action, such as clicking the Confirm Button, is performed, the field will display a short prompt for a limited period of time, then switch
  back to normal. If the player attempts to enter a name with the incorrect format, the prompt will usually be red, stating "INCORRECT NAMING FORMAT!".
  If the player attempts to one of the [keywords](#Prohibited-Usernames), the prompt will usually be red, stating "GAME KEYWORD PROHIBITED!". If the login
  was successful, the field will display the short prompt, usually green, "SUCCESSFUL PLAYER LOGIN!" and then display the logged in player's username.  

- <b>Confirm Button</b>  

  This is a button similar to all the other redirection buttons, but appears usually in green and contains the text "ENTER".  
  
  When clicked, the launcher will check the current text inside the Username Entering Field. If the name is valid and matches the current logged-in user,
  nothing will happen. If the name is valid and representing a new login, the field will display "SUCCESSFUL PLAYER LOGIN!". If the name is one of the
  [keywords](#Prohibited-Usernames), the field will display a red prompt stating "GAME KEYWORD PROHIBITED!". If the name does not meet the rules and
  is not valid, the field will display a red prompt stating "INCORRECT NAMING FORMAT!".  

  Player must click on this button to confirm their newly entered username when attempting login. Otherwise, nothing will happen.  

- <b>Quit Button</b>  
  
  This is a button similar to all the other redirection buttons, but appears usually in red and contains the text "QUIT".  
  When clicked, this button redirects to the [Main Page](Main-Page) unconditionally. Your inputs in the Username Entering Field may not be saved.  

- <b>Game Credits</b>  

  A section detailing the credit and copyright information of the HappyHex game, identical to that in [Main Page](#Main-Page).  

#### Settings Page

The settings page allows the player to control over key game parameters, enhancing the game functionality while make HappyHex more fun.  

The page contains the following elements:  

- <b>Game Title</b>  
  
  This is the exact same element as that in [Main Page](Main-Page), with the only difference being the elimination of the space above this title.
  The element contains the game title, appears on the topmost of the page. The title contain the characters "⬢HAPPY⬢⬢HEX⬢" with 12 different colors,
  which will be the exact same color as the [colors](#Normal-Color-Theme) used for piece and block generation. 
  The sequencing of the colors is randomized every time the page is refreshed.

- <b>Settings Title</b>  
  
  Under the Game Title, there is a text "Settings", which indicates that this is the settings page.  

- <b>Easy Mode Settings</b>  
  
  Turned `OFF` by default. Turning the switch `ON` enables Easy Mode, while turning it `OFF` disables Easy Mode.  
  
  This switch controls whether to enables "Easy Mode," which modifies [piece generation](#Piece-Generation) to make the game significantly easier.  
  
  Easy mode is compatible with all special game modes in the [Easter Eggs](#Easter-Eggs).  

- <b>Restart Games Settings</b>  
  
  Turned `OFF` by default. Turning the switch `ON` enables functionality to restart unfinished games, while turning it `OFF` disables it.  

  This setting only applies to the [Start Button](#Main-Page) in the main page. If you want to restart a specific game or start a new game, you may always
  access the [Resume Page](#Resume-Page).  
  
  This switch controls whether to restart an unfinished game when it is possible to do so. When disabled, ever game will be new no matter how many previously
  unfinished games the player have. When enabled, the game will try to find a previously unfinished game of the player in this particular setting, and if it
  cannot find such games, it will start a new game.
  
  Disabling restart game will not in any ways affect the data logging of games, in `logs.json`, `.hyphex.json`, and binary `.hyphex` data files.

- <b>Use Autoplay Settings</b>

  Turned `OFF` by default. Turning the switch `ON` enables Autoplay, while turning it `OFF` disables Autoplay.

  This switch controls whether to use Autoplay, which starts a python script to place pieces for the player at relatively optimal positions determined by multiple factors.

  Autoplay can be enabled in all piece generation and game mode settings.

- <b>Game Board Size Setting</b>  

> [!NOTE]  
> Since first [Official Release](https://github.com/williamwutq/game_HappyHex/releases/v1.0.0) (v1.0.0), unspecified game mode creation is no longer supported.  

  Players can choose between **Small**, **Medium**, and **Large** game board sizes for their next game. The default size is Small, and selecting
  one option will automatically deselect the others. 

  - The Small size will result in a game board radius of 5 and piece queue size of 3.  
  - The Medium size will result in a game board radius of 8 and piece queue size of 5.  
  - The Large size will result in a game board radius of 11 and piece queue size of 7.  

  It is recommended to use the small size for beginners and players who do not want to spend a lot of time on a single game session. Large size often prolong
  game session to over an hour, if played well at normal pace.

- <b>Quit Button</b>  
  
  This is a button similar to all the other redirection buttons, but appears usually in red and contains the text "QUIT".  
  When clicked, this button redirects to the [Main Page](#Main-Page) unconditionally. All changes in settings will be saved.  

- <b>Game Credits</b>  

  A section detailing the credit and copyright information of the HappyHex game, identical to that in [Main Page](#Main-Page).  

#### Themes Page

- <b>Game Title</b>  
  
  This is the exact same element as that in [Main Page](#Main-Page), with the only difference being the elimination of the space above this title.
  The element contains the game title, appears on the topmost of the page. The title contain the characters "⬢HAPPY⬢⬢HEX⬢" with 12 different colors,
  which will be the exact same color as the [colors](#Normal-Color-Theme) used for piece and block generation. 
  The sequencing of the colors is randomized every time the page is refreshed.

- <b>Theme Setting</b>  
  
  For special themes, see in the [Easter Eggs](#Easter-Eggs) section. For detailed description on the themes, see the
  [dedicated theme](Theme,-Color-and-Font) section.

  Players can choose between **Normal**, **Dark**, and **White** game themes for displaying. These themes alter the color of all game graphics and are 
  effective immediately. When a switch is flicked, the user can notice the change in the coloring of the theme page. All changes in the theme in theme
  settings will be carried to all other pages, until the theme settings is modified again. The default theme is Normal.  

  The theme settings has nothing to do with the game settings and will not affect game logic at all.  

  The changes of theme maybe override by special dates dictated by [Special Themes](#Special-Themes). When a special theme is active, the buttons will still
  work but the theme will remain the same.  

  There's no superior theme among the three themes. Personally I prefer the Dark Theme, but the most classic one is the default theme, namely the Normal Theme.  

- <b>Quit Button</b>  
  
  This is a button similar to all the other redirection buttons, but appears usually in red and contains the text "QUIT".  
  When clicked, this button redirects to the [Main Page](#Main-Page) unconditionally. All changes in theme settings will be saved.  

- <b>Game Credits</b>  

  A section detailing the credit and copyright information of the HappyHex game, identical to that in [Main Page](#Main-Page).  

#### Resume Page

This page helps the player to resume a previously stated but unfinished game stored in the `data` directory. For a game to show up on this page, the following
requirements all be meet:  

1. A valid `.hpyhex` game log file, in colored format, must exist in the `data` directory. Validity of game file means that they are not corrupted and
   can be read by the HappyHex program.  
2. The game is unfinished. This means in the binary `.hpyhex` files need to have the key `complete` set to `false`.  
3. The username and user ID recorded in the binary `.hpyhex` game log files matched the currently [logged-in](#Login-Page) user.
   In addition, the user logged in, as when user is not logged in, games will not be able to resumed or recorded.  
4. The game queue and board sizes matches the sizes currently selected in the [settings](#settings-page). If the settings do not match, no game may show up.  

Whenever a game shows up, the panel will have a section with round corners displaying essential information about the game. This information does not include
username or id because whenever resuming game is possible, the user must be logged in. This information, however, includes the game's file name, which is a
randomly generated 16 hexadecimal character hash (64 bit). The file name may include its location, namely `data/`, referring to the data directory. In addition,
the field also displays the game's current score and turn, indicating the game's progress. Players can judge based on the score and turn to decide which game
to resume and play.  

Each section has a green button, show as green text "RESUME". Click on the button to the specific game. If the game cannot be resumed due to undetected data
corruption in the game log, or when game log reading encountered an error, the application will automatically start a new game for the player. If the player
does not want to play the new game, they may quit out of the game.  

If there are no sections indicating existence of unfinished games, the field will be blank. If there are more sections than what can be displayed in the field
at the same time, a scroll bar will show up, enable scrolling of the field. Move the scroll bar vertically to view more game entries and click on their resume
buttons to resume those games.  

On the bottom of the page, there are three buttons:  

- <b>Quit Button</b>

  This is a button similar to all the other redirection buttons, appears in red and contains the text "QUIT".  
  When clicked, this button redirects to the [Main Page](#Main-Page) unconditionally.  

- <b>Start Button</b>

  This button functions exactly like the [start button](#start-game) of the [Main Page](#Main-Page)m appears in black and contains the text "RESUME".  
  This will start a new game or resume an unfinished game based on the game setting.  

- <b>New Button</b>

  This is a button to start an entirely new game, appears in green and contains the text "START". This button will not resume any game.  

#### Start Game

This will direct the page out of the launcher and initialize the game components in a new game page.  

Usually, the game page have separate colors and fonts as the launcher, and is made up from two panels instead of a single panel. This provides significant
efficiency to the game execution and make the game play process much smoother.  

The game page includes components such as:  
- **Hexagonal Game Board**: The core game engine using hexagonal coordinates, where players can place blocks.  
- **Piece Queue**: A set of randomly generated pieces with predefined shapes and colors. The difficulty level affects piece generation.  
- **Game Information**: The current game turn and game score are displayed on the two top corners.  
- **Player Information**: The current player name is displayed in the down right corner.  
- **Quit Button**: Clicking on the quit button will enable you to quit the game. Current progress will be logged into `log.json` if a game has started.  

If autoplay is turned on in settings, the game will call a python script in the background and initiate autoplay sequence, which will place a piece at
relatively optimal position every 1 second. Manually placing pieces is also available during autoplay, but that will interrupt the autoplay. IF the quit
button is clicked during autoplay or the python script stopped running, the autoplay may end. To restart the autoplay, quit the current game and resume
it in the resume panel.  

### Theme, Color and Font
This describes the default themes of the game, including coloring and fonts of texts. For special themes, see the [Special Themes](#Special-Themes)
section.  

#### Fonts
The various sections of the game GUI use the following Fonts, they are stored as static variables in
`GUI.GameEssentials` and `Launcher.LaunchEssentials`. They may be modified by [Easter eggs](#Easter-Eggs).

- Fonts for author information and copyright on the button of launcher pages are always "Helvetica".  
- Font for the mark "W.W" is always "Georgia".  
- Font for the title, styled "Happy Hex", is "Courier".  
- Font for the version string beneath the title is "Comic Sans MS".  
- Font for the buttons to switch pages in the launcher or to start the game is "Times New Roman".  
- Fonts for hints, displayed texts, username rules, etc. that require a mono font is "Courier".
- Fonts for the sliding ON-OFF switch in settings and theme pages are "Helvetica".
- Fonts for quit button, user information, game turns and score inside the game page is "Courier".

#### Normal Color Theme
This theme is the default game theme, or can be activated in the launcher page themes, when the `Normal` switch is set to `ON`.  

- Color for the background of any launcher page is `rgb(241, 243, 213)`.
- Color for the background of the strip behind the title, styled "Happy Hex", is `rgb(219, 223, 151)`.
- Colors for author information and copyright on the button of launcher pages are `rgb(0, 73, 54)`.
- Colors for the mark "W.W" is `rgb(0, 0, 0)`.
- Colors for game version string beneath the title is `rgb(0, 0, 0)`.
- Colors for game hints string beneath the title is `rgb(128, 128, 128)`.
- Color for the background of the log-in field in the log-in page in launcher is `rgb(247, 248, 238)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a logged in player name, is `rgb(136, 136, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a prompt, is `rgb(0, 136, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a warning or an error, is `rgb(136, 0, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a special message, is `rgb(0, 136, 136)`.
- Colors for the background of the buttons to switch pages in the launcher are `rgb(0, 0, 0)`.
- Colors for the background of the buttons to quit to main page from another launcher page in the launcher are `rgb(255, 0, 0)`.
- Color for the background of the button to confirm the information player inputted in the login field in the launcher is `rgb(0, 223, 39)`.
- Colors for the foreground of the button to start a new game when an existing game [ends](#Game-Ending) are `rgb(0, 193, 211)`.
- Colors for the empty portion of the sliding ON-OFF switch in settings and theme pages are `rgb(255, 255, 255)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `ON`, are `rgb(0, 255, 0)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `OFF`, are `rgb(255, 0, 0)`.
- Color for the background of the game field in game page is `rgb(213, 236, 230)`.
- Color for the background of the piece queue in game page is `rgb(113, 129, 122)`.
- Color for the background of the game over page is `rgb(163, 188, 180)`.
- Color for texts displaying game username, turns, and score information, in game page, is `rgb(5, 34, 24)`.
- Color for the [quit](#Game-Quitting) button in game page, is `rgb(136, 7, 7)`.
- Colors for blocks in the game field, when they are not filled, are `rgb(0, 0, 0)`.
- Color for the block selected in the piece selected in the piece queue, is `rgb(168, 213, 201)`.
- The 12 Colors available for blocks, are, 
`rgb(0, 0, 240)`, `rgb(0, 100, 190)`, `rgb(0, 180, 180)`, `rgb(0, 180, 120)`,
`rgb(0, 210, 0)`, `rgb(100, 180, 0)`, `rgb(180, 180, 0)`, `rgb(200, 90, 0)`,
`rgb(210, 0, 0)`, `rgb(200, 0, 120)`, `rgb(180, 0, 180)`, and `rgb(100, 0, 200)`.

#### Dark Theme
This theme can be activated in the launcher page themes, when the `Dark` switch is set to `ON`.  
- Color for the background of any launcher page is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the background of the strip behind the title, styled "Happy Hex", is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Colors for author information and copyright on the button of launcher pages are `rgb(158, 157, 232)`.
- Colors for the mark "W.W" is `rgb(204, 204, 204)`.
- Colors for game version string beneath the title is `rgb(204, 204, 204)`.
- Colors for game hints string beneath the title is `rgb(128, 128, 128)`.
- Color for the background of the log-in field in the log-in page in launcher is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a logged in player name, is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a prompt, is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a warning or an error, is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a special message, is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Colors for the background of the buttons to switch pages in the launcher are `rgb(0, 0, 0)`.
- Colors for the background of the buttons to quit to main page from another launcher page in the launcher are `rgb(255, 0, 0)`.
- Color for the background of the button to confirm the information player inputted in the login field in the launcher is `rgb(0, 223, 39)`.
- Colors for the foreground of the button to start a new game when an existing game [ends](#Game-Ending) are the inverted colors of the [Normal Theme](#Normal-Color-Theme).
- Colors for the empty portion of the sliding ON-OFF switch in settings and theme pages are `rgb(22, 22, 22)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `ON`, are `rgb(0, 255, 0)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `OFF`, are `rgb(255, 0, 0)`.
- Color for the background of the game field in game page is `rgb(23, 23, 42)`.
- Color for the background of the piece queue in game page is `rgb(63, 61, 112)`.
- Color for the background of the game over page is `rgb(63, 61, 112)`.
- Color for texts displaying game username, turns, and score information, in game page, is the inverted color of the [Normal Theme](#Normal-Color-Theme).
- Color for the [quit](#Game-Quitting) button in game page, is `rgb(255, 144, 110)`.
- Colors for blocks in the game field, when they are not filled, are `rgb(22, 22, 22)`.
- Color for the block selected in the piece selected in the piece queue, is `rgb(36, 33, 101)`.
- The 12 Colors available for blocks are the inverted colors of the [Normal Theme](#Normal-Color-Theme).

#### White Theme
This theme can be activated in the launcher page themes, when the `White` switch is set to `ON`.  

- Color for the background of any launcher page is `rgb(255, 255, 255)`.
- Color for the background of the strip behind the title, styled "Happy Hex", is `rgb(255, 255, 255)`.
- Colors for author information and copyright on the button of launcher pages are `rgb(0, 73, 54)`.
- Colors for the mark "W.W" is `rgb(0, 0, 0)`.
- Colors for game version string beneath the title is `rgb(0, 0, 0)`.
- Colors for game hints string beneath the title is `rgb(192, 192, 192)`.
- Color for the background of the log-in field in the log-in page in launcher is `rgb(255, 255, 255)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a logged in player name, is `rgb(136, 136, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a prompt, is `rgb(0, 136, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a warning or an error, is `rgb(136, 0, 0)`.
- Color for the foreground of the log-in field in the log-in page in launcher, if the text is a special message, is `rgb(0, 136, 136)`.
- Colors for the background of the buttons to switch pages in the launcher are `rgb(64, 64, 64)`.
- Colors for the background of the buttons to quit to main page from another launcher page in the launcher are `rgb(64, 64, 64)`.
- Color for the background of the button to confirm the information player inputted in the login field in the launcher is `rgb(64, 64, 64)`.
- Colors for the foreground of the button to start a new game when an existing game [ends](#Game-Ending) are `rgb(64, 64, 64)`.
- Colors for the empty portion of the sliding ON-OFF switch in settings and theme pages are `rgb(64, 64, 64)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `ON`, are `rgb(0, 0, 0)`.
- Colors for the foreground of the sliding ON-OFF switch in settings and theme pages, when `OFF`, are `rgb(0, 0, 0)`.
- Color for the background of the game field in game page is `rgb(255, 255, 255)`.
- Color for the background of the piece queue in game page is `rgb(255, 255, 255)`.
- Color for the background of the game over page is `rgb(255, 255, 255)`.
- Color for texts displaying game username, turns, and score information, in game page, is `rgb(64, 64, 64)`.
- Color for the [quit](#Game-Quitting) button in game page, is `rgb(64, 64, 64)`.
- Colors for blocks in the game field, when they are not filled, are `rgb(0, 0, 0)`.
- Color for the block selected in the piece selected in the piece queue, is `rgb(192, 192, 192)`.
- The 12 Colors available for blocks, are the same colors as they are in the [Normal Theme](#Normal-Color-Theme)

#### Screen Shots
These are screenshots of various portions of the game as of [patch 1.1.3](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.1.3).
The colors and fonts of the three basic themes have not changed since, but a resume button similar to all other buttons has been added to the main page.  
- [Normal Color Theme](#Normal-Color-Theme):<br/>
  <img width="275" alt="Normal Theme Main Page" src="https://github.com/user-attachments/assets/febf09bb-7fd2-4824-b8c3-d65e578c41ad" />
  <img width="275" alt="Normal Theme Login Page" src="https://github.com/user-attachments/assets/71475819-ab58-45d8-bba6-fd2bb540480f" />
  <img width="275" alt="Normal Theme Game Board" src="https://github.com/user-attachments/assets/57bf32e7-c075-45be-ba20-82f9f00c1c81" />
- [Dark Theme](#Dark-Theme):<br/>
  <img width="275" alt="Dark Theme Main Page" src="https://github.com/user-attachments/assets/aec476f1-c8b6-4098-87df-44efd57d13f6" />
  <img width="275" alt="Dark Theme Login Page" src="https://github.com/user-attachments/assets/bd17ed67-15b8-4b05-9a65-a91084d9bae5" />
  <img width="275" alt="Dark Theme Game Board" src="https://github.com/user-attachments/assets/5b95363e-5a29-4102-8be7-23b19fe1bf79" />
- [White Theme](#White-Theme):<br/>
  <img width="275" alt="White Theme Main Page" src="https://github.com/user-attachments/assets/b59f8a39-24b2-43eb-bfc0-7badf9a549d7" />
  <img width="275" alt="White Theme Login Page" src="https://github.com/user-attachments/assets/128afaa6-6f8c-44a3-9764-c180a4257286" />
  <img width="275" alt="White Theme Game Board" src="https://github.com/user-attachments/assets/c3b3ba4f-b77a-46ed-abd8-860d73af28b9" />

### Game Rules
These game rules are automatically enforced by game logic stored in the game code. Players do not need to pay special attentions to them, but it is a good idea
to understand why the game behaves the way it does. [Easter Eggs](#Easter-Eggs) can sometimes override game rules.
#### Game Start
- If the player is logged in, the game will start with the current player information. Otherwise, it will enter guest mode with player being "Guest".
- The game will start with the current difficult setting and size setting, both of which can be adjusted in settings page.
- If the player is logged in and an unfinished game of the same size is detected to exist, the unfinished game will resume. The color of the board and queue
  of the unfinished game will be the exact same according to the current theme.
- If no valid unfinished game is detected, a new empty game will start with empty game board and randomized game queue according to
  [piece generation](#Piece-Generation) logic.
#### Piece Generation
- Pieces are generated by an underlying piece generator that is mostly based on random.
- In Normal Mode (Easy Mode is turned `OFF` in Settings), the generator generates almost equal frequency of all pieces. The generator will never
generate the single-block piece but may generate the full 7-block piece.
- In Easy Mode (Easy Mode is turned `ON` in Settings), the generator favors small and more placeable pieces. The generator will never generate full
7-block piece but may generate the single block piece.
- Pieces will be generated according to logic in the queue whenever another piece is dequeued by [placement](#Piece-Placement).
- The color of the piece will be randomly generated according to the current theme. All blocks in a piece will have the exact same color.
- There are 12 possible colors for any piece or block according to the current theme. The piece or block may also be gray or in another default color
  if an old version unfinished game is loaded.
- Pieces cannot contain unoccupied blocks.
- The game queue is always filled.
#### Piece Placement
- Player must select pieces in the game piece queue. They may not select any other piece.
- Player can only select one piece at a time.
- Player must click on a block on the selected piece to complete selection.
- Pieces can <b>NOT</b> be rotated.
- Pieces can <b>NOT</b> change in size or color.
- Player can only place pieces when a piece is selected a valid location is clicked. This means the placed piece will not overlap with existing filled blocks,
  extend out of the boundaries of the game board, or occupy a location of a previously placed piece.
- Invalid placement will result in the deselection of piece and no change to the piece queue or game board.
- Once placed, piece will be dequeued from the game queue, which will result in another new piece be generated according to logic.
- Placing a piece of `n` blocks increments the game score by `n`, and game turn by `1`.
#### Block Elimination
- When a row in any of the three directions are filled with blocks after a placement, that row will be eliminated. This means all blocks in that row will
  be set to empty.
- There is no particular sequence of elimination, which means two intersection row can be eliminated at the same time.
- Elimination is automatic, activated after any [piece placement](#Piece-Placement).
- Not all piece placements will result in block elimination.
- There is a short time delay between piece placement and elimination, as designed.
- Eliminate a total of `n` blocks increments the game score by `5 * n`. If a piece is overlapped between two rows, it counts twice.
#### Game Ending
- When a piece is placed, queue updated, and elimination checked such that no more piece can be placed, the game ends.
- The score and turns of the game will be recorded.
- The game will be logged in a colored binary `.hpyhex` file inside the folder `data`.
- The terminated game will be connected with the user information.
#### Game Quitting
- The player may quit the game by clicking on the quit button inside the game at any time.
- The score and turns of the game will be recorded.
- The game will be logged in a colored binary `.hpyhex` file  inside the folder `data`.
- The unfinished game will be connected with the user information.
- If the player logs in again with the identical information and game setting, and the game log exists in the correct location, the unfinished game
  could be revived with the unfinished board and game queue. The color of the board and queue of the unfinished game will be the exact same according
  to the current theme, because colors will be stored in color indexes since version 1.3.0. If you have are playing a safe on a version before that,
  colors maybe gray or random, depending on the implementation.
- The player may quit the game by quit the application.

### Easter Eggs  
#### Prohibited Usernames  
The following usernames are prohibited from inputting into the log-in field in the log-in page. This includes the capitalized and lowercase
versions of all words in the following list.
If the player attempts to log in with those names, the message "GAME KEYWORD PROHIBITED!" will be displayed.  

However, under special circumstances (as activating an Easter Egg), they may do something else. In addition, "out", "log out", and "logout" are
functional keywords the logs the current player out.   
<details>
<summary>Keyword List</summary>
- player<br/>
- default<br/>
- dev<br/>
- guest<br/>
- host<br/>
- user<br/>
- driver<br/>
- auto<br/>
- autoplay<br/>
- execute<br/>
- god<br/>
- evil<br/>
- devil<br/>
- hard<br/>
- easy<br/>
- harmony<br/>
- hash<br/>
- code<br/>
- game<br/>
- gamer<br/>
- happyhex<br/>
- hex<br/>
- name<br/>
- club<br/>
- event<br/>
- out<br/>
- logout<br/>
- log out<br/>
</details>

#### Special Game Modes  
Special game modes alter game mechanics, and they would not be affected by setting or change the setting of the game. Currently, they are for you to discover
and unlock. There are no prerequisites for activation.  

They are normally not activated as it is for every other Easter Eggs, but it is also possible to switch to normal after activation.
1. <b>God Mode</b>  
   A special game mode that alters the game piece generation. This mode effectively check the current game board statues and only generate pieces
   that will be placeable. This ensures that you can basically never end or lose the game, except by force quitting.
   
   This mode is packaged inside `special.Logic` and contained in `special.FeatureFactory` with the class tag `hex.Piece` and hint tag `God`.  

   <details>
     <summary>Spoiler: Activation</summary>  
     &emsp;To activate, go to settings and turn on easy mode. Then go to log in page and try log in as "God". This will display the message
     "THE DIVINE INTERVENTION!".<br/>
     &emsp;To quit out of special modes, regardless of the current game setting, type "Normal" into the login field to switch back to normal.
     This will not change the game setting otherwise.
   </details>

2. <b>Hard Mode</b>  
   A special game mode that alters the game piece generation. This mode effectively attempts to generate the most difficult pieces to play. It usually favors 
   pieces with 4 or more block but also accept pieces that are currently hard to play in the game field. Usually, if you can survive 25 moves in Hard Mode, you
   will have proven to have real skills in <b>HappyHex</b>.
   
   This mode is packaged inside `special.Logic` and contained in `special.FeatureFactory` with the class tag `hex.Piece` and hint tag `Hard`.  

   <details>
     <summary>Spoiler: Activation</summary>  
     &emsp;Method 1:<br/>
     &emsp;&emsp;Go to log in page and try log in as "Hard" or "Evil". This will display the message "RELEASING THE HARD MODE!".<br/>
     &emsp;Method 2:<br/>
     &emsp;&emsp;Go to log in page and try log in as "Devil". This will display the message "PLACE UNBREAKABLE CURSE!".<br/>
     &emsp;Quit:<br/>
     &emsp;&emsp;To quit out of special modes, regardless of the current game setting, type "Normal" into the login field to switch back to normal.
     This will not change the game setting otherwise.
   </details>

#### Special Themes  
These are themes that could only be activated during certain days. They will <b>always</b> activate regardless of the theme chosen or the game settings.
When these themes are in activation, the theme switch button will still work, but they will not change the theme.  

These themes can change the color of every component in the game and the launcher, and may also change the font of components.  
1. <b>Grayscale</b>  
   This theme turns everything on the game screen into grayscale. This theme is always activated on September 11th and has a 6% chance of activating on
   a normal day.
   
   This theme is packaged inside `special.Styles` and contained in `special.FeatureFactory` with the class tag `java.awt.Color`.
2. <b>Lovely Theme</b> (`FilledWithLove`)  
   This is a Valentine's Day special theme. It turns the launcher and game board into primarily pink and purple color and also modify other colors
   in other components. This theme also replaces the animation that is placed after piece elimination with a disappearing pink heart. It is always activated
   only on Valentine's Day.

   This theme is packaged inside `special.Valentine` and contained in `special.FeatureFactory` with the class tag `java.awt.Color` and
   `GUI.animation.Animation`.
3. <b>Spooky Theme</b>  
   This is a Halloween theme. It turns the launcher and game board into primarily orange and red, with other colors populating other areas of the graphics.
   This theme also replaces the font of the title, buttons, and display messages with spooky fonts to enhance effects of the holiday. It is always activated
   only on Halloween.

   This theme is packaged inside `special.Halloween` and contained in `special.FeatureFactory` with the class tag `java.awt.Color` and
   `java.awt.Font`.
4. <b>Independence Day Theme</b>  
   Active on Independence Day. This theme contains two themes, one white and one dark, which can be toggled through the use of the
   [theme settings](#Themes-Page). These two themes populate every pixel of the game with the colors of the flag, namely white, blue, and red.
   Game pieces colors will be reduced to six colors, three blue and three red. It is automatically activated on July Fourth.

   This theme is packaged inside `special.Independence` and contained in `special.FeatureFactory` with the class tag `java.awt.Color`.  
5. <b>GratefulHarvest Theme</b>  
   This is a Thanksgiving theme. It turns the launcher and game board into primarily orange, with other colors populating other areas of the graphics.
   The game pieces use colors that mimic the colors usually seen on the dinner table on Thanksgiving dinner and those during harvest, invoking a sense of grace.
   This theme also replaces the font of the title, buttons, and display messages with fonts to enhance effects of the holiday. It is always activated
   only on Thanksgiving.

   This theme is packaged inside `special.Thanksgiving` and contained in `special.FeatureFactory` with the class tag `java.awt.Color` and
   `java.awt.Font`. In the package, a special date class is defined to determine whether a day is Thanksgiving.  
6. <b>Snowy!!!</b>  
   This is a really special theme. This feature is to celebrate my friend Matthew Ye, who is the first person to reach 40 turns in Devil Mode,
   which is part of the [Easter Eggs](#easter-eggs-) and can be activated by typing keywords into the [Login Page](#login-page). As he really enjoys at that
   time the version 1.3.2 of HappyHex, I revealed to him the ways to access hidden modes and my friend found Devil Mode especially interesting.
   For that, I promise him that I will add a new theme designed by him if he can reach a record score in that mode, and so he did. All colors in this special
   theme are picked by my friend and implemented by me as promised.

   This theme is packaged inside `special.Snowy` and contained in `special.FeatureFactory` with the class tag `java.awt.Color`.

### Auto Play
The autoplay feature will be added in the future. With a click of a button, the player will be able to enjoy hands-off automatic piece placement in the 
"HappyHex" game. There would a button starting and stopping the automatic playing of the game. Meanwhile, the player may also place pieces manually, and
Their intervention will pause the autoplay until the player starts it again. Separately, one button will enable the player to see engine suggestions of
piece placements.  
The first game engine will be built with random moves, and will not support suggestions. The more advanced version of Auto Play will feature methods to score
positions of placements and suggests placements accordingly. As for now, code in game piece and game engine are gradually build to support position scoring
and future machine learning. The final result of Auto Play will incorporate [machine learning](#develop--machine-learning), which will enable better
game results and even personalized game play.  

## Development
### Contribution
This is mostly my project under my design, and I am managing all branches.  

Currently (Before Release 1.4), I have no plans for collaboration with others on this project. However, if you wish to contribute on this small game, please
feel free to contact me.  

If you wish to suggest a feature, create an [issue](https://github.com/williamwutq/game_HappyHex/issues) and add appropriate tags. Please add the
[enhancement](https://github.com/williamwutq/game_HappyHex/issues?q=is%3Aissue%20state%3Aopen%20label%3Aenhancement) label to your issue. Please also
note that any suggestions to change architecture, coding language, and most suggestions to graphics improvements will be dismissed.  

Contribution for Game Viewer please see [Game Viewer README](viewer/README.md#contribution).  

### Developed Sections
These are parts of the game that are considered fully developed and refined. Unless there is a bug no further updates will be made.
1. <a name="develop--game-mechanics"><b>Game Mechanics</b></a>  
The underling data structure of the game, packaged in `hex`, is extensively tested and used from pre-release version 0.4 to the most recent release.
Although method may be added for future purposes, modification in implementation or structuring are unlikely.<br/>
2. <a name="develop--game-graphics"><b>Game Graphics</b></a>  
Not to be confused with [Launcher Graphics](#develop--launcher-graphics), the game graphics refers to the page where the player actually plays the game.
This portion include a hexagonal grid representing the game field (or engine), a piece queue for potential selection of pieces, and game information display.
This page had its own background colors, which are separated from that of the launcher. However, it is dependent on the information provided by the launcher
and cannot run on its own. All code for this section is currently under the `GUI` package.<br/>
3. <a name="develop--game-animations"><b>Graphics Animations</b></a>  
The robust linear animation handler [animation](https://github.com/williamwutq/game_HappyHex/blob/main/GUI/animation/Animation.java) is tested and documented.
The handler is designed for simple temporary effects such as fading, expanding, or progress animations and implemented use `java.awt` package and `java.awt.event`.
It is lightweight and not a Swing component. You are welcome to use it for your own project. Follow the javadoc in the source code for more details.<br/>
4. <a name="develop--special-modes"><b>Special Modes</b></a>  
Easter eggs modifying something fundamental in the game, whether it is piece generation, scoring and elimination rules, or others. Current implemented special
modes include `GodMode` and `HardMode`, both of which change the difficulty of the game by change the piece generation logic. These game modes use the same 
injection interface as [SpecialThemes](#develop--special-themes), and are packaged in the same `special` package. Dedicated branch for special modes is `special`.<br/>  
5. <a name="develop--game-viewer"><b>Game Viewer</b></a>  
A separate runnable program that may read `.hpyhex` files and display them in a hexagonal grid without color.
This program can be grayscale and simple to run with minimal dependencies. This viewer can read binary files or maybe json files.  
The Game Viewer feature on screen keyboard inputs to select which file to view, buttons to increment and decrement the moves recorded in the game,
buttons to animate the game process forward or backward, slider for adjusting animation speed. It displays real time game scores, turn, and state 
associated with the moves. The game viewer is developed and should be a separate concern. See [Game Viewer README](viewer/README.md) for more.<br/>
6. <a name="develop--launcher-graphics"><b>Launcher Graphics</b></a>   
This section concerns about the launcher of the game, namely the window that shows up when you open the application. The current launcher consists of a main
page, with buttons redirecting to pages for player log in, settings, graphics themes, and, of course, the game itself. Branches such as `gui` are dedicated to
the improvements of user experience. The branch `fancy`, which is no longer in use, was typically used to add fancy graphics.<br/>

### Directions of Development
The developing process are mainly separated into the following parts:
1. <a name="develop--game-play"><b>Game Play</b></a>  
How the game is played. This including scoring systems, awards, resetting, fetching and displaying previous game scores, etc. Currently, the most recent score
and turns, the highest score and turns, and the average score and turns can be seen when the game is over.<br/><br/>
Future considerations in this include setting passwords for user accounts, adding an achievement and purchasable system that would be linked with graphic themes, 
and providing a page for more detailed user game analysis.  
2. <a name="develop--special-themes"><b>Special Themes</b></a>  
These are themes that could only be activated during certain days, such as Halloween, Independence Day, or Christmas. Some of them also serve as memorial to 
tragic events such as the September 11th attacks. These themes use the same injection interface as [SpecialModes](#develop--special-modes), and are packaged
in the same `special` package. Dedicated branches would be `special` and sometimes `fancy`. Only Christmas theme is to be developed.<br/>  
3. <a name="develop--machine-learning"><b>Machine Learning</b></a>  
A more precise way of describing this direction would be auto-game-play. This means a machine would be build to play the game while the player sit there and
watch. This autoplay would be initially enabled through scoring algorithms and later through trained artificial intelligence operated in Python. This is a feature
of the future but current architecture is being designed around it. At the same time, scoring and reward functions are gradually coming online.<br/> 

### Future Timeline
> This timeline is subject to frequent change
- Latest Release: [1.3.4](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.3.4)
- Version 1.4
  - Graphics improvements
  - Autoplay logic improvements
  - Remove autoplay activation from settings
  - Add pause and run autoplay buttons to the game graphics
  - Add rare chance shinning font ("HappyHex" font animated by `animation` class switching between the colors of the rainbow)
  - Add Christmas super special (Christmas colors, gift blocks, decals)
- Version 1.5
  - Potentially add 3 way selection button

- Version 2  

  > Version 2 will be fundamentally different from all of Version 1 and be incompatible.
  - Add user achievements
  - Add completed achievements and unlockable system
  - Make themes and shinning font unlock-able
  - Use machine learning to train AI for advanced autoplay

## Code packages
The packages in the source code, their dependencies, and their functions.
### Mechanics
package `hex`  
<b>Dependencies</b>:  
None  
<b>Function</b>:  
The backbone of the game. It provides classes and interfaces for managing a hexagonal grid system, including coordinate calculations,
game engine operations, and game piece operations.  

### Game Data
package `game`  
<b>Dependencies</b>:  
`hex` `special`, and `Launcher`  
<b>Function</b>:  
Handles more complex data of the game build on `hex`. This includes game piece queue operations, game piece generations, and game engine simulations.

### Game Data Logging
package `hexio`  
<b>Dependencies</b>:  
None  
<b>Function</b>:  
Provides utility methods for converting hexagonal game components such as Hex, Block, Piece, HexEngine, and game moves to and from
colored and uncolored binary representations. It helps developers to save game states to binary `.hpyhex` files, read them back,
and manage game data efficiently. It is not dependent on any other packages and use internal versioning separated from `Launcher`.  

### Logging
package `io`  
<b>Dependencies</b>:  
`javax.json`  
<b>Function</b>:  
Provide data storage structures, such as game information, player information, game time, and logging functionalities. This package enables reading, 
writing, and converting game metadata, including player information, game sessions, and configuration presets into JSON format for persistent storage 
or transmission. It is not dependent on any other packages.

### Graphics (GUI)
package `GUI`  
<b>Dependencies</b>:  
`hex`, `hexio`, `Launcher`, `special`, `comm` and `javax.swing`  
<b>Function</b>:  
Provides game page graphics through Java Swing. It also records critical game information and serves fundamental game logic to make the game function.
This includes functions for buttons, color indication generations, interaction with the launcher, timer for elimination, and more.  
This graphics component support dynamic resizing.  

### Launcher
package `Launcher`  
<b>Dependencies</b>:  
`io`, `hexio`, `GUI`, `special`, and `javax.swing`  
<b>Function</b>:  
Provides game launcher graphics through Java Swing. This includes the main page, the settings page, the login page, the game over page, the resume page, and the themes page.
It also provides links to set up evironment and start or resume a [game](#Graphics-(GUI)). In addition, it calls to the internal game logger to read local data.
This includes logging player scores and turns, loading previous unfinished games, calculate player average turns and score, and more.

### Python
package `python`  
<b>Dependencies</b>:  
`pathlib` and `comm`
<b>Function</b>:  
All python code are packaged here. These include basic hex package, game log reading, both coded in python and following the exact same logic as
the java code in other packages. This also include autoplay code.

### Tests
package `tests`  
<b>Dependencies</b>:  
`hex`, `hexio`, and `org.junit.jupiter.api`.  
<b>Function</b>:  
Serves as a test package for the most fundamental functions of the game, ensuring the backbones are working as intended.

### Special Features
package `special`  
<b>Dependencies</b>:  
dynamic, can include everything  
<b>Function</b>:  
Provide special themes, features, and Easter Eggs to the game. The generation is recorded in `special.FeatureFactory`.

### Game Viewer
package `viewer`  
**Dependencies**:  
`java.awt`, `javax.swing`, `hex`, and `hexio`  
**Function**:  
The Game Viewer, a developer tool and standalone, grayscale visualizer for `.hpyhex` game logs created with the game.
It is not integrated with the main game’s Launcher and is grayscale by design for simplicity. See
[Game Viewer README](viewer/README.md) for more.  

### Command Processors
package `comm`  
**Dependencies**:  
None  
**Function**:  
To establish an interface to execute commands and enable callbacks between controllers and runners. This facilitates thus
the communication between different processes by streamlining the command execution service.  
