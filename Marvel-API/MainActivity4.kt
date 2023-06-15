package com.example.finalproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import coil.compose.SubcomposeAsyncImage
import com.example.finalproject.api.MarvelService
import com.example.finalproject.data.CharacterInformation
import com.example.finalproject.data.resultsInformation
//import com.example.finalproject.data.imageUrl
import com.example.finalproject.ui.theme.FinalprojectTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest
import kotlin.random.Random

var charactersId = 0
var charactersIndex = 0
var characterList: MutableList<List<resultsInformation>> = mutableListOf()

class MainActivity4 : ComponentActivity() {
    private val marvelService = MarvelService.create()

    /*** Initialization for API call ***/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinalprojectTheme {
                MaterialTheme(
                    colors = MaterialTheme.colors.copy(
                        background = MaterialTheme.colors.secondaryVariant
                    )
                ) {

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        val randomValues =
                            List(2) { Random.nextInt(0, 100) } // 0 = offset, 1 = limit
                        val randomChars =
                            List(2) { Random.nextInt(0, randomValues[0]) }// 0 = char1, 1 = char 2
                        dosearch(
                            randomChars[0],
                            100,
                            0,
                            "1837a09a56a1cb936e2c27377c350017",
                            0
                        )
                    }
                }
            }
        }
    }

    /*** API Call ***/
    private fun dosearch(charIndex: Int, limit: Int, offset: Int, key : String, isDrawer: Int){
        val timestamp = System.currentTimeMillis().toString()
        val hash = md5(timestamp + "c8bcb840fe5dfdf33aa38e92c94d9a638b18a3f9" + key)
        var offsetOffset = 0
        for (i in 1..5) {
            marvelService.searchMultipleChar(limit, offset + offsetOffset, key, hash, timestamp)
                .enqueue(object : Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.d("4th activity", "Status code: ${response.code()}")
                        Log.d("4th activity", "Response body: ${response.body()}")

                        CharacterInformation(response.body()!!).data.results
                        parseJson(response.body()!!, charIndex, isDrawer)

                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("MainActivity", "Error making API call: ${t.message}")
                    }
                })
            offsetOffset += 100
        }
    }

    /*** API Call ***/
    private fun parseJson(json:String, charIndex: Int, isDrawer: Int){

        val foos2 = CharacterInformation(json)
        Log.d("MainActivity4", "Name: ${foos2.data.results!![charIndex].name}")
        Log.d("MainActivity4", "Description: ${foos2.data.results!![charIndex].description}")
        Log.d("MainActivity4", "Good path: ${foos2.data.results[charIndex].thumbnail.imageUrl()}")
        characterList.add(foos2.data.results)


        setContent {
            FinalprojectTheme {
                MaterialTheme(
                    colors = MaterialTheme.colors.copy(
                        background = MaterialTheme.colors.secondaryVariant
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        charactersId = foos2.data.results[charIndex].id
                        Activity4Screen(characters = foos2.data.results)
                    }
                }
            }
        }

    }

// loosely adapted from: https://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l
    fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val messageDigest = md.digest(input.toByteArray())
        val hexString = StringBuilder()
        for (i in messageDigest.indices) {
            val hex = Integer.toHexString(0xFF and messageDigest[i].toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }

}


