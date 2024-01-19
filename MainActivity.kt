package com.example.silviobaldwintsangacadet_dm2_projet

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException

import android.os.Parcel
import android.os.Parcelable

data class Player(
    val playerName: String,
    var playerScore: Int,
    var selected: Boolean,
    var symbol: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() // Read the symbol as a String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(playerName)
        parcel.writeInt(playerScore)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeString(symbol) // Write the symbol as a String
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Player> {
        override fun createFromParcel(parcel: Parcel): Player {
            return Player(parcel)
        }

        override fun newArray(size: Int): Array<Player?> {
            return arrayOfNulls(size)
        }
    }
}




class MainActivity : ComponentActivity() {
    private var playerSelection: LinearLayout? = null // to save 1st layout of Activity 1
    private var symbolSelection: LinearLayout? = null//to save 2nd layout of Activity 1
    private var playerName: TextView? = null//to save player name textview of Activity 1
    private var PlayerRecyclerView: RecyclerView? = null;
    private var SymbolRecyclerView: RecyclerView? = null;
    private var playerArray = ArrayList<Player>();//to save all players in ArrayList
    private var selectedPlayers = ArrayList<Player>();//to save selected players
    private lateinit var playerFile:File;
    private lateinit var playerLines: List<String>;
    private lateinit var adapter: PlayerAdapter
    private lateinit var adapterSymbol: SymbolAdapter
    private lateinit var Images: Images;//to access all images for the game
    private var playerNameTextView = ""// to save playername textview





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity_layout) // Set the content view first

        // Initialize UI elements after setting the content view
        playerSelection = findViewById(R.id.playerSelection)//get player layout
        symbolSelection = findViewById(R.id.symbolSelection)//get smbol layout
        PlayerRecyclerView = findViewById<RecyclerView>(R.id.playerList)//get player recyclerView
        SymbolRecyclerView = findViewById<RecyclerView>(R.id.symbolList)//get symbol recyclerView
        playerName = findViewById(R.id.playerName)//get player name edit_text


        playerSelection?.isVisible = true//set player recyclerView visible
        symbolSelection?.isVisible = false//set symbol recyclerView invisible


       try {
           playerFile = File(this.filesDir, "players.txt")//get internal file
           if(playerFile.exists()){//if the file exists
               loadPlayersFromFile();//get the players from the file
           }
       } catch (e:IOException){//if error
           e.printStackTrace()//print the stack trace
       }
        // Initialize the Symbol adapter and set it to symbolList RecyclerView
        Images = Images(this) // Create an instance of the Images class

        val symbolArray = mutableListOf<MutableMap<String, Any?>>()//to save maps of drawable ressources
        for ((name, drawable) in Images.imageMap) { // for each images in Images class
            if (!name.contains("_win")) {//If the name does not have _win
                val symbolItem = mutableMapOf<String, Any?>()//create a new map
                symbolItem["name"] = name//save the name as key
                symbolItem["drawable"] = drawable//save drawable as item
                symbolArray.add(symbolItem)//ad the new map to the list
            }
        }

        adapterSymbol = SymbolAdapter(symbolArray)
        SymbolRecyclerView?.layoutManager= LinearLayoutManager(this)
        SymbolRecyclerView?.adapter = adapterSymbol
        // Initialize the Player  adapter and set it to the playerList RecyclerView
        createPlayerRecycler()//updates player recyclerView

        //Pop up with player indication
        showMessageBox(//show winning message
            "Select a player or add one",
            "OK"
        ) {
            //do nothing
        }
    }

    fun symbolSelection(view: View){//to trigger symbol selection interface
        playerSelection?.isVisible = false//set player recyclerView invisible
        symbolSelection?.isVisible = true//set player recyclerVIew visiblee
        playerNameTextView = view.findViewById<TextView>(R.id.playerName).text.toString()//get the name of the player selected
        findViewById<TextView>(R.id.selectedPlayerSymbol).text = playerNameTextView + " select a symbol";//display which player picks symbol
    }
    fun selectSymbol(view:View){//to select a symbol
        val playerToFind = playerArray.find { it.playerName == playerNameTextView }//get player selected from the array

        if (playerToFind != null) {//if player is not null
            playerToFind.selected = true// changes selected attribute to true
            playerToFind.symbol = view.findViewById<TextView>(R.id.symbolName).text.toString()//get the name of the selected symbol resource string
        } else {
            return //do nothing
        }
        selectedPlayers.add(playerToFind)//add the found player into the selected player array
        createPlayerRecycler()//update player recyclerVIew to remove selected player
        playerSelection?.isVisible = true//set player recylerView visible
        symbolSelection?.isVisible = false//set symbol recyclerView invisible

        if(selectedPlayers.size == 2){//if 2 players are selected
            //launch next activity
            val intent = Intent(this, GameActivity::class.java)//create intent f for game_activity

            // Pass data to the next activity using extras
            intent.putExtra("player1", selectedPlayers[0])//pass 1st player selected
            intent.putExtra("player2", selectedPlayers[1])//pass 2nd player selected
            intent.putExtra("playerFile",playerFile)//pass the player file
            intent.putExtra("playerList", playerArray)//pass the player list
            startActivity(intent) // Start the next activity
        }
    }

    fun onClickAdd(view: View) { //to add players in the game
        if (playerName != null) {//if player is not null
            val playerNameText = playerName!!.text.toString()//get the player name
            val initialScore = 0;//set initial score to 0
            val player = Player(playerNameText, initialScore,selected = false, symbol = null)//Create a Player object
            playerArray.add(player)//adds it to the array
        }
        writeFile(playerArray)//save the new player
        createPlayerRecycler()//update player recyclerView
    }



private fun loadPlayersFromFile() {
//to load players from file
    try{
        if (playerFile.exists()) {//if file exist
            val stringData= playerFile.bufferedReader().readText()//read the file content

            val entries = stringData.split(";")//split players
            for (entry in entries) {
                val parts = entry.split(",")//split name and score
                if (parts.size == 2) {//if strings has 2 parts
                    val playerName = parts[0]//1st part is the name
                    val playerScore = parts[1].toIntOrNull() ?: 0//second part is the score
                    val player = Player(playerName, playerScore, selected = false,symbol = null)//creates players
                    playerArray.add(player)// add it to array
                }
            }
        }
    }catch (e: IOException){//if error
        e.printStackTrace()//print error
    }

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
    fun createPlayerRecycler(){
        // Filter the playerArray to get only players with selected attribute set to false
        var filteredPlayerArray = playerArray.filter { !it.selected }

        // Create the adapter using the filtered list
        adapter = PlayerAdapter(filteredPlayerArray as ArrayList<Player>)
        PlayerRecyclerView?.layoutManager = LinearLayoutManager(this)
        PlayerRecyclerView?.adapter = adapter
    }
}





