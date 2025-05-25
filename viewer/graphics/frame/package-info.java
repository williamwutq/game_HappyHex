/**
 * Provides graphical user interface components for the main interactive panels of the HappyHex game.
 * <p>
 * The {@code graphics.frame} package defines high-level visual components used to construct and manage
 * the graphical layout and user interactions within the HappyHex viewer and controller interface.
 * These classes are responsible for integrating the game board, user input controls, simulation buttons,
 * scoring indicators, and overall layout strategy into a cohesive interface.
 * <p>
 * Each class in this package represents a distinct visual or control element:
 *
 * <ul>
 *   <li>{@link viewer.graphics.frame.EnterField} – A composite component that manages keyboard-based text input
 *       along with a visual display, used primarily for entering filenames or numeric codes. Includes an
 *       internal cursor and editing controls mapped to on-screen buttons.</li>
 *
 *   <li>{@link viewer.graphics.frame.GamePanel} – A lightweight custom {@code java.awt.Component} that renders
 *       the core HappyHex game board using hexagonal tiles. This panel displays the current state of play
 *       and upcoming game pieces with geometric precision and efficient rendering techniques.</li>
 *
 *   <li>{@link viewer.graphics.frame.InfoPanel} – A compact Swing component displaying real-time game statistics
 *       such as turn and score using stylized seven-segment indicators. The data is padded and aligned
 *       with clear labeling for consistent visual formatting.</li>
 *
 *   <li>{@link viewer.graphics.frame.GameUI} – A high-level interface panel that integrates multiple visual components,
 *       including {@code GamePanel}, {@code InfoPanel}, and control buttons. This class arranges these components
 *       in a structured layout and provides an interface for user interaction and simulation control
 *       (e.g., advancing game states or adjusting speed).</li>
 *
 *   <li>{@link viewer.graphics.frame.ViewerGUI} – The top-level container for the HappyHex game interface. This class
 *       combines {@code GameUI} and {@code EnterField} into a single view, manages layout logic based on current
 *       interface state (such as open/closed keyboards), and acts as the main viewer displayed to the user.</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li>All components are Swing-based and implement relevant GUI interfaces for compatibility with the game's
 *       controller and model layers.</li>
 *   <li>Rendering logic emphasizes consistent visual themes, geometric accuracy, and efficient Java 2D drawing.</li>
 *   <li>Components are arranged using custom layout rules rather than standard layout managers to enable fine-grained
 *       control over positioning and scaling.</li>
 *   <li>Each visual element encapsulates its behavior and presentation, with external state controlled via dedicated
 *       setters and update methods.</li>
 * </ul>
 *
 * <h2>Component Relationships</h2>
 * <p>
 * The typical initialization flow proceeds as follows:
 * <ol>
 *   <li>A {@code ViewerGUI} instance is created to represent the full application view.</li>
 *   <li>Inside the {@code ViewerGUI}, the {@code EnterField} and {@code GameUI} components are initialized and positioned.</li>
 *   <li>{@code GameUI} itself embeds a {@code GamePanel}, {@code InfoPanel}, simulation control buttons, and a {@code SpeedSlider}.</li>
 *   <li>Interactions between GUI and logic layers are mediated by a central {@code Controller} object, injected into components
 *       as needed to manage state updates and user actions.</li>
 * </ol>
 *
 * <h2>Dependencies</h2>
 * <ul>
 *   <li><b>{@code logic} package</b> – Provides the {@link viewer.logic.Controller} used for mediating game state,
 *       file parsing, and control event handling. All user interaction in this package routes through
 *       the controller.</li>
 *   <li><b>{@code graphics.interactive} package</b> – Supplies essential interactive components including
 *       {@link viewer.graphics.interactive.HexButton}, {@link viewer.graphics.interactive.SpeedSlider},
 *       and {@link viewer.graphics.interactive.GeneralIndicator}, all of which are used throughout this
 *       package to render buttons, sliders, and seven-segment displays.</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * Typical usage involves embedding the {@link viewer.graphics.frame.ViewerGUI} component into a host window or frame.
 * This package is self-contained and All control and rendering is internally managed and updated via component methods
 * and interactions with the controller. Developers are not expected to manually modify layout or repaint logic.
 *
 * @author William Wu
 * @version 1.0 (HappyHex 1.3)
 * @since 1.0 (HappyHex 1.3)
 * @see viewer.graphics.frame.EnterField
 * @see viewer.graphics.frame.GamePanel
 * @see viewer.graphics.frame.InfoPanel
 * @see viewer.graphics.frame.GameUI
 * @see viewer.graphics.frame.ViewerGUI
 */

package viewer.graphics.frame;