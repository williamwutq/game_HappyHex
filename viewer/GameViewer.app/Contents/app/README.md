# HappyHex Game Viewer

**Latest Version:** 1.1  
**Latest release:** [GameViewer 1.1](https://github.com/williamwutq/game_HappyHex/releases/tag/viewer-1.1)  
**Last edited:** 30/07/2025  
**Backward compatible with HappyHex Game Version:** 1.3.0  
**Forward compatible with HappyHex Game Version:** < 2.1.0  
**License:** [MIT](LICENSE)  
**Author** William Wu  
**Language** Java  
**Distribution Format:** `GameViewer.jar`  
**Launchable:** Directly executable as a standalone JAR

> [!NOTE]
> The game viewer may contain bugs:  
> - Report bugs by create an [issue](https://github.com/williamwutq/game_HappyHex/issues).
> - The most recent release have ***zero*** detected bugs.

> [!IMPORTANT]
> Limitations to the Game Viewer:  
> - Only supports `.hpyhex` (binary format) game logs.
> - **May not** support `.json` or any other formats.
> - This viewer is **not integrated** with the main game.
> - Development is partially decoupled from the main HappyHex codebase.

---

## About
The **HappyHex Game Viewer** is a standalone, grayscale visualizer for `.hpyhex` game logs created with the HappyHex game.
It is designed primarily as a developer tool for reviewing and analyzing gameplay step-by-step. However, any user can run
it to view recorded games without launching the full game. This tool is not connected to the main game application, and
the two cannot launch each other. Although it uses some shared packages (e.g., `hex`, `hexio`), it is developed and
compiled separately.

## Contribution

The HappyHex Game Viewer is released under the [MIT License](https://opensource.org/licenses/MIT) and is free for
personal, educational, and commercial use. See [LICENSE](LICENSE) for more details.  

However, **active development is complete**, and the team will **not accept pull requests**.  

- The `viewer` branch is the **main and protected branch** for viewer development.  
- No new features will be added unless strictly necessary.  
- **No new file formats** are planned before HappyHex version 1.5.  
- If you encounter a **bug**, feel free to [create an issue](https://github.com/williamwutq/game_HappyHex/issues).
  In the issue title, make sure to mention "Viewer" or "Game Viewer". Bugs will be fixed if reported.  
- **New Feature requests, such as adding colors, will not be accepted.**  
- If you'd like to build an enhanced version of game viewer, **please fork the repository** and develop independently.  
- This contribution guideline **only apply to the Game Viewer** development, not the main HappyHex game.  

Remember, game viewer mainly serves as a developer tool, not a game product or feature.  

## Features

- Lightweight and minimal dependencies.
- Displays gameplay from binary `.hpyhex` logs.
- Supports drop in file from anywhere, if the operating system allows it.
- No reliance on fonts, themes, or any system-specific graphics.
- Simple grayscale UI using only Java standard GUI libraries.
- Interactive slider for speed control.
- Button-based step control (forward/backward).
- On-screen hex keyboard and system keyboard for input.
- Score and turn indicators included.
- Fully self-contained in a single JAR.
- Eye like icon for the program.

## Dependencies

All required packages are bundled inside the JAR:

- `java.awt`
- `javax.swing`
- `hex`
- `hexio`

No additional libraries or themes are needed. There are **No** Easter Eggs, hidden logic paths, or graphical overlays.

## How to Run

1. **Execute the JAR**:
   ```java -jar GameViewer.jar```
   or double-click `GameViewer.jar`.

2. **Enter Game Log Filename**:
    - This only accepts files from the `data/` folder.
    - Upon launching the viewer application, a calculator-like keyboard and display interface should show up.
    - Use the on-screen keyboard to input a filename (no extension). By default, the filename is a **16-digit hexadecimal** number.
    - Use the `0`-`F` keys to enter hexadecimal characters, `<`, `>`, `STT`, `END` keys to move cursor.
    - Use your system keyboard to enter any character, if you prefer.
    - Use the `DEL` and `-` keys to delete characters, and the `+` key to duplicate character at cursor position.
    - Press `ENT` (Enter) when the full name is entered.
    - If the file exists in the `data/` folder, it will be loaded.
    - Depending on the version, if binary file does not exist, it may try to load json file if exists.

3. **Navigating the Game**:
    - After loading, click the filename field to exit keyboard mode and show the game field.
    - Use the **top two arrow buttons** to move step-by-step **(backward/forward)**.
    - Use the **bottom two arrow buttons** to run continuously **(backward/forward)**.
    - Use the **slider at the bottom** to adjust the playback speed.
    - The **score and turn** will be shown at the top center, between the filename and the game board.

4. **Refresh Game**:
   - Games are automatically refreshed from drop in.
   - Games are only refreshed when the `ENT` key is pressed.

5. **Load Another Game**:
    - Click the filename again to re-activate the keyboard.
    - Press `CLR` (Clear), enter a new filename, and press `ENT`.
    - Or drop in a file anywhere in the application window to load a new game.

## Packages

### Core Viewer Interface
package `viewer`  
**Dependencies**:  
`java.awt`, `javax.swing`, `hex`, `hexio`, `viewer.logic`, `viewer.graphics.interactive`  
**Function**:  
Main application package. It provides the entry point for the program and constructs the overall interface layout.
It initializes game state rendering, handles basic file input, and coordinates UI elements. It is not integrated
with the main game’s Launcher and is grayscale by design for simplicity.

### Viewer Logic Controller
package `viewer.logic`  
**Dependencies**:  
`hex`, `hexio`  
**Function**:  
Encapsulates game-viewing logic. Handles game step navigation, replay speed, file loading, and synchronization of
UI elements with internal game state. Acts as the logical bridge between UI components and the underlying game engine.

### Interactive Components
package `graphics.interactive`  
**Dependencies**:  
`java.awt`, `javax.swing`  
**Function**:  
Provides reusable UI components such as the on-screen keyboard, buttons, and slider used in the Viewer. These
components are tailored for grayscale interface usage and are loosely coupled for easy integration. They are
shared with other GUI tools that follow minimal UI design principles.