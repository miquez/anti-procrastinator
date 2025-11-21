# Anti-Procrastination App - Design Document

**Date:** 2025-11-21
**Platform:** Android 13+ only
**Architecture:** Self-contained, no cloud backend

## Overview

An anti-procrastination app that helps users stay accountable to their scheduled tasks through lock screen reminders, notifications, and detailed tracking of work patterns.

## Core Features

- Task management with specific start/end date-times
- Lock screen widget showing active tasks
- Push notifications for task start/end
- Inline check-ins from notifications
- Completion tracking (done/gave up)
- Task detail view with check-in history
- Overlap prevention

## Architecture

**Pattern:** MVVM with Jetpack Compose

**Key Components:**
- Repository pattern for data access
- WorkManager for scheduled notifications
- Glance API for lock screen widget
- Local JSON file storage

## Data Model

### Task
```kotlin
Task {
  id: UUID
  title: String
  startDateTime: LocalDateTime
  endDateTime: LocalDateTime
  lifecycleState: SCHEDULED | IN_PROGRESS | ENDED
  completionStatus: DONE | GAVE_UP | null
  actualStartTime: Instant?  // when moved to IN_PROGRESS
  actualEndTime: Instant?    // when marked DONE/GAVE_UP
  checkIns: List<CheckIn>
}
```

### CheckIn
```kotlin
CheckIn {
  timestamp: Instant
  status: WORKING | TAKING_BREAK | DISTRACTED | GAVE_UP
  note: String?  // optional "what were you doing instead"
}
```

## Task Lifecycle

### States
1. **SCHEDULED** - Task created, waiting for start time
2. **IN_PROGRESS** - Between start and end time
3. **ENDED** - Past end time or marked complete early

### State Transitions

**At Start Time:**
- Update: `lifecycleState = IN_PROGRESS`, `actualStartTime = now()`
- Send start notification
- Update lock screen widget

**At End Time:**
- Update: `lifecycleState = ENDED`
- If no completion status: send end notification
- Update widget

**Early Completion (Gave Up):**
- User clicks "gave up" from notification
- Update: `completionStatus = GAVE_UP`, `actualEndTime = now()`, `lifecycleState = ENDED`
- Cancel end alarm
- Dismiss notification
- Update widget

**Manual Completion:**
- User marks done from notification or app
- Update: `completionStatus = DONE`, `actualEndTime = now()`
- If still in progress: update `lifecycleState = ENDED`, cancel end alarm

### Overlap Prevention
- When creating/editing task, check for overlapping `[startDateTime, endDateTime]` ranges
- Block save with error: "Conflicts with [Task Title] ([date time range])"
- Adjacent tasks (one ends exactly when another starts) are allowed

## Lock Screen Widget

**Display (Android 13+ Glance API):**

When task IN_PROGRESS:
- Task title
- Time remaining (e.g., "45 min left")
- "Open App" button

When no active task:
- Next scheduled task: "[Title] at [time]"
- Or "No tasks scheduled today"

**Update Strategy:**
- On lock screen display (`onGlanceableUpdate`)
- Periodic updates every 5 minutes when task IN_PROGRESS
- Immediate updates on state transitions

## Notifications

### Start Notification
**Trigger:** At task start time

**Content:**
- Title: "Time to work on [Task Title]"
- Expanded inline buttons: [Working] [Taking a Break] [Distracted] [Gave Up]
- Standard actions: [Mark Done] [Open App]

**Behavior:**
- Persistent/ongoing notification (stays visible)
- Clicking status button logs check-in, shows toast, notification stays
- Exception: [Gave Up] ends task early
- Dismissed when: task ends, marked done, or manually dismissed

### End Notification
**Trigger:** At task end time (only if no completion status)

