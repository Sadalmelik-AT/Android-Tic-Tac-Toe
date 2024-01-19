package com.example.silviobaldwintsangacadet_dm2_projet

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File
import java.io.IOException

enum class PlayerSymbol {
    X, O
}

class GameActivity : ComponentActivity() {
    private lateinit var player1Name: TextView;//Name of player 1
    private lateinit var player2Name: TextView;//name of Player 2
    private lateinit var messageBox: TextView;//message box
    private lateinit var gameGrid: ConstraintLayout;//TicTacToe grid
    private lateinit var player1pts: TextView;//player 1 points
    private lateinit var player2pts: TextView;//player 2 points
    private lateinit var initialButtonBackground: Drawable;//drawable background
    private var gameRunning: Boolean = false;//game runner
    private lateinit var currentPlayer: PlayerSymbol//to save player who is playing
    val tileList = ArrayList<View>()//to store the tiles of the game
    val winningPatterns = arrayOf(//all the winning patterns
        intArrayOf(0, 1, 2), // Top row
        intArrayOf(3, 4, 5), // Middle row
        intArrayOf(6, 7, 8), // Bottom row
        intArrayOf(0, 3, 6), // Left column
        intArrayOf(1, 4, 7), // Middle column
        intArrayOf(2, 5, 8), // Right column
        intArrayOf(0, 4, 8), // Diagonal from top-left to bottom-right
        intArrayOf(2, 4, 6)  // Diagonal from top-right to bottom-left
    )
    lateinit var backgroundResourceName :String;//to save name of the image drawn
    private var playerArray = ArrayList<Player>();//to save all players in ArrayList
    lateinit var playerFile:File;//to save players.txt file
     var player1:Player? = null;//to save current player 1
     var player2:Player? = null;//to save current player 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity_layout)//set layout
        player1Name = findViewById(R.id.player1)//get player 1 name textview
        player2Name = findViewById(R.id.player2)//get player 2 bame textview
        gameGrid = findViewById(R.id.gameGrid)//get the tic-tac-toe grid
        player1pts = findViewById(R.id.player1Points)//get player 1 point textview
        player2pts = findViewById(R.id.player2Points)//get player 2 point textview
        messageBox = findViewById(R.id.messageBox)//get messagebox textview

        // Retrieve data from the Intent
        player1 = intent.getParcelableExtra<Player>("player1")//get first player selected
        player2 = intent.getParcelableExtra<Player>("player2")//get second player selected
         playerFile = intent.getSerializableExtra("playerFile") as File//get players.txt file
        playerArray = intent.getSerializableExtra("playerList") as ArrayList<Player>//get the complete player Array

        //Sets up player stat interface
        player1Name.text = player1?.playerName.toString()//set player 1 name
        player1pts.text = player1?.playerScore.toString()+ " pts"//set player 1 score
        player2Name.text = player2?.playerName.toString()//set player 2 name
        player2pts.text = player2?.playerScore.toString() + " pts"//set player 2 score

        currentPlayer = PlayerSymbol.X // Player.X starts
        for (i in 0 until gameGrid.childCount) {
            //for each children of the game grid
            val tile: View = gameGrid.getChildAt(i)//get the current children
            tile.tag = " ";//set its tag to empty string
            tileList.add(tile)//add it to the tile List
        }
        initialButtonBackground = tileList[0].background;//set first tile's background drawable as initial bagckground
        showMessageBox(//good game message
            "Have a good game!",
            "OK"
        ) {
            // OK button clicked
            gameRunning = true // game started
            if (gameRunning) {
                displayTurn()//display player 1 turn
            }
        }

    }

    fun displayTurn() {//To display Player turn
        when (currentPlayer) {//depending on which player is playing
            //When Player 1
            PlayerSymbol.X -> messageBox.text = "It's " + player1Name.text + "'s turn";
            //When Player 2
            PlayerSymbol.O -> messageBox.text = "It's " + player2Name.text + "'s turn";
        }

    }
    fun changeBackground(view: View) {//To draw X or O on button background

        val symbolName = player1?.symbol ?: "cross" //Get symbol string or sets cross as default for player 1
        val symbolName2 = player2?.symbol?: "circle"//Get symbol string or sets circle as default for player 2

        //get player 1 image
        val player1Symbol = resources.getIdentifier(
            symbolName,
            "drawable",
            "com.example.silviobaldwintsangacadet_dm2_projet"
        )
        //get player 2 image
        val player2Symbol = resources.getIdentifier(
            symbolName2,
            "drawable",
            "com.example.silviobaldwintsangacadet_dm2_projet"
        )

        val button = view as Button//get the button clicked
        backgroundResourceName = if (currentPlayer == PlayerSymbol.X) symbolName else symbolName2//draw image depending on currentPlauer
        button.tag = backgroundResourceName;//set the tag property to resource name

        when (currentPlayer) {//to manages who's drawing
            //When PLayer 1 turn
            PlayerSymbol.X -> button.setBackgroundResource(player1Symbol)//draw player 1 symbol on button bg
            //When Player 2 turn
            PlayerSymbol.O -> button.setBackgroundResource(player2Symbol)//draw player 2 symbol on button bg
        }
        button.isEnabled = false; // deactivate button


        if (detectPattern()) {//If a winning patter detected
            val winnerName = if (currentPlayer == PlayerSymbol.X) player1Name.text else player2Name.text; //Get the name of the winner
            showMessageBox(//show winning message
                "Congratulation $winnerName, you won!",
                "OK"
            ) {
                // OK button clicked
                incrementScore();//increase winning player score
                gameRunning = false//game stopped
                switchTurn(1)//to make winner start next round, function called again at the end
            }
        }

        if (areAllTilesClicked()) {//If all tiles have been clicked

            showMessageBox(//show winning message
                "The game ended in a draw",
                "OK"
            ) {
                gameRunning = false;//game stopped
            }
        }
        switchTurn(1)//Switch Player turn
    }

    fun detectPattern(): Boolean {
        //To check if winning pattern created
        for (pattern in winningPatterns) {
            //For each pattern  in the array
            val tile1ResourceName =
                tileList[pattern[0]].tag as? String//Get the tag of first tile of the pattern checked
            val tile2ResourceName =
                tileList[pattern[1]].tag as? String//Get the tag of second tile of the pattern checked
            val tile3ResourceName =
                tileList[pattern[2]].tag as? String//Get the tag of third tile of the pattern checked
            val patternTiles = mutableListOf<Button>()
            if (tile1ResourceName == backgroundResourceName &&
                tile2ResourceName == backgroundResourceName &&
                tile3ResourceName == backgroundResourceName
            ) {
                // If a winning pattern is found, add the buttons to the patternTiles list
                patternTiles.add(tileList[pattern[0]] as Button)
                patternTiles.add(tileList[pattern[1]] as Button)
                patternTiles.add(tileList[pattern[2]] as Button)

                val symbolName = player1?.symbol?.let { it + "_win" } ?: "cross_win" //Sets winning image for player 1 (cross default)
                val symbolName2 = player2?.symbol?.let { it + "_win" } ?: "circle_win" //Sets winning image for player 2 (circle default)

                //get player 1 winning image
                val player1Symbol = resources.getIdentifier(
                    symbolName,
                    "drawable",
                    "com.example.silviobaldwintsangacadet_dm2_projet"
                )
                //get player 2 winning image
                val player2Symbol = resources.getIdentifier(
                    symbolName2,
                    "drawable",
                    "com.example.silviobaldwintsangacadet_dm2_projet"
                )
                for (button in patternTiles) {
                    //for each button in the pattern
                    when (currentPlayer) {
                        PlayerSymbol.X -> button.setBackgroundResource(player1Symbol);// Set image winning tiles if player 1 win
                        PlayerSymbol.O -> button.setBackgroundResource(player2Symbol)// Set image  winning tiles if player 2 win
                    }
                }
                disableAllTiles()//disable all tiles
                gameRunning = false;//game stopped
                return true // Player has won
            }
        }
        return false // No winning pattern found
    }

    fun incrementScore() {//to increment score
        val winnerTextView = if (currentPlayer != PlayerSymbol.X) player1pts else player2pts//get winning player name
        val currentScore = winnerTextView.text.toString().split(" ")[0].toInt() + 1//increment winning player score by 1
        winnerTextView.text = "$currentScore pts"//display new score
    }

    fun switchTurn(int: Int) {// To switch Player's turn
        when (int) {//depending of if its a win or not
            1 -> currentPlayer = if (currentPlayer == PlayerSymbol.X) PlayerSymbol.O else PlayerSymbol.X//makes winner start
            0 -> currentPlayer = PlayerSymbol.X//makes player 1 start
        }
        displayTurn();//Display who's turn it is

    }
    fun areAllTilesClicked(): Boolean {//to check if all tiles are clicked
        for (i in 0 until gameGrid.childCount) {
            //for each tile in the grid
            val tile = gameGrid.getChildAt(i)//get the current tile
            //image checkers
            val isCross = tile.tag == R.drawable.cross || tile.tag == R.drawable.cross_win
            val isCircle = tile.tag == R.drawable.circle || tile.tag == R.drawable.circle_win
            val isStar = tile.tag == R.drawable.star || tile.tag == R.drawable.star_win
            val isPlanet = tile.tag == R.drawable.planet || tile.tag == R.drawable.planet_win
            val isAtom = tile.tag == R.drawable.atom || tile.tag == R.drawable.atom_win

            if (tile is Button) {
                //if the tile is a button
                if (tile.tag == " " && (!isCross || !isCircle || !isStar || !isPlanet || !isAtom)) {
                    //if the tag property is empty and no drawable ressources
                    return false //all buttons have not been clicked
                }
            }
        }
        return true//all buttons have been clicked
    }
    fun resetCurrentGame(view: View) {//To reset the current game
        showChoiceBox(//Ask if player is sure
            "Confirmation",
            "Do you want to restart the game?",
            "Yes",
            "No",
            {
                // Positive button clicked action
                for (i in 0 until gameGrid.childCount) {
                    //for each child of the game grid
                    val tile: View = gameGrid.getChildAt(i)//get the child
                    if (tile is Button) { // Check if the tile is of type Button
                        tile.background = initialButtonBackground;//set child bg to initial bg
                        tile.tag = " ";//empty tag button for pattern check
                    }
                }
                if (gameRunning) { //if th game was running
                    switchTurn(0)//make X start
                }
                enableAllTiles();//reactivate all tiles
                gameRunning = true;//game is running
            },
            {
                // Negative button clicked action
                //do nothing
            }
        )
    }
    fun writeFile(playerList: MutableList<Player>){// to save players score into file

        val fileContent = StringBuilder()//create string builder
        try {
            for ((index, player) in playerList.withIndex()) {
                //for each indexed player in the list
                val data = player.playerName.toString() + "," + player.playerScore.toString()//recuperate player name and score
                fileContent.append(data)//add it to the stringbuilder


                if (index < playerList.size - 1) {//if this is not the last player in the array
                    fileContent.append(";")// Append a ; after player score
                }
            }
            playerFile.outputStream().use {
                it.write(fileContent.toString().toByteArray())//write the stringbuilder in the file
            }
        } catch (e: IOException){//if error
            e.printStackTrace()//prints error
        }
    }
    fun restartApp(view: View) {//To create a new game
        showChoiceBox(//Ask if user is sure
            "Confirmation",
            "Do you want to exit the game?",
            "Yes",
            "No",
            {
                // Positive button clicked action
                    playerArray.find { it.playerName == player1?.playerName }?.playerScore = player1pts.text.toString().split(" ")[0].toInt() //updates player 1 score
                    playerArray.find { it.playerName == player2?.playerName }?.playerScore = player2pts.text.toString().split(" ")[0].toInt() //updates player 2 score
                writeFile(playerArray)//save all new scores
                val intent = Intent(this, MainActivity::class.java)//create intent for main_activity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK//set the flag for the intent
                startActivity(intent)//start activity with intent
            },
            {
                // Negative button clicked action
                //do nothing
            }
        )

    }
    fun enableAllTiles() {//To activate all tiles
        for (i in 0 until gameGrid.childCount) {
            //for each child in the gridd
            val tile = gameGrid.getChildAt(i)//get child
            if (tile is Button) {//if child is button
                tile.isEnabled = true//activate the button
            }
        }
    }
    fun disableAllTiles() {//To activate all tiles
        for (i in 0 until gameGrid.childCount) {
            //for each child in the gridd
            val tile = gameGrid.getChildAt(i)//get child
            if (tile is Button) {//if child is button
                tile.isEnabled = false//deactivate the button
            }
        }
    }

    fun showMessageBox(message: String, positiveText: String, onPositiveClick: () -> Unit) {//to show simple message
        val alertDialogBuilder = AlertDialog.Builder(this)//set context

        alertDialogBuilder.setMessage(message)//set message

        //set ok button
        alertDialogBuilder.setPositiveButton(positiveText) { dialogInterface: DialogInterface, _: Int ->
            onPositiveClick.invoke()
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()//create message box
        alertDialog.show()//show on screen
    }
    fun showChoiceBox(//to show choice message box
        title: String,
        message: String,
        positiveText: String,
        negativeText: String,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit
    ) {
        val alertDialogBuilder = AlertDialog.Builder(this)//set the context

        alertDialogBuilder.setTitle(title)//set title
        alertDialogBuilder.setMessage(message)//set message

        //create positive answer
        alertDialogBuilder.setPositiveButton(positiveText) { dialogInterface: DialogInterface, _: Int ->
            onPositiveClick.invoke()
            dialogInterface.dismiss()
        }
        //create negative answer
        alertDialogBuilder.setNegativeButton(negativeText) { dialogInterface: DialogInterface, _: Int ->
            onNegativeClick.invoke()
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()//create the message box
        alertDialog.show()//show on screen
    }

}




