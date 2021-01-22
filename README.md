ECE 452 - Software Design and Architectures </br>
University of Waterloo </br>
Electrical and Computer Engineering 2021</br>

Kristopher Sousa, Zachary Walford, Jackson Barr, William Huang, Daniel Tran and Cristiano Chelotti </br>

### !!! Note: This Repo is a mirror of the private Repo. !!!

## Features
- Screen record gameplay
- Share recordings to social media
- 3 Difficulty levels
- Item drops providing buffs
- Variety of monsters: Slimes, Orcs, etc
- Boss Fight varies behavior based on difficulty

## Game Demo
A demo and project description is available here: https://youtu.be/8AhAuv4iPXk

# Code Breakdown
* All the code can be found in /app/src/main/java/org.o7planning.android2dgame.
* All the resources are stored /app/src/main/res.
* The most important classes are the MainActivity, GameSurface, GameThread and GameObject classes.

# MainActivity
* MainActivity's `onCreate()` method is the starting point for the application.
* In this method, there is some basic code that sets the app to fullscreen, removes the title and instantiates a GameSurface object.

# GameSurface
* When a game surface is created, the `surfaceCreated()` method is run.
    * Right now, this method decodes resources (images, spritesheets, etc) and turns them into bitmaps before instantiating the sprite classes (which are the chibi characters and map for now).
    * Then, the method creates a GameThread instance and starts it up.
* The GameSurface class contains two other very important methods: `update()` and `draw()`.
    * These methods are the "master methods" for updating and drawing - that is, the update and draw methods of all other classes should be called within them.
    * The `update()` methods are responsible for updating any class specific parameters (such as updating the x and y of a sprite).
    * The `draw()` methods are responsible for drawing images on the canvas.

# GameThread
* This class holds the main the game loop in its `run()` method.
* This thread should only be started and stopped after creation through the use of its `isRunning` property.
* The `run()` method is responsible for calling the GameSurface's `update()` and `draw()` methods in a loop.

# GameObject
* This class is an abstract class implemented by all other sprite classes that defines some basic properties, such as the width, height, and x and y of each, as well as methods for retrieving them.
* The `createSubImageAt()` method allows one to pass in a bigger image and break it down into sub-images based on the # of specified rows and columns, which is good for working with sprite sheets or the map grid.
