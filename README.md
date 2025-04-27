# HappyHex  
A hexagonal Block Blast (e.g. Hex FRVR) implementation using java, build with custom graphics and advanced data processing.  

This is a very happy and fun game, with some easter eggs, for everyone :)

<b>Author:</b> William Wu  
<b>Lauguages:</b> Java ([Graphics](#Graphics-(GUI))), Python (Future, used for [ML](#develop--machine-learning))  
<b>Last edited:</b> 27/04/2025  
<b>Latest release:</b> [1.2.2](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.2.2)  

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
> - The most recent release have ***no*** detected bugs.

## About
This is just a cute project of mine. From early on after learning java in high school and helped the teacher taught its graphics library `javax.swing` I
felt compelled to actually program a functional game. Combined with my earlier ideas of designing a hexagonal grid coordinate system, the rough picture of
what would be future `HappyHex` formed in my mind. In my spare time, I gradually developed this project through following my [timeline](#Future-Timeline)
and added increasingly attractive features. From version 0.4 to 1.2, it has grown from just a game page with buggy information display that had to be
launched through terminal commands and contained no color indication to a comprehensive and enjoyable game that features settings, themes, and animation.
It has became that one project which I could not stop thinking about. Step by step improvements will be made in different aspects and merged together for
revisions after revisions. In the end, I would incorperate intelligent autoplay systems, unlockable achievements, and even fancier graphics.

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
2. Download code from github.com  
   1. Find the Latest Release (See the [top](#HappyHex) of this document).  
   2. Download the asset from the release.
   3. Download [dependencies](#Dependencies). You don't need `javax.swing` if it is already installed.
   4. Set the downloaded dependencies as project dependencies or libraries.
   4. Remove `HappyHex_jar Version x.x.x.zip` zip file, which contain a compiled jar.
   5. If tests are not needed, remove the `tests` package.
   6. Compile the program.
   7. Run main method in `Main` class in the main directory, usually named `game_HappyHex`.

### Dependencies
- [javax.swing](https://docs.oracle.com/javase/7/docs/api/javax/swing/package-summary.html)
- [javax.json](https://docs.oracle.com/javaee/7/api/javax/json/package-summary.html)
- [javax.json api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)
- [org.junit.jupiter.api](https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/package-summary.html)

## Game Play
### Game Rules
These game rules are automatically enforced by game logic stored in the game code. Players do not need to pay special attentions to them, but it is a good idea
to understand why the game bahaves the way it does. [Easter Eggs](#Easter-Eggs) can sometimes override game rules.
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
- Player can only pieces when a piece is selected an a valid location is clicked. This means the placed piece will not overlap with existing filled blocks,
  extend out of the boundries of the game board, or occupy a location of a previously placed piece.
- Invalid placement will result in the deselection of piece and no change to the piece queue or game board.
- Once placed, piece will be dequeued from the game queue, which will result in another new piece be generated according to logic.
- Placing a piece of `n` blocks increments the game score by `n`, and game turn by `1`.
#### Block Elimination
- When a row in any of the three directions are filled with blocks after a placement, that row will be eliminated. This means all blocks in that row will
  be set to empty.
- There is not particular sequence of elimination, which means two intersection row can be eliminated at the same time.
- Elimination is automatic, activated after any [piece placement](#Piece-Placement).
- Not all piece placements will result in block elimination.
- There is a small time delay between piece placement and elimination, as designed.
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
The following usernames are prohibited from inputting into the log in field in the log in page. This includes the capitalized and lowercase
versions of all words in the following list.
If the player attempts to log in with those names, the message "GAME KEYWORD PROHIBITED!" will be displayed.  

However, under special circumstances (as activating an easter egg), they may do something else. In addition, "out", "log out", and "logout" are
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
and unlock. There are not prerequisites for activation.  

They are normally not activated as it is for every other easter eggs, but it is also possible to switch to normal after activation.
1. <b>God Mode</b>  
   A special game mode that alters the game piece generation. This mode effectively check the current game board statues and only generate pieces
   that will be placeable. This ensures that you can basically never end or lose the game, except by force quitting.
   
   This mode is packaged inside `special.Logic` and contained in `special.FeatureFactory` with the class tag `hex.Piece` and hint tag `God`.  

   <details>
     <summary>Spoiler: Activation</summary>  
     &emsp;To activate, go to settings and turn on easy mode. Then go to log in page and try log in as "God". This will display the message
     "THE DIVINE INTERVENTION!".<br/>
     &emsp;To quit out of special modes, regardless of the current game setting, type "Normal" into the log in field to switch back to normal.
     This will not change the game setting otherwise.
   </details>

2. <b>Hard Mode</b>  
   A special game mode that alters the game piece generation. This mode effectively attempts to generate the most difficult pieces to play. It usually favor 
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
     &emsp;&emsp;To quit out of special modes, regardless of the current game setting, type "Normal" into the log in field to switch back to normal.
     This will not change the game setting otherwise.
   </details>

#### Special Themes  
These are themes that could only be activated during certain days. They will <b>always</b> activate regardless of the theme choosen or the game settings.
When these themes are in activation, the theme switch button will still work but they will not change the theme.  

These themes can change the color of every components in the game and the launcher, and may also change the font of components.  
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

Currently (As of Release 1.4), I have no plans for collabration with others on this project. However, if you wish to contribute on this small game, please
feel free to contact me.  

If you wish to suggest a feature, create an [issue](https://github.com/williamwutq/game_HappyHex/issues) and add appropriate tags. Please add the
[enhancement](https://github.com/williamwutq/game_HappyHex/issues?q=is%3Aissue%20state%3Aopen%20label%3Aenhancement) label to your issue. Please also
note that any suggestions to change architecture, coding lauguage, and most suggestions to graphics improvements will be dismissed.

### Developed Sections
These are parts of the game that are considered fully developed and refined. Unless there is an bug no further updates will be made.
1. <a name="develop--game-mechanics"><b>Game Mechanics</b></a>  
The underling data structure of the game, packaged in `hex`, is extensively tested and used from pre-release version 0.4 to the most recent release.
Although method may be added for future purposes, modification in implementation or structuring are unlikely.<br/>
2. <a name="develop--game-graphics"><b>Game Graphics</b></a>  
Not to be comfused with [Launcher Graphics](#develop--launcher-graphics), the game graphics refers to the page where the player actually plays the game.
This portion include a hexagonal grid representing the game field (or engine), a piece queue for potential selection of pieces, and game information display.
This page had its own background colors, which are separated from that of the launcher. However, it is dependent on the information provided by the launcher
and cannot run on its own. All code for this section is currently under the `GUI` package.<br/>
4. <a name="develop--game-animations"><b>Graphics Animations</b></a>  
The robust linear animation handler [animation](https://github.com/williamwutq/game_HappyHex/blob/main/GUI/animation/Animation.java) is tested and documented.
The handler is designed for simple temporary effects such as fading, expanding, or progress animations and implemented use `java.awt` package and `java.awt.event`.
It is lightweight and not a Swing component. You are welcome to use it for your own project. Follow the javadoc in the source code for more details.<br/>

### Directions of Development
The developing process are mainly seperated into the following parts:
1. <a name="develop--launcher-graphics"><b>Launcher Grahics</b></a>   
This section concerns about the launcher of the game, namely the window that shows up when you opens the application. The current launcher consists of a main
page, with buttons redirecting to pages for player log in, settings, graphics themes, and, of course, the game itself. Branches such as `gui` are dedicated to
the improvements of user experience. The branch `fancy`, which is no longer in use, was typically used to add fancy graphics.<br/>
2. <a name="develop--game-play"><b>Game Play</b></a>  
How the game is played. This including scoreing systems, awards, resetting, fetching and displaying previous game scores, etc. Currently, the most recent score
and turns, the highest score and turns, and the adverage score and turns can be seen when the game is over.<br/><br/>
Future considerations in this include setting passwords for user accounts, adding an achievement and purchasable system that would be linked with graphic themes, 
and providing a page for more detailed user game analysis. It is also considered to develop a separate runnable program derived from the current
[Game Graphics](#develop--game-graphics), which can help the player to review their game. This may potentially also help with
[Machine learning](#develop--machine-learning) and intelligent autoplay. For these reasons, it has its own tab [GameViewer](#develop--game-viewer).
3. <a name="develop--special-themes"><b>Special Themes</b></a>  
These are themes that could only be activated during certain days, such as Halloween, Independence Day, or Christmas. Some of them also serve as memorial to 
tragic events such as the September 11th attacks. These themes use the same injection interface as [SpecialModes](#develop--special-modes), and are packaged
in the same `special` package. Dedicated branches would be `special` and sometimes `fancy`.<br/>  
4. <a name="develop--special-modes"><b>Special Modes</b></a>  
Easter eggs modifying something fundamental in the game, whether it is piece generation, scoring and elimination rules, or others. Current implemented special
modes include `GodMode` and `HardMode`, both of which change the difficulty of the game by change the piece generation logic. These themes use the same injection
interface as [SpecialThemes](#develop--special-themes), and are packaged in the same `special` package. Dedicated branch for special modes is `special`.<br/>  
4. <a name="develop--machine-learning"><b>Machine Learning</b></a>  
A more precise way of describing this direction would be auto-game-play. This means a machine would be build to play the game while the player sit there and
watch. This autoplay would be initially enabled through scoreing algorithms and later through trained artificial intelligence operated in Python. This is a feature
of the future but current architecture is being designed around it. At the same time, scoring and reward functions are gradually comming online.<br/> 

### Future Timeline
> This timeline is subject to frequent change
- Latest Release: [1.2.2](https://github.com/williamwutq/game_HappyHex/releases/tag/v1.2.2)
- Patch 1.2.3
  - Add feature to automatically resume unfinished game
  - Add read game data log capacity
  - Add more tests for [Game Mechanics](#develop--game-mechanics)
  - Add method to convert `Piece` to byte representation
  - Add compiled jar
  - Add README.md
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
### Mechanics
### Graphics (GUI)
### Tests
