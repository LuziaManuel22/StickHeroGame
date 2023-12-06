
# Stick Hero Game

**Advanced Programming -------- Project**

A JavaFX-based game that challenges players to extend a stick to reach a moving platform, collect cherries, and navigate obstacles. The game features levels, a power-up mechanism, and immersive sound effects.



## Getting Started

Assumptions:
- No specific requirements were mentioned regarding the directory structure, so the assumed home folder is "src."
- Assuming you've already installed JavaFX, follow these steps :

Instructions:

1. Download the src code folder from Classroom and unzip it.

2. Open the project using IntelliJ IDEA or your preferred IDE.

3. All the commands should be run on the terminal in the HOME_FOLDER unless otherwise specified.


3- Ensure Maven is installed and configured:

Check Maven installation: Run mvn -version in your terminal and verify it displays the Maven version.

4- Import the project as a JAVAFX and choose Maven project within your IDE.

5- Run the application by right-clicking on the StickHeroGameApp.java (Main.java) class and selecting "Run."

6- If you want to run the project without an IDE or directly with the JAR file

Navigate to the directory containing the JAR file in the terminal:
run : cd path_to_directory_with_the_jar_file

-Execute the following command to run the JAR file:

run: java -jar StickHeroGame_Group105-1.0-SNAPSHOT.jar

Please adjust IDE settings to set StickHeroGameApp.java (Main.java) as the program's entry point. These steps should apply to most Maven-compatible IDEs.



## Game Controls

- Click "Start" to initiate the game.
- Click "Restart" to reset the game.
- Click "Power Up" to activate a special ability (pending implementation).

## Game Rules

1. Extend the stick using the "Start" button.
2. Land the stick on the moving platform to score points.
3. Collect cherries on the platform for extra points.
4. Avoid spikes on the platform to prevent game over.
5. Every 5 points, the game level increases, ramping up the challenge.

## Game Features

- Dynamic background color changes during gameplay.
- Power-up button for potential game enhancements .
- Sound effects for an immersive gaming experience .

## Game Entities

- **Hero:** Represents the player character.
- **Platform:** A moving platform for the hero to land on.
- **Stick:** Extended by the hero to bridge the gap.
- **Cherries:** Collectible items on the platform.
- **Spikes:** Obstacles to avoid on the platform.

## Bonus Components

### Score Manager

Manages the player's score and facilitates level progression.

### Game Graphics

Enhances visual appeal through various graphics and animations.

### Power-Up Button

Activates a special ability to enhance gameplay.

### Sound

Creates an immersive atmosphere with sound effects.

## Additional Components

- **Background Color Animation:** Dynamically changes during gameplay.

## Dependencies

- JavaFX's library for creating the graphical user interface.


# By: Luzia Manuel and Ria Malhotra
