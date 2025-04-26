# HappyHex  
A hexagonal Block Blast (e.g. Hex FRVR) implementation using java, build with custom graphics and advanced data processing.  

This is a very happy and fun game, with some easter eggs, for everyone :)

<b>Author:</b> William Wu  
<b>Lauguages:</b> Java ([Graphics](#Graphics-(GUI))), Python (Future, used for [ML](#develop--machine-learning))  
<b>Last edited:</b> 26/04/2025  
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