**Content:**
- Title: "Did you finish [Task Title]?"
- Actions: [Finished] [Didn't Finish]
- Tap opens task detail

## UI Structure

### 1. Main Screen - Task List
- Date selector (swipeable, default today)
- Tasks sorted by start time
- Task cards show:
  - Title
  - Time range (with date if crosses days)
  - Lifecycle state indicator
  - Completion badge (if done/gave up)
- Tasks shown if they start, end, or are active on selected date
- FAB: "Add Task"
- Empty state: "No tasks for [date]"

### 2. Task Detail Screen
- Task info (title, date/time range)
- Edit/Delete buttons
- Status display
- Check-ins section:
  - Chronological list
  - Each: timestamp, status, optional note
- Quick actions: [Mark Done] [Mark Gave Up] (if in progress)

### 3. Add/Edit Task Screen
- Title field (required)
- Start: date picker + time picker
- End: date picker + time picker
- Validation:
  - End must be after start
  - No overlaps with existing tasks
- Save/Cancel buttons

## Data Persistence

**Storage:** JSON files in app private directory

**Location:** `/data/data/com.yourapp.antiprocrastination/files/`

**Format:**
```json
{
  "tasks": [
    {
      "id": "uuid",
      "title": "Design mockups",
      "startDateTime": "2025-11-22T10:00:00",
      "endDateTime": "2025-11-22T11:30:00",
      "lifecycleState": "IN_PROGRESS",
      "completionStatus": null,
      "actualStartTime": "2025-11-22T10:00:15Z",
      "actualEndTime": null,
      "checkIns": [
        {
          "timestamp": "2025-11-22T10:15:30Z",
          "status": "WORKING",
          "note": null
        }
      ]
    }
  ]
}
```

**Repository Pattern:**
- Read file on app start, keep in memory
- Expose `StateFlow<List<Task>>` for UI observation
- Write to file on every modification
- Atomic writes (temp file + rename) prevent corruption

## Background Work

**WorkManager Jobs:**
- Two `OneTimeWorkRequest` per task:
  - Start job at `startDateTime`
  - End job at `endDateTime`
- Tagged with `task.id` for cancellation
- Persist across reboots

**On Task Edit/Delete:**
- Cancel old jobs by tag
- Schedule new jobs if edited

## Permissions

**Required:**
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

**Runtime Flow:**
- First launch: request notification permission
- Direct to settings for exact alarm permission
- Show rationale dialogs

## Error Handling

**Validation:**
- Empty title: inline error
- End before start: inline error
- Overlap detected: show conflicting task details

**Permission Denied:**
- Persistent banner with Settings button

**Data Corruption:**
- Backup corrupt file as `.backup`
- Start with empty list
- Show one-time toast

**WorkManager Failures:**
- Retry once after 1 minute
- Silent failure if persistent (no annoying errors)

## Technology Stack

**Platform:**
- Minimum SDK: Android 13 (API 33)
- Language: Kotlin
- Build: Gradle with Kotlin DSL

**Key Dependencies:**
- Jetpack Compose (UI)
- Material 3 (design)
- Glance (lock screen widget)
- WorkManager (background jobs)
- Kotlinx Serialization (JSON)
- Kotlinx DateTime (date/time handling)
- Navigation Compose (screen navigation)
- Lifecycle/ViewModel

**Project Structure:**
```
app/src/main/java/com/yourapp/antiprocrastination/
├── data/
│   ├── model/           # Task, CheckIn
│   ├── repository/      # TaskRepository
│   └── storage/         # JSON I/O
├── ui/
│   ├── screens/         # Main, Detail, AddEdit
│   ├── components/      # Reusable UI
│   └── theme/           # Material 3 theme
├── widget/              # Glance widget
├── worker/              # WorkManager workers
└── MainActivity.kt
```

## Testing Focus Areas

1. Cross-day tasks (11 PM - 2 AM)
2. Overlap detection (same-day, cross-day, adjacent)
3. State transitions and early completion
4. Notification persistence and check-ins
5. Data persistence and corruption recovery
6. WorkManager job scheduling/cancellation

## MVP Scope

**Included:**
- All core features listed above
- Simple chronological check-in list
- Local storage only

**Excluded (Future):**
- Analytics/reports UI
- Visual timeline for check-ins
- Task recurrence
- Backup/restore
- Data export
- Task categories/tags
- Home screen widgets
- Pre-task reminders

## Future Enhancements

1. Visual timeline for check-ins
2. Analytics dashboard
3. Task recurrence patterns
4. CSV/JSON export
