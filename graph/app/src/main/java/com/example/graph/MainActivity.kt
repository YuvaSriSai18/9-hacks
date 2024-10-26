@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.graph


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.graph.ui.theme.GraphTheme

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object Signup : Routes("Signup")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GraphTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),Datasource().loadList()
                    )
                }
            }

        }
    }
}



@Composable

fun Greeting(modifier: Modifier = Modifier,dataList:List<Data>) {
    var next by remember {
        mutableIntStateOf(1)
    }
    Scaffold (
        topBar ={ CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.largeTopAppBarColors(
                containerColor =MaterialTheme.colorScheme.primaryContainer
            ),
            title = {
                Text(text = "App",
                fontWeight = FontWeight.Bold
            )
            }
        )
        }
    ){ padding ->
        Spacer(modifier = Modifier.height(500.dp))
        if (next==1) {
            Column (verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(padding)){

                Image(painter = painterResource(id = R.drawable.datacent),
                    contentDescription = "Data Centre",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
                Column {
                    Text(text = "Charts",
                        modifier = Modifier
                            .padding(start = 20.dp, bottom = 20.dp),
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                        )

                    Row {
                        Button(onClick = { next = 2 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)){
                            ImageRes(next = 1)
                        }
                        Button(onClick = { next = 3 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            ImageRes(next = 2)
                        }
                        Button(onClick = { next = 4 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            ImageRes(next = 3)
                        }
                        Button(onClick = { next = 5 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)) {
                            ImageRes(next = 4)
                        }
                    }

                }
                LazyColumn (modifier = Modifier) {
                    items(dataList){ data ->
                        TextF(data = data)


                    }




                }
            }
        }
        else{
            Values(next)

        }

    }

}

@Composable
fun TextF(data: Data){
    Spacer(modifier = Modifier.padding(50.dp))
    Column (horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()){
        Text(text = "Temperature: ${data.temperature}",
            fontSize = 20.sp,
            )
        Text(text = "Humidity: ${data.humidity}",
            fontSize = 20.sp)
        Text(text = "Hydrogen: ${data.hydrogen}",
            fontSize = 20.sp)
        Text(text = "Gas leakage: ${data.lpg}",
            fontSize = 20.sp)
        Text(text = "Smoke detection: ${data.smoke}",
            fontSize = 20.sp)
        Text(text = "Carbon Monoxide: ${data.carbonMonoxide}",
            fontSize = 20.sp)

    }


}




@Composable
fun Values(a: Int){

    var stringData = when(a){
        2 -> "Weekly temperature data"
        3 -> "Weekly humidity data"
        4 -> "Weekly hydrogen data"
        else -> "Weekly Carbon Monoxide data"
    }

    Column (verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()){
        Text(text = stringData,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 80.dp, bottom = 70.dp)

        )
        LineCharts(a)
    }

}

@Composable
fun ImageRes(next: Int,modifier: Modifier = Modifier){



    val imageResource = when (next){
        1 -> R.drawable.temp
        2 -> R.drawable.humidity
        3 -> R.drawable.hydrogen
        else -> R.drawable.carbonm
    }
    Image(painter = painterResource(id = imageResource),
        contentDescription = "Air logo",
        modifier = Modifier
            .height(40.dp)
            .padding(start = 10.dp)
            .fillMaxHeight()
            )

}




@Preview(showBackground = true,
    showSystemUi = true)
@Composable
fun GreetingPreview() {
    GraphTheme {
        Greeting(modifier = Modifier,Datasource().loadList())
    }
}