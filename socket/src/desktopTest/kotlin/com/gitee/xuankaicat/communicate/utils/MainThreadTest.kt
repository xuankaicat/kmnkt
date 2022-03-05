package com.gitee.xuankaicat.communicate.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import kotlin.concurrent.thread
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@ExperimentalCoroutinesApi
class MainThreadTest {

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testMainThread() = runTest {
        val mainThreadId = Thread.currentThread().id
        var threadId = -1L
        thread {
            assertNotEquals(mainThreadId, Thread.currentThread().id)
            mainThread {
                threadId = Thread.currentThread().id
            }
        }
        while (threadId == -1L) {
            delay(50)
        }
        assertEquals(mainThreadId, threadId)
    }
}