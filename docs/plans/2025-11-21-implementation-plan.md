# Anti-Procrastination App Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a self-contained Android app (API 33+) that helps users stay accountable to scheduled tasks through lock screen widgets, notifications, and detailed work pattern tracking.

**Architecture:** MVVM with Jetpack Compose UI, Repository pattern for data access, WorkManager for scheduling, Glance API for lock screen widgets, local JSON storage.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3, Glance, WorkManager, Kotlinx Serialization, Kotlinx DateTime

---

## Phase 1: Project Setup & Dependencies

### Task 1: Create Android Project Structure

**Files:**
- Create: `app/build.gradle.kts`
- Create: `settings.gradle.kts`
- Create: `gradle.properties`
- Create: `build.gradle.kts` (root)

**Step 1: Create root build.gradle.kts**

```kotlin
// Top-level build file
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20" apply false
}
```

**Step 2: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AntiProcrastinator"
include(":app")
```

**Step 3: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=false
kotlin.code.style=official
android.nonTransitiveRClass=true
```

**Step 4: Create app/build.gradle.kts**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.antiprocrastinator.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.antiprocrastinator.app"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Glance (Lock Screen Widget)
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // DateTime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.01.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

**Step 5: Create AndroidManifest.xml**

Create: `app/src/main/AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.AntiProcrastinator">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.AntiProcrastinator">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

**Step 6: Create basic directory structure**

```bash
mkdir -p app/src/main/java/com/antiprocrastinator/app/{data/{model,repository,storage},ui/{screens,components,theme},widget,worker}
mkdir -p app/src/test/java/com/antiprocrastinator/app
mkdir -p app/src/main/res/{values,mipmap-hdpi,mipmap-mdpi,mipmap-xhdpi,mipmap-xxhdpi,mipmap-xxxhdpi}
```

**Step 7: Create basic resources**

Create: `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name">Anti-Procrastinator</string>
</resources>
```

Create: `app/src/main/res/values/themes.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.AntiProcrastinator" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
```

**Step 8: Commit**

```bash
git add .
git commit -m "feat: initial Android project setup with dependencies"
```

---

## Phase 2: Data Models & Serialization

### Task 2: Define Core Data Models

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/data/model/Task.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CheckIn.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/data/model/TaskLifecycleState.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CompletionStatus.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CheckInStatus.kt`

**Step 1: Create CheckInStatus enum**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CheckInStatus.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CheckInStatus {
    WORKING,
    TAKING_BREAK,
    DISTRACTED,
    GAVE_UP
}
```

**Step 2: Create CompletionStatus enum**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CompletionStatus.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CompletionStatus {
    DONE,
    GAVE_UP
}
```

**Step 3: Create TaskLifecycleState enum**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/TaskLifecycleState.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskLifecycleState {
    SCHEDULED,
    IN_PROGRESS,
    ENDED
}
```

**Step 4: Create CheckIn data class**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/CheckIn.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CheckIn(
    val timestamp: Instant,
    val status: CheckInStatus,
    val note: String? = null
)
```

**Step 5: Create custom LocalDateTime serializer**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/LocalDateTimeSerializer.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString())
    }
}
```

**Step 6: Create Task data class**

Create: `app/src/main/java/com/antiprocrastinator/app/data/model/Task.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDateTime: LocalDateTime,
    val lifecycleState: TaskLifecycleState = TaskLifecycleState.SCHEDULED,
    val completionStatus: CompletionStatus? = null,
    val actualStartTime: Instant? = null,
    val actualEndTime: Instant? = null,
    val checkIns: List<CheckIn> = emptyList()
) {
    fun overlaps(other: Task): Boolean {
        return this.startDateTime < other.endDateTime &&
               this.endDateTime > other.startDateTime
    }
}
```

**Step 7: Write tests for Task.overlaps()**

Create: `app/src/test/java/com/antiprocrastinator/app/data/model/TaskTest.kt`

```kotlin
package com.antiprocrastinator.app.data.model

import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class TaskTest {

    @Test
    fun `overlaps returns true for same-day overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T10:30:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:30:00")
        )
        assertTrue(task1.overlaps(task2))
        assertTrue(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns true for cross-day overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T23:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-23T01:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-23T00:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-23T02:00:00")
        )
        assertTrue(task1.overlaps(task2))
        assertTrue(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns false for adjacent tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T11:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T12:00:00")
        )
        assertFalse(task1.overlaps(task2))
        assertFalse(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns false for non-overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T14:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T15:00:00")
        )
        assertFalse(task1.overlaps(task2))
        assertFalse(task2.overlaps(task1))
    }
}
```

**Step 8: Run tests**

```bash
./gradlew test
```

Expected: All tests pass

**Step 9: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/data/model/
git add app/src/test/java/com/antiprocrastinator/app/data/model/TaskTest.kt
git commit -m "feat: add core data models with serialization"
```

---

## Phase 3: JSON Storage Layer

### Task 3: Implement JSON Storage

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/data/storage/JsonStorage.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/data/storage/TasksData.kt`

**Step 1: Create TasksData wrapper**

Create: `app/src/main/java/com/antiprocrastinator/app/data/storage/TasksData.kt`

```kotlin
package com.antiprocrastinator.app.data.storage

import com.antiprocrastinator.app.data.model.Task
import kotlinx.serialization.Serializable

@Serializable
data class TasksData(
    val tasks: List<Task> = emptyList()
)
```

**Step 2: Create JsonStorage interface**

Create: `app/src/main/java/com/antiprocrastinator/app/data/storage/JsonStorage.kt`

```kotlin
package com.antiprocrastinator.app.data.storage

import android.content.Context
import com.antiprocrastinator.app.data.model.Task
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException

class JsonStorage(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val tasksFile: File
        get() = File(context.filesDir, TASKS_FILE_NAME)

    private val tempFile: File
        get() = File(context.filesDir, "$TASKS_FILE_NAME.tmp")

    @Throws(IOException::class)
    fun loadTasks(): List<Task> {
        if (!tasksFile.exists()) {
            return emptyList()
        }

        return try {
            val jsonString = tasksFile.readText()
            val tasksData = json.decodeFromString<TasksData>(jsonString)
            tasksData.tasks
        } catch (e: Exception) {
            // Backup corrupt file
            val backupFile = File(context.filesDir, "$TASKS_FILE_NAME.backup")
            if (tasksFile.exists()) {
                tasksFile.copyTo(backupFile, overwrite = true)
            }
            emptyList()
        }
    }

    @Throws(IOException::class)
    fun saveTasks(tasks: List<Task>) {
        val tasksData = TasksData(tasks)
        val jsonString = json.encodeToString(tasksData)

        // Atomic write: write to temp file, then rename
        tempFile.writeText(jsonString)
        tempFile.renameTo(tasksFile)
    }

    companion object {
        private const val TASKS_FILE_NAME = "tasks.json"
    }
}
```

**Step 3: Write tests for JsonStorage**

Create: `app/src/test/java/com/antiprocrastinator/app/data/storage/JsonStorageTest.kt`

```kotlin
package com.antiprocrastinator.app.data.storage

import android.content.Context
import com.antiprocrastinator.app.data.model.Task
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class JsonStorageTest {

    private lateinit var context: Context
    private lateinit var storage: JsonStorage
    private lateinit var tasksFile: File

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        storage = JsonStorage(context)
        tasksFile = File(context.filesDir, "tasks.json")
        tasksFile.delete() // Clean slate
    }

    @Test
    fun `loadTasks returns empty list when file does not exist`() {
        val tasks = storage.loadTasks()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `saveTasks and loadTasks round-trip successfully`() {
        val task = Task(
            title = "Test Task",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )

        storage.saveTasks(listOf(task))
        val loaded = storage.loadTasks()

        assertEquals(1, loaded.size)
        assertEquals(task, loaded[0])
    }

    @Test
    fun `loadTasks returns empty list and creates backup on corrupt file`() {
        tasksFile.writeText("not valid json")

        val tasks = storage.loadTasks()

        assertTrue(tasks.isEmpty())
        val backupFile = File(context.filesDir, "tasks.json.backup")
        assertTrue(backupFile.exists())
        assertEquals("not valid json", backupFile.readText())
    }
}
```

**Step 4: Add Robolectric dependency for Android unit tests**

Modify: `app/build.gradle.kts` (add to dependencies block)

```kotlin
    testImplementation("org.robolectric:robolectric:4.11.1")
```

**Step 5: Run tests**

```bash
./gradlew test
```

Expected: All tests pass

**Step 6: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/data/storage/
git add app/src/test/java/com/antiprocrastinator/app/data/storage/
git add app/build.gradle.kts
git commit -m "feat: implement JSON storage with atomic writes"
```

---

## Phase 4: Repository Layer

### Task 4: Create TaskRepository

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/data/repository/TaskRepository.kt`

**Step 1: Create TaskRepository**

Create: `app/src/main/java/com/antiprocrastinator/app/data/repository/TaskRepository.kt`

```kotlin
package com.antiprocrastinator.app.data.repository

import com.antiprocrastinator.app.data.model.CheckIn
import com.antiprocrastinator.app.data.model.CompletionStatus
import com.antiprocrastinator.app.data.model.Task
import com.antiprocrastinator.app.data.model.TaskLifecycleState
import com.antiprocrastinator.app.data.storage.JsonStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class TaskRepository(private val storage: JsonStorage) {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        _tasks.value = storage.loadTasks()
    }

    private fun saveTasks() {
        storage.saveTasks(_tasks.value)
    }

    fun addTask(task: Task): Result<Unit> {
        // Check for overlaps
        val overlapping = _tasks.value.find { it.id != task.id && it.overlaps(task) }
        if (overlapping != null) {
            return Result.failure(
                IllegalArgumentException("Conflicts with ${overlapping.title}")
            )
        }

        _tasks.value = _tasks.value + task
        saveTasks()
        return Result.success(Unit)
    }

    fun updateTask(task: Task): Result<Unit> {
        // Check for overlaps (excluding self)
        val overlapping = _tasks.value.find {
            it.id != task.id && it.overlaps(task)
        }
        if (overlapping != null) {
            return Result.failure(
                IllegalArgumentException("Conflicts with ${overlapping.title}")
            )
        }

        _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
        saveTasks()
        return Result.success(Unit)
    }

    fun deleteTask(taskId: String) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
        saveTasks()
    }

    fun getTask(taskId: String): Task? {
        return _tasks.value.find { it.id == taskId }
    }

    fun getTasksForDate(date: LocalDate): List<Task> {
        return _tasks.value.filter { task ->
            val taskStartDate = task.startDateTime.date
            val taskEndDate = task.endDateTime.date
            date >= taskStartDate && date <= taskEndDate
        }.sortedBy { it.startDateTime }
    }

    fun startTask(taskId: String, actualStartTime: Instant) {
        val task = getTask(taskId) ?: return
        val updated = task.copy(
            lifecycleState = TaskLifecycleState.IN_PROGRESS,
            actualStartTime = actualStartTime
        )
        updateTask(updated)
    }

    fun endTask(taskId: String) {
        val task = getTask(taskId) ?: return
        val updated = task.copy(lifecycleState = TaskLifecycleState.ENDED)
        updateTask(updated)
    }

    fun markTaskComplete(taskId: String, status: CompletionStatus, actualEndTime: Instant) {
        val task = getTask(taskId) ?: return
        val updated = task.copy(
            completionStatus = status,
            actualEndTime = actualEndTime,
            lifecycleState = TaskLifecycleState.ENDED
        )
        updateTask(updated)
    }

    fun addCheckIn(taskId: String, checkIn: CheckIn) {
        val task = getTask(taskId) ?: return
        val updated = task.copy(checkIns = task.checkIns + checkIn)
        updateTask(updated)
    }

    fun getActiveTask(): Task? {
        return _tasks.value.find { it.lifecycleState == TaskLifecycleState.IN_PROGRESS }
    }
}
```

**Step 2: Write tests for TaskRepository**

Create: `app/src/test/java/com/antiprocrastinator/app/data/repository/TaskRepositoryTest.kt`

```kotlin
package com.antiprocrastinator.app.data.repository

import android.content.Context
import com.antiprocrastinator.app.data.model.*
import com.antiprocrastinator.app.data.storage.JsonStorage
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class TaskRepositoryTest {

    private lateinit var context: Context
    private lateinit var storage: JsonStorage
    private lateinit var repository: TaskRepository

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        storage = JsonStorage(context)
        // Clean slate
        context.filesDir.listFiles()?.forEach { it.delete() }
        repository = TaskRepository(storage)
    }

    @Test
    fun `addTask successfully adds non-overlapping task`() {
        val task = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )

        val result = repository.addTask(task)

        assertTrue(result.isSuccess)
        assertEquals(1, repository.tasks.value.size)
    }

    @Test
    fun `addTask fails when task overlaps existing task`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T10:30:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:30:00")
        )

        repository.addTask(task1)
        val result = repository.addTask(task2)

        assertTrue(result.isFailure)
        assertEquals(1, repository.tasks.value.size)
    }

    @Test
    fun `getTasksForDate returns tasks on that date`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-23T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-23T11:00:00")
        )

        repository.addTask(task1)
        repository.addTask(task2)

        val tasksOnNov22 = repository.getTasksForDate(
            LocalDateTime.parse("2025-11-22T00:00:00").date
        )

        assertEquals(1, tasksOnNov22.size)
        assertEquals("Task 1", tasksOnNov22[0].title)
    }

    @Test
    fun `startTask updates task to IN_PROGRESS`() {
        val task = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        repository.addTask(task)

        val now = Clock.System.now()
        repository.startTask(task.id, now)

        val updated = repository.getTask(task.id)
        assertEquals(TaskLifecycleState.IN_PROGRESS, updated?.lifecycleState)
        assertEquals(now, updated?.actualStartTime)
    }

    @Test
    fun `markTaskComplete sets completion status and ends task`() {
        val task = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        repository.addTask(task)
        repository.startTask(task.id, Clock.System.now())

        val endTime = Clock.System.now()
        repository.markTaskComplete(task.id, CompletionStatus.DONE, endTime)

        val updated = repository.getTask(task.id)
        assertEquals(CompletionStatus.DONE, updated?.completionStatus)
        assertEquals(TaskLifecycleState.ENDED, updated?.lifecycleState)
        assertEquals(endTime, updated?.actualEndTime)
    }

    @Test
    fun `addCheckIn appends to task check-ins`() {
        val task = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        repository.addTask(task)

        val checkIn = CheckIn(
            timestamp = Clock.System.now(),
            status = CheckInStatus.WORKING
        )
        repository.addCheckIn(task.id, checkIn)

        val updated = repository.getTask(task.id)
        assertEquals(1, updated?.checkIns?.size)
        assertEquals(CheckInStatus.WORKING, updated?.checkIns?.get(0)?.status)
    }
}
```

**Step 3: Run tests**

```bash
./gradlew test
```

Expected: All tests pass

**Step 4: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/data/repository/
git add app/src/test/java/com/antiprocrastinator/app/data/repository/
git commit -m "feat: implement TaskRepository with overlap validation"
```

---

## Phase 5: WorkManager for Notifications

### Task 5: Create WorkManager Workers

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/worker/TaskStartWorker.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/worker/TaskEndWorker.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/worker/NotificationHelper.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/worker/WorkManagerScheduler.kt`

**Step 1: Create NotificationHelper**

Create: `app/src/main/java/com/antiprocrastinator/app/worker/NotificationHelper.kt`

```kotlin
package com.antiprocrastinator.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.antiprocrastinator.app.MainActivity
import com.antiprocrastinator.app.R

object NotificationHelper {

    private const val CHANNEL_ID = "task_notifications"
    private const val CHANNEL_NAME = "Task Notifications"

    const val ACTION_CHECK_IN = "com.antiprocrastinator.app.ACTION_CHECK_IN"
    const val ACTION_MARK_DONE = "com.antiprocrastinator.app.ACTION_MARK_DONE"
    const val ACTION_FINISHED = "com.antiprocrastinator.app.ACTION_FINISHED"
    const val ACTION_NOT_FINISHED = "com.antiprocrastinator.app.ACTION_NOT_FINISHED"

    const val EXTRA_TASK_ID = "task_id"
    const val EXTRA_CHECK_IN_STATUS = "check_in_status"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for task reminders"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    fun buildStartNotification(
        context: Context,
        taskId: String,
        taskTitle: String
    ): android.app.Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to work on $taskTitle")
            .setContentText("Tap to open app or log your status")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true) // Persistent notification
            .addAction(createCheckInAction(context, taskId, "WORKING"))
            .addAction(createCheckInAction(context, taskId, "TAKING_BREAK"))
            .addAction(createCheckInAction(context, taskId, "DISTRACTED"))
            .addAction(createCheckInAction(context, taskId, "GAVE_UP"))
            .addAction(createMarkDoneAction(context, taskId))
            .build()
    }

    fun buildEndNotification(
        context: Context,
        taskId: String,
        taskTitle: String
    ): android.app.Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Did you finish $taskTitle?")
            .setContentText("Tap to open app")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppPendingIntent)
            .addAction(createFinishedAction(context, taskId))
            .addAction(createNotFinishedAction(context, taskId))
            .setAutoCancel(true)
            .build()
    }

    private fun createCheckInAction(
        context: Context,
        taskId: String,
        status: String
    ): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_CHECK_IN
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_CHECK_IN_STATUS, status)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "$taskId-$status".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(
            0,
            status.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
            pendingIntent
        ).build()
    }

    private fun createMarkDoneAction(
        context: Context,
        taskId: String
    ): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_MARK_DONE
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "$taskId-done".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, "Mark Done", pendingIntent).build()
    }

    private fun createFinishedAction(
        context: Context,
        taskId: String
    ): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_FINISHED
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "$taskId-finished".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, "Finished", pendingIntent).build()
    }

    private fun createNotFinishedAction(
        context: Context,
        taskId: String
    ): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_NOT_FINISHED
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            "$taskId-not-finished".hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(0, "Didn't Finish", pendingIntent).build()
    }
}
```

**Step 2: Create NotificationActionReceiver**

Create: `app/src/main/java/com/antiprocrastinator/app/worker/NotificationActionReceiver.kt`

```kotlin
package com.antiprocrastinator.app.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.antiprocrastinator.app.data.model.CheckIn
import com.antiprocrastinator.app.data.model.CheckInStatus
import com.antiprocrastinator.app.data.model.CompletionStatus
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.data.storage.JsonStorage
import kotlinx.datetime.Clock

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(NotificationHelper.EXTRA_TASK_ID) ?: return
        val storage = JsonStorage(context)
        val repository = TaskRepository(storage)

        when (intent.action) {
            NotificationHelper.ACTION_CHECK_IN -> {
                val statusString = intent.getStringExtra(NotificationHelper.EXTRA_CHECK_IN_STATUS)
                val status = CheckInStatus.valueOf(statusString ?: return)

                val checkIn = CheckIn(
                    timestamp = Clock.System.now(),
                    status = status
                )
                repository.addCheckIn(taskId, checkIn)

                Toast.makeText(
                    context,
                    "Logged: ${status.name.replace("_", " ").lowercase()}",
                    Toast.LENGTH_SHORT
                ).show()

                // If "gave up", mark task complete and cancel notification
                if (status == CheckInStatus.GAVE_UP) {
                    repository.markTaskComplete(
                        taskId,
                        CompletionStatus.GAVE_UP,
                        Clock.System.now()
                    )
                    cancelNotification(context, taskId)
                    WorkManagerScheduler.cancelTaskEndWork(context, taskId)
                }
            }

            NotificationHelper.ACTION_MARK_DONE -> {
                repository.markTaskComplete(
                    taskId,
                    CompletionStatus.DONE,
                    Clock.System.now()
                )
                cancelNotification(context, taskId)
                WorkManagerScheduler.cancelTaskEndWork(context, taskId)
            }

            NotificationHelper.ACTION_FINISHED -> {
                repository.markTaskComplete(
                    taskId,
                    CompletionStatus.DONE,
                    Clock.System.now()
                )
                cancelNotification(context, taskId)
            }

            NotificationHelper.ACTION_NOT_FINISHED -> {
                repository.markTaskComplete(
                    taskId,
                    CompletionStatus.GAVE_UP,
                    Clock.System.now()
                )
                cancelNotification(context, taskId)
            }
        }
    }

    private fun cancelNotification(context: Context, taskId: String) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(taskId.hashCode())
    }
}
```

**Step 3: Create TaskStartWorker**

Create: `app/src/main/java/com/antiprocrastinator/app/worker/TaskStartWorker.kt`

```kotlin
package com.antiprocrastinator.app.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.data.storage.JsonStorage
import kotlinx.datetime.Clock

class TaskStartWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()

        val storage = JsonStorage(applicationContext)
        val repository = TaskRepository(storage)

        val task = repository.getTask(taskId) ?: return Result.failure()

        // Update task to IN_PROGRESS
        repository.startTask(taskId, Clock.System.now())

        // Show notification
        NotificationHelper.createNotificationChannel(applicationContext)
        val notification = NotificationHelper.buildStartNotification(
            applicationContext,
            taskId,
            task.title
        )

        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.notify(taskId.hashCode(), notification)

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
    }
}
```

**Step 4: Create TaskEndWorker**

Create: `app/src/main/java/com/antiprocrastinator/app/worker/TaskEndWorker.kt`

```kotlin
package com.antiprocrastinator.app.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.data.storage.JsonStorage

class TaskEndWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskId = inputData.getString(KEY_TASK_ID) ?: return Result.failure()

        val storage = JsonStorage(applicationContext)
        val repository = TaskRepository(storage)

        val task = repository.getTask(taskId) ?: return Result.failure()

        // Update task to ENDED
        repository.endTask(taskId)

        // Cancel start notification
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(taskId.hashCode())

        // If no completion status, show end notification
        if (task.completionStatus == null) {
            NotificationHelper.createNotificationChannel(applicationContext)
            val notification = NotificationHelper.buildEndNotification(
                applicationContext,
                taskId,
                task.title
            )
            notificationManager.notify(taskId.hashCode(), notification)
        }

        return Result.success()
    }

    companion object {
        const val KEY_TASK_ID = "task_id"
    }
}
```

**Step 5: Create WorkManagerScheduler**

Create: `app/src/main/java/com/antiprocrastinator/app/worker/WorkManagerScheduler.kt`

```kotlin
package com.antiprocrastinator.app.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.antiprocrastinator.app.data.model.Task
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {

    private const val TAG_TASK_START = "task_start_"
    private const val TAG_TASK_END = "task_end_"

    fun scheduleTaskNotifications(context: Context, task: Task) {
        val workManager = WorkManager.getInstance(context)

        // Schedule start notification
        val startTime = task.startDateTime.toInstant(TimeZone.currentSystemDefault())
        val startDelay = startTime.toEpochMilliseconds() - System.currentTimeMillis()

        if (startDelay > 0) {
            val startData = Data.Builder()
                .putString(TaskStartWorker.KEY_TASK_ID, task.id)
                .build()

            val startWork = OneTimeWorkRequestBuilder<TaskStartWorker>()
                .setInitialDelay(startDelay, TimeUnit.MILLISECONDS)
                .setInputData(startData)
                .addTag(TAG_TASK_START + task.id)
                .build()

            workManager.enqueueUniqueWork(
                TAG_TASK_START + task.id,
                ExistingWorkPolicy.REPLACE,
                startWork
            )
        }

        // Schedule end notification
        val endTime = task.endDateTime.toInstant(TimeZone.currentSystemDefault())
        val endDelay = endTime.toEpochMilliseconds() - System.currentTimeMillis()

        if (endDelay > 0) {
            val endData = Data.Builder()
                .putString(TaskEndWorker.KEY_TASK_ID, task.id)
                .build()

            val endWork = OneTimeWorkRequestBuilder<TaskEndWorker>()
                .setInitialDelay(endDelay, TimeUnit.MILLISECONDS)
                .setInputData(endData)
                .addTag(TAG_TASK_END + task.id)
                .build()

            workManager.enqueueUniqueWork(
                TAG_TASK_END + task.id,
                ExistingWorkPolicy.REPLACE,
                endWork
            )
        }
    }

    fun cancelTaskNotifications(context: Context, taskId: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(TAG_TASK_START + taskId)
        workManager.cancelUniqueWork(TAG_TASK_END + taskId)
    }

    fun cancelTaskEndWork(context: Context, taskId: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork(TAG_TASK_END + taskId)
    }
}
```

**Step 6: Register NotificationActionReceiver in AndroidManifest.xml**

Modify: `app/src/main/AndroidManifest.xml` (add inside `<application>` tag)

```xml
        <receiver
            android:name=".worker.NotificationActionReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.antiprocrastinator.app.ACTION_CHECK_IN" />
                <action android:name="com.antiprocrastinator.app.ACTION_MARK_DONE" />
                <action android:name="com.antiprocrastinator.app.ACTION_FINISHED" />
                <action android:name="com.antiprocrastinator.app.ACTION_NOT_FINISHED" />
            </intent-filter>
        </receiver>
```

**Step 7: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/worker/
git add app/src/main/AndroidManifest.xml
git commit -m "feat: implement WorkManager workers and notification system"
```

---

## Phase 6: UI Theme & Navigation

### Task 6: Setup Material 3 Theme

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Color.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Theme.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Type.kt`

**Step 1: Create Color.kt**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Color.kt`

```kotlin
package com.antiprocrastinator.app.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
```

**Step 2: Create Type.kt**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Type.kt`

```kotlin
package com.antiprocrastinator.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
```

**Step 3: Create Theme.kt**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/theme/Theme.kt`

```kotlin
package com.antiprocrastinator.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AntiProcrastinatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**Step 4: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/ui/theme/
git commit -m "feat: setup Material 3 theme"
```

---

### Task 7: Create Navigation Setup

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/Navigation.kt`

**Step 1: Create Navigation.kt**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/Navigation.kt`

```kotlin
package com.antiprocrastinator.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.ui.screens.AddEditTaskScreen
import com.antiprocrastinator.app.ui.screens.TaskDetailScreen
import com.antiprocrastinator.app.ui.screens.TaskListScreen

sealed class Screen(val route: String) {
    object TaskList : Screen("task_list")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: String) = "edit_task/$taskId"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    repository: TaskRepository
) {
    NavHost(
        navController = navController,
        startDestination = Screen.TaskList.route
    ) {
        composable(Screen.TaskList.route) {
            TaskListScreen(
                repository = repository,
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onAddTaskClick = {
                    navController.navigate(Screen.AddTask.route)
                }
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() },
                onEditTask = { navController.navigate(Screen.EditTask.createRoute(taskId)) }
            )
        }

        composable(Screen.AddTask.route) {
            AddEditTaskScreen(
                taskId = null,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTask.route,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            AddEditTaskScreen(
                taskId = taskId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/ui/Navigation.kt
git commit -m "feat: setup navigation with sealed class routes"
```

---

## Phase 7: UI Screens

### Task 8: Create TaskListScreen

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskListScreen.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/components/TaskCard.kt`

**Step 1: Create TaskCard component**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/components/TaskCard.kt`

```kotlin
package com.antiprocrastinator.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antiprocrastinator.app.data.model.Task
import com.antiprocrastinator.app.data.model.TaskLifecycleState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // Lifecycle state badge
                Surface(
                    color = when (task.lifecycleState) {
                        TaskLifecycleState.SCHEDULED -> MaterialTheme.colorScheme.secondaryContainer
                        TaskLifecycleState.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                        TaskLifecycleState.ENDED -> MaterialTheme.colorScheme.tertiaryContainer
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (task.lifecycleState) {
                            TaskLifecycleState.SCHEDULED -> "Scheduled"
                            TaskLifecycleState.IN_PROGRESS -> "In Progress"
                            TaskLifecycleState.ENDED -> "Ended"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time range
            val startDate = task.startDateTime.date
            val endDate = task.endDateTime.date
            val timeRangeText = if (startDate == endDate) {
                "${task.startDateTime.time} - ${task.endDateTime.time}"
            } else {
                "$startDate ${task.startDateTime.time} - $endDate ${task.endDateTime.time}"
            }
            Text(
                text = timeRangeText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Completion badge
            task.completionStatus?.let { status ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = when (status) {
                        com.antiprocrastinator.app.data.model.CompletionStatus.DONE ->
                            MaterialTheme.colorScheme.primaryContainer
                        com.antiprocrastinator.app.data.model.CompletionStatus.GAVE_UP ->
                            MaterialTheme.colorScheme.errorContainer
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = when (status) {
                            com.antiprocrastinator.app.data.model.CompletionStatus.DONE -> "✓ Done"
                            com.antiprocrastinator.app.data.model.CompletionStatus.GAVE_UP -> "✗ Gave Up"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
```

**Step 2: Create TaskListScreen**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskListScreen.kt`

```kotlin
package com.antiprocrastinator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.ui.components.TaskCard
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    repository: TaskRepository,
    onTaskClick: (String) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val tasks by repository.tasks.collectAsState()
    val today = remember { Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date }
    var selectedDate by remember { mutableStateOf(today) }

    val tasksForDate = remember(tasks, selectedDate) {
        repository.getTasksForDate(selectedDate)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Date selector
            DateSelector(
                selectedDate = selectedDate,
                onDateChanged = { selectedDate = it }
            )

            Divider()

            // Task list
            if (tasksForDate.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks for ${selectedDate}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasksForDate, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = {
            onDateChanged(selectedDate.minus(1, kotlinx.datetime.DateTimeUnit.DAY))
        }) {
            Text("← Previous")
        }

        Text(
            text = selectedDate.toString(),
            style = MaterialTheme.typography.titleMedium
        )

        TextButton(onClick = {
            onDateChanged(selectedDate.plus(1, kotlinx.datetime.DateTimeUnit.DAY))
        }) {
            Text("Next →")
        }
    }
}
```

**Step 3: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskListScreen.kt
git add app/src/main/java/com/antiprocrastinator/app/ui/components/TaskCard.kt
git commit -m "feat: implement task list screen with date navigation"
```

---

### Task 9: Create AddEditTaskScreen

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/AddEditTaskScreen.kt`

**Step 1: Create AddEditTaskScreen**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/AddEditTaskScreen.kt`

```kotlin
package com.antiprocrastinator.app.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.antiprocrastinator.app.data.model.Task
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.worker.WorkManagerScheduler
import kotlinx.datetime.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    taskId: String?,
    repository: TaskRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val existingTask = taskId?.let { repository.getTask(it) }
    val isEditMode = existingTask != null

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var startDateTime by remember {
        mutableStateOf(
            existingTask?.startDateTime ?: Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let { it.copy(minute = 0, second = 0, nanosecond = 0) }
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            existingTask?.endDateTime ?: Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let { it.copy(hour = it.hour + 1, minute = 0, second = 0, nanosecond = 0) }
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Task" else "Add Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank()
            )
            if (title.isBlank()) {
                Text(
                    text = "Task title is required",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Start date/time
            Text("Start", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDatePicker(context, startDateTime.date) { date ->
                        startDateTime = LocalDateTime(
                            date,
                            startDateTime.time
                        )
                    }},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(startDateTime.date.toString())
                }
                OutlinedButton(
                    onClick = { showTimePicker(context, startDateTime.time) { time ->
                        startDateTime = LocalDateTime(
                            startDateTime.date,
                            time
                        )
                    }},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(formatTime(startDateTime.time))
                }
            }

            // End date/time
            Text("End", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDatePicker(context, endDateTime.date) { date ->
                        endDateTime = LocalDateTime(
                            date,
                            endDateTime.time
                        )
                    }},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(endDateTime.date.toString())
                }
                OutlinedButton(
                    onClick = { showTimePicker(context, endDateTime.time) { time ->
                        endDateTime = LocalDateTime(
                            endDateTime.date,
                            time
                        )
                    }},
                    modifier = Modifier.weight(1f)
                ) {
                    Text(formatTime(endDateTime.time))
                }
            }

            // Validation error
            if (endDateTime <= startDateTime) {
                Text(
                    text = "End time must be after start time",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Error message from overlap
            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Task title is required"
                        return@Button
                    }
                    if (endDateTime <= startDateTime) {
                        errorMessage = "End time must be after start time"
                        return@Button
                    }

                    val task = Task(
                        id = existingTask?.id ?: UUID.randomUUID().toString(),
                        title = title,
                        startDateTime = startDateTime,
                        endDateTime = endDateTime,
                        lifecycleState = existingTask?.lifecycleState
                            ?: com.antiprocrastinator.app.data.model.TaskLifecycleState.SCHEDULED,
                        completionStatus = existingTask?.completionStatus,
                        actualStartTime = existingTask?.actualStartTime,
                        actualEndTime = existingTask?.actualEndTime,
                        checkIns = existingTask?.checkIns ?: emptyList()
                    )

                    val result = if (isEditMode) {
                        // Cancel old notifications
                        WorkManagerScheduler.cancelTaskNotifications(context, task.id)
                        repository.updateTask(task)
                    } else {
                        repository.addTask(task)
                    }

                    if (result.isSuccess) {
                        // Schedule new notifications
                        WorkManagerScheduler.scheduleTaskNotifications(context, task)
                        onNavigateBack()
                    } else {
                        errorMessage = result.exceptionOrNull()?.message
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && endDateTime > startDateTime
            ) {
                Text(if (isEditMode) "Save Changes" else "Add Task")
            }
        }
    }
}

private fun showDatePicker(
    context: Context,
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(initialDate.year, initialDate.monthNumber - 1, initialDate.dayOfMonth)
    }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun showTimePicker(
    context: Context,
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit
) {
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(LocalTime(hourOfDay, minute))
        },
        initialTime.hour,
        initialTime.minute,
        true // 24-hour format
    ).show()
}

private fun formatTime(time: LocalTime): String {
    return String.format("%02d:%02d", time.hour, time.minute)
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/ui/screens/AddEditTaskScreen.kt
git commit -m "feat: implement add/edit task screen with validation"
```

---

### Task 10: Create TaskDetailScreen

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskDetailScreen.kt`

**Step 1: Create TaskDetailScreen**

Create: `app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskDetailScreen.kt`

```kotlin
package com.antiprocrastinator.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.antiprocrastinator.app.data.model.CompletionStatus
import com.antiprocrastinator.app.data.model.TaskLifecycleState
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.worker.WorkManagerScheduler
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: String,
    repository: TaskRepository,
    onNavigateBack: () -> Unit,
    onEditTask: () -> Unit
) {
    val context = LocalContext.current
    val tasks by repository.tasks.collectAsState()
    val task = remember(tasks) { repository.getTask(taskId) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (task == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Task not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditTask) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task title
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Divider()

            // Time range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Start", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${task.startDateTime.date} ${task.startDateTime.time}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Column {
                    Text("End", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${task.endDateTime.date} ${task.endDateTime.time}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Divider()

            // Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Lifecycle State", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = when (task.lifecycleState) {
                            TaskLifecycleState.SCHEDULED -> "Scheduled"
                            TaskLifecycleState.IN_PROGRESS -> "In Progress"
                            TaskLifecycleState.ENDED -> "Ended"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                task.completionStatus?.let { status ->
                    Column {
                        Text("Completion", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = when (status) {
                                CompletionStatus.DONE -> "Done"
                                CompletionStatus.GAVE_UP -> "Gave Up"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = when (status) {
                                CompletionStatus.DONE -> MaterialTheme.colorScheme.primary
                                CompletionStatus.GAVE_UP -> MaterialTheme.colorScheme.error
                            }
                        )
                    }
                }
            }

            // Quick actions
            if (task.lifecycleState == TaskLifecycleState.IN_PROGRESS &&
                task.completionStatus == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            repository.markTaskComplete(
                                taskId,
                                CompletionStatus.DONE,
                                Clock.System.now()
                            )
                            WorkManagerScheduler.cancelTaskEndWork(context, taskId)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark Done")
                    }
                    OutlinedButton(
                        onClick = {
                            repository.markTaskComplete(
                                taskId,
                                CompletionStatus.GAVE_UP,
                                Clock.System.now()
                            )
                            WorkManagerScheduler.cancelTaskEndWork(context, taskId)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Mark Gave Up")
                    }
                }
            }

            Divider()

            // Check-ins section
            Text(
                text = "Check-ins",
                style = MaterialTheme.typography.titleMedium
            )

            if (task.checkIns.isEmpty()) {
                Text(
                    text = if (task.lifecycleState == TaskLifecycleState.SCHEDULED) {
                        "Task hasn't started yet"
                    } else {
                        "No check-ins recorded"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(task.checkIns.sortedByDescending { it.timestamp }) { checkIn ->
                        Card {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = checkIn.status.name
                                            .replace("_", " ")
                                            .lowercase()
                                            .replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = checkIn.timestamp
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                                            .time.toString(),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                checkIn.note?.let { note ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = note,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Task?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        WorkManagerScheduler.cancelTaskNotifications(context, taskId)
                        repository.deleteTask(taskId)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
```

**Step 2: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/ui/screens/TaskDetailScreen.kt
git commit -m "feat: implement task detail screen with check-ins"
```

---

## Phase 8: MainActivity & App Setup

### Task 11: Wire Up MainActivity

**Files:**
- Modify: `app/src/main/java/com/antiprocrastinator/app/MainActivity.kt`

**Step 1: Create MainActivity**

Create: `app/src/main/java/com/antiprocrastinator/app/MainActivity.kt`

```kotlin
package com.antiprocrastinator.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.data.storage.JsonStorage
import com.antiprocrastinator.app.ui.AppNavigation
import com.antiprocrastinator.app.ui.theme.AntiProcrastinatorTheme
import com.antiprocrastinator.app.worker.NotificationHelper

class MainActivity : ComponentActivity() {

    private lateinit var repository: TaskRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // Show explanation
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize repository
        val storage = JsonStorage(this)
        repository = TaskRepository(storage)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        // Request permissions
        requestPermissions()

        setContent {
            AntiProcrastinatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        repository = repository
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        // Request notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Check exact alarm permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(android.app.AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                // Direct user to settings
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
```

**Step 2: Run the app**

```bash
./gradlew installDebug
```

Expected: App builds and installs successfully

**Step 3: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/MainActivity.kt
git commit -m "feat: wire up MainActivity with permissions"
```

---

## Phase 9: Lock Screen Widget (Glance)

### Task 12: Implement Lock Screen Widget

**Files:**
- Create: `app/src/main/java/com/antiprocrastinator/app/widget/TaskWidget.kt`
- Create: `app/src/main/java/com/antiprocrastinator/app/widget/TaskWidgetReceiver.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/res/xml/task_widget_info.xml`

**Step 1: Create TaskWidget**

Create: `app/src/main/java/com/antiprocrastinator/app/widget/TaskWidget.kt`

```kotlin
package com.antiprocrastinator.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.antiprocrastinator.app.MainActivity
import com.antiprocrastinator.app.data.model.TaskLifecycleState
import com.antiprocrastinator.app.data.repository.TaskRepository
import com.antiprocrastinator.app.data.storage.JsonStorage
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

class TaskWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val storage = JsonStorage(context)
        val repository = TaskRepository(storage)

        provideContent {
            TaskWidgetContent(context, repository)
        }
    }
}

@Composable
fun TaskWidgetContent(context: Context, repository: TaskRepository) {
    val activeTask = repository.getActiveTask()
    val now = Clock.System.now()

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (activeTask != null) {
            // Show active task
            Text(
                text = activeTask.title,
                style = TextStyle(
                    fontSize = 18.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            // Calculate time remaining
            val endTime = activeTask.endDateTime
                .toInstant(TimeZone.currentSystemDefault())
            val remainingMillis = endTime.toEpochMilliseconds() - now.toEpochMilliseconds()
            val remainingMinutes = (remainingMillis / 60000).toInt()

            if (remainingMinutes > 0) {
                Text(
                    text = "$remainingMinutes min left",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                    )
                )
            } else {
                Text(
                    text = "Time's up!",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(12.dp))

            Text(
                text = "Open App",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(androidx.compose.ui.graphics.Color.LightGray)
                )
            )
        } else {
            // No active task - show next scheduled
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            val nextTask = repository.getTasksForDate(today)
                .firstOrNull { it.lifecycleState == TaskLifecycleState.SCHEDULED }

            if (nextTask != null) {
                Text(
                    text = "Next: ${nextTask.title}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "at ${nextTask.startDateTime.time}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.LightGray)
                    )
                )
            } else {
                Text(
                    text = "No tasks scheduled today",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ColorProvider(androidx.compose.ui.graphics.Color.White)
                    )
                )
            }
        }
    }
}
```

**Step 2: Create TaskWidgetReceiver**

Create: `app/src/main/java/com/antiprocrastinator/app/widget/TaskWidgetReceiver.kt`

```kotlin
package com.antiprocrastinator.app.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TaskWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TaskWidget()
}
```

**Step 3: Create widget info XML**

Create: `app/src/main/res/xml/task_widget_info.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:minWidth="250dp"
    android:minHeight="100dp"
    android:updatePeriodMillis="300000"
    android:initialLayout="@layout/glance_default_loading_layout"
    android:resizeMode="horizontal|vertical"
    android:widgetCategory="keyguard|home_screen"
    android:description="@string/widget_description" />
```

**Step 4: Add widget description to strings.xml**

Modify: `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name">Anti-Procrastinator</string>
    <string name="widget_description">Shows your current task and time remaining</string>
</resources>
```

**Step 5: Register widget in AndroidManifest.xml**

Modify: `app/src/main/AndroidManifest.xml` (add inside `<application>` tag)

```xml
        <receiver
            android:name=".widget.TaskWidgetReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/task_widget_info" />
        </receiver>
```

**Step 6: Create glance default loading layout**

Create: `app/src/main/res/layout/glance_default_loading_layout.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <ProgressBar
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center" />
</FrameLayout>
```

**Step 7: Update TaskStartWorker and TaskEndWorker to update widget**

Modify: `app/src/main/java/com/antiprocrastinator/app/worker/TaskStartWorker.kt`

Add at the end of `doWork()` before `return Result.success()`:

```kotlin
        // Update widget
        TaskWidget().updateAll(applicationContext)
```

Add import:
```kotlin
import com.antiprocrastinator.app.widget.TaskWidget
import androidx.glance.appwidget.updateAll
```

Modify: `app/src/main/java/com/antiprocrastinator/app/worker/TaskEndWorker.kt`

Add at the end of `doWork()` before `return Result.success()`:

```kotlin
        // Update widget
        TaskWidget().updateAll(applicationContext)
```

Add import:
```kotlin
import com.antiprocrastinator.app.widget.TaskWidget
import androidx.glance.appwidget.updateAll
```

**Step 8: Commit**

```bash
git add app/src/main/java/com/antiprocrastinator/app/widget/
git add app/src/main/res/xml/task_widget_info.xml
git add app/src/main/res/layout/glance_default_loading_layout.xml
git add app/src/main/res/values/strings.xml
git add app/src/main/AndroidManifest.xml
git add app/src/main/java/com/antiprocrastinator/app/worker/TaskStartWorker.kt
git add app/src/main/java/com/antiprocrastinator/app/worker/TaskEndWorker.kt
git commit -m "feat: implement lock screen widget with Glance"
```

---

## Phase 10: Testing & Polish

### Task 13: Add Integration Tests

**Files:**
- Create: `app/src/androidTest/java/com/antiprocrastinator/app/TaskFlowTest.kt`

**Step 1: Create integration test**

Create: `app/src/androidTest/java/com/antiprocrastinator/app/TaskFlowTest.kt`

```kotlin
package com.antiprocrastinator.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addTask_appearsInList() {
        // Click FAB
        composeTestRule.onNodeWithContentDescription("Add Task").performClick()

        // Enter task title
        composeTestRule.onNodeWithText("Task Title").performTextInput("Test Task")

        // Click Add Task button
        composeTestRule.onNodeWithText("Add Task").performClick()

        // Verify task appears in list
        composeTestRule.onNodeWithText("Test Task").assertExists()
    }

    @Test
    fun taskCard_opensDetailScreen() {
        // Add a task first (simplified - assumes task exists)
        composeTestRule.onNodeWithContentDescription("Add Task").performClick()
        composeTestRule.onNodeWithText("Task Title").performTextInput("Detail Test")
        composeTestRule.onNodeWithText("Add Task").performClick()

        // Click on task card
        composeTestRule.onNodeWithText("Detail Test").performClick()

        // Verify detail screen shows
        composeTestRule.onNodeWithText("Task Details").assertExists()
    }
}
```

**Step 2: Run instrumented tests**

```bash
./gradlew connectedAndroidTest
```

Expected: Tests pass (requires emulator or device)

**Step 3: Commit**

```bash
git add app/src/androidTest/java/com/antiprocrastinator/app/TaskFlowTest.kt
git commit -m "test: add integration tests for task flow"
```

---

### Task 14: Final Polish & Bug Fixes

**Step 1: Add proper app icon**

Note: Use Android Studio's Image Asset tool to generate proper launcher icons.

For now, the default icons are fine for MVP.

**Step 2: Test end-to-end flow manually**

1. Launch app
2. Add a task scheduled for 1 minute from now
3. Wait for start notification
4. Log check-ins from notification
5. Mark task done
6. Verify task shows as completed in detail screen
7. Add widget to lock screen
8. Verify widget shows active task

**Step 3: Fix any bugs discovered**

Document bugs in issues and fix as needed.

**Step 4: Final commit**

```bash
git add .
git commit -m "chore: final polish and bug fixes for MVP"
```

---

## Summary

This implementation plan covers:

1. ✅ Project setup with all dependencies
2. ✅ Data models with serialization
3. ✅ JSON storage layer with atomic writes
4. ✅ Repository with overlap validation
5. ✅ WorkManager for notifications
6. ✅ Material 3 UI theme
7. ✅ Navigation setup
8. ✅ Task list screen with date navigation
9. ✅ Add/edit task screen with validation
10. ✅ Task detail screen with check-ins
11. ✅ MainActivity with permissions
12. ✅ Lock screen widget with Glance
13. ✅ Integration tests
14. ✅ Final polish

**Total estimated time:** 2-3 days of focused development

**Next steps after MVP:**
- Visual timeline for check-ins
- Analytics dashboard
- Task recurrence
- Data export/backup
