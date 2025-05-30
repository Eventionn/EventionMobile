package com.example.evention.ui.screens.auth.register

import AuthTextField
import AuthConfirmButton
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.evention.R
import com.example.evention.data.remote.authentication.RegisterViewModelFactory
import com.example.evention.ui.components.auth.AuthGoogle
import com.example.evention.ui.screens.auth.login.LoginScreenViewModel
import com.example.evention.ui.theme.EventionTheme


@Composable
fun RegisterScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: RegisterScreenViewModel = viewModel(factory = RegisterViewModelFactory(context))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Arrow Back",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 16.dp)
                .size(24.dp)
                .clickable { navController.navigate("signIn") },
            tint = Color.Black
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Sign up",
            fontSize = 24.sp,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(22.dp))

        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmpassword by remember { mutableStateOf("") }

        AuthTextField(
            placeholderText = "Full Name",
            iconResId = R.drawable.profile,
            value = username,
            password = false,
            onValueChange = { username = it }
        )

        Spacer(modifier = Modifier.height(22.dp))

        AuthTextField(
            placeholderText = "abc@email.com",
            iconResId = R.drawable.mail,
            value = email,
            password = false,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(22.dp))

        AuthTextField(
            placeholderText = "Your Password",
            iconResId = R.drawable.lock,
            value = password,
            password = true,
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(22.dp))

        AuthTextField(
            placeholderText = "Confirm Password",
            iconResId = R.drawable.lock,
            value = confirmpassword,
            password = true,
            onValueChange = { confirmpassword = it }
        )

        Spacer(modifier = Modifier.height(50.dp))

        AuthConfirmButton("SIGN UP", onClick = {
            Log.d("RegisterScreen", "SIGN UP button clicked")
            viewModel.register(username, email, password, confirmpassword)
        })

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            "OR",
            color = Color(0xFF9D9898),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )

        Spacer(modifier = Modifier.height(30.dp))

        AuthGoogle("Sign Up with Google", onClick = { /* Lógica do login c/google */ })

        Spacer(modifier = Modifier.height(120.dp))

        Row {
            Text(
                text = "Already have an account?",
                color = Color(0xFF120D26),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
            )
            Text(
                text = " Sign in",
                modifier = Modifier.clickable { navController.navigate("signIn") },
                color = Color(0xFF5669FF),
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
            )
        }

    }

}

@Preview(showBackground = true)
@Composable
fun Preview() {
    EventionTheme {
        val navController = rememberNavController()
        RegisterScreen(navController = navController)
    }
}