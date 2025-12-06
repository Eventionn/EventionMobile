package com.example.evention

import com.example.evention.create.CreateScreenTests
import com.example.evention.login.LoginScreenTests
import com.example.evention.register.RegisterScreenTests
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    RegisterScreenTests::class,
    LoginScreenTests::class,
    SearchEventTest::class,
    EventJoinPaidTest::class,
    CreateScreenTests::class
)
class AllInstrumentationTests
