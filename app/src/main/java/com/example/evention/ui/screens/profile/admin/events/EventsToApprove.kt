package com.example.evention.ui.screens.profile.admin.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evention.R
import com.example.evention.model.Event
import com.example.evention.ui.components.TitleComponent
import com.example.evention.ui.components.admin.events.EventListRow
import com.example.evention.ui.theme.EventionTheme

@Composable
fun EventsToApprove(events: List<Event>, navController: NavController, viewModel: EventsToApproveViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 18.dp)
    ) {

        TitleComponent("Approve Events", true, navController)

        if (events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.noevents),
                        contentDescription = "No Events"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No suspended events yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        } else {

            LazyColumn {
                items(events.size) { index ->
                    EventListRow(
                        event = events[index],
                        firstSection = "Approve event",
                        secondSection = "Reject event",
                        onEdit = { viewModel.approveEvent(events[index].eventID) },
                        onRemove = { viewModel.deleteEvent(events[index].eventID) },
                        navController = navController
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsToApprovePreview() {
    EventionTheme {
        val navController = rememberNavController()
        var events = listOf<Event>()
        EventsToApprove(events, navController)
    }
}