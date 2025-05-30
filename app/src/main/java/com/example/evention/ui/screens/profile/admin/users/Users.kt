package com.example.evention.ui.screens.profile.admin.users

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
import com.example.evention.model.User
import com.example.evention.ui.components.TitleComponent
import com.example.evention.ui.components.admin.users.UsersListRow
import com.example.evention.ui.theme.EventionTheme

@Composable
fun AllUsers(users: List<User>, navController: NavController, viewModel: UsersViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp, vertical = 18.dp)
    ) {

        TitleComponent("Users", true, navController)

        if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.noevents),
                        contentDescription = "No Users"
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No users yet",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        } else {
            LazyColumn {
                items(users.size) { index ->
                    UsersListRow(
                        user = users[index],
                        onEdit = { navController.navigate("userEdit/${users[index].userID}") },
                        onRemove = { viewModel.deleteUser(users[index].userID) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllUsersPreview() {
    EventionTheme {
        val navController = rememberNavController()
        val users = listOf<User>()
        AllUsers(users, navController)
    }
}