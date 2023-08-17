# Java Course Project

Develop a game named "DiamondCircle" played on a matrix. The matrix must have a minimum size of 7x7 and a maximum size of 10x10. The game supports 2 to 4 players. The matrix dimensions and the number of players are specified before launching the application, and user inputs need to be validated.

Each player is assigned a unique name and has four figures, all of the same color. These figures come in three types: standard, flying, and super fast. The available colors are red, green, blue, and yellow. A standard figure and a super-fast figure can move across a certain number of fields, while a flying figure can also cross fields but can levitate above holes. The super-fast figure can move twice the number of fields compared to the standard figure. Figures can also drop into holes, except for flying figures.

At the start of the game, each player is given four randomly selected figures of the same color. Additionally, there is a ghost figure that moves behind the scenes. This ghost figure places bonus fields (diamonds) on the matrix, randomly selecting a number between 2 and the matrix's dimensions. Diamonds appear every five seconds throughout the game. When a figure collects a diamond, it gains a bonus to the number of fields it can move in its next turn.

The game proceeds with a specific movement pattern on the matrix as described in "PJ2 – projektni zadatak – maj 2022.pdf." Players take turns playing, moving their figures a specific number of fields based on cards drawn from a deck. The cards determine the movement distance and potential special effects, such as creating holes. Figures that fall into holes and are not flying figures are eliminated. Players continue with surviving figures until all figures have either reached the finish line or have been eliminated. The game keeps track of fields crossed and game time for each figure.

The application includes a GUI that displays the figure's color and type. The game can be started, paused, and resumed automatically. When the game ends, results are saved in a text file with a name in the format "IGRA_current_time.txt."

Clicking the "Prikaz liste fajlova sa rezultatima" button opens a new form containing a table with a list of files. Clicking on a file displays its content.

The fields "Vrijeme trajanja igre" (Game Duration) and "Opis znacenja karte" (Card Description) are updated every second. The number of squares on the card corresponds to the number of fields a figure can move. The central matrix displays the figure's movement.

To handle exceptions, utilize the Logger class in all parts of the application.

This Java-based GUI application is designed to bring the game "DiamondCircle" to life.


![d1](https://user-images.githubusercontent.com/62095336/194740581-e0ce97fe-207b-4f35-a309-094aa82920f3.png)


![d2](https://user-images.githubusercontent.com/62095336/194740606-6a8029b5-0060-4627-902e-8db31df643f8.png)


![d3](https://user-images.githubusercontent.com/62095336/194740614-018e4e59-4e08-4885-8898-e490b9624bc4.png)