@Composable
private fun heroWithImage(character: resultsInformation?, popup: MutableState<Boolean>){
    val dialog = remember {
        mutableStateOf("")
    }
    val scrollState = rememberScrollState()
    SubcomposeAsyncImage(
        model = character?.thumbnail?.imageUrl(),
        loading = {
            CircularProgressIndicator()
        },
        contentDescription = "thing",
        modifier = Modifier
            .size(500.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(96.dp))
            .padding(top = 0.dp)
    )
    if (character != null) {
        var size: Double = 75 - character.name!!.length.toDouble()
        if (character.name!!.length > 10) {
            size = 70 - (character.name!!.length * 1.25)
            if (character.name!!.length > 15) {
                size = 70 - (character.name!!.length * 1.5)
                if (character.name!!.length > 20) {
                    size = 70 - (character.name!!.length * 1.5)
                }
            }
        }
        Text(text = character.name!!, color=Color.White, fontSize = size.sp)
        if (character.description != "") {
            if (!popup.value) {
                dialog.value = "View Character Description Here!"
                Text(text = dialog.value, color=Color.White, modifier = Modifier.clickable {
                    popup.value = true
                })
            }
            else {
                dialog.value = "Hide Character Description"
                Text(text = dialog.value, color=Color.White, modifier = Modifier.clickable {
                    popup.value = false
                })
            }
        }
        else {
            dialog.value = "No Character Description Available"
            Text(text = dialog.value, color=Color.White)
        }

        if (popup.value) {
            Popup(
                alignment = Alignment.TopCenter,
                properties = PopupProperties()
            ) {
                Box(
                    modifier = Modifier
                        .size(500.dp)
                        .padding(top = 5.dp)
                        .background(color = MaterialTheme.colors.primary, RoundedCornerShape(10.dp))
                        .border(1.dp, color = Color.Black, RoundedCornerShape(10.dp))
                        .verticalScroll(scrollState)
                ) {
                    Text(text = character.description!!, color = Color.White,fontSize = 50.sp, modifier = Modifier.padding(5.dp))
                }
            }
        }
    }
}


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Activity4Screen(characters: List<resultsInformation>) {
    var subsetIndex: MutableState<Int> = remember {
        mutableStateOf(charactersIndex)
    }
    Log.d("MainActivity4", "char list size: ${characterList.size - 1}")
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()
    val indexOfCharacterInSubsetDatabase: MutableState<Int> = remember {
        mutableStateOf(charactersIndex)
    }
    val indexOfCharacterInDatabase: MutableState<Int> = remember {
        mutableStateOf(charactersIndex)
    }
    val popup = remember {
        mutableStateOf(false)
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("The Marvel Universe") },
                backgroundColor = MaterialTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = {
                        characterList.clear()
                        context.startActivity(Intent(context, MainActivity::class.java))
                    })
                    {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = {
            /*** Image, Name, and Description ***/
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (characters != null) {
                        for (ind in 1..characterList.size) {
                            if (indexOfCharacterInDatabase.value < (100 * ind) ) {
                                subsetIndex.value = ind - 1
                                indexOfCharacterInSubsetDatabase.value = indexOfCharacterInDatabase.value - (100 * (ind - 1))
                                break
                            }
                            else {
                                subsetIndex.value = 0
                            }
                        }
                        var characterListIndex = characterList[subsetIndex.value]
                        heroWithImage(character = characterListIndex[indexOfCharacterInSubsetDatabase.value], popup)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    /*** Previous Character Button ***/
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.Start,
                    ) {

                        Button(
                            onClick = {
                                if (indexOfCharacterInDatabase.value - 1 >= 0) {
                                    indexOfCharacterInDatabase.value -= 1
                                    popup.value = false
                                }
                            },
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                top = 12.dp,
                                end = 20.dp,
                                bottom = 12.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Previous Hero",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        /*** Next Character Button ***/
                        Button(
                            onClick = {
                                if (indexOfCharacterInDatabase.value + 1 < characterList.size * 100) {
                                    indexOfCharacterInDatabase.value += 1
                                    popup.value = false
                                }
                            },
                            contentPadding = PaddingValues(
                                start = 20.dp,
                                top = 12.dp,
                                end = 20.dp,
                                bottom = 12.dp
                            ),
                        ) {
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Next Hero",
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                        }
                    }
                }
            }


        },
        drawerContent = {
            /*** Drawer Creation ***/
            Column(
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.secondaryVariant)
                    .verticalScroll(scrollState)

            ) {
                // Repeat is a loop which
                // takes count as argument
                if (characters != null) {

                    /*** Sorting The Drawer ***/
                    while (true) {
                        var errorCount = 0
                        repeat(characterList.size) { index ->
                            val characterListIndex = characterList[index]
                            if (index + 1 < characterList.size) {
                                val characterListIndex2 = characterList[index + 1]
                                val individualCharacter = characterListIndex[0].name
                                val individualCharacter2 = characterListIndex2[0].name
                                if (individualCharacter!![0] > individualCharacter2!![0]) {
                                    val storage = characterList[index]
                                    characterList[index] = characterList[index + 1]
                                    characterList[index + 1] = storage
                                    errorCount += 1
                                }
                            }
                        }

                        if (errorCount == 0) {
                            break
                        }
                    }

                    /*** Clicking On A Character Within Drawer ***/
                    repeat(characterList.size) { index ->
                        val characterListIndex = characterList[index]
                        repeat(characterListIndex.size) { item ->
                            Text(text = characterListIndex[item].name!! ,Modifier
                                .padding(8.dp)
                                .clickable {
                                    indexOfCharacterInDatabase.value = item + ((index) * 100)
                                },
                            style = TextStyle(color = Color.White)
                            )

                        }
                    }
                }
            }
        },
        floatingActionButton = {
            /*** Open Drawer Button ***/
            ExtendedFloatingActionButton(
                text = { Text("Character List") },
                onClick = {
                    scope.launch {
                        scaffoldState.drawerState.apply {
                            if (isClosed) open() else close()
                        }
                    }
                    popup.value = false
                },
            )
        },

        )

}
