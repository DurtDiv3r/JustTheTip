package com.islaharper.tipcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.islaharper.tipcalculator.components.InputField
import com.islaharper.tipcalculator.components.Loader
import com.islaharper.tipcalculator.ui.theme.TipCalculatorTheme
import com.islaharper.tipcalculator.utils.calculateTip
import com.islaharper.tipcalculator.utils.calculateTotalPerPerson
import com.islaharper.tipcalculator.widgets.RoundIconButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
//                Navigation()
                MainContent()
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

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {

    val splitBillState = remember {
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val range = IntRange(start = 1, endInclusive = 20)

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    BillForm(
        range = range,
        splitBillState = splitBillState,
        tipAmountState = tipAmountState,
        totalPerPersonState = totalPerPersonState
    ) {
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitBillState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChange: (String) -> Unit = {}
) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Surface(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TotalPerPerson(totalPerPerson = totalPerPersonState.value)

            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )

            if (validState) {
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Default.Remove, onClick = {
                            if (splitBillState.value > range.first) {
                                splitBillState.value -= 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBillState = splitBillState.value,
                                    tipPercentage = tipPercentage
                                )

                            }
                        })
                        Text(
                            text = splitBillState.value.toString(), modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 9.dp)
                        )
                        RoundIconButton(imageVector = Icons.Default.Add, onClick = {
                            if (splitBillState.value < range.last) {
                                splitBillState.value += 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBillState = splitBillState.value,
                                    tipPercentage = tipPercentage
                                )

                            }
                        })
                    }
                }

                //Tip Row
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Row(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "R${"%.2f".format(tipAmountState.value)}",
                            modifier = Modifier.align(alignment = Alignment.CenterVertically)
                        )

                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPercentage%")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(
                        value = sliderPositionState.value, onValueChange = { newValue ->
                            sliderPositionState.value = newValue
                            tipAmountState.value = calculateTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage
                            )
                            totalPerPersonState.value = calculateTotalPerPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBillState = splitBillState.value,
                                tipPercentage = tipPercentage
                            )
                        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 19
                    )
                }
            } else {
                Box {

                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
    }
}


@ExperimentalComposeUiApi
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        // Main Screen
        composable("main_screen") {
            MainContent()
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun SplashScreen(navController: NavController) {
    // AnimationEffect
    Loader()

    CoroutineScope(Dispatchers.Main).launch {
        delay(3000L)
        navController.navigate("main_screen")
    }

}