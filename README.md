# Stick Hero Game

This is a Java implementation of the Stick Hero game using JavaFX. It is a game where a hero navigates platforms by extending a stick, collecting cherries, and avoiding falling into the abyss.

## Prerequisites

- Java Development Kit (JDK) installed
- JavaFX library


## Game Controls

- **SPACE:** Flip the player character.
- **RIGHT ARROW:** Extend the stick to the right.
- **LEFT ARROW:** Extend the stick to the left.

## Game Features

- **Platforms:** Pillars and cherries are randomly generated as platforms for the hero to land on.
- **Stick:** The hero can extend the stick to reach platforms and collect cherries.
- **Cherries:** Collecting cherries increases the score and can be used for revival.
- **Revival:** The player can be revived using collected cherries under certain conditions.
- **Saving and Loading:** The game state can be saved and loaded.

## Code Structure

- **Platform:** Abstract class representing a generic platform.
- **Pillar and Cherry:** Concrete platform classes.
- **Stick:** Represents the stick used by the hero.
- **StickHeroGameModel:** Contains the game logic and state.
- **StickHeroGameController:** Handles user input and game events.
- **StickHeroGameView:** Manages the game UI and visualization.
- **StickHeroCharacter:** Represents the hero character.
- **StickHeroGame:** Main class extending JavaFX Application.
- **StickHeroGameModelTest:** JUnit tests for the game model.

## Testing & Design patterns

JUnit's tests are provided in the `StickHeroGameModelTest` class to verify the functionality of key game model methods.

Feel free to explore and modify the code to enhance or customize the Stick Hero game!
Also, factory method and sigleton design pattern have been implemented in the code along with various OOPs concepts.