# Anti-Procrastinator

An Android app that helps you stay accountable to your scheduled tasks through lock screen reminders, notifications, and detailed work pattern tracking.

## Features

- **Task Management** - Schedule tasks with specific start and end date-times
- **Lock Screen Widget** - Shows your current task and time remaining directly on your lock screen
- **Smart Notifications** - Get notified when tasks start and end, with inline check-in buttons
- **Work Pattern Tracking** - Log what you're actually doing (working, taking a break, distracted, gave up)
- **Completion Tracking** - Mark tasks as done or gave up, with timestamps
- **Overlap Prevention** - Prevents scheduling conflicting tasks
- **Local Storage** - All data stored locally in JSON, no cloud backend required

## Requirements

- **Android 13+** (API 33+)
- The app requires Android 13 or higher to support lock screen widgets via the Glance API

## Architecture

Built with modern Android development practices:

- **MVVM Architecture** with Jetpack Compose for UI
- **Glance API** for lock screen widgets
- **WorkManager** for reliable background task scheduling
- **Kotlinx Serialization** for JSON data persistence
- **Material 3** design system
- **Repository Pattern** for data access

## Project Structure

```
app/src/main/java/com/antiprocrastinator/app/
├── data/
│   ├── model/          # Data classes and enums
│   ├── repository/     # Repository layer for data access
│   └── storage/        # JSON file storage
├── ui/
│   ├── screens/        # Compose screens
│   ├── components/     # Reusable UI components
│   └── theme/          # Material 3 theme
├── widget/             # Glance lock screen widget
└── worker/             # WorkManager background jobs
```

## Development Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK with API 34

### Building

```bash
# Clone the repository
git clone https://github.com/miquez/anti-procrastinator.git
cd anti-procrastinator

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## How It Works

### Task Lifecycle

1. **Scheduled** - Task is created and waiting for its start time
2. **In Progress** - Task has started, notification is shown with check-in options
3. **Ended** - Task time has expired or user marked it complete

### Notifications

**Start Notification:**
- Shown when task starts
- Persistent with inline action buttons: Working, Taking a Break, Distracted, Gave Up
- Additional actions: Mark Done, Open App

**End Notification:**
- Shown if task ends without being marked complete
- Options: Finished or Didn't Finish

### Check-ins vs Completion

- **Check-ins** (Working, Taking a Break, Distracted) are logged notes with optional text
- **Gave Up** check-in also marks the task as incomplete and ends it immediately
- **Mark Done** sets completion status and ends the task

## Implementation Status

### ✅ Completed
- Android project structure with all dependencies
- Core data models with serialization support
- Overlap detection logic with comprehensive tests

### 🚧 In Progress
See [docs/plans/2025-11-21-implementation-plan.md](docs/plans/2025-11-21-implementation-plan.md) for the full implementation plan.

Remaining tasks:
- JSON storage layer
- Task repository
- WorkManager notification system
- Material 3 UI theme
- Compose screens (task list, add/edit, detail)
- Lock screen widget
- Integration tests

## Permissions

The app requires the following permissions:

- `POST_NOTIFICATIONS` - To send task reminders
- `SCHEDULE_EXACT_ALARM` - For precise task timing
- `WAKE_LOCK` - To ensure notifications display when device is sleeping

## Contributing

This is a personal project, but suggestions and bug reports are welcome via GitHub issues.

## License

MIT License - See LICENSE file for details

## Design Documents

- [Design Document](docs/plans/2025-11-21-anti-procrastination-app-design.md) - Complete feature specification and architecture
- [Implementation Plan](docs/plans/2025-11-21-implementation-plan.md) - Step-by-step development guide
- [CLAUDE.md](CLAUDE.md) - Development guidance for Claude Code
