package com.example.evention.ui.screens.home.details

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.evention.mock.MockData
import com.example.evention.mock.MockUserData
import com.example.evention.model.Event
import com.example.evention.ui.components.eventDetails.EventDescription
import com.example.evention.ui.components.eventDetails.EventDetailsRow
import com.example.evention.ui.theme.EventionBlue
import com.example.evention.ui.theme.EventionTheme
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.evention.ui.screens.home.payment.PaymentViewModel
import com.example.evention.ui.screens.ticket.TicketScreenViewModel
import com.google.gson.Gson


fun formatDate(date: Date): String {
    val localDate = date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val formatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH)
    return localDate.format(formatter)
}

fun formatTime(date: Date): String {
    val localDateTime = date.toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    val formatter = DateTimeFormatter.ofPattern("EEEE, hh:mm a", Locale.ENGLISH)
    return localDateTime.format(formatter)
}

@Composable
fun getDrawableId(name: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@Composable
fun EventDetails(eventDetails: Event, modifier: Modifier = Modifier, navController: NavController, viewModel: TicketScreenViewModel = viewModel(), viewModelE: EventDetailsViewModel = viewModel()) {
    val viewModelT: TicketScreenViewModel = viewModel()
    val ticketId by viewModelT.ticketId.collectAsState()
    var navigateToPayment by remember { mutableStateOf(false) }

    LaunchedEffect(ticketId) {
        if (!ticketId.isNullOrBlank() && navigateToPayment) {
            val eventJson = Uri.encode(Gson().toJson(eventDetails))
            navController.navigate("payment/${eventJson}/$ticketId")
            navigateToPayment = false
            viewModel.clearCreateResult()
        }
    }

    var hasError by remember { mutableStateOf(false) }

    eventDetails.let { event ->
        val user by viewModelE.user.collectAsState()

        LaunchedEffect(event.userId) {
            if (viewModelE.user.value == null) {
                viewModelE.loadUserById(event.userId)
            }
        }

        val imageUrl = "https://10.0.2.2:5010/event${event.eventPicture}"
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    if (hasError) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        )
                    } else {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Event Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            onError = { hasError = true }
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(24.dp)
                            .size(28.dp)
                            .clickable {
                                navController.popBackStack()
                            }
                    )
                }

                Text(
                    text = event.name,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 25.dp, vertical = 18.dp)
                )

                EventDetailsRow(
                    icon = Icons.Outlined.DateRange,
                    contentDescription = "Calendar",
                    title = formatDate(event.startAt),
                    subtitle = formatTime(event.endAt)
                )

                val address = event.addressEvents.firstOrNull()

                EventDetailsRow(
                    icon = Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    title = address?.localtown ?: "Unknown location",
                    subtitle = address?.road ?: "Unknown road"
                )

                val isLoadingUser = user == null

                EventDetailsRow(
                    icon = Icons.Filled.Person,
                    contentDescription = "Person",
                    title = if (isLoadingUser) "A carregar utilizador..." else user!!.username,
                    subtitle = "",
                    rating = if (isLoadingUser) 0.0 else 4.8,
                    onClick = if (!isLoadingUser) {
                        {
                            val userJson = Uri.encode(Gson().toJson(user))
                            navController.navigate("profile?userJson=$userJson")
                        }
                    } else null
                )


                EventDescription(event)
            }

            Button(
                onClick = {

                    viewModel.createTicket(eventDetails.eventID)
                    navigateToPayment = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EventionBlue),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "BUY TICKET ${event.price}€",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun Preview() {
    EventionTheme {
        val navController = rememberNavController()
        val event = MockData.events.first()
        EventDetails(MockData.events.first(), navController = navController)
    }
}