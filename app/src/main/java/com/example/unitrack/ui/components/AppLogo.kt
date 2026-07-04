package com.example.unitrack.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.unitrack.R

@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    width: Dp = 150.dp
) {
    Image(
        painter = painterResource(id = R.drawable.unitrack_logo),
        contentDescription = "Logótipo UniTrack",
        modifier = modifier.width(width)
    )
}