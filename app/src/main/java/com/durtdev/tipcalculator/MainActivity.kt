package com.durtdev.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.durtdev.tipcalculator.ui.theme.TipCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                TotalPerPerson()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    TipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

//@Preview
@Composable
fun TotalPerPerson(totalPerPerson: Double = 0.0) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(100.dp),
        elevation = 5.dp,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        backgroundColor = Color.LightGray,
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "R$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {

        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
    }
}