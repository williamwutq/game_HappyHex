# HappyHex  
A hexagonal Block Blast (e.g. Hex FRVR) implementation using java, build with custom graphics and advanced data processing.  

This is a very happy and fun game, with some Easter Eggs, for everyone :)

<b>Author:</b> William Wu  
<b>Languages:</b> Java ([Graphics](#Graphics-(GUI))), Python (Future, used for [ML](#develop--machine-learning))  
<b>Last edited:</b> 27/04/2025  
<b>Latest release:</b> [1.2.3](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.2.3)  

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
> - The most recent release have ***one*** detected bugs:
>   1. [Restarted game score remain at 0](https://github.com/williamwutq/game_HappyHex/issues/32)
       Statues: Temporarily fixed
       Fixes: Disabled restart unfinished game feature.

## About
This is just a cute project of mine. From early on after learning java in high school and helped the teacher taught its graphics library `javax.swing` I
felt compelled to actually program a functional game. Combined with my earlier ideas of designing a hexagonal grid coordinate system, the rough picture of
what would be future `HappyHex` formed in my mind. In my spare time, I gradually developed this project through following my [timeline](#Future-Timeline)
and added increasingly attractive features. From version 0.4 to 1.2, it has grown from just a game page with buggy information display that had to be
launched through terminal commands and contained no color indication to a comprehensive and enjoyable game that features settings, themes, and animation.
It has become that one project which I could not stop thinking about. Step by step improvements will be made in different aspects and merged together for
revisions after revisions. In the end, I would incorporate intelligent autoplay systems, unlockable achievements, and even fancier graphics.

## License
Distributed under the MIT License

[License Link](https://github.com/williamwutq/game_HappyHex/blob/main/LICENSE)

## Usage
How to download the game and play, or, if you want, compile it yourself.  
1. Run the `.jar` file  
   1. Find the Latest Release (See the [top](#HappyHex) of this document).  
   2. Download the asset from the release.
   3. Find `HappyHex_jar Version x.x.x.zip` zip file.  
   4. Use a tool to decompress the zip file, which will create a folder named `HappyHex_jar`.  
   5. Make sure that the folder contains:  
      a. The main jar file, `game_HappyHex.jar`.  
      b. The JSON utilities, `javax.json-1.1.4.jar` and `javax.json-api-1.1.jar`.  
      c. The `data` folder.  
   6. Double click or use a tool to run `game_HappyHex.jar`.  
2. Download code from GitHub.com  
   1. Find the Latest Release (See the [top](#HappyHex) of this document).  
   2. Download the asset from the release.
   3. Download [dependencies](#Dependencies). You don't need `javax.swing` if it is already installed.
   4. Set the downloaded dependencies as project dependencies or libraries.
   5. Remove `HappyHex_jar Version x.x.x.zip` zip file, which contain a compiled jar.
   6. If tests are not needed, remove the `tests` package.
   7. Compile the program.
   8. Run main method in `Main` class in the main directory, usually named `game_HappyHex`.

### Dependencies
- [javax.swing](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html)
- [javax.json](https://docs.oracle.com/javaee/7/api/javax/json/package-summary.html)
- [javax.json api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)
- [org.junit.jupiter.api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)

## Game Play
### Theme, Color and Font
This describes the default themes of the game, including coloring and fonts of texts. For special themes, see the [Special Themes](Special-Themes)
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
The colors and fonts of the three basic themes have not changed since.  
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
  of the unfinished game will be the same according to the current theme. They maybe gray or black, but will not be the usual 12 colors.
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
- There are 12 possible colors for any piece or block according to the current theme. The piece or block may also be gray if an unfinished game is loaded.
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
- The game will be logged in a `.hpyhex.json` file inside the folder `data`.
- The terminated game will be connected with the user information.
#### Game Quitting
- The player may quit the game by clicking on the quit button inside the game at any time.
- The score and turns of the game will be recorded.
- The game will be logged in a `.hpyhex.json` file inside the folder `data`.
- The unfinished game will be connected with the user information.
- If the player logs in again with the identical information and game setting, and the game log exists in the correct location, the unfinished game
  could be revived with the unfinished board and game queue. The color of the board and queue of the unfinished game will be the same according
  to the current theme. They maybe gray or black, but will not be the usual 12 colors.
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

## Development
### Contribution
This is mostly my project under my design, and I am managing all branches.  

Currently (Before Release 1.4), I have no plans for collaboration with others on this project. However, if you wish to contribute on this small game, please
feel free to contact me.  

If you wish to suggest a feature, create an [issue](https://github.com/williamwutq/game_HappyHex/issues) and add appropriate tags. Please add the
[enhancement](https://github.com/williamwutq/game_HappyHex/issues?q=is%3Aissue%20state%3Aopen%20label%3Aenhancement) label to your issue. Please also
note that any suggestions to change architecture, coding language, and most suggestions to graphics improvements will be dismissed.

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

### Directions of Development
The developing process are mainly separated into the following parts:
1. <a name="develop--launcher-graphics"><b>Launcher Graphics</b></a>   
This section concerns about the launcher of the game, namely the window that shows up when you open the application. The current launcher consists of a main
page, with buttons redirecting to pages for player log in, settings, graphics themes, and, of course, the game itself. Branches such as `gui` are dedicated to
the improvements of user experience. The branch `fancy`, which is no longer in use, was typically used to add fancy graphics.<br/>
2. <a name="develop--game-play"><b>Game Play</b></a>  
How the game is played. This including scoring systems, awards, resetting, fetching and displaying previous game scores, etc. Currently, the most recent score
and turns, the highest score and turns, and the average score and turns can be seen when the game is over.<br/><br/>
Future considerations in this include setting passwords for user accounts, adding an achievement and purchasable system that would be linked with graphic themes, 
and providing a page for more detailed user game analysis. It is also considered to develop a separate runnable program derived from the current
[Game Graphics](#develop--game-graphics), which can help the player to review their game. This may potentially also help with
[Machine learning](#develop--machine-learning) and intelligent autoplay. For these reasons, it has its own tab [Game Viewer](#develop--game-viewer).
3. <a name="develop--special-themes"><b>Special Themes</b></a>  
These are themes that could only be activated during certain days, such as Halloween, Independence Day, or Christmas. Some of them also serve as memorial to 
tragic events such as the September 11th attacks. These themes use the same injection interface as [SpecialModes](#develop--special-modes), and are packaged
in the same `special` package. Dedicated branches would be `special` and sometimes `fancy`.<br/>  
4. <a name="develop--special-modes"><b>Special Modes</b></a>  
Easter eggs modifying something fundamental in the game, whether it is piece generation, scoring and elimination rules, or others. Current implemented special
modes include `GodMode` and `HardMode`, both of which change the difficulty of the game by change the piece generation logic. These themes use the same injection
interface as [SpecialThemes](#develop--special-themes), and are packaged in the same `special` package. Dedicated branch for special modes is `special`.<br/>  
5. <a name="develop--game-viewer"><b>Game Viewer</b></a>  
Develop a separate runnable program that may read `.hpyhex.json` files and display them in a hexagonal grid without color.
This program can be grayscale and simple to run with minimal dependencies.  
The Game Viewer should feature inputs to select which file to view, buttons to increment and decrement the moves recorded in the game, and display
real time game scores associated with the moves. The program may also feature a button to automatically increment the game with timed intervals or buttons
to skip a certain numbers of steps.<br/>  
6. <a name="develop--machine-learning"><b>Machine Learning</b></a>  
A more precise way of describing this direction would be auto-game-play. This means a machine would be build to play the game while the player sit there and
watch. This autoplay would be initially enabled through scoring algorithms and later through trained artificial intelligence operated in Python. This is a feature
of the future but current architecture is being designed around it. At the same time, scoring and reward functions are gradually coming online.<br/> 

### Future Timeline
> This timeline is subject to frequent change
- Latest Release: [1.2.3](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.2.3)
- Patch 1.2.4
  - Add more information to README.md
  - [Fix restarted game score remain at 0](https://github.com/williamwutq/game_HappyHex/issues/32)
- Version 1.3
  - Add Independence Day special (color and font theme)
  - Add Thanksgiving special (color and font theme)
  - Finalize `.hpyhex.json` formats
  - Add feature to read all formats and write in the most recent one
- Version 1.4
  - Add autoplay based on random, turn on via settings
  - Add and compiled game viewer
  - Add first python code
  - Graphics improvements
- Version 1.5
  - Complete [resume game on demand](https://github.com/williamwutq/game_HappyHex/issues/21)
  - Add rare chance shinning font ("HappyHex" font animated by `animation` class switching between the colors of the rainbow)
  - Add Christmas super special (Christmas colors, gift blocks, decals)
- Version 1.6
  - Add autoplay based on rating algo
  - Remove autoplay activation from settings and add autoplay mode selection
  - Potentially add 3 way selection button
  - Add pause and run autoplay buttons to the game graphics

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
none  
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
`javax.json`  
<b>Function</b>:  
Provides utility methods for converting hexagonal game components such as Hex, Block, Piece, HexEngine, and game moves to and from
JSON representations. It helps developers to save game states to `hpyhex.json` files, read them back, and manage game data efficiently.
It is not dependent on any other packages and use internal versioning separated from `Launcher`.  

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
`hex`, `hexio`, `Launcher`, `special`, and `javax.swing`  
<b>Function</b>:  
Provides game page graphics through Java Swing. It also records critical game information and serves fundamental game logic to make the game function.
This includes functions for buttons, color indication generations, interaction with the launcher, timer for elimination, and more.  
This graphics component support dynamic resizing.  

### Launcher
package `Launcher`  
<b>Dependencies</b>:  
`io`, `hexio`, `GUI`, `special`, and `javax.swing`  
<b>Function</b>:  
Provides game launcher graphics through Java Swing. This includes the main page, the settings page, the login page, the game over page, and the themes page.
It also provides links to set up evironment and start a [game](#Graphics-(GUI)). In addition, it calls to the internal game logger to read local data.
This includes logging player scores and turns, loading previous unfinished games, calculate player average turns and score, and more.

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
