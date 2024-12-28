package ru.mixail_akulov.interactions

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.mixail_akulov.interactions.ui.theme.InteractionsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InteractionsTheme {
                CompositionLocalProvider(
                    LocalIndication provides CustomIndicationNodeFactory
                ) {
                    App()
                }
             }
        }
    }
}

@Composable
fun App() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        val ineractionSource = remember {
//            MutableInteractionSource()
//        }
//        val isPressed by ineractionSource.collectIsPressedAsState()
//        val scaleFactor = if (isPressed) 1.3f else 1.0f
//        val animatedScaleFactor by animateFloatAsState(targetValue = scaleFactor)
//        val indication = ripple(
//            color = Color.Red,
//            bounded = false,
//            radius = 100.dp
//        )
        val context = LocalContext.current
        Text(
            text = "Click Me",
            fontSize = 20.sp,
            modifier = Modifier
                .clickable {
                    Toast
                        .makeText(context, "Clicked", Toast.LENGTH_SHORT)
                        .show()
                }
//                .clickable(
//                    onClick = {
//                        Toast
//                            .makeText(context, "Clicked", Toast.LENGTH_SHORT)
//                            .show()
//                    },
//                    interactionSource = ineractionSource,
//                    indication = CustomIndicationNodeFactory
//                )
                .padding(16.dp)
        )
//        LaunchedEffect(Unit) {
//            launch {
//                delay(2000)
//                val press = PressInteraction.Press(Offset.Zero)
//                ineractionSource.emit(press)
//                delay(2000)
//                ineractionSource.emit(PressInteraction.Release(press))
//            }
//            ineractionSource.interactions.collect { interaction ->
//                println("AAA $interaction")
//            }
//        }
    }
}

data object CustomIndicationNodeFactory : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return CustomIndicationNode(interactionSource)
    }
}

private class CustomIndicationNode(
    private val interactionSource: InteractionSource
) : Modifier.Node(), DrawModifierNode {
    private val animatedScaleFactor = Animatable(1f)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collectLatest { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> animatedScaleFactor.animateTo(1.3f)
                    else -> animatedScaleFactor.animateTo(1f)
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        scale(animatedScaleFactor.value) {
            this@draw.drawContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    InteractionsTheme {
        App()
    }
}