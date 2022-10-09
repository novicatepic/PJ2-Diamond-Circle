# PJ2-JavaProject

Implement a game called DiamondCircle. Game is played on a matrix. Minimum size of the matrix is 7x7, maximum size is 10x10. Maximum of 4 players can play this game, minimum of two. Dimensions of the matrix and number of players are specified before the application starts. It Is required to check validity of the user input.

Each player has a name (which has to be unique) and has four figures, each figure has the same color. Every figure has its own color and the way of moving. There are three types of figures: standard, flying and super fast figure. Colors that are allowed are: red, green, blue, yellow. Standard figure can cross given number of fields, as well as the flying figure. Super fast figure can cross twice more fields. Standard and super fast figure can drop in to the hole, while flying figure can levitate above the hole. When the game starts, each player gets four randomly chosen figures of the same color.

Besides these figures, there is a ghost figure – it starts its movement when the first player starts and moves behind the scenes. Ghost figure sets bonus fields – diamonds. It can set random number of diamonds between 2 and dimension of the matrix on random positions. Diamonds are placed every five seconds and this is done as long as the game lasts. When a figure finds a diamond, it collects a diamond and in the next move, number of fields which the figure crosses is increased by the number of diamonds it previously collected.
There is a specific movement on the matrix showed in the PJ2 – projektni zadatak – maj 2022.pdf.

Order in which players play the game is random and players play the game one after another. One move is considered moving the figure certain number of fields. If the field is already taken, figure is set on the next free field. Moving from one field to the other field takes one second. The way figure moves depends on the card which is pulled out from the deck that contains 52 cards. There are simple and special cards. Simple card contains a picture and number of fields that the figure is supposed to cross. Special card only contains a picture. When a special card is pulled out, there are n holes created on the map. Color of the hole is black. Card goes back into the deck after it has been pulled out. Pulling out a card means showing it in the GUI part of the application. If the figure is in the hole and it is not a flying figure, it dies. When a figure dies, if a player has more figures alive, he starts with a new one. Game finishes when there are no figures left for all the players, which means that any figure has come to the finish line or it has died. It is supposed to save the information about fields crossed and game time played for every figure. In GUI side of the application, color and type of the figure must be shown. Game can be stopped and started again. Game is played automatically. When the game finishes, results are saved in a textual file. Each textual file has a name in this pattern: IGRA_current_time.txt.

When you click on the button “Prikaz liste fajlova sa rezultatima”, a new form is opened in which there is a table that contains list of files. Clicking on a certain file means that user is shown its content.

“Vrijeme trajanja igre” and “Opis znacenja karte” are fields that are refreshed each second. Number of squares on the card represents number of fields that a figure must cross. On the central matrix movement of the figure is shown.

Logger class must be used to handle exceptions in each and every class.

This is a GUI application made in Java programming language.


![d1](https://user-images.githubusercontent.com/62095336/194740581-e0ce97fe-207b-4f35-a309-094aa82920f3.png)


![d2](https://user-images.githubusercontent.com/62095336/194740606-6a8029b5-0060-4627-902e-8db31df643f8.png)


![d3](https://user-images.githubusercontent.com/62095336/194740614-018e4e59-4e08-4885-8898-e490b9624bc4.png)
