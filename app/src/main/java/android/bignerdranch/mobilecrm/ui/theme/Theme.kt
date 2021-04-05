package android.bignerdranch.mobilecrm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
        primary = PrimaryColor,
        primaryVariant = PrimaryDarkColor,
        secondary = SecondaryColor,
        //onPrimary = LightPrimaryLightColor,
        //secondaryVariant = SecondaryLightColor,
        //onSecondary = PrimaryTextColor,
        //onSurface = Color.White
)

private val LightColorPalette = lightColors(
        primary = PrimaryColor,
        primaryVariant = PrimaryDarkColor,
        secondary = SecondaryColor,
        //onPrimary = PrimaryTextColor,
        //secondaryVariant = SecondaryLightColor,
        //onSecondary = PrimaryDarkColor,

        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MyTestComposeTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
    )
}