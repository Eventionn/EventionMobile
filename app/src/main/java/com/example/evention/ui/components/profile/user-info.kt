package com.example.evention.ui.components.profile

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evention.model.User
import com.example.evention.ui.screens.home.details.getDrawableId
import com.example.evention.ui.theme.EventionBlue
import com.google.gson.Gson

@Composable
fun UserInfo(user: User, navController: NavController){
    val imageUrl = user.profilePicture?.let { "http://10.0.2.2:5010/user/$it" }
    var hasError by remember { mutableStateOf(false) }

    if (user.profilePicture == null || hasError) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = "User Profile Image",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape),
            onError = { hasError = true }
        )
    }
    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = user.username,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            val userJson = Uri.encode(Gson().toJson(user))
            navController.navigate("userEdit/$userJson")
        },
        modifier = Modifier
            .size(154.dp, 50.dp)
            .border(1.dp, EventionBlue, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = "Edit Profile",
            color = EventionBlue,
            fontWeight = FontWeight.Bold
        )
    }
}