# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Anti-procrastination Android app (minSdk 33, Android 13+) that helps users stay accountable through lock screen widgets, notifications, and work pattern tracking. Self-contained with no cloud backend - all data stored locally in JSON.

**Key Tech Stack:**
- Kotlin with Jetpack Compose + Material 3
- Glance API for lock screen widgets
- WorkManager for scheduled notifications
- Kotlinx Serialization for JSON persistence
- Kotlinx DateTime for time handling
- MVVM architecture pattern

## Development Commands

### Build and Test

```bash
# Set Java home (required for Gradle)
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.17/libexec/openjdk.jdk/Contents/Home

# Run all tests
./gradlew test --no-daemon

# Run specific test class
./gradlew test --tests "com.antiprocrastinator.app.data.model.TaskTest"

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

### Project Structure

```
app/src/main/java/com/antiprocrastinator/app/
├── data/
│   ├── model/          # Data classes with Kotlinx Serialization
│   ├── repository/     # Repository pattern (not yet implemented)
│   └── storage/        # JSON file I/O (not yet implemented)
├── ui/
│   ├── screens/        # Compose screens (not yet implemented)
│   ├── components/     # Reusable UI components (not yet implemented)
│   └── theme/          # Material 3 theme (not yet implemented)
├── widget/             # Glance lock screen widget (not yet implemented)
└── worker/             # WorkManager background tasks (not yet implemented)
```

## Architecture Details

### Task Lifecycle State Machine

Tasks flow through three lifecycle states:
1. **SCHEDULED** → Task created, waiting for start time
2. **IN_PROGRESS** → WorkManager fires at startDateTime, sets actualStartTime, shows notification
3. **ENDED** → WorkManager fires at endDateTime, OR user marks done/gave up early

Completion status is independent from lifecycle state:
- **DONE** - User explicitly marked task complete
- **GAVE_UP** - User explicitly marked task as abandoned
- **null** - No completion action taken yet

**Critical Rule:** If user selects "gave up" from notification during a task, it immediately ends the task (sets ENDED state, cancels end alarm), unlike other check-ins which are just logged.

### Overlap Prevention

Tasks are validated to prevent time conflicts using `Task.overlaps(other)`:
```kotlin
// Detects if [startDateTime, endDateTime) intervals overlap
this.startDateTime < other.endDateTime && this.endDateTime > other.startDateTime
```

**Important:** Adjacent tasks (where one ends exactly when another starts) are explicitly allowed and will NOT trigger overlap errors.

### Data Persistence Strategy

- **Storage:** JSON files in app private directory (`/data/data/com.antiprocrastinator.app/files/tasks.json`)
- **Pattern:** Load entire file on app start → keep in memory → write on every modification
- **Safety:** Atomic writes (write to temp file → rename) to prevent corruption
- **Repository:** Exposes `StateFlow<List<Task>>` for UI observation
- **Serialization:** Custom `LocalDateTimeSerializer` for kotlinx.datetime.LocalDateTime fields

### WorkManager Notification Scheduling

For each task, two OneTimeWorkRequest jobs are scheduled:
1. **Start job** at `startDateTime` - Shows persistent notification with check-in buttons
2. **End job** at `endDateTime` - Shows completion prompt if task not already marked done/gave up

Jobs are tagged with `task.id` for easy cancellation when tasks are edited/deleted.

### Notification Behavior

**Start Notification (persistent, ongoing):**
- Inline action buttons: [Working] [Taking a Break] [Distracted] [Gave Up]
- Clicking buttons logs check-in but keeps notification visible (except "Gave Up" which ends task)
- Additional actions: [Mark Done] [Open App]
- Auto-dismissed when task ends or marked complete

**End Notification:**
- Only shown if no completion status set
- Actions: [Finished] [Didn't Finish]

### Lock Screen Widget Updates

Widget updates triggered by:
- On lock screen display (`onGlanceableUpdate`) - most battery efficient
- Periodic updates every 5 minutes when task IN_PROGRESS
- Immediate updates on task state transitions (start/end/completion)

## Implementation Status

**Completed (Tasks 1-2):**
- ✅ Android project structure with all dependencies
- ✅ Core data models (Task, CheckIn, enums) with serialization
- ✅ Unit tests for overlap detection logic

**Remaining (Tasks 3-14) - See docs/plans/2025-11-21-implementation-plan.md:**
- JSON storage layer
- TaskRepository with StateFlow
- WorkManager workers for notifications
- NotificationActionReceiver for inline check-ins
- Material 3 theme setup
- Navigation with Compose
- UI screens (TaskList, AddEdit, Detail)
- MainActivity with permission requests
- Glance lock screen widget
- Integration tests

## Key Design Decisions

**No Task Recurrence:** Tasks are one-time only in MVP. Users manually create tasks for each occurrence.

**Cross-Day Tasks:** Tasks can span multiple days (e.g., 11 PM - 2 AM). UI shows tasks on all dates they overlap.

**Check-In vs Completion:**
- Check-ins (WORKING, TAKING_BREAK, DISTRACTED) are logged notes that don't affect task state
- GAVE_UP as check-in also sets completion status and ends task
- DONE as completion status ends task

**No Analytics in MVP:** Check-in data is collected but no analytics/visualization UI is built yet.

## Testing Notes

- Unit tests use JUnit 4 (not JUnit 5)
- Android instrumented tests use Espresso and Compose testing
- Tests require valid launcher icons in res/mipmap-* directories (placeholder 1x1 PNGs currently present)
- Run tests with `--no-daemon` flag to avoid Gradle daemon issues with Java home detection
